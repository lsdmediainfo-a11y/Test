# RESOLVER ARCHITECTURE

Medya adaylarını (Media Candidates) çözümlemek, doğrulamak ve sınıflandırmak için monolitik tek bir mantık (örneğin dev bir Regex zinciri) yerine **Modüler Resolver Zinciri (Pipeline)** kullanılır.

## Tasarım Prensibi
10 temel resolver, ortak `MediaResolver` arayüzünü (id, priority, resolve metodu) uygular ve `ResolverPipeline` tarafından sırayla (en yüksek öncelikten düşüğe doğru) çalıştırılır.

### Aktif Resolver Sınıfları
1. `VideoElementResolver`
2. `DirectFileResolver` (MP4/WebM tespiti)
3. `HlsManifestResolver` (M3U8)
4. `DashManifestResolver` (MPD)
5. `ContentSignatureResolver` (Byte pattern kontrolü)
6. `NetworkPatternResolver`
7. `IframeContextResolver`
8. `JsonApiResolver`
9. `SegmentClusterResolver` (TS/M4S kümeleri)
10. `BlobSourceResolver` (MediaSource takibi)

## Hata Toleransı ve Çıktı (ResolverOutcome)
- **Resolved**: İçerik %100 doğrulandı (Kullanıcıya gösterilebilir).
- **Partial**: İçerik kısmi olarak çözüldü, ancak eksikler var (Örn: Sadece blob URL'si bulundu).
- **NotMatched**: Resolver bu olayla ilgilenmediğini belirtti, sonraki Resolver'a geçilir.
- **Failed**: Çözümleme denemesinde hata çıktı.

> **Kritik Kural**: Eğer bir resolver (örneğin JSON ayrıştırma esnasında) çökerse, fırlatılan Exception asla uygulamanın veya Pipeline'ın çökmesine yol açmaz. Hata loglanır (`AppLogger.w`) ve bir sonraki resolver'dan devam edilir. Kullanıcıya ASLA teknik "NullPointerException" gibi stack trace hataları gösterilmez. Sadece UI'a dostane kodlar (Örn: `ERR_RESOLVE_TIMEOUT`) sunulur. Her resolver Timeout destekler (coroutine `withTimeout`).
