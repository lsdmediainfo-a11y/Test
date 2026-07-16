package com.example.universalvideodownloader.domain.extractor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

enum class DownloadFlowState(val message: String) {
    ANALYZING_TRAFFIC("Trafik inceleniyor…"),
    VERIFYING_CANDIDATES("Medya adayları doğrulanıyor…"),
    PARSING_VIDEO_URL("Video URL'si ayrıştırılıyor…"),
    EXTRACTING_QUALITIES("Kaliteler çıkarılıyor…"),
    PREPARING_CONTENT("İndirilebilir içerik hazırlanıyor…"),
    COMPLETED("İşlem tamamlandı")
}

class DownloadFlowCoordinator(
    private val pipeline: ResolverPipeline,
    private val contentVerifier: ContentVerifier,
    private val scoringEngine: MediaScoringEngine
) {

    fun executeDownloadFlow(
        activeTrafficSnapshot: List<CapturedNetworkEvent>,
        activeSession: PlaybackCaptureSession?
    ): Flow<Pair<DownloadFlowState, List<MediaCandidate>>> = flow {
        
        // 1 & 2 Snapshot zaten alındı
        
        emit(Pair(DownloadFlowState.ANALYZING_TRAFFIC, emptyList()))
        
        // 3. Adayları çıkar ve ResolverPipeline'ı çalıştır
        val candidates = mutableListOf<MediaCandidate>()
        for (event in activeTrafficSnapshot) {
            val resolveContext = ResolveContext(event, activeSession)
            val candidate = pipeline.processEvent(resolveContext)
            if (candidate != null) candidates.add(candidate)
        }
        
        // 4. Duplicate (Kopya) URL temizliği
        val uniqueCandidates = candidates.distinctBy { it.url }
        
        emit(Pair(DownloadFlowState.VERIFYING_CANDIDATES, emptyList()))
        emit(Pair(DownloadFlowState.PARSING_VIDEO_URL, emptyList()))
        
        // 5. İçerik doğrulama
        val verifiedCandidates = mutableListOf<MediaCandidate>()
        for (candidate in uniqueCandidates) {
            val verifiedType = contentVerifier.verify(candidate)
            if (verifiedType != MediaType.UNKNOWN) {
                verifiedCandidates.add(candidate.copy(mediaType = verifiedType))
            }
        }
        
        // 7. Kaliteler çıkarılıyor
        emit(Pair(DownloadFlowState.EXTRACTING_QUALITIES, emptyList()))
        // TODO: HlsParser ve DashParser ile alt kalitelerin çıkarılması işlemi
        
        // 9 & 10. Reklam puanlaması ve Gruplama
        emit(Pair(DownloadFlowState.PREPARING_CONTENT, emptyList()))
        verifiedCandidates.forEach { scoringEngine.scoreCandidate(it, activeSession) }
        val finalCandidates = verifiedCandidates.sortedByDescending { it.score }
        
        // 11. Sonucu UI'a bildir
        emit(Pair(DownloadFlowState.COMPLETED, finalCandidates))
        
    }.flowOn(Dispatchers.Default) // Tüm işlemler background thread (coroutine) içinde yürütülür, UI donmaz.
}
