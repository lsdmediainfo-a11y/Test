package com.example.universalvideodownloader.ui.browser.capture

data class PlaybackCaptureSession(
    val pageUrl: String,
    val sessionId: String,
    val activeEvents: List<CapturedNetworkEvent> = emptyList(),
    val isVideoPlaying: Boolean = false
)
