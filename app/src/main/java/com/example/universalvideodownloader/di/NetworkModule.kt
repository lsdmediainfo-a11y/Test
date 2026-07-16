package com.example.universalvideodownloader.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.chromium.net.CronetEngine
import javax.inject.Singleton
import okhttp3.OkHttpClient
import java.io.File

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideCronetEngine(@ApplicationContext context: Context): CronetEngine {
        val cacheDir = File(context.cacheDir, "cronet-cache")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        
        return CronetEngine.Builder(context)
            .enableHttp2(true)
            .enableQuic(true) // HTTP/3 Support
            .enableBrotli(true)
            .setStoragePath(cacheDir.absolutePath)
            .enableHttpCache(CronetEngine.Builder.HTTP_CACHE_DISK_NO_HTTP, 100 * 1024 * 1024)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }
}
