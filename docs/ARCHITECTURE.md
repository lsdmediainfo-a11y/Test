# ARCHITECTURE

Universal Video Downloader (UVD), Android üzerinde Clean Architecture ve MVVM ilkelerine sıkı sıkıya bağlı olarak geliştirilmiştir. Proje, başlangıçta aşırı mühendislikten kaçınmak için "tek bir app modülü" (`com.example.universaldownloader`) üzerinde koşturulmaktadır.

## Teknoloji Yığını
- **Arayüz Katmanı (UI)**: Jetpack Compose, Material 3, Navigation Compose.
- **Eşzamanlılık (Concurrency)**: Kotlin Coroutines ve StateFlow. (Kural: UI thread üzerinde ASLA ağ işlemi veya XML/JSON/DOM ayrıştırma (parsing) işlemi yapılmaz. Bütün coroutine'ler cancellation desteklemelidir.)
- **Ağ Katmanı**: OkHttp (Aşama 1-11 için), sonrasında HTTP/3 QUIC destekli Cronet. (Retrofit kullanılmaz).
- **Veritabanı ve Depolama**: Room Database (Yapısal durum saklama), DataStore (Ayarlar), Storage Access Framework / DocumentFile (Video dosyası kaydetme). `MANAGE_EXTERNAL_STORAGE` izni kullanılmaz.
- **Dependency Injection**: Hilt (Application ve WorkManager entegrasyonu dahil).
- **Arka Plan İşlemleri**: WorkManager. Kural: Bir video (HLS dahil) her zaman TEK bir Worker içinde halledilir, her TS segmenti için Worker spam edilmez.
- **Oynatıcı**: Media3 ExoPlayer.

## Paket Yapısı (Package Structure)
`com.example.universaldownloader`
- `ui`: Arayüzler (Browser, Downloads, Player, Settings)
- `capture`: Trafik dinleyici, WebView entegrasyonları
- `resolver`: Modüler Pipeline mimarisi ve çözücü alt sınıflar (`hls`, `dash`, `blob`, `segment`)
- `download`: WorkManager worker'ları, ContentVerifier
- `database`: Room DB objeleri ve DAO'lar
- `player`: Yerel izleme Activity ve Media3 UI sarmalayıcıları
- `adblock`: Reklam süzme, MediaScoringEngine
- `storage`: SAF dosya adı normalizasyonu, `.part` uzantı yönetimi
- `model`: Veri modelleri (`MediaCandidate`, `CapturedNetworkEvent`, `PlaybackCaptureSession` vb.)
- `common`: `AppLogger`, Exception Handler araçları.
