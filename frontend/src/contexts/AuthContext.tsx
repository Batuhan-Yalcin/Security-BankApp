import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSnackbar } from 'notistack';
import { LoginRequest, RegisterRequest, JwtResponse } from '../interfaces/auth';
import authService from '../services/authService';

// Context tipi
interface AuthContextType {
  isAuthenticated: boolean;
  user: JwtResponse | null;
  loading: boolean;
  error: string | null;
  login: (data: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
  forgotPassword: (email: string) => Promise<void>;
  clearError: () => void;
}

// AuthContext oluşturulması
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Custom hook
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth hook must be used within an AuthProvider');
  }
  return context;
};

// Props tipi
interface AuthProviderProps {
  children: ReactNode;
}

// Provider bileşeni
export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [user, setUser] = useState<JwtResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const { enqueueSnackbar } = useSnackbar();

  // Sayfa yüklendiğinde localStorage'dan kullanıcı bilgilerini kontrol et
  useEffect(() => {
    const checkAuth = () => {
      const accessToken = localStorage.getItem('accessToken');
      const userStr = localStorage.getItem('user');

      if (accessToken && userStr) {
        try {
          const userData = JSON.parse(userStr) as JwtResponse;
          setUser(userData);
          setIsAuthenticated(true);
        } catch (err) {
          console.error('Kullanıcı bilgileri JSON parse hatası:', err);
          // Geçersiz veri, localStorage'ı temizle
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('user');
        }
      }
    };

    checkAuth();
  }, []);

  // Giriş fonksiyonu
  const login = async (data: LoginRequest) => {
    setLoading(true);
    setError(null);
    try {
      console.log("Giriş isteği gönderiliyor:", data.email);
      const response = await authService.login(data);
      
      if (response.success && response.data) {
        // Token ve kullanıcı bilgilerini localStorage'a kaydet
        localStorage.setItem('accessToken', response.data.accessToken);
        localStorage.setItem('refreshToken', response.data.refreshToken);
        localStorage.setItem('user', JSON.stringify({
          id: response.data.id,
          email: response.data.email,
          firstName: response.data.firstName,
          lastName: response.data.lastName,
          roles: response.data.roles
        }));
        
        setUser(response.data);
        setIsAuthenticated(true);
        navigate('/dashboard');
        enqueueSnackbar('Giriş başarılı!', { variant: 'success' });
      } else {
        throw new Error(response.message || 'Giriş başarısız');
      }
    } catch (err: any) {
      console.error('Giriş sırasında hata oluştu:', err);
      const errorMessage = err.message || 'Giriş yapılamadı. Lütfen tekrar deneyin.';
      setError(errorMessage);
      enqueueSnackbar(errorMessage, { variant: 'error' });
    } finally {
      setLoading(false);
    }
  };

  // Kayıt fonksiyonu
  const register = async (data: RegisterRequest) => {
    setLoading(true);
    setError(null);
    try {
      console.log("AuthContext - gönderilen kayıt verileri:", data);
      const response = await authService.register(data);
      
      if (response.success) {
        enqueueSnackbar('Kayıt başarılı! Şimdi giriş yapabilirsiniz.', { variant: 'success' });
        navigate('/login');
      } else {
        throw new Error(response.message || 'Kayıt başarısız');
      }
    } catch (err: any) {
      console.error('Kayıt sırasında hata oluştu:', err);
      
      // Hata mesajını göster
      const errorMessage = err.message || 'Kayıt işlemi sırasında bir hata oluştu.';
      setError(errorMessage);
      enqueueSnackbar(errorMessage, { variant: 'error' });
    } finally {
      setLoading(false);
    }
  };

  // Çıkış fonksiyonu
  const logout = async () => {
    setLoading(true);
    try {
      const refreshToken = localStorage.getItem('refreshToken');
      if (!refreshToken) {
        throw new Error('Oturum bilgisi bulunamadı');
      }
      
      await authService.logout(refreshToken);
      
      // Tüm oturum verilerini temizle
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
      
      setUser(null);
      setIsAuthenticated(false);
      navigate('/login');
      enqueueSnackbar('Çıkış yapıldı', { variant: 'info' });
    } catch (err: any) {
      console.error('Çıkış sırasında hata oluştu:', err);
      
      // Hataya rağmen oturumu temizle
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
      
      setUser(null);
      setIsAuthenticated(false);
      navigate('/login');
    } finally {
      setLoading(false);
    }
  };

  // Şifre sıfırlama
  const forgotPassword = async (email: string) => {
    setLoading(true);
    setError(null);
    try {
      const response = await authService.forgotPassword(email);
      if (response.success) {
        enqueueSnackbar('Şifre sıfırlama bağlantısı gönderildi', { variant: 'success' });
      } else {
        throw new Error(response.message || 'Şifre sıfırlama başarısız');
      }
    } catch (err: any) {
      console.error('Şifre sıfırlama sırasında hata:', err);
      setError(err.message || 'Şifre sıfırlama işlemi sırasında bir hata oluştu');
      enqueueSnackbar(err.message || 'Şifre sıfırlama işlemi sırasında bir hata oluştu', { variant: 'error' });
    } finally {
      setLoading(false);
    }
  };

  // Hata temizleme
  const clearError = () => {
    setError(null);
  };

  // Context değeri
  const value = {
    isAuthenticated,
    user,
    loading,
    error,
    login,
    register,
    logout,
    forgotPassword,
    clearError
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}; 