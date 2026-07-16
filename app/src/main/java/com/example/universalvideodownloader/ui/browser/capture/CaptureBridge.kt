package com.example.universalvideodownloader.ui.browser.capture

import android.webkit.JavascriptInterface
import android.util.Log
import kotlinx.serialization.json.Json

class CaptureBridge(private val onEventCaptured: (CapturedNetworkEvent) -> Unit) {
    
    @JavascriptInterface
    fun onMediaDiscovered(jsonPayload: String) {
        try {
            val event = Json { ignoreUnknownKeys = true }.decodeFromString<CapturedNetworkEvent>(jsonPayload)
            Log.d("CaptureBridge", "DISCOVERED MEDIA -> Source: ${event.source}, Type: ${event.type}, URL: ${event.url}")
            onEventCaptured(event)
        } catch (e: Exception) {
            Log.e("CaptureBridge", "Error parsing media payload", e)
        }
    }
}
