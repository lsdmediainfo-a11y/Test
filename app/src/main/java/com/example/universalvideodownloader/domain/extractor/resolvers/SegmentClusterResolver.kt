package com.example.universalvideodownloader.domain.extractor.resolvers

import com.example.universalvideodownloader.domain.extractor.*
import java.net.URL

class SegmentClusterResolver : MediaResolver {
    override val id = "SegmentClusterResolver"
    override val priority = 20
    
    // Geçici hafıza: Host ve path yapısına göre segmentleri kümelemek için
    private val activeClusters = mutableMapOf<String, MutableList<String>>()

    override suspend fun resolve(context: ResolveContext): ResolverOutcome {
        val urlStr = context.event.url
        if (urlStr.endsWith(".ts") || urlStr.endsWith(".m4s") || urlStr.contains("chunk-stream")) {
            try {
                val url = URL(urlStr)
                val host = url.host
                val path = url.path
                
                // "video_0001.ts" veya "chunk_12.m4s" -> base pattern ("video", "chunk") çıkarmak için
                val basePath = path.substringBeforeLast("_").substringBeforeLast("-")
                val clusterKey = "${host}_${basePath}"
                
                if (!activeClusters.containsKey(clusterKey)) {
                    activeClusters[clusterKey] = mutableListOf()
                }
                activeClusters[clusterKey]?.add(urlStr)
                
                // Kullanıcıya bu parçaları ayrı ayrı GÖSTERME
                return ResolverOutcome.Partial(
                     candidate = MediaCandidate(
                        id = clusterKey.hashCode().toString(),
                        url = "cluster://$clusterKey",
                        title = "Media Segment Cluster",
                        mimeType = null,
                        extension = null,
                        mediaType = MediaType.MEDIA_SEGMENT,
                        requestContext = MediaRequestContext(
                            mediaUrl = urlStr,
                            pageUrl = context.session?.pageUrl ?: "",
                            frameUrl = null,
                            userAgent = "Mozilla/5.0",
                            cookie = null,
                            referer = context.session?.pageUrl,
                            origin = null,
                            authorization = null,
                            extraHeaders = emptyMap(),
                            capturedAt = System.currentTimeMillis()
                        ),
                        detectedAt = System.currentTimeMillis(),
                        evidence = listOf(MediaEvidence.EXTENSION_MATCH)
                    ),
                    missingDetails = "Kümedeki segment sayısı: ${activeClusters[clusterKey]?.size}. Eğer ana manifest (.m3u8/.mpd) bulunursa bu küme gizlenecektir."
                )
            } catch (e: Exception) {
                return ResolverOutcome.NotMatched
            }
        }
        return ResolverOutcome.NotMatched
    }
}
