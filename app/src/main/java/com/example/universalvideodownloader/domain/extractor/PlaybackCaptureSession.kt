package com.example.universalvideodownloader.domain.extractor

enum class CaptureStatus {
    ACTIVE,
    PAUSED,
    COMPLETED,
    ERROR
}

data class PlaybackCaptureSession(
    val id: String,
    val pageUrl: String,
    val startedAt: Long,
    val status: CaptureStatus,
    val relatedRequestIds: Set<String>
)
