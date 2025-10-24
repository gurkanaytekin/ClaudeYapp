# Firebase Entegrasyonu Kurulum Rehberi

Bu dokümantasyon, Firebase App Distribution ve Firebase Test Lab entegrasyonunu kurmak için gereken adımları açıklar.

## Gereksinimler

- Firebase projesi
- Google Cloud projesi (Firebase ile bağlantılı)
- GitHub repository admin erişimi

## Adım 1: Firebase Projesi Oluşturma

1. [Firebase Console](https://console.firebase.google.com/) adresine gidin
2. "Add project" / "Proje ekle" butonuna tıklayın
3. Proje adını girin (örn: `ClaudeYapp`)
4. Google Analytics'i isteğe bağlı olarak etkinleştirin
5. Projeyi oluşturun

## Adım 2: Android Uygulamasını Firebase'e Ekleyin

1. Firebase Console'da projenize gidin
2. Android ikonuna tıklayın
3. Paket adını girin: `com.gurkan.yapp.ai`
4. Uygulama takma adını girin (örn: `ClaudeYapp`)
5. "Register app" / "Uygulamayı kaydet" butonuna tıklayın
6. `google-services.json` dosyasını indirin (şimdilik kullanmayacağız ama ileride gerekebilir)

## Adım 3: Firebase App Distribution'ı Etkinleştirin

1. Firebase Console'da sol menüden **Release & Monitor** > **App Distribution** seçin
2. "Get started" butonuna tıklayın
3. Tester grupları oluşturun:
   - "Groups" sekmesine gidin
   - "Create Group" butonuna tıklayın
   - Grup adı: `testers`
   - Test kullanıcılarınızın email adreslerini ekleyin
   - "Create" butonuna tıklayın

## Adım 4: Firebase Test Lab'ı Etkinleştirin

1. Firebase Console'da sol menüden **Release & Monitor** > **Test Lab** seçin
2. "Get started" butonuna tıklayın
3. Test Lab otomatik olarak etkinleştirilecektir

## Adım 5: Service Account Oluşturun

1. [Google Cloud Console](https://console.cloud.google.com/) adresine gidin
2. Firebase projenizi seçin
3. Sol menüden **IAM & Admin** > **Service Accounts** seçin
4. "CREATE SERVICE ACCOUNT" butonuna tıklayın
5. Service account detayları:
   - **Name**: `github-actions-firebase`
   - **Description**: `Service account for GitHub Actions Firebase integration`
6. "CREATE AND CONTINUE" butonuna tıklayın
7. Rolleri ekleyin:
   - `Firebase App Distribution Admin`
   - `Firebase Test Lab Admin`
   - `Cloud Storage Admin` (Test sonuçları için)
8. "CONTINUE" ve "DONE" butonlarına tıklayın

## Adım 6: Service Account Key Oluşturun

1. Yeni oluşturduğunuz service account'a tıklayın
2. "KEYS" sekmesine gidin
3. "ADD KEY" > "Create new key" seçin
4. "JSON" formatını seçin
5. "CREATE" butonuna tıklayın
6. JSON dosyası indirilecek - **BU DOSYAYI GÜVENLİ SAKLAYIN!**

## Adım 7: Cloud Storage Bucket Oluşturun (Test Sonuçları İçin)

1. Google Cloud Console'da sol menüden **Cloud Storage** > **Buckets** seçin
2. "CREATE BUCKET" butonuna tıklayın
3. Bucket adı girin (örn: `claudeyapp-test-results`)
4. Region seçin (örn: `us-central1`)
5. "CREATE" butonuna tıklayın

## Adım 8: Firebase App Distribution Authentication Seçimi

Firebase App Distribution için **iki farklı yöntem** vardır:

### Yöntem A: Service Account (Önerilen) ✅

Şu anki GitHub Actions workflow'u bu yöntemi kullanıyor. Service Account JSON yeterlidir.

**Avantajları:**
- Daha güvenli (role-based access)
- Otomatik yenileme gerektirmez
- Production ortamları için önerilir

### Yöntem B: Firebase CLI Token (Alternatif)

Firebase CLI ile login token kullanımı.

**Nasıl Alınır:**
```bash
# Firebase CLI yükleyin
npm install -g firebase-tools

# Login yapın ve token alın
firebase login:ci
```

Bu komut size bir token verecek. Bu token'ı `FIREBASE_TOKEN` secret'ı olarak GitHub'a ekleyebilirsiniz.

**Not:** Yöntem B için workflow dosyasında değişiklik gerekir. Mevcut kurulum Yöntem A kullanıyor.

---

## Adım 9: GitHub Secrets Oluşturun

GitHub repository'nizde aşağıdaki secrets'ları oluşturun:

### Repository Settings > Secrets and variables > Actions > New repository secret

### Zorunlu Secrets:

1. **FIREBASE_APP_ID** (Her iki yöntem için gerekli)
   - Firebase Console > Project Settings > General
   - Android uygulamanızın altındaki "App ID" değeri
   - Format: `1:123456789:android:abcdef123456`
   - **Nasıl Bulunur:**
     - Firebase Console > Projeni seç
     - ⚙️ (Settings) > Project Settings
     - "Your apps" bölümünde Android uygulamanızı bulun
     - "App ID" alanını kopyalayın

2. **FIREBASE_SERVICE_ACCOUNT** (Yöntem A için - Önerilen)
   - Adım 6'da indirdiğiniz JSON dosyasının **tüm içeriğini** kopyalayın
   - JSON formatında olmalı (başı `{` sonu `}`)
   - **Format Örneği:**
     ```json
     {
       "type": "service_account",
       "project_id": "your-project-id",
       "private_key_id": "...",
       "private_key": "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n",
       "client_email": "...",
       "client_id": "...",
       ...
     }
     ```

**VEYA**

2b. **FIREBASE_TOKEN** (Yöntem B için - Alternatif)
   - `firebase login:ci` komutuyla aldığınız token
   - **Not:** Bu yöntem için workflow dosyasında değişiklik gerekir

3. **FIREBASE_PROJECT_ID** (Test Lab için)
   - Firebase Console > Project Settings > General
   - "Project ID" değeri
   - Örnek: `claudeyapp-12345`

4. **FIREBASE_TEST_BUCKET** (Test Lab için)
   - Adım 7'de oluşturduğunuz bucket adı
   - Örnek: `claudeyapp-test-results`
   - **NOT:** `gs://` prefix'i OLMADAN sadece bucket adı

## Adım 10: GitHub Secrets Ekleme Adımları

1. GitHub repository'nize gidin
2. **Settings** > **Secrets and variables** > **Actions** seçin
3. **New repository secret** butonuna tıklayın
4. Her secret için:
   - Name: Secret adını girin (BÜYÜK HARFLERLE)
   - Value: İlgili değeri girin
   - "Add secret" butonuna tıklayın

## Doğrulama

Tüm adımları tamamladıktan sonra:

1. Yeni bir commit pushlayın veya PR açın
2. GitHub Actions workflow'unun çalıştığını kontrol edin
3. Başarılı olursa:
   - APK GitHub Actions artifacts'ta görünecek
   - APK Firebase App Distribution'da görünecek
   - Test sonuçları Firebase Test Lab'da görünecek

## Sorun Giderme

### Firebase App Distribution hatası alıyorsanız:
- `FIREBASE_APP_ID` doğru formatta mı kontrol edin
- Service account'un `Firebase App Distribution Admin` rolüne sahip olduğundan emin olun

### Firebase Test Lab hatası alıyorsanız:
- `FIREBASE_PROJECT_ID` doğru mu kontrol edin
- `FIREBASE_TEST_BUCKET` bucket adının doğru olduğundan emin olun
- Service account'un `Firebase Test Lab Admin` ve `Cloud Storage Admin` rollerine sahip olduğundan emin olun

### Service Account authentication hatası:
- `FIREBASE_SERVICE_ACCOUNT` secret'ının tam JSON içeriğini içerdiğinden emin olun
- JSON formatının bozulmadığını kontrol edin
- Private key içinde `\n` karakterlerinin korunduğundan emin olun
- JSON'ı kopyalarken başta/sonda boşluk olmamalı

### Token ile ilgili hatalar:
- Service Account JSON kullanıyorsanız `FIREBASE_TOKEN` gerekmez
- Her iki token türünü (SERVICE_ACCOUNT ve TOKEN) aynı anda kullanmayın
- Service Account rollerinin doğru atandığından emin olun

## Test Türleri

Şu anda **Robo Test** kullanılıyor:
- Otomatik UI testleri
- Kullanıcı akışlarını simüle eder
- Test yazmanıza gerek yok

İleride **Instrumentation Tests** eklenebilir:
- Custom UI testleri
- `app/src/androidTest` klasöründe test yazabilirsiniz

## Maliyet

- **Firebase App Distribution**: Ücretsiz
- **Firebase Test Lab**:
  - Her gün ilk 10 test ücretsiz (fiziksel cihazlar)
  - Her gün ilk 5 test ücretsiz (sanal cihazlar)
  - Detaylar: https://firebase.google.com/pricing

## Yararlı Linkler

- [Firebase Console](https://console.firebase.google.com/)
- [Google Cloud Console](https://console.cloud.google.com/)
- [Firebase App Distribution Docs](https://firebase.google.com/docs/app-distribution)
- [Firebase Test Lab Docs](https://firebase.google.com/docs/test-lab)
