package com.example.universalvideodownloader.domain.extractor

enum class MediaType {
    DIRECT_FILE, HLS_MASTER, HLS_VARIANT, DASH_MANIFEST, MEDIA_SEGMENT, AUDIO, BLOB_REFERENCE, UNKNOWN
}

enum class MediaEvidence {
    EXTENSION_MATCH, CONTENT_TYPE_MATCH, BYTE_SIGNATURE_MATCH, MANIFEST_PARSED, HEURISTIC_MATCH, USER_INTERACTION
}

data class MediaCandidate(
    val id: String,
    val url: String,
    val title: String?,
    val mimeType: String?,
    val extension: String?,
    val mediaType: MediaType,
    val requestContext: MediaRequestContext, // Headers ve Cookies yerine artık MediaRequestContext kullanılıyor
    val detectedAt: Long,
    val evidence: List<MediaEvidence>,
    var score: Int = 0 // MediaScoringEngine tarafından puanlanacak
)
