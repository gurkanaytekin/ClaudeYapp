# Firebase Token - Hızlı Referans

## Hangi Authentication Yöntemi Kullanılıyor?

Bu proje **Service Account JSON** yöntemi kullanıyor (Yöntem A - Önerilen).

## Gerekli Secrets

### Zorunlu (Firebase App Distribution için):
- ✅ `FIREBASE_APP_ID` - Firebase uygulamanızın ID'si
- ✅ `FIREBASE_SERVICE_ACCOUNT` - Service Account JSON dosyasının tüm içeriği

### Opsiyonel (Firebase Test Lab için):
- `FIREBASE_PROJECT_ID` - Firebase proje ID'si
- `FIREBASE_TEST_BUCKET` - Cloud Storage bucket adı

## Service Account vs Firebase Token

| Özellik | Service Account (Mevcut) | Firebase CLI Token |
|---------|-------------------------|-------------------|
| Güvenlik | ✅ Yüksek (Role-based) | ⚠️ Orta |
| Süre Sınırı | ❌ Yok | ✅ Var (yenileme gerekir) |
| Kurulum | Orta | Kolay |
| Önerilen | ✅ Evet (Production) | Kişisel projeler |

## Service Account Nasıl Alınır?

### Hızlı Adımlar:

1. **Google Cloud Console'a gidin:**
   - https://console.cloud.google.com/
   - Firebase projenizi seçin

2. **Service Account oluşturun:**
   - IAM & Admin > Service Accounts
   - CREATE SERVICE ACCOUNT
   - Name: `github-actions-firebase`

3. **Roller ekleyin:**
   - `Firebase App Distribution Admin`
   - `Firebase Test Lab Admin` (opsiyonel)
   - `Cloud Storage Admin` (opsiyonel)

4. **Key oluşturun:**
   - KEYS sekmesi > ADD KEY > Create new key
   - JSON formatı seçin
   - İndir

5. **GitHub Secret olarak ekleyin:**
   - Repository > Settings > Secrets > Actions
   - New repository secret
   - Name: `FIREBASE_SERVICE_ACCOUNT`
   - Value: JSON dosyasının **tüm içeriğini** yapıştır

## Firebase CLI Token Nasıl Alınır? (Alternatif)

Eğer Service Account yerine Firebase Token kullanmak isterseniz:

```bash
# 1. Firebase CLI yükleyin
npm install -g firebase-tools

# 2. Login yapın ve token alın
firebase login:ci

# 3. Çıktıdaki token'ı kopyalayın
# Örnek: 1//03Ak9gXXXXXXXXXXXXXXXX
```

**Not:** Bu yöntemi kullanmak için workflow dosyasında değişiklik gerekir.

## Firebase App ID Nasıl Bulunur?

1. Firebase Console: https://console.firebase.google.com/
2. Projenizi seçin
3. ⚙️ (Settings) > Project Settings
4. "Your apps" bölümünde Android uygulamanızı bulun
5. "App ID" alanını kopyalayın
   - Format: `1:123456789:android:abcdef123456`

## Sık Sorulan Sorular

### Token gerekli mi?
Evet, ama **hangi token gerekli** önemli:
- Mevcut kurulum: `FIREBASE_SERVICE_ACCOUNT` (JSON)
- Alternatif: `FIREBASE_TOKEN` (CLI token)

### İkisini birden eklemeli miyim?
Hayır! Sadece birini seçin. Önerimiz: `FIREBASE_SERVICE_ACCOUNT`

### Service Account JSON'ı nasıl kopyalamalıyım?
```bash
# Mac/Linux:
cat service-account.json | pbcopy

# Veya dosyayı text editor'de açıp tümünü kopyalayın
# Başta/sonda boşluk bırakmayın!
```

### Token süresi doluyor mu?
- Service Account: Hayır, süresiz
- Firebase CLI Token: Evet, 30-90 gün sonra yenilemeniz gerekebilir

### Secrets eklendikten sonra ne yapmalıyım?
1. Yeni bir commit pushlayın
2. GitHub Actions'ı izleyin
3. Firebase Console'da App Distribution'ı kontrol edin

## Sorun Giderme

### "Authentication failed" hatası:
- Secret adları tam doğru mu? (BÜYÜK HARF)
- JSON formatı bozuk mu? (JSON validator kullanın)
- Service Account rolleri doğru mu?

### "App not found" hatası:
- `FIREBASE_APP_ID` doğru formatta mı?
- Android uygulama Firebase'e eklenmiş mi?

### Secrets ekledim ama çalışmıyor:
1. Secret adlarını kontrol edin (typo?)
2. Yeni bir commit push edin (secrets güncelleme hemen aktif olmayabilir)
3. Workflow logs'u kontrol edin

## Detaylı Dokümantasyon

Tam kurulum rehberi için: [FIREBASE_SETUP.md](FIREBASE_SETUP.md)
