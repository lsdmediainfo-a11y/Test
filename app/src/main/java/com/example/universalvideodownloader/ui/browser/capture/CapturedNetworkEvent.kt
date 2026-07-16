package com.example.universalvideodownloader.ui.browser.capture

import kotlinx.serialization.Serializable

@Serializable
data class CapturedNetworkEvent(
    val url: String,
    val type: String,
    val method: String = "GET",
    val source: String,
    val headers: String = "{}",
    var score: Int = 0,
    var isAd: Boolean = false
)
