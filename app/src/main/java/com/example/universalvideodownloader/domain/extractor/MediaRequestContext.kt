package com.example.universalvideodownloader.domain.extractor

data class MediaRequestContext(
    val mediaUrl: String,
    val pageUrl: String,
    val frameUrl: String?,
    val userAgent: String,
    val cookie: String?,
    val referer: String?,
    val origin: String?,
    val authorization: String?,
    val extraHeaders: Map<String, String>,
    val capturedAt: Long
)
