package com.example.universalvideodownloader.domain.extractor

sealed class ResolverOutcome {
    data class Resolved(val candidate: MediaCandidate) : ResolverOutcome()
    data class Partial(val candidate: MediaCandidate, val missingDetails: String) : ResolverOutcome()
    object NotMatched : ResolverOutcome()
    data class Failed(val reason: String) : ResolverOutcome()
}

data class ResolveContext(
    val event: CapturedNetworkEvent,
    val session: PlaybackCaptureSession?,
    val pageHtml: String? = null
)

interface MediaResolver {
    val id: String
    val priority: Int
    
    suspend fun resolve(context: ResolveContext): ResolverOutcome
}
