# Android Release Signing Rehberi

## Mevcut Durum

Workflow şu anda **DEBUG keystore** ile RELEASE APK oluşturuyor. Production için **kendi signing key**'inizi kullanmalısınız.

## Neden Signing Key Gerekli?

- ✅ Google Play Store'a yüklemek için zorunlu
- ✅ Güvenli uygulama güncellemeleri
- ✅ App'in sahibini doğrulama
- ⚠️ Signing key kaybedilirse app güncellenemez!

## Signing Key Oluşturma

### Adım 1: Keystore Dosyası Oluşturun

```bash
# Terminal'de çalıştırın:
keytool -genkey -v -keystore release-keystore.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias release-key

# Sizden şunlar istenecek:
# - Keystore password (ÇOK ÖNEMLİ - KAYDET!)
# - Key password (ÇOK ÖNEMLİ - KAYDET!)
# - İsim, organizasyon, şehir, ülke bilgileri
```

**ÖNEMLİ:**
- `release-keystore.jks` dosyası oluşturulacak
- **Bu dosyayı GÜVENLİ BİR YERE YEDEKLEY İN!**
- **Şifreleri KAYDET İN! (password manager kullanın)**
- **Bu keystore'u kaybederseniz app'i güncelleyemezsiniz!**

### Adım 2: Keystore'u Base64'e Çevirin

```bash
# Mac/Linux:
base64 release-keystore.jks | tr -d '\n' > keystore.txt

# Windows (PowerShell):
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release-keystore.jks")) > keystore.txt
```

Bu komut `keystore.txt` dosyası oluşturacak - içeriğini kopyalayın.

### Adım 3: GitHub Secrets Ekleyin

```
GitHub Repository
→ Settings
→ Secrets and variables
→ Actions
→ New repository secret
```

**Eklenecek Secrets:**

1. **RELEASE_KEYSTORE**
   - Name: `RELEASE_KEYSTORE`
   - Value: `keystore.txt` dosyasının içeriği (base64 string)

2. **RELEASE_KEYSTORE_PASSWORD**
   - Name: `RELEASE_KEYSTORE_PASSWORD`
   - Value: Keystore oluştururken girdiğiniz password

3. **RELEASE_KEY_ALIAS**
   - Name: `RELEASE_KEY_ALIAS`
   - Value: `release-key` (veya keytool'da kullandığınız alias)

4. **RELEASE_KEY_PASSWORD**
   - Name: `RELEASE_KEY_PASSWORD`
   - Value: Key password (genelde keystore password ile aynı)

### Adım 4: app/build.gradle.kts Güncelleme

`app/build.gradle.kts` dosyasına signing config ekleyin:

```kotlin
android {
    // ... mevcut config

    signingConfigs {
        create("release") {
            // CI/CD için environment variable'lardan al
            storeFile = file(System.getenv("RELEASE_KEYSTORE_FILE") ?: "release-keystore.jks")
            storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("RELEASE_KEY_ALIAS")
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true  // ProGuard/R8 aktif
            isShrinkResources = true  // Kullanılmayan resource'ları sil
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")  // Signing ekle
        }
    }
}
```

### Adım 5: Workflow'da Signing Kullanma

Workflow dosyasına (`.github/workflows/android-build.yml`) şunu ekleyin:

```yaml
# Build RELEASE APK only on main branch or tags
- name: Build Release APK
  if: github.ref == 'refs/heads/main' || startsWith(github.ref, 'refs/tags/')
  env:
    RELEASE_KEYSTORE_FILE: ${{ github.workspace }}/release-keystore.jks
    RELEASE_KEYSTORE_PASSWORD: ${{ secrets.RELEASE_KEYSTORE_PASSWORD }}
    RELEASE_KEY_ALIAS: ${{ secrets.RELEASE_KEY_ALIAS }}
    RELEASE_KEY_PASSWORD: ${{ secrets.RELEASE_KEY_PASSWORD }}
  run: |
    echo "${{ secrets.RELEASE_KEYSTORE }}" | base64 -d > release-keystore.jks
    ./gradlew assembleRelease --stacktrace
    rm release-keystore.jks  # Güvenlik için sil
```

## Mevcut Durum (Signing Olmadan)

Şu anda RELEASE APK **debug keystore** ile imzalanıyor:

**Avantajları:**
- ✅ Hemen çalışır, ekstra kurulum gerekmez
- ✅ Test için yeterli
- ✅ Firebase App Distribution çalışır

**Dezavantajları:**
- ❌ Google Play Store'a yüklenemez
- ❌ Production için uygun değil
- ⚠️ Debug keystore herkeste aynı

## Signing Key ile Production Build

Signing key ekledikten sonra:

**Avantajları:**
- ✅ Google Play Store'a yüklenebilir
- ✅ Güvenli ve unique
- ✅ Production-ready
- ✅ App Bundle (AAB) da oluşturulabilir

**Google Play Store için AAB oluşturma:**
```yaml
# Workflow'a ekleyin:
- name: Build Release AAB (Bundle)
  if: startsWith(github.ref, 'refs/tags/')  # Sadece tag'lerde
  env:
    # ... same signing envs
  run: |
    echo "${{ secrets.RELEASE_KEYSTORE }}" | base64 -d > release-keystore.jks
    ./gradlew bundleRelease --stacktrace
    rm release-keystore.jks
```

## Firebase App Distribution Grupları

Workflow'da iki grup var:

1. **`testers`** - DEBUG APK alır (her PR)
   - Geliştirme ekibi
   - QA team
   - Hızlı test döngüsü

2. **`production-testers`** - RELEASE APK alır (main/tags)
   - Beta testers
   - Stakeholders
   - Production-ready test

**Firebase Console'da grup oluşturun:**
```
Firebase Console
→ App Distribution
→ Groups
→ Create Group: "production-testers"
→ Email adreslerini ekleyin
```

## Güvenlik İpuçları

1. **Keystore Yedekleme:**
   - ✅ Cloud storage'a şifreli yedekleyin
   - ✅ Farklı yerlerde tutun (1Password, Google Drive, vb.)
   - ✅ Şifreleri password manager'da saklayın

2. **Keystore Güvenliği:**
   - ❌ ASLA git'e commit etmeyin
   - ❌ ASLA public yapamayın
   - ❌ Slack/Email ile paylaşmayın
   - ✅ Sadece GitHub Secrets'ta tutun

3. **Key Kaybı Durumunda:**
   - ⚠️ Yeni app yayınlamanız gerekir (yeni package name)
   - ⚠️ Kullanıcılar yeni app indirmeli
   - ⚠️ App güncellenemez
   - **O yüzden ÇOK İYİ YEDEKLEYİN!**

## Play Console İçin Play App Signing

Google Play Console, **App Bundle** yüklediğinizde kendi key'i ile imzalar:

**Avantajları:**
- ✅ Google key'i yedekler
- ✅ Key kaybı riski yok
- ✅ Optimize APK'lar

**Nasıl Kullanılır:**
1. Play Console'da App Signing'i aktif edin
2. AAB (bundle) yükleyin, APK değil
3. Google otomatik imzalar

## Hızlı Başlangıç Checklist

Şimdilik signing olmadan devam edebilirsiniz:

- [x] DEBUG APK her PR'da çalışıyor
- [x] RELEASE APK main/tag'lerde oluşuyor (debug keystore ile)
- [x] Firebase Test Lab çalışıyor
- [ ] Production signing key ekle (ileride)
- [ ] Google Play Store'a yükle (ileride)

**Signing'i ileride eklemek istediğinizde bu dokümana bakın!**

## Yararlı Komutlar

```bash
# Keystore bilgilerini görüntüle:
keytool -list -v -keystore release-keystore.jks

# SHA-1 fingerprint (Firebase için):
keytool -list -v -keystore release-keystore.jks -alias release-key

# Keystore şifresini değiştir:
keytool -storepasswd -keystore release-keystore.jks
```

## Sorun Giderme

### "keystore not found" hatası:
- Keystore base64 decode doğru yapılmış mı?
- GitHub Secrets eklenmiş mi?
- Environment variable'lar doğru set edilmiş mi?

### "wrong password" hatası:
- Secret'taki password doğru mu?
- Keystore password != Key password olabilir

### APK imzalanamıyor:
- signingConfig build.gradle.kts'de eklenmiş mi?
- Release build type signing config kullanıyor mu?
