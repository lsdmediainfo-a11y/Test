# CODING RULES

Tüm proje geliştiriminde uyulması zorunlu yazılım ve kodlama kuralları (Bölüm 28 ve 29 baz alınmıştır):

1. **Dil ve Ortam**: Yalnızca Kotlin. Java 17 (jvmTarget) kullanılacaktır.
2. **UI Thread Güvenliği**: UI thread (Main Thread) üzerinde kesinlikle ağ veya parser işlemi yapılamaz. `Dispatchers.IO` ve `Dispatchers.Default` zorunludur. (Kural 12).
3. **Coroutine İptali**: Tüm coroutine işlemleri (özellikle uzun süren ağ ve dosya yazma işlemleri) iptal (cancellation) desteklemelidir. (Kural 13).
4. **Hata Yönetimi (Exception Handling)**: Exception'lar (Try/Catch) ASLA sessizce yutulmaz. Hatalar loglanır. (Kural 14). Kullanıcıya gösterilecek olan şey uzun Java stack-traceleri değil, anlamlı ve teknik hata kodlarıdır (Örn: `E-NETWORK-404`). (Kural 15).
5. **Küçük ve Net Sınıflar**: Büyük (God Class) dosyalar gereksiz yere yeniden oluşturulmaz ve sınıflar küçük, anlaşılır olmalıdır. (Kural 10, 11).
6. **TODO Mantığı**: Kod içine yazılan her `// TODO`, aynı anda `TASKS.md` dosyasına Checkbox veya Not olarak işlenmek ZORUNDADIR. Kod içinde unutulmuş kör placeholder bırakmak yasaktır. (Kural 6, 7).
