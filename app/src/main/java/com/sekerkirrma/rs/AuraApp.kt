package com.sekerkirrma.rs

import android.app.Application
import android.util.Log
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AuraApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initYoutubeDL()
    }

    private fun initYoutubeDL() {
        try {
            YoutubeDL.getInstance().init(this)
            Log.d("AuraApp", "YoutubeDL initialized successfully.")
        } catch (e: YoutubeDLException) {
            Log.e("AuraApp", "Failed to initialize YoutubeDL", e)
        }
    }
}
