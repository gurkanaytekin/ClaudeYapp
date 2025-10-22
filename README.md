# ClaudeYapp

Modern Android uygulaması - Jetpack Compose ile geliştirildi.

## Özellikler

- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)
- **Programlama Dili**: Kotlin
- **UI Framework**: Jetpack Compose
- **Build System**: Gradle (Kotlin DSL)

## Proje Yapısı

```
ClaudeYapp/
├── app/                          # Ana uygulama modülü
│   ├── src/
│   │   └── main/
│   │       ├── java/com/gurkan/yapp/ai/
│   │       │   ├── MainActivity.kt    # Ana aktivite
│   │       │   └── ui/theme/          # Compose tema dosyaları
│   │       ├── res/                   # Android kaynakları
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts              # Uygulama build yapılandırması
├── gradle/                           # Gradle wrapper dosyaları
├── .github/workflows/                # GitHub Actions CI/CD
└── build.gradle.kts                  # Proje seviyesi build yapılandırması
```

## Gereksinimler

- JDK 17 veya üzeri
- Android Studio Hedgehog | 2023.1.1 veya üzeri (önerilir)
- Android SDK 35

## Kurulum

1. Repoyu klonlayın:
```bash
git clone <repository-url>
cd ClaudeYapp
```

2. Android Studio'da projeyi açın veya komut satırından build alın:
```bash
./gradlew build
```

3. Uygulamayı çalıştırın:
```bash
./gradlew installDebug
```

## GitHub Actions CI/CD

Bu proje, otomatik build ve test süreçleri için GitHub Actions kullanır:

- **Tetikleyiciler**: Her PR ve commit'te
- **İşlemler**:
  - Build kontrolü
  - Unit testler
  - APK oluşturma
  - Build sonucu hakkında PR'a yorum ekleme

Build başarılı olduğunda, artifacts bölümünden debug APK indirebilirsiniz.

## Geliştirme

### Build Komutları

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Testleri çalıştır
./gradlew test

# Lint kontrolü
./gradlew lint
```

### Paket Yapısı

```
com.gurkan.yapp.ai
├── MainActivity.kt      # Ana giriş noktası
└── ui/
    └── theme/          # Compose tema tanımlamaları
        ├── Color.kt    # Renk paleti
        ├── Theme.kt    # Ana tema
        └── Type.kt     # Tipografi
```

## Teknolojiler

- **Jetpack Compose**: Modern, deklaratif UI framework
- **Material 3**: Google'ın en son tasarım sistemi
- **Kotlin**: Modern, güvenli programlama dili
- **Gradle Kotlin DSL**: Tip-güvenli build yapılandırması

## Lisans

Bu proje özel kullanım içindir.
