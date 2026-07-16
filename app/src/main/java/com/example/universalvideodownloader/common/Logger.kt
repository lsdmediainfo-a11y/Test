package com.example.universalvideodownloader.common

import android.util.Log

enum class LogCategory {
    BROWSER, CAPTURE, RESOLVER, HLS, DASH, DOWNLOAD, ADBLOCK, STORAGE, SECURITY
}

object AppLogger {
    
    // Release modunu simüle etmek için şimdilik false yapıldı (BuildConfig'e bağlanabilir)
    private val isDebug = false 

    fun d(category: LogCategory, message: String) {
        val safeMessage = if (isDebug) message else maskSensitiveData(message)
        Log.d("UVD_${category.name}", safeMessage)
    }

    fun e(category: LogCategory, message: String, throwable: Throwable? = null) {
        val safeMessage = if (isDebug) message else maskSensitiveData(message)
        Log.e("UVD_${category.name}", safeMessage, throwable)
    }

    fun w(category: LogCategory, message: String) {
        val safeMessage = if (isDebug) message else maskSensitiveData(message)
        Log.w("UVD_${category.name}", safeMessage)
    }

    private fun maskSensitiveData(message: String): String {
        // Release build'de Token, Cookie, Auth ve query parametrelerini maskele
        var masked = message.replace(Regex("(?i)(cookie|authorization):\\s*[^\\n\\r]+"), "$1: [MASKED]")
        masked = masked.replace(Regex("(?i)([?&])(token|signature|sig|auth)=([^&\\s]+)"), "$1$2=[MASKED]")
        return masked
    }
}
