package com.example.universalvideodownloader.ui.browser.capture

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Singleton
class CaptureManager @Inject constructor() {

    private val _currentSession = MutableStateFlow<PlaybackCaptureSession?>(null)
    val currentSession: StateFlow<PlaybackCaptureSession?> = _currentSession.asStateFlow()

    fun onNewPage(url: String) {
        _currentSession.value = PlaybackCaptureSession(
            pageUrl = url,
            sessionId = System.currentTimeMillis().toString()
        )
    }

    fun onEventCaptured(event: CapturedNetworkEvent) {
        _currentSession.update { session ->
            if (session == null) return@update null
            
            // Scoring
            val urlLower = event.url.lowercase()
            var score = 0
            if (urlLower.contains(".m3u8") || urlLower.contains(".mpd") || urlLower.contains(".mp4")) score += 50
            if (event.source == "play_event") score += 40
            
            if (urlLower.contains("vast") || urlLower.contains("vpaid") || urlLower.contains("midroll") || 
                urlLower.contains("preroll") || urlLower.contains("adserver") || urlLower.contains("promo")) {
                score -= 100
            }
            
            event.score = score
            event.isAd = score < 0
            
            // Deduplication (Aynı tür ve URL'ye sahip etkinlikleri ekleme)
            val exists = session.activeEvents.any { it.url == event.url }
            if (exists) {
                // Sadece isVideoPlaying durumunu güncellememiz gerekebilir (play_event gelirse)
                if (event.source == "play_event" && !session.isVideoPlaying) {
                    session.copy(isVideoPlaying = true)
                } else {
                    session
                }
            } else {
                val updatedEvents = session.activeEvents.toMutableList().apply { add(event) }
                val isPlaying = session.isVideoPlaying || event.source == "play_event"
                session.copy(
                    activeEvents = updatedEvents,
                    isVideoPlaying = isPlaying
                )
            }
        }
    }
}
