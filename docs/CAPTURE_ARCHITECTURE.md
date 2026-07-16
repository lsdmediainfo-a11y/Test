# CAPTURE ARCHITECTURE

Trafik yakalama (Traffic Capture), WebView'in standart işleyişini yavaşlatmadan veya bozmadan arka planda sessizce gerçekleştirilir. 

## Yakalama Katmanları (14 Katman)
1. `WebViewClient.shouldInterceptRequest`: Sadece **gözlem** yapar. (Daima `null` döndürür).
2. `WebViewClient.onLoadResource`
3. `ServiceWorkerControllerCompat`
4. HTML `video` elementi gözlemcisi
5. HTML `source` elementi gözlemcisi
6. `video.currentSrc` değişim gözlemcisi
7. JavaScript `fetch` interception
8. JavaScript `XMLHttpRequest` (XHR) interception
9. `PerformanceObserver` (Resource türündeki kayıtlar)
10. `MutationObserver` (DOM değişiklikleri)
11. `URL.createObjectURL` kancası (Blob çözümü için)
12. `MediaSource` olay gözlemcisi
13. iFrame URL takibi
14. Frame context gözlemcisi

## Playback Capture Session (Oturum Yakalama)
Web sitesi ilk açıldığında arka planda inen reklam veya ön izleme dosyalarını (preload) asıl videodan ayırmak için `PlaybackCaptureSession` kullanılır.
- Kullanıcı videoya `play` işlemi yaptığında (JavaScript `play`, `playing` eventleri tetiklendiğinde) sistem uyanır.
- Bu andan sonra gelen tüm medya istekleri bu Session'a (oturum) öncelikli olarak bağlanır.

## Deduplication (Kopya Ayıklama)
Aynı medya URL'si `XHR`, `shouldInterceptRequest` ve `PerformanceObserver` tarafından 3 kez rapor edilebilir. `CaptureRepository` gelen `CapturedNetworkEvent` objelerini `id` ve `url` üzerinden süzerek Pipeline'a tekilleştirilmiş biçimde iletir.
