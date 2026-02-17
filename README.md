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

## GitHub Actions CI/CD + Firebase Entegrasyonu

Bu proje, otomatik build ve test süreçleri için GitHub Actions kullanır:

- **Tetikleyiciler**: Her PR ve commit'te
- **İşlemler**:
  - Build kontrolü
  - Unit testler
  - APK oluşturma
  - **Firebase App Distribution**: APK'yı test kullanıcılarına otomatik dağıtım
  - **Firebase Test Lab**: Robo testleri ile otomatik UI testleri
  - Build sonucu hakkında PR'a detaylı yorum ekleme

📖 **Detaylı Analiz:** GitHub Actions workflow'unun ne yaptığını adım adım öğrenmek için [GITHUB_ACTIONS_ANALIZI.md](GITHUB_ACTIONS_ANALIZI.md) dosyasına bakın.

### Firebase Kurulumu

Firebase entegrasyonunu kurmak için `FIREBASE_SETUP.md` dosyasına bakın. Gerekli adımlar:

1. Firebase projesi oluşturun
2. Service account oluşturun ve JSON key indirin
3. GitHub Secrets ekleyin:
   - `FIREBASE_APP_ID`
   - `FIREBASE_SERVICE_ACCOUNT`
   - `FIREBASE_PROJECT_ID`
   - `FIREBASE_TEST_BUCKET`

Detaylı kurulum rehberi: [FIREBASE_SETUP.md](FIREBASE_SETUP.md)

### Build Strategy (Hybrid Yaklaşım)

Proje, optimal hız ve güvenlik için **hybrid build strategy** kullanır:

| Durum | Build Type | Firebase Group | Test Lab |
|-------|-----------|----------------|----------|
| **PR / Feature Branch** | DEBUG APK | `testers` | ✅ Debug |
| **Main Branch** | DEBUG + RELEASE | `testers` + `production-testers` | ✅ Both |
| **Git Tag (v\*)** | DEBUG + RELEASE | `testers` + `production-testers` | ✅ Both |

**Avantajları:**
- ⚡ **Hızlı PR feedback**: Debug build 2-3 dakika
- 🚀 **Production-ready test**: Main'de release APK test edilir
- 🔒 **Güvenli**: Release signing sadece main/tag'lerde
- 📦 **Organize**: Test ve production testers ayrı gruplar

### CI/CD Pipeline Özellikleri

- **Build Artifacts**: DEBUG APK (30 gün), RELEASE APK (90 gün - sadece main/tags)
- **Firebase Distribution**:
  - DEBUG → `testers` grubu (her build)
  - RELEASE → `production-testers` grubu (sadece main/tags)
- **Automated Testing**: Firebase Test Lab (MediumPhone.arm, Android 11)
- **PR Comments**: Build sonucu, test durumu ve artifact bilgileri otomatik yorumlar

### Release Build & Signing

Release APK şu anda debug keystore ile imzalanıyor (test için yeterli).

**Production için signing key eklemek istiyorsanız:**
- Detaylı rehber: [RELEASE_SIGNING.md](RELEASE_SIGNING.md)
- Google Play Store yüklemeleri için gerekli
- Signing key oluşturma ve GitHub Secrets ekleme adımları

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
