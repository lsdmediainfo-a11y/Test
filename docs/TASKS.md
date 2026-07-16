# Universal Video Downloader - TASKS

## Aşama 0 — Belgeler
- [x] AGENTS.md oluştur.
- [x] PROJECT_VISION.md oluştur.
- [x] MASTER_PLAN.md oluştur.
- [x] ARCHITECTURE.md oluştur.
- [x] RESOLVER_ARCHITECTURE.md oluştur.
- [x] CAPTURE_ARCHITECTURE.md oluştur.
- [x] DOWNLOAD_ARCHITECTURE.md oluştur.
- [x] ADBLOCK_ARCHITECTURE.md oluştur.
- [x] SECURITY_RULES.md oluştur.
- [x] CODING_RULES.md oluştur.
- [x] TEST_PLAN.md oluştur.
- [x] TASKS.md oluştur.
- [x] DEPENDENCIES.md oluştur.
- [x] DATA_MODELS.md oluştur.
**Kabul Kriterleri:** Belgeler teknik olarak detaylı, tutarlı, uygulanabilir ve checkbox tabanlı olmalıdır.

## Aşama 1 — Android proje iskeleti
- [x] İlgili Gradle bağımlılıkları (Compose, Hilt, Room, Media3, WorkManager, vb.) eklenecek.
**Kabul Kriterleri:** `./gradlew assembleDebug` hatasız derlenmeli.

## Aşama 2 — Tarayıcı
- [x] WebView tabanlı ana ekran, arama çubuğu ve WebViewClient entegrasyonları.
**Kabul Kriterleri:** Varsayılan User-Agent ile native Chromium WebView davranışında bir sayfa yüklenebilmeli.

## Aşama 3 — Trafik yakalama
- [x] WebView JS hook'ları ve native interceptor (`shouldInterceptRequest`).
**Kabul Kriterleri:** Video oynatıldığında ağ isteklerinin ve media kaynaklarının konsola veya loglara düşmesi.duplicate edilmiş biçimde toplanabilmeli.

## Aşama 4 — Playback session
- [x] Video playback dinleyicileri (play, loadedmetadata) aktif trafiğe snapshot olarak kaydedilecek.
**Kabul Kriterleri:** Video oynatıldığında session önceliklendirilmeli ve "İndir" butonuna basılana kadar log tutulmalı.

## Aşama 5 — Direct media
- [x] MP4/WebM algılama ve byte signature `ContentVerifier` kuralları.
**Kabul Kriterleri:** MP4/WebM uzantıları veya içerikleri doğru ayrıştırılıp indirilme kuyruğuna aktarılmalı.

## Aşama 6 — HLS
- [x] `HlsParser` yazılacak (#EXTM3U). Varyantlar (çözünürlük, bant genişliği) çekilecek.
**Kabul Kriterleri:** TS segmentleri ve M3U8 DRM'siz yapıları başarıyla çözümlenebilmeli.

## Aşama 7 — Reklam ayrımı
- [x] VAST/VPAID detection, popup blocking, `MediaScoringEngine` filtreleri eklenecek.
**Kabul Kriterleri:** Reklam segmentleri negatif puan alıp, asıl video ile aralarındaki fark UI'a yansıtılmalı.

## Aşama 8 — DASH
- [x] `XmlPullParser` tabanlı `DashParser` geliştirilecek.
**Kabul Kriterleri:** MPD dosyalarından ses/video ayrımı (ör: 1080p + AAC) çıkarılabilmeli.

## Aşama 9 — Blob/MSE
- [x] `BlobSourceResolver` ve `SegmentClusterResolver` yazılacak.
**Kabul Kriterleri:** `blob:` kaynakları veya kopuk TS kümeleri asıl kaynak altında gruplanabilmeli.

## Aşama 10 — İndirme yönetimi
- [x] `DownloadsScreen` tasarımı, `Room` kaydı, Notification ve Wi-Fi kısıtlamaları eklenecek.
**Kabul Kriterleri:** SAF ile güvenli indirme (.part uzantılı başlama) ve Pause/Resume fonksiyonları çalışmalı.

## Aşama 11 — Player
- [x] Media3 ExoPlayer tabanlı tam ekran oynatıcı arayüzü eklenecek.
**Kabul Kriterleri:** Lokal cihazdaki indirilen videolar hız/ses/altyazı denetimleriyle izlenebilmeli.

## Aşama 12 — Cronet
- [x] Gelişmiş indirmeler için HTTP/3 destekli Cronet entegrasyonu (OkHttp fallback).
**Kabul Kriterleri:** Cronet motorunda Cookie ve referer header'ları doğru taşınmalı.

## Aşama 13 — Lokal site kuralları
- [x] Domain bazlı özel JS kodları ve DOM parsing stratejileri (`SiteProfile` yapısı).
**Kabul Kriterleri:** x.com veya instagram benzeri sitelerde özel çözümleme (DOM parse) kuralı enjekte edilebilmeli.

## Aşama 14 — Testler
- [x] MockWebServer ile yerel HTTP sunucusu kurularak farklı test senaryoları simüle edilecek.
**Kabul Kriterleri:** Testler yeşil olarak (başarıyla) tamamlanmalı.
