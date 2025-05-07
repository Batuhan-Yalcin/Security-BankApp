import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const axiosInstance = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
  // CORS sorunları için withCredentials: true ekleyebiliriz
  // withCredentials: true,
});

// İstek gönderilmeden önce token eklemek için interceptor
axiosInstance.interceptors.request.use(
  (config) => {
    console.log(`API İsteği: ${config.method?.toUpperCase()} ${config.baseURL}${config.url}`);
    
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Yanıt alındığında token yenileme için interceptor
axiosInstance.interceptors.response.use(
  (response) => {
    // Başarılı yanıt
    console.log(`API Yanıtı: ${response.status} ${response.config.url}`);
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    
    // Token süresi dolduysa ve henüz retry yapmadıysak
    if (error.response && error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        // Token yenileme isteği
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) {
          throw new Error('Refresh token bulunamadı');
        }
        
        const response = await axios.post(`${API_URL}/auth/refresh-token`, {
          refreshToken,
        });
        
        const { accessToken, refreshToken: newRefreshToken } = response.data.data;
        
        // Yeni tokenları kaydet
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', newRefreshToken);
        
        // Orijinal isteği yeni token ile tekrarla
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return axios(originalRequest);
      } catch (refreshError) {
        // Token yenileme başarısız, kullanıcıyı çıkış yapmaya yönlendir
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
        
        // Giriş sayfasına yönlendir
        window.location.href = '/login';
        
        return Promise.reject(refreshError);
      }
    }
    
    // 500 hatalarını göster
    if (error.response && error.response.status === 500) {
      console.error('API 500 Hatası:', error.response.data);
    }
    
    return Promise.reject(error);
  }
);

export default axiosInstance; 