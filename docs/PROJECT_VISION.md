# PROJECT_VISION

Universal Video Downloader (UVD), Android cihazlar üzerinde çalışan, hiçbir harici uzak sunucu (backend/resolver/proxy/cloud) bağımlılığı bulunmayan "%100 lokal" bir medya indirme aracıdır.

**Vizyon Prensipleri:**
1. **Lokalite**: Video çözme (resolving) işlemi sadece cihazdaki Chromium WebView ve JS Bridge üzerinden gerçekleşir. Kullanıcının mahremiyeti esastır.
2. **Güvenlik (Zero-Trust)**: Kullanıcı parolaları veya hassas form verileri okunmaz/saklanmaz. DRM korumalı sistemler kırılmaya (circumvention) çalışılmaz. SSL hataları görmezden gelinmez. Log kayıtlarında token, query signature ve çerezler Release modunda maskelenir.
3. **Başarı Hedefi**: İlk MVP'de %65-75, gelişmiş sürümlerde %90-95 başarı hedeflenmiştir. Hiçbir aşamada "tüm internette %100 çalışacak" gibi gerçekçi olmayan vaatlerde bulunulmaz.
4. **Kalite Odaklı UX**: Uygulama, kullanıcının izlemekte olduğu videonun kalitelerini (1080p, 720p + AAC vb.) modüler bir Resolver Pipeline yapısıyla tespit eder ve reklamları (MediaScoringEngine) süzerek, karmaşıklıktan uzak bir UI deneyimi sunar. UI işlemlerinde asla ana iş parçacığı (Main/UI Thread) kilitlenmez, tüm parser süreçleri coroutine cancellation mimarisini destekler.
