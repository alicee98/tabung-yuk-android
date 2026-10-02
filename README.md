# Tabung Yuk — Android ringan

Aplikasi native Java + Android WebView untuk membuka https://tabung-yuk.vercel.app/.
Android minimum: 6.0 (API 23). Target/compile SDK: 35. Tidak memakai library tambahan.

## Memasang APK
Unduh Tabung_Yuk.apk di ponsel Android, buka, lalu izinkan pemasangan dari aplikasi pengunduh jika Android memintanya. Aplikasi membutuhkan internet dan Android System WebView yang berfungsi. APK ini ditandatangani dengan kunci pengembangan untuk pemasangan langsung/proyek sekolah; bukan rilis Play Store.

## Fitur
- Langsung membuka website, login tetap melalui halaman website.
- Indikator loading, tampilan gagal memuat, tombol coba lagi.
- Tombol/gestur kembali menelusuri riwayat halaman kemudian menutup aplikasi.
- Tautan di luar domain website dibuka dengan aplikasi eksternal.
- Hanya meminta izin internet; tidak meminta kontak, lokasi, kamera, atau penyimpanan.
- Cookie login dan penyimpanan web dikelola WebView. Masa login dan fitur tetap mengikuti server website. Tidak ada database Android tambahan.
- Pembaruan website langsung tersedia tanpa memasang APK baru. Bukan aplikasi offline; tidak menambah verifikasi QRIS/pembayaran.

## Membuka kode
Buka folder ini sebagai proyek Gradle di Android Studio. Gunakan JDK 17, Gradle 8.9, Android SDK Platform 35, dan Build Tools 35.0.0. Pilih distribusi Gradle lokal 8.9 bila diminta (wrapper Gradle tidak disertakan). Izinkan sinkronisasi dependency lalu Build > Build APK(s), atau `gradle :app:assembleDebug`.

URL ada di `app/src/main/java/id/tabungyuk/app/MainActivity.java` pada HOME dan pengecekan domain di route(). Ubah keduanya bila domain berubah.

Kunci `app/development.keystore` sengaja disertakan untuk rebuild proyek percobaan ini. Password store/key: android, alias: androiddebugkey. Kunci ini BUKAN rahasia produksi. Gunakan kunci privat sendiri untuk distribusi publik. Untuk memperbarui APK yang telah dipasang, gunakan kunci yang sama dan naikkan versionCode.

## Validasi versi 1.0
APK dibangun dari kode Java menggunakan ECJ dan Android Build Tools resmi (aapt2, D8, zipalign, apksigner). Tanda tangan APK v1/v2/v3 diverifikasi; manifest, activity launcher, API minimum, izin internet, dan keberadaan DEX diperiksa.
