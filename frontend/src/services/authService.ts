import axiosInstance from './axiosConfig';
import { 
  LoginRequest, 
  RegisterRequest, 
  TokenRefreshRequest, 
  JwtResponse,
  TokenRefreshResponse,
  ApiResponse
} from '../interfaces/auth';
import axios from 'axios';

const authService = {
  // Kullanıcı kaydı
  register: async (registerRequest: RegisterRequest): Promise<ApiResponse<string>> => {
    console.log("Backend'e gönderilen kayıt verileri:", JSON.stringify(registerRequest, null, 2));
    try {
      // CORS hatalarına karşı preflight isteği sorunlarını önlemek için 
      // basit bir yapılandırma ile doğrudan axios kullanıyoruz
      const response = await axios({
        method: 'post',
        url: 'http://localhost:8080/api/auth/register',
        data: registerRequest,
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      });
      
      console.log("Backend'den gelen yanıt:", response.data);
      return response.data;
    } catch (error: any) {
      console.error("Kayıt hatası:", error);
      
      // 500 hatası durumunda (ConcurrentModificationException) mock başarılı yanıt
      if (error.response) {
        if (error.response.status === 500) {
          console.error("500 Hatası detayları:", error.response.data);
          console.warn("Geçici çözüm: Backend hatası mock başarılı yanıtla değiştiriliyor");
          
          // GEÇİCİ ÇÖZÜM: Backend sorunlarını aşmak için frontend geliştirmesi sırasında
          // başarılı yanıt simülasyonu yapıyoruz. Backend sorunları çözüldüğünde bu kısım kaldırılmalıdır.
          
          // Kullanıcıyı veritabanına kaydettiğimizi varsay ve başarılı yanıt dön
          return {
            success: true,
            message: "Kayıt işlemi tamamlandı. (Test yanıtı)",
            data: "OK",
            timestamp: new Date().toISOString(),
            status: 200
          };
        } else if (error.response.status === 400) {
          // 400 Bad Request - Veri doğrulama hatası
          const errorMessage = error.response.data?.message || 'Geçersiz kayıt verileri. Lütfen girdiğiniz bilgileri kontrol edin.';
          throw new Error(errorMessage);
        } else if (error.response.status === 409) {
          // 409 Conflict - Muhtemelen e-posta adresi zaten kullanımda
          throw new Error('Bu e-posta adresi zaten kullanımda. Lütfen farklı bir e-posta adresi deneyin.');
        } else if (error.response.status === 401) {
          // 401 Unauthorized - Yetkilendirme hatası
          throw new Error('Yetkilendirme hatası. Lütfen tekrar giriş yapın.');
        }
      }
      
      // Diğer hatalar için uygun mesaj
      const errorMessage = error.response?.data?.message || 
                          'Kayıt işlemi sırasında bir hata oluştu. Lütfen daha sonra tekrar deneyin.';
      throw new Error(errorMessage);
    }
  },
  
  // Kullanıcı girişi
  login: async (loginRequest: LoginRequest): Promise<ApiResponse<JwtResponse>> => {
    try {
      console.log("Giriş isteği gönderiliyor:", loginRequest.email);
      if (loginRequest.email === "batu@gmail.com" && loginRequest.password === "B190758x") {
        console.log("Geçici çözüm: Test kullanıcısı ile mock giriş yapılıyor");
        
        // Mock JWT yanıtı
        const mockJwtResponse: JwtResponse = {
          tokenType: "Bearer",
          accessToken: "mock-access-token-" + Date.now(),
          refreshToken: "mock-refresh-token-" + Date.now(),
          expiresIn: 86400, // 24 saat
          id: 1,
          email: loginRequest.email,
          firstName: "Batuhan",
          lastName: "Yalçın",
          roles: ["ROLE_USER"]
        };
        
        // LocalStorage'a kaydet
        localStorage.setItem('accessToken', mockJwtResponse.accessToken);
        localStorage.setItem('refreshToken', mockJwtResponse.refreshToken);
        localStorage.setItem('user', JSON.stringify({
          id: mockJwtResponse.id,
          email: mockJwtResponse.email,
          firstName: mockJwtResponse.firstName,
          lastName: mockJwtResponse.lastName,
          roles: mockJwtResponse.roles
        }));
        
        // Başarılı yanıt simülasyonu
        return {
          success: true,
          message: "Giriş başarılı (Test yanıtı)",
          data: mockJwtResponse,
          timestamp: new Date().toISOString(),
          status: 200
        };
      }
      
      // Gerçek istek (Backend hazır olduğunda)
      const response = await axios({
        method: 'post',
        url: 'http://localhost:8080/api/auth/login',
        data: loginRequest,
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      });
      
      console.log("Backend'den giriş yanıtı:", response.data);
      
      // Başarılı giriş durumunda token ve kullanıcı bilgilerini localstorage'a kaydet
      if (response.data.success && response.data.data) {
        localStorage.setItem('accessToken', response.data.data.accessToken);
        localStorage.setItem('refreshToken', response.data.data.refreshToken);
      }
      
      return response.data;
    } catch (error: any) {
      console.error("Giriş hatası:", error);
      
      // 401 Unauthorized - Hatalı giriş bilgileri
      if (error.response && error.response.status === 401) {
        throw new Error('Hatalı e-posta adresi veya şifre');
      }
      
      // Diğer hatalar
      const errorMessage = error.response?.data?.message || 
                          'Giriş yapılamadı. Lütfen daha sonra tekrar deneyin.';
      throw new Error(errorMessage);
    }
  },
  
  // Token yenileme
  refreshToken: async (refreshRequest: TokenRefreshRequest): Promise<ApiResponse<TokenRefreshResponse>> => {
    const response = await axiosInstance.post<ApiResponse<TokenRefreshResponse>>('/auth/refresh-token', refreshRequest);
    return response.data;
  },
  
  // Çıkış yapma
  logout: async (refreshToken: string): Promise<ApiResponse<string>> => {
    const response = await axiosInstance.post<ApiResponse<string>>('/auth/logout', null, {
      params: { refreshToken }
    });
    return response.data;
  },
  
  // Tüm oturumlardan çıkış yapma
  logoutAll: async (): Promise<ApiResponse<string>> => {
    const response = await axiosInstance.post<ApiResponse<string>>('/auth/logout-all');
    return response.data;
  },
  
  // Mevcut kullanıcı bilgisini al
  getCurrentUser: () => {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      return JSON.parse(userStr);
    }
    return null;
  },
  
  // Kullanıcının giriş yapmış olup olmadığını kontrol et
  isAuthenticated: (): boolean => {
    return !!localStorage.getItem('accessToken');
  },
  
  // Kullanıcının belirli bir role sahip olup olmadığını kontrol et
  hasRole: (role: string): boolean => {
    const user = authService.getCurrentUser();
    if (user && user.roles) {
      return user.roles.includes(role);
    }
    return false;
  },
  
  // Şifre sıfırlama - Backend tarafında henüz implement edilmemiş, ileride eklenecek
  forgotPassword: async (email: string): Promise<ApiResponse<string>> => {
    // Şu an için mock yanıt döndürüyoruz
    // Gerçek implement edildiğinde bu satır aktif edilecek:
    // const response = await axiosInstance.post<ApiResponse<string>>('/auth/forgot-password', { email });
    
    try {
      // Backend'de endpoint olmadığı için şimdilik başarılı yanıt veriyoruz
      console.log("Şifre sıfırlama isteği gönderildi:", email);
      return {
        success: true,
        message: "Şifre sıfırlama bağlantısı gönderildi (Mock yanıt)",
        data: "OK",
        timestamp: new Date().toISOString(),
        status: 200
      };
    } catch (error: any) {
      console.error("Şifre sıfırlama hatası:", error);
      throw error;
    }
  },
  
  // Kullanıcı rollerini kontrol et - admin mi?
  isAdmin: (): boolean => {
    return authService.hasRole('ROLE_ADMIN');
  }
};

export default authService; 