# Damen Widget

Nothing OS tarzı Android home-screen widget'ları. [RingerWidget](https://github.com/Yaksh-Patel/RingerWidget) (MIT) tabanlıdır, baştan temalandı.

## Widget'lar

| Widget | Boyut | İşlev |
|---|---|---|
| Ringer Toggle | 1×1 | Dokun → Ring / Vibrate geçişi. Kırmızı LED = vibrate |
| Volume Slider | 4×1 | Dokun → medya sesi. Kırmızı uç = mevcut seviye |

## Tasarım dili

- Saf siyah zemin `#000`, beyaz glifler, tek kırmızı vurgu `#FF0000`
- Uygulama içi yazı tipi: DotGothic16 (dot-matrix, OFL lisanslı)
- Launcher ikonu: dot-matrix "D" + imza kırmızı nokta

## APK alma

Telefonda derleme yok — GitHub'a push'layınca Actions `assembleDebug` çalıştırıp APK'yı artifact olarak verir (`.github/workflows/android.yml`). İlk push'tan önce kendi GitHub reponda yeni repo açıp remote'u oraya çevir:

```sh
git remote remove origin
git remote add origin https://github.com/KULLANICI/damen-widget.git
git push -u origin main
```

## Yol haritası

- Shizuku aç/kapa toggle'ı
- pi web (damen-gateway) başlat/durdur toggle'ı
- Çalışan process'ler listesi widget'ı
