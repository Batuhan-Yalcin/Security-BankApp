import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import { SnackbarProvider } from 'notistack';
import theme from './styles/theme';
import GlobalStyles from './styles/GlobalStyles';
import { AuthProvider } from './contexts/AuthContext';

// Sayfa bileşenleri
import Login from './pages/Login';
import Register from './pages/Register';
import ForgotPassword from './pages/ForgotPassword';
// import Dashboard from './pages/Dashboard';
// import AccountsPage from './pages/AccountsPage';
// import TransactionsPage from './pages/TransactionsPage';
// import ProfilePage from './pages/ProfilePage';

// Koruyucu bileşen (Route Guard)
// Şu anda ihtiyaç olmadığı için yorum satırına alındı
// interface ProtectedRouteProps {
//   children: React.ReactNode;
// }

const App: React.FC = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <GlobalStyles />
      <SnackbarProvider 
        maxSnack={3} 
        anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
        autoHideDuration={3000}
      >
        <Router>
          <AuthProvider>
            <Routes>
              {/* Kimlik doğrulama sayfaları */}
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/forgot-password" element={<ForgotPassword />} />
              
              {/* Koruyucu rotalar (henüz uygulanmadı) */}
              {/* <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
              <Route path="/accounts" element={<ProtectedRoute><AccountsPage /></ProtectedRoute>} />
              <Route path="/transactions" element={<ProtectedRoute><TransactionsPage /></ProtectedRoute>} />
              <Route path="/profile" element={<ProtectedRoute><ProfilePage /></ProtectedRoute>} /> */}
              
              {/* Ana sayfa yönlendirmesi */}
              <Route path="/" element={<Navigate to="/login" replace />} />
              
              {/* 404 sayfası */}
              <Route path="*" element={<Navigate to="/login" replace />} />
            </Routes>
          </AuthProvider>
        </Router>
      </SnackbarProvider>
    </ThemeProvider>
  );
};

export default App;
