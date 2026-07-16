package com.example.universalvideodownloader.domain.extractor.site

data class SiteProfile(
    val domainPattern: String,
    val customJsInjection: String? = null,
    val overrideUserAgent: String? = null
)
