# Bankacılık Uygulaması API

Bu proje, modern güvenlik özellikleriyle donatılmış, kapsamlı bir bankacılık uygulaması backend API'si sunmaktadır. Spring Boot, Spring Security, JWT token tabanlı kimlik doğrulama kullanılarak geliştirilmiştir.

## 🛠️ Kullanılan Teknolojiler

- **Spring Boot 3.2.x** - Temel framework
- **Spring Security 6.x** - Güvenlik ve yetkilendirme
- **Spring Data JPA** - Veritabanı etkileşimleri
- **PostgreSQL** - Veritabanı sistemi
- **H2 Database** - Test ortamı için gömülü veritabanı
- **JWT (JSON Web Token)** - Stateless kimlik doğrulama
- **Lombok** - Yazılabilir kodu azaltmak için kod üreteci
- **Hibernate Validator** - Veri doğrulama
- **OpenAPI/Swagger** - API dokümantasyonu
- **JUnit 5** - Birim ve entegrasyon testleri için test framework'ü
- **Mockito** - Testlerde mock nesneleri oluşturmak için
- **MockMvc** - Controller testleri için
- **Jackson** - JSON işleme kütüphanesi

## 🏗️ Mimari Yapı

Proje, temiz kod ve sorumlulukların ayrılması (Separation of Concerns) prensibine dayalı katmanlı bir mimari kullanmaktadır:

```
com.batuhanyalcin.BankApp/
├── controller/       # API endpointlerinin tanımlandığı katman
├── service/          # İş mantığının yürütüldüğü katman
├── repository/       # Veritabanı işlemlerinin yapıldığı katman
├── entity/           # Veritabanı tablolarını temsil eden model sınıfları
├── dto/              # Veri transfer nesneleri
├── exception/        # Özel hata sınıfları ve global exception handler
├── security/         # Güvenlik yapılandırması ve servisleri
│   ├── config/       # Spring Security yapılandırması
│   └── service/      # Güvenlik ile ilgili servisler
└── config/           # Uygulama yapılandırmaları
```

## 📋 Özellikler

- JWT tabanlı kimlik doğrulama ve yetkilendirme
- Refresh token desteği ile oturum yönetimi
- RBAC (Role-Based Access Control) - Rol tabanlı erişim kontrolü
- Hesap ve işlem yönetimi
- Kapsamlı hata yönetimi ve özel exception sınıfları
- Global exception handling ile standardize edilmiş hata yanıtları
- API dokümantasyonu (Swagger/OpenAPI)
- Kapsamlı birim ve entegrasyon testleri
- Profile bazlı ortam yapılandırması (dev, test, prod)
- Log mekanizmaları

## 🔐 Güvenlik Özellikleri

- JWT token tabanlı kimlik doğrulama
- Password encoding (BCrypt)
- CORS yapılandırması
- Cross-Site Request Forgery (CSRF) koruması
- Rol tabanlı erişim kontrolü (ROLE_USER, ROLE_ADMIN)
- Süresi dolmuş/geçersiz tokenların yönetimi
- Refresh token yönetimi
- Method düzeyinde yetkilendirme (@PreAuthorize)

## 📱 API Endpointleri

### 🔑 Kimlik Doğrulama API'leri

| Endpoint | Metod | Açıklama | Yetki |
|----------|-------|----------|-------|
| `/api/auth/register` | POST | Yeni kullanıcı kaydı | Public |
| `/api/auth/login` | POST | Kullanıcı girişi ve token alma | Public |
| `/api/auth/refresh-token` | POST | Yeni access token alma | Public |
| `/api/auth/logout` | POST | Çıkış yapma | Public |
| `/api/auth/logout-all` | POST | Tüm oturumları sonlandırma | USER |
| `/api/auth/me` | GET | Mevcut kullanıcı bilgilerini görüntüleme | USER |
| `/api/auth/admin-check` | GET | Admin yetkisi kontrolü | ADMIN |

### 👤 Müşteri API'leri

| Endpoint | Metod | Açıklama | Yetki |
|----------|-------|----------|-------|
| `/api/customers` | GET | Tüm müşterileri listeleme | ADMIN |
| `/api/customers/{id}` | GET | Müşteri detaylarını görüntüleme | USER, ADMIN |
| `/api/customers` | POST | Yeni müşteri oluşturma | ADMIN |
| `/api/customers/{id}` | PUT | Müşteri bilgilerini güncelleme | USER, ADMIN |
| `/api/customers/{id}` | DELETE | Müşteri silme | ADMIN |

### 💰 Hesap API'leri

| Endpoint | Metod | Açıklama | Yetki |
|----------|-------|----------|-------|
| `/api/accounts` | GET | Tüm hesapları listeleme | ADMIN |
| `/api/accounts/{id}` | GET | Hesap detaylarını görüntüleme | USER, ADMIN |
| `/api/accounts/customer/{customerId}` | GET | Müşteriye ait hesapları listeleme | USER, ADMIN |
| `/api/accounts` | POST | Yeni hesap oluşturma | USER, ADMIN |
| `/api/accounts/{id}` | DELETE | Hesap silme | ADMIN |

### 💸 İşlem API'leri

| Endpoint | Metod | Açıklama | Yetki |
|----------|-------|----------|-------|
| `/api/transactions` | GET | Tüm işlemleri listeleme | ADMIN |
| `/api/transactions/{id}` | GET | İşlem detaylarını görüntüleme | USER, ADMIN |
| `/api/transactions/account/{accountId}` | GET | Hesaba ait işlemleri listeleme | USER, ADMIN |
| `/api/transactions/deposit` | POST | Para yatırma | USER, ADMIN |
| `/api/transactions/withdraw` | POST | Para çekme | USER, ADMIN |
| `/api/transactions/transfer` | POST | Para transferi | USER, ADMIN |

## 🧪 Test Yapısı

Bu proje, yazılım kalitesini ve güvenilirliğini garanti altına almak için kapsamlı test stratejileri uygulamaktadır. Her katman için ayrı ve profesyonel testler oluşturulmuştur:

### Birim Testleri

Servis ve yardımcı sınıfların işlevselliğini test etmek için JUnit 5 ve Mockito kullanılmıştır:

- **Service Testleri**: Her servis için ayrı test sınıfı oluşturularak tüm iş mantığı servislerinin (CustomerService, AccountService, TransactionService, AuthService) davranışları detaylı olarak doğrulanmıştır. Bu testlerde bağımlılıklar (dependency) mocklanarak izole bir ortam sağlanmıştır.
- **Repository Testleri**: @DataJpaTest anotasyonu kullanılarak veritabanı katmanının doğru çalıştığı test edilmiştir. Bu testlerde H2 gömülü veritabanı kullanılmıştır.
- **DTO/Entity Dönüşüm Testleri**: Veri model dönüşümlerinin doğruluğu test edilmiştir.

### Entegrasyon Testleri

Controller sınıfları için tam Spring uygulama bağlamında API davranışını doğrulamak üzere MockMvc kullanılmıştır:

- **Controller Testleri**: Tüm controller sınıfları için tam kapsamlı entegrasyon testleri geliştirilmiş, HTTP isteklerine verilen yanıtlar, durum kodları, JSON yanıtları ve validation kuralları test edilmiştir.
- **Security Testleri**: @WithMockUser anotasyonları kullanılarak yetkilendirme ve kimlik doğrulama mekanizmalarının doğru çalıştığı doğrulanmıştır.
- **Exception Handling Testleri**: Özel hata durumlarının doğru şekilde işlendiği doğrulanmıştır.

### Test Profili ve Konfigürasyon

- **Test Profili**: Testler özel bir test profili altında çalıştırılarak (@ActiveProfiles("test")), gerçek veritabanından yalıtılmıştır.
- **Test Veritabanı**: Entegrasyon testleri için H2 gömülü veritabanı kullanılmıştır.
- **Test Konfigürasyonu**: Özel test konfigürasyonları (@TestConfiguration) ile test ortamı düzenlenmiştir.

### Test Özellikleri ve Metodolojisi

- **Kapsamlı Test Coverage**: Tüm servisler, controllerlar ve kritik business logic için %80+ test kapsamı sağlanmıştır.
- **Gerçekçi Test Verileri**: Gerçek senaryoları simüle etmek için gerçekçi test verileri kullanılmıştır.
- **Happy Path ve Edge Case Testleri**: Hem başarılı durumlar hem de sınır durumları ve hata durumları test edilmiştir.
- **Parametrized Testler**: Benzer senaryolar için parametrelendirilmiş testler kullanılmıştır.
- **Descriptive Naming**: Test metodlarında açıklayıcı isimlendirme kullanılmıştır (methodName_scenario_expectedBehavior).
- **Debugging Logs**: Testlerde sorun takibi için ayrıntılı log kayıtları eklenmiştir.

## 🚀 Çalıştırma Talimatları

### Ön Koşullar

- Java 17 veya üzeri
- Maven
- PostgreSQL veritabanı

### Kurulum

1. Projeyi klonlayın:
```bash
git clone https://github.com/Batuhan-Yalcin/Security-BankApp.git
cd BankApp
```

2. PostgreSQL veritabanı yapılandırmasını `application.properties` dosyasında düzenleyin:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bankapp
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Uygulamayı derleyin ve testleri çalıştırın:
```bash
mvn clean install
```

4. Uygulamayı çalıştırın:
```bash
mvn spring-boot:run
```

5. API dokümantasyonuna erişim:
```
http://localhost:8080/swagger-ui.html
```

## 📈 Gelecek Geliştirmeler

- Mikro servis mimarisine geçiş
- Hesap aktiviteleri için gerçek zamanlı bildirimler
- İki faktörlü kimlik doğrulama (2FA)
- API hız sınırlama ve throttling
- GraphQL desteği
- Docker containerization ve Kubernetes ile dağıtım
- CI/CD pipeline entegrasyonu

## 📜 Lisans

Bu proje MIT lisansı altında lisanslanmıştır - detaylar için LICENSE dosyasına bakınız. 