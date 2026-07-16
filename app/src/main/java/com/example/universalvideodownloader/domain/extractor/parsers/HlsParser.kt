package com.example.universalvideodownloader.domain.extractor.parsers

import android.net.Uri

data class HlsVariant(
    val url: String,
    val resolution: String?,
    val bandwidth: Int?,
    val codecs: String?
)

class HlsParser {
    fun parseMasterPlaylist(baseUrl: String, content: String): List<HlsVariant> {
        val variants = mutableListOf<HlsVariant>()
        val lines = content.lines()
        
        // DRM check: AES-128 dışında (SAMPLE-AES vs) veya karmaşık şifreleme desteklenmez.
        if (content.contains("#EXT-X-KEY:METHOD=SAMPLE-AES") || 
            (content.contains("#EXT-X-KEY:METHOD=") && !content.contains("METHOD=NONE") && !content.contains("METHOD=AES-128"))) {
            throw UnsupportedOperationException("DRM korumalı HLS yayınları desteklenmemektedir.")
        }
        
        var currentResolution: String? = null
        var currentBandwidth: Int? = null
        var currentCodecs: String? = null
        
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.startsWith("#EXT-X-STREAM-INF:")) {
                val attrs = trimmed.substringAfter(":")
                
                // Çözünürlük çıkar
                val resMatch = Regex("RESOLUTION=(\\d+x\\d+)").find(attrs)
                currentResolution = resMatch?.groupValues?.get(1)
                
                // Bant genişliği çıkar
                val bwMatch = Regex("BANDWIDTH=(\\d+)").find(attrs)
                currentBandwidth = bwMatch?.groupValues?.get(1)?.toIntOrNull()
                
                // Codec çıkar
                val codecsMatch = Regex("CODECS=\"([^\"]+)\"").find(attrs)
                currentCodecs = codecsMatch?.groupValues?.get(1)
                
            } else if (!trimmed.startsWith("#") && trimmed.isNotEmpty()) {
                if (currentResolution != null || currentBandwidth != null) {
                    val absoluteUrl = resolveUrl(baseUrl, trimmed)
                    variants.add(
                        HlsVariant(
                            url = absoluteUrl,
                            resolution = currentResolution,
                            bandwidth = currentBandwidth,
                            codecs = currentCodecs
                        )
                    )
                    currentResolution = null
                    currentBandwidth = null
                    currentCodecs = null
                }
            }
        }
        return variants
    }

    private fun resolveUrl(base: String, path: String): String {
        return if (path.startsWith("http://") || path.startsWith("https://")) {
            path
        } else {
            // Relative URL'leri manifest URL'sine göre absolute yap
            val baseUri = Uri.parse(base)
            val pathSegments = baseUri.pathSegments.toMutableList()
            if (pathSegments.isNotEmpty()) {
                pathSegments.removeLast() // Dosya adını (.m3u8) sil
            }
            var builder = baseUri.buildUpon().path("")
            pathSegments.forEach { builder = builder.appendPath(it) }
            builder.appendEncodedPath(path).build().toString()
        }
    }
}
