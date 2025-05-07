import React, { useState, useEffect } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import * as Yup from 'yup';
import { Formik, Form } from 'formik';
import FormInput from '../components/ui/FormInput';
import {
  Container,
  Box,
  Typography,
  Button,
  Stack,
  Paper,
  Link,
  InputAdornment,
  IconButton,
  useTheme,
} from '@mui/material';
import { Visibility, VisibilityOff, LockOutlined, EmailOutlined } from '@mui/icons-material';
import { motion } from 'framer-motion';
import styled from 'styled-components';

// Özel stiller
const GradientBackground = styled(Box)`
  background: linear-gradient(135deg, #457B9D 0%, #1D3557 100%);
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
`;

const StyledPaper = styled(Paper)`
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(29, 53, 87, 0.15);
`;

const LoginBox = styled(Box)`
  padding: 40px;
  
  @media (max-width: 600px) {
    padding: 20px;
  }
`;

const ImageBox = styled(Box)`
  background-image: url('/banking-image.jpg');
  background-size: cover;
  background-position: center;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  color: white;
  padding: 40px;
  position: relative;
  
  &:before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(0deg, rgba(29, 53, 87, 0.8) 0%, rgba(29, 53, 87, 0.4) 100%);
  }
  
  & > * {
    position: relative;
    z-index: 1;
  }
`;

// Form doğrulama şeması
const validationSchema = Yup.object({
  email: Yup.string()
    .email('Geçerli bir e-posta adresi girin')
    .required('E-posta adresi gereklidir'),
  password: Yup.string().required('Şifre gereklidir'),
});

const Login: React.FC = () => {
  const { login } = useAuth();
  // eslint-disable-next-line
  const navigate = useNavigate();
  const theme = useTheme();
  const [showPassword, setShowPassword] = useState(false);

  // Sayfa yüklendiğinde eski oturum verilerini temizle
  useEffect(() => {
    // JWT token hatalarını önlemek için localStorage'ı temizleme
    const clearLocalStorage = () => {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
      console.log('Önceki oturum bilgileri temizlendi');
    };
    
    clearLocalStorage();
  }, []);

  // Parola görünürlüğünü değiştir
  const handleClickShowPassword = () => {
    setShowPassword(!showPassword);
  };

  // Formun gönderilmesi
  const handleSubmit = async (values: { email: string; password: string }) => {
    try {
      await login(values);
    } catch (error) {
      console.error('Giriş hatası:', error);
      // Hatanın AuthContext'te işlendiğini varsayıyoruz
    }
  };

  // Animasyon varyantları
  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: { 
        duration: 0.8,
        staggerChildren: 0.2
      }
    }
  };

  const itemVariants = {
    hidden: { y: 20, opacity: 0 },
    visible: { 
      y: 0, 
      opacity: 1,
      transition: { duration: 0.5 }
    }
  };

  return (
    <GradientBackground>
      <Container maxWidth="lg">
        <motion.div
          initial="hidden"
          animate="visible"
          variants={containerVariants}
        >
          <StyledPaper elevation={10}>
            <Stack direction={{ xs: 'column', md: 'row' }}>
              {/* Sol taraf (giriş formu) */}
              <Box sx={{ flex: 1 }}>
                <LoginBox>
                  <motion.div variants={itemVariants}>
                    <Typography 
                      variant="h4" 
                      color="primary" 
                      gutterBottom 
                      fontWeight="bold"
                      sx={{ mb: 4 }}
                    >
                      Hesabınıza Giriş Yapın
                    </Typography>
                  </motion.div>

                  <Formik
                    initialValues={{ email: '', password: '' }}
                    validationSchema={validationSchema}
                    onSubmit={handleSubmit}
                  >
                    {({ isSubmitting }) => (
                      <Form>
                        <motion.div variants={itemVariants}>
                          <FormInput
                            name="email"
                            label="E-posta Adresi"
                            placeholder="E-posta adresinizi girin"
                            InputProps={{
                              startAdornment: (
                                <InputAdornment position="start">
                                  <EmailOutlined color="primary" />
                                </InputAdornment>
                              ),
                            }}
                          />
                        </motion.div>

                        <motion.div variants={itemVariants}>
                          <FormInput
                            name="password"
                            label="Şifre"
                            type={showPassword ? 'text' : 'password'}
                            placeholder="Şifrenizi girin"
                            InputProps={{
                              startAdornment: (
                                <InputAdornment position="start">
                                  <LockOutlined color="primary" />
                                </InputAdornment>
                              ),
                              endAdornment: (
                                <InputAdornment position="end">
                                  <IconButton
                                    onClick={handleClickShowPassword}
                                    edge="end"
                                  >
                                    {showPassword ? (
                                      <VisibilityOff />
                                    ) : (
                                      <Visibility />
                                    )}
                                  </IconButton>
                                </InputAdornment>
                              ),
                            }}
                          />
                        </motion.div>

                        <motion.div 
                          variants={itemVariants}
                          style={{ 
                            display: 'flex', 
                            justifyContent: 'flex-end', 
                            marginTop: '10px', 
                            marginBottom: '20px' 
                          }}
                        >
                          <Link 
                            component={RouterLink} 
                            to="/forgot-password" 
                            variant="body2"
                            color="primary"
                            sx={{ 
                              textDecoration: 'none',
                              '&:hover': { textDecoration: 'underline' }
                            }}
                          >
                            Şifrenizi mi unuttunuz?
                          </Link>
                        </motion.div>

                        <motion.div variants={itemVariants}>
                          <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            color="primary"
                            size="large"
                            disabled={isSubmitting}
                            sx={{
                              py: 1.5,
                              mt: 2,
                              mb: 3,
                              fontWeight: 'bold',
                              fontSize: '1rem',
                              boxShadow: theme.shadows[4],
                              '&:hover': {
                                boxShadow: theme.shadows[8],
                              },
                            }}
                          >
                            {isSubmitting ? 'Giriş Yapılıyor...' : 'Giriş Yap'}
                          </Button>
                        </motion.div>
                      </Form>
                    )}
                  </Formik>

                  <motion.div variants={itemVariants} style={{ textAlign: 'center' }}>
                    <Typography variant="body1" color="textSecondary">
                      Hesabınız yok mu?{' '}
                      <Link
                        component={RouterLink}
                        to="/register"
                        color="primary"
                        sx={{ 
                          fontWeight: 'bold',
                          textDecoration: 'none',
                          '&:hover': { textDecoration: 'underline' }
                        }}
                      >
                        Şimdi Kaydolun
                      </Link>
                    </Typography>
                  </motion.div>
                </LoginBox>
              </Box>

              {/* Sağ taraf (görsel) */}
              <Box 
                sx={{ 
                  flex: 1, 
                  display: { xs: 'none', md: 'block' } 
                }}
              >
                <ImageBox>
                  <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.8, delay: 0.5 }}
                  >
                    <Typography variant="h4" fontWeight="bold" gutterBottom>
                      Online Bankacılık
                    </Typography>
                    <Typography variant="body1" sx={{ mb: 2 }}>
                      Modern bankacılık deneyimi için doğru yerdesiniz.
                      7/24 güvenli işlemler, kolay para transferleri ve daha fazlası.
                    </Typography>
                  </motion.div>
                </ImageBox>
              </Box>
            </Stack>
          </StyledPaper>
        </motion.div>
      </Container>
    </GradientBackground>
  );
};

export default Login; 