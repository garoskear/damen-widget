# Damen Widget

Nothing OS tarzı Android home-screen **durum widget'ları**. [RingerWidget](https://github.com/Yaksh-Patel/RingerWidget) (MIT) tabanlıdır, baştan temalandı.

Tüm widget'lar aynı sözleşmede: **durumu gösterir, dokunmak tazeler, hiçbiri uygulama açmaz.**

| Widget | Boyut | Gösterir |
|---|---|---|
| Zil | 1×1 | Ring/VIB + LED (kırmızı = titreşim) |
| Ses | 4×1 | Medya seviyesi (kırmızı uç = anlık seviye) |
| Shizuku | 1×1 | RUNNING/STOPPED + LED |
| Gateway | 1×1 | pi web ON/BOOT/OFF (`/api/health`) |
| Procs | 4×2 | Bellek kullanımına göre ilk process'ler (Shizuku `ps`) |

- **Responsive:** hepsi yeniden boyutlandırılabilir; içerik `LocalSize` ile boyuta uyarlanır (dar 1×1'de yazı gizlenir, Procs satır sayısı yüksekliğe göre 3/5/7).
- **Otomatik tazeleme:** WorkManager 15 dk'da bir üç durum widget'ını da yeniler; widget içindeki saat verinin yaşıdır.
- **Shizuku izni** uygulamadan istenir (SHIZUKU kartı → İZNİ VER).
- **Process listesi** Shizuku UserService (AIDL) içinde `ps -A -o COMM,RSS` ile çekilir.

## Tasarım dili

- Saf siyah zemin `#000`, beyaz dot-matrix glifler, tek kırmızı vurgu `#FF0000`
- Yazı: sistem monospace yığını (damen-gateway ile aynı his)
- Launcher ikonu: dot-matrix "D" + imza kırmızı nokta

## APK alma

Telefonda derleme yok — GitHub Actions derler:

- **Her push:** debug APK, artifact olarak (`APK` workflow)
- **`v*` tag'i:** imzalı release APK, GitHub Releases'e düşer (`Release` workflow)

```sh
git tag v1.0 && git push origin v1.0   # release tetikler
```

İmza repodaki `app/damen-debug.keystore` ile (debug + release aynı anahtar).
