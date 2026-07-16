package com.example.universalvideodownloader.domain.extractor

enum class VideoCategory {
    MAIN_VIDEO,
    OTHER_MEDIA,
    PROBABLE_AD
}

class MediaScoringEngine {
    
    // Adayı puanlar ve reklam / asıl video olarak sınıflandırır
    fun scoreCandidate(candidate: MediaCandidate, currentSession: PlaybackCaptureSession?): VideoCategory {
        var score = 0
        val url = candidate.url.lowercase()
        
        // --- Pozitif Sinyaller (Puan Arttırıcı) ---
        if (candidate.mediaType == MediaType.HLS_MASTER || candidate.mediaType == MediaType.DASH_MANIFEST) {
            score += 50
        }
        
        if (currentSession != null && currentSession.relatedRequestIds.contains(candidate.id)) {
            score += 40
        }
        
        // --- Negatif Sinyaller (Puan Düşürücü / Reklam Göstergesi) ---
        if (url.contains("vast") || url.contains("vpaid") || url.contains("midroll") || 
            url.contains("preroll") || url.contains("adserver") || url.contains("promo")) {
            score -= 100
        }
        
        // Kaydedilen puan
        candidate.score = score
        
        return when {
            score >= 40 -> VideoCategory.MAIN_VIDEO
            score < 0 -> VideoCategory.PROBABLE_AD // Bu kategori UI tarafında gizlenecek/kapalı olacak
            else -> VideoCategory.OTHER_MEDIA
        }
    }
}
