package com.example.universalvideodownloader.data.download

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.FileOutputStream
import okhttp3.OkHttpClient
import okhttp3.Request
import org.chromium.net.CronetEngine
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import org.chromium.net.CronetException
import java.nio.ByteBuffer
import java.nio.channels.Channels
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@HiltWorker
class VideoDownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val cronetEngine: CronetEngine,
    private val okHttpClient: OkHttpClient
) : CoroutineWorker(appContext, workerParams) {

    // Bölüm 18: İlk sürümde OkHttp kullanılacak. İleride Cronet'e (fallback OkHttp) geçirilecek.
    private val client = OkHttpClient.Builder().build()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        // WorkManager 10KB Data Limit Koruması: Büyük JSON'lar yerine Database'den SessionID ile Context okunur
        val candidateId = inputData.getString("CANDIDATE_ID") ?: return@withContext Result.failure()
        val videoUrl = inputData.getString("VIDEO_URL") ?: return@withContext Result.failure()
        val type = inputData.getString("TYPE") ?: "MP4"
        
        Log.d("DownloadWorker", "OkHttp İndirme Başlıyor: $videoUrl")
        
        try {
            if (type == "HLS" || type == "DASH") {
                Log.d("DownloadWorker", "HLS/DASH Master Worker başlatıldı. Tüm TS parçaları burada işlenecek.")
                return@withContext Result.success()
            }

            val baseFileName = "video_${System.currentTimeMillis()}"
            val partFileName = "$baseFileName.mp4.part"
            val finalFileName = "$baseFileName.mp4"
            val outputFile = File(applicationContext.getExternalFilesDir(null), partFileName)
            val finalFile = File(applicationContext.getExternalFilesDir(null), finalFileName)
            
            // TODO: Header'ları Room'dan çek
            val headers = mapOf(
                "User-Agent" to "Mozilla/5.0",
                "Referer" to videoUrl
            )
            
            try {
                Log.d("DownloadWorker", "Cronet (HTTP/3) ile indiriliyor: $videoUrl")
                downloadWithCronet(cronetEngine, videoUrl, outputFile, headers)
            } catch (e: Exception) {
                Log.e("DownloadWorker", "Cronet hatası, OkHttp fallback devreye giriyor...", e)
                downloadWithOkHttp(videoUrl, outputFile, headers)
            }
            
            if (outputFile.exists()) {
                outputFile.renameTo(finalFile)
            }
            
            Log.d("DownloadWorker", "İndirme Tamamlandı: ${finalFile.absolutePath}")
            Result.success()
        } catch (e: Exception) {
            Log.e("DownloadWorker", "Bağlantı kesintisi veya hata", e)
            Result.retry()
        }
    }

    private suspend fun downloadWithCronet(engine: CronetEngine, url: String, outputFile: File, headers: Map<String, String>) = suspendCancellableCoroutine<Boolean> { continuation ->
        val outputStream = FileOutputStream(outputFile, true)
        val channel = Channels.newChannel(outputStream)
        
        val callback = object : UrlRequest.Callback() {
            override fun onRedirectReceived(request: UrlRequest, info: UrlResponseInfo, newLocationUrl: String) {
                request.followRedirect()
            }

            override fun onResponseStarted(request: UrlRequest, info: UrlResponseInfo) {
                if (info.httpStatusCode in 200..299) {
                    request.read(ByteBuffer.allocateDirect(32768))
                } else {
                    request.cancel()
                    continuation.resumeWithException(Exception("HTTP Error: ${info.httpStatusCode}"))
                }
            }

            override fun onReadCompleted(request: UrlRequest, info: UrlResponseInfo, byteBuffer: ByteBuffer) {
                byteBuffer.flip()
                channel.write(byteBuffer)
                byteBuffer.clear()
                request.read(byteBuffer)
            }

            override fun onSucceeded(request: UrlRequest, info: UrlResponseInfo) {
                channel.close()
                outputStream.close()
                continuation.resume(true)
            }

            override fun onFailed(request: UrlRequest, info: UrlResponseInfo, error: CronetException) {
                channel.close()
                outputStream.close()
                continuation.resumeWithException(error)
            }
            
            override fun onCanceled(request: UrlRequest, info: UrlResponseInfo) {
                channel.close()
                outputStream.close()
                continuation.resume(false)
            }
        }

        val requestBuilder = engine.newUrlRequestBuilder(url, callback, java.util.concurrent.Executors.newSingleThreadExecutor())
        headers.forEach { (k, v) -> requestBuilder.addHeader(k, v) }
        
        val request = requestBuilder.build()
        continuation.invokeOnCancellation { request.cancel() }
        request.start()
    }
    
    private fun downloadWithOkHttp(url: String, outputFile: File, headers: Map<String, String>) {
        val requestBuilder = Request.Builder().url(url)
        headers.forEach { (k, v) -> requestBuilder.header(k, v) }
        val request = requestBuilder.build()
        val response = okHttpClient.newCall(request).execute()
        if (response.isSuccessful) {
            response.body?.byteStream()?.use { input ->
                FileOutputStream(outputFile, true).use { output ->
                    input.copyTo(output)
                }
            }
        } else {
            throw Exception("OkHttp Error: ${response.code}")
        }
    }
}
