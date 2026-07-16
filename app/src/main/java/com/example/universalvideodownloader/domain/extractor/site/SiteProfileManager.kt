package com.example.universalvideodownloader.domain.extractor.site

import android.net.Uri
import android.util.Log

class SiteProfileManager {
    
    private val profiles = listOf(
        SiteProfile(
            domainPattern = "x.com",
            customJsInjection = """
                (function() {
                    console.log('Universal Downloader: X.com kuralı devrede');
                    // X (Twitter) GraphQL Blob veya API interception'ı için özel MutationObserver
                    // Bu sadece x.com domaine sahip sekmede aktif olur
                })();
            """.trimIndent(),
            overrideUserAgent = null
        ),
        SiteProfile(
            domainPattern = "instagram.com",
            customJsInjection = """
                (function() {
                    console.log('Universal Downloader: Instagram kuralı devrede');
                    // Instagram DOM elementlerini taramak için özel kanca
                })();
            """.trimIndent(),
            overrideUserAgent = null
        )
    )
    
    fun getProfileForUrl(url: String): SiteProfile? {
        try {
            val uri = Uri.parse(url)
            val host = uri.host ?: return null
            return profiles.find { host.contains(it.domainPattern) }
        } catch (e: Exception) {
            Log.e("SiteProfileManager", "Invalid URL parsing for profile: $url")
            return null
        }
    }
}
