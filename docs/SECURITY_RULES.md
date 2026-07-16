# SECURITY & CODING RULES

Bu proje için belirlenmiş güvenlik ve yazılım standartları aşağıdadır:

## Güvenlik Kuralları (SECURITY_RULES)
- **Ağ Güvenliği**: SSL hatalarında (`onReceivedSslError`) ASLA `proceed()` komutu kullanılmamalı. Trust-all certificate yöneticileri (X509TrustManager) kullanılmamalıdır.
- **Güvenli Gezinme**: Hostname verification veya Safe Browsing ayarları kapatılmamalıdır.
- **Kullanıcı Verisi (Privacy)**: Kullanıcının klavye girdileri, şifreleri, login form verileri okunmaz ve kaydedilmez.
- **Uzaktan Kod Çalıştırma (RCE) Engeli**: Sunucudan dinamik Kotlin/Java kodu indirilip çalıştırılamaz. Resolver'lar içerisinde cihazda shell (bash/sh) çalıştıran bir mantık olamaz.
- **DRM Sınırı**: DRM (Widevine, PlayReady, FairPlay) anahtarlarını cihazdan çıkarmaya çalışmak yasaktır.
- **JS Bridge (JavascriptInterface)**: Sadece gerekli medya metadata bilgisini ve yakalanan URL'i alacak kadar dar metotlar (minimum bridge) içerir. Girdiler güvenlik filtresinden (Validation) geçirilir.
- **Hassas Context**: İndirme sonrası Worker'da saklanan JSON tabanlı Session (Cookies, Auth Headers) anında silinir.

## Yazılım Standartları (CODING_RULES)
- **Programlama Dili**: Sadece Kotlin. (Java 17 JvmTarget). KSP kullanılacaktır.
- **UI & Ağ Kilidi**: Kural #12 uyarınca Main (UI) thread üzerinde disk (Room) veya ağ (OkHttp/Cronet) / parser işlemi (XML/HLS) yapılmaz. Dispatchers.IO ve Dispatchers.Default kullanılır.
- **Exception Yönetimi**: `Exception`'lar körü körüne yutulmaz. Try-catch blokları log (`AppLogger`) bırakmak zorundadır. Stack trace kullanıcıya sunulmaz (Anlaşılır hata kodları verilir).
- **Kod Temizliği**: Büyük, monolitik tanrıcıl sınıflar (God Classes) yerine "Küçük ve Anlaşılır" sınıflar / interfaceler yazılır.
- **TODO Kullanımı**: Koda bırakılan her `// TODO`, eşzamanlı olarak `TASKS.md` dosyasına açıklama olarak eklenmek ZORUNDADIR. Placeholder bırakmak yasaktır.
