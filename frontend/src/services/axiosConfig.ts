import axios from 'axios';

// API'nin temel URL'si
const API_URL = 'http://localhost:8080/api'; 

// Axios instance oluşturma
const axiosInstance = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// Request interceptor: Her istekte token ekler
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor: Token yenileme mantığı ve hata işleme
axiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    
    // Token geçersizse ve daha önce token yenileme denenmemişse
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        // Token yenileme isteği
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) {
          // Refresh token yoksa kullanıcı çıkış yapmalıdır
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('user');
          
          // Giriş sayfasına yönlendir
          window.location.href = '/login';
          return Promise.reject(error);
        }
        
        // Token yenileme işlemi
        const refreshResponse = await axios.post('http://localhost:8080/api/auth/refresh-token', 
          { refreshToken },
          {
            headers: {
              'Content-Type': 'application/json'
            }
          }
        );
        
        if (refreshResponse.data.success && refreshResponse.data.data) {
          const { accessToken, refreshToken: newRefreshToken } = refreshResponse.data.data;
          
          // Yeni tokenları kaydet
          localStorage.setItem('accessToken', accessToken);
          localStorage.setItem('refreshToken', newRefreshToken);
          
          // Orijinal isteği yeni token ile yeniden gönder
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
          return axios(originalRequest);
        } else {
          // Token yenileme başarısız, kullanıcıyı çıkış yaptır
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('user');
          
          window.location.href = '/login';
          return Promise.reject(new Error('Oturum süresi doldu. Lütfen tekrar giriş yapınız.'));
        }
      } catch (refreshError) {
        console.error('Token yenileme hatası:', refreshError);
        
        // Token yenileme başarısız, kullanıcıyı çıkış yaptır
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
        
        window.location.href = '/login';
        return Promise.reject(new Error('Oturum süresi doldu. Lütfen tekrar giriş yapınız.'));
      }
    }
    
    // Diğer hatalar için hata mesajlarını iyileştir
    if (error.response?.data?.message) {
      error.message = error.response.data.message;
    } else if (error.response?.status === 404) {
      error.message = 'İstek yapılan kaynak bulunamadı.';
    } else if (error.response?.status === 403) {
      error.message = 'Bu işlem için yetkiniz bulunmamaktadır.';
    } else if (error.response?.status === 500) {
      error.message = 'Sunucu hatası. Lütfen daha sonra tekrar deneyiniz.';
    } else if (!error.response && error.request) {
      error.message = 'Sunucuya bağlanılamadı. İnternet bağlantınızı kontrol edin.';
    }
    
    return Promise.reject(error);
  }
);

export default axiosInstance; 