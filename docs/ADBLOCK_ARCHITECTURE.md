# ADBLOCK ARCHITECTURE

Reklam engelleme ve medya kalitelerini ayıklama işlemi iki ana servise bölünmüştür.

## 1. AdBlockEngine ("Bu kaynak yüklenmeli mi?")
- Bu servis `shouldInterceptRequest` ve `shouldOverrideUrlLoading` (Navigation Guard) içinde çalışır.
- **Yerel Filtreler**: `assets/filters/` altındaki `ad_domains.txt`, `popup_domains.txt`, `allowed_media_domains.txt` ve `cosmetic_rules.json` dosyalarını kullanır.
- Kullanıcının doğrudan izni olmayan, tık tuzağı pop-upları engeller (`isUserGesture` kontrolü).
- Ana sayfanın alakasız reklamlara yönlenmesini engeller.

## 2. MediaScoringEngine ("Bu video bir reklam mı?")
İndirme ekranında (Adaylar belirlendikten sonra) devreye girer. Adaylar `VideoCategory` enum'ına göre 3 gruba (MAIN_VIDEO, OTHER_MEDIA, PROBABLE_AD) ayrılır.

**Pozitif Sinyaller (Puan Arttırır):**
- Master Playlist olması (HLS/DASH)
- `video.currentSrc` ile birebir eşleşme
- Videonun oynatma (play) eyleminden SONRA belirmesi
- Dosya boyutunun büyük olması veya uzun süre segment indirmesi

**Negatif Sinyaller (Puan Düşürür):**
- VAST, VPAID etiketleri
- URL içinde "midroll", "preroll", "promo", "adserver" geçmesi
- Medya süresinin çok kısa olması (Örn: < 15 saniye)
- Ana video başlamadan önce bitmesi (Pre-roll paterni).

> Puanı sıfırın altına düşen (`< 0`) kaynaklar kullanıcı arayüzünde "Muhtemel Reklamlar" sekmesinde gizlenir.
