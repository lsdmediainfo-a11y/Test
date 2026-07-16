package com.example.universalvideodownloader.domain.extractor

enum class CaptureSource {
    WEBVIEW_INTERCEPT,
    LOAD_RESOURCE,
    SERVICE_WORKER,
    FETCH,
    XHR,
    PERFORMANCE_RESOURCE,
    VIDEO_ELEMENT,
    SOURCE_ELEMENT,
    DOWNLOAD_LISTENER
}

data class CapturedNetworkEvent(
    val id: String,
    val url: String,
    val method: String,
    val pageUrl: String?,
    val frameUrl: String?,
    val requestHeaders: Map<String, String>,
    val cookie: String?,
    val source: CaptureSource
)
