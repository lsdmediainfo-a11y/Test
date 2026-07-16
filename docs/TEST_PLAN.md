# TEST PLAN

Uygulamanın çalışırlığı dış (gerçek) sunuculara (ki bunlar kapanabilir, format değiştirebilir) dayandırılamaz. Testler tamamen **MockWebServer** ve lokal `fixture` (örnek test dosyaları) kullanılarak yapılacaktır.

## Yazılacak Testler (Bölüm 26)
Her yeni bir bileşen için Unit ve Entegrasyon testi yazılır:

### MockWebServer Senaryoları:
1. **Direct MP4**
2. **Redirect MP4** (HTTP 302 sonrası asıl dosyaya ulaşma)
3. **Cookie Protected MP4** (Geçersiz cookie verildiğinde 403, geçerliğinde 200 dönen test)
4. **Referer Protected MP4**
5. **Master HLS** (M3U8 variant testi)
6. **Variant HLS**
7. **Relative HLS URL** (Manifest içindeki alt `.ts` dosyalarının absolute path'e dönüştürülmesi testi)
8. **HLS TS Segments**
9. **HLS fMP4**
10. **DASH SegmentTemplate**
11. **DASH Separate Audio/Video** (Video ve ses representation'larının doğru birleşimi testi)
12. **Blob + Fetch**
13. **MediaSource + M4S**
14. **Service Worker Media**
15. **IFrame Media**
16. **Popup Ad + Real Video** (Pop-up engellenip asıl videonun başarıyla indirilmesi testi)
17. **VAST Pre-roll + Main Video** (`MediaScoringEngine` testi: VAST eksi puan almalı, asıl video artı puan)
18. **Expiring Signed URL Simulation** (Query signature'lı linkin süresi dolduğunda uygulamanın tavrı)
19. **Range Resume** (Kopan indirmenin WorkManager ve `Range` header'ı ile tekrar başlaması testi)
20. **Network Interruption** (Bağlantı kesildiğinde HLS chunk'ının Worker'ı kırmadan yeniden denenmesi testi)

> **Kural #9:** Bir test başarısızken (fail ediyorken) TASKS.md'deki bir sonraki aşamaya KESİNLİKLE GEÇİLMEZ!
