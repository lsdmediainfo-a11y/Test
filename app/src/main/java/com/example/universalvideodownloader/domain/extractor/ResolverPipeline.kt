package com.example.universalvideodownloader.domain.extractor

import android.util.Log
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

class ResolverPipeline(
    resolversList: List<MediaResolver>
) {
    // Öncelik değerine göre azalan sırada sırala (yüksek öncelikli olanlar önce çalışır)
    private val resolvers = resolversList.sortedByDescending { it.priority }

    suspend fun processEvent(context: ResolveContext): MediaCandidate? {
        for (resolver in resolvers) {
            try {
                // Her resolver timeout destekler (Örn: Maksimum 3000 ms limit)
                val outcome = withTimeout(3000L) {
                    resolver.resolve(context)
                }
                
                when (outcome) {
                    is ResolverOutcome.Resolved -> {
                        Log.d("ResolverPipeline", "Olay çözümlendi: ${resolver.id}")
                        return outcome.candidate
                    }
                    is ResolverOutcome.Partial -> {
                        Log.d("ResolverPipeline", "Kısmi çözümleme: ${resolver.id} - ${outcome.missingDetails}")
                        // Kısmi durumda stratejiye göre pipeline devam edebilir veya partial dönebilir.
                    }
                    is ResolverOutcome.NotMatched -> {
                        // Eşleşmedi, bir sonraki resolver'a geç.
                    }
                    is ResolverOutcome.Failed -> {
                        // Hata loglanır, kullanıcıya stack trace gösterilmez, pipeline KESİLMEZ.
                        Log.w("ResolverPipeline", "Resolver ${resolver.id} başarısız oldu: ${outcome.reason}")
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.w("ResolverPipeline", "Resolver ${resolver.id} timeout'a düştü.")
            } catch (e: Exception) {
                // Herhangi bir resolver exception fırlatsa bile zincir durmaz!
                Log.e("ResolverPipeline", "Resolver ${resolver.id} hata fırlattı (Zincir devam ediyor)", e)
            }
        }
        return null // Hiçbir resolver başarılı olamadı
    }
}
