import React, { useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import * as Yup from 'yup';
import { Formik, Form } from 'formik';
import FormInput from '../components/ui/FormInput';
import {
  Container,
  Box,
  Typography,
  Button,
  Paper,
  Link,
  InputAdornment,
  Divider,
  useTheme,
} from '@mui/material';
import { EmailOutlined, SendOutlined, CheckCircleOutline } from '@mui/icons-material';
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
  max-width: 500px;
  width: 100%;
  margin: 0 auto;
`;

const ForgotPasswordBox = styled(Box)`
  padding: 40px;
  
  @media (max-width: 600px) {
    padding: 20px;
  }
`;

// Form doğrulama şeması
const validationSchema = Yup.object({
  email: Yup.string()
    .email('Geçerli bir e-posta adresi girin')
    .required('E-posta adresi gereklidir'),
});

const ForgotPassword: React.FC = () => {
  const { forgotPassword } = useAuth();
  const theme = useTheme();
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [submittedEmail, setSubmittedEmail] = useState('');

  // Formun gönderilmesi
  const handleSubmit = async (values: { email: string }) => {
    await forgotPassword(values.email);
    setSubmittedEmail(values.email);
    setIsSubmitted(true);
  };

  // Animasyon varyantları
  const containerVariants = {
    hidden: { opacity: 0, scale: 0.95 },
    visible: {
      opacity: 1,
      scale: 1,
      transition: { 
        duration: 0.5,
        ease: "easeOut"
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
      <Container maxWidth="sm">
        <motion.div
          initial="hidden"
          animate="visible"
          variants={containerVariants}
        >
          <StyledPaper elevation={10}>
            <ForgotPasswordBox>
              {!isSubmitted ? (
                <>
                  <motion.div variants={itemVariants}>
                    <Typography 
                      variant="h4" 
                      color="primary" 
                      gutterBottom 
                      fontWeight="bold"
                      sx={{ mb: 1 }}
                    >
                      Şifremi Unuttum
                    </Typography>
                    <Typography 
                      variant="body1" 
                      color="textSecondary" 
                      sx={{ mb: 4 }}
                    >
                      Şifrenizi sıfırlamak için kayıtlı e-posta adresinizi girin. 
                      Size şifre sıfırlama bağlantısı göndereceğiz.
                    </Typography>
                  </motion.div>

                  <Formik
                    initialValues={{ email: '' }}
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
                            type="email"
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
                          <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            color="primary"
                            size="large"
                            disabled={isSubmitting}
                            endIcon={<SendOutlined />}
                            sx={{
                              py: 1.5,
                              mt: 3,
                              mb: 2,
                              fontWeight: 'bold',
                              fontSize: '1rem',
                              boxShadow: theme.shadows[4],
                              '&:hover': {
                                boxShadow: theme.shadows[8],
                                transform: 'translateY(-2px)',
                              },
                              transition: 'all 0.3s ease',
                            }}
                          >
                            {isSubmitting ? 'Gönderiliyor...' : 'Sıfırlama Bağlantısı Gönder'}
                          </Button>
                        </motion.div>
                      </Form>
                    )}
                  </Formik>
                </>
              ) : (
                <motion.div 
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ duration: 0.5 }}
                >
                  <Box sx={{ textAlign: 'center', py: 3 }}>
                    <CheckCircleOutline sx={{ fontSize: 70, color: 'success.main', mb: 2 }} />
                    <Typography 
                      variant="h5" 
                      color="primary" 
                      gutterBottom 
                      fontWeight="bold"
                    >
                      Bağlantı Gönderildi
                    </Typography>
                    <Typography 
                      variant="body1" 
                      color="textSecondary" 
                      sx={{ mb: 3 }}
                    >
                      <strong>{submittedEmail}</strong> adresine şifre sıfırlama bağlantısı gönderdik. 
                      Lütfen e-posta kutunuzu (ve spam klasörünüzü) kontrol edin.
                    </Typography>
                    <Button
                      component={RouterLink}
                      to="/login"
                      variant="outlined"
                      color="primary"
                      sx={{ 
                        fontWeight: 'medium',
                        mt: 2 
                      }}
                    >
                      Giriş Sayfasına Dön
                    </Button>
                  </Box>
                </motion.div>
              )}

              <Divider sx={{ my: 3 }} />

              <motion.div variants={itemVariants} style={{ textAlign: 'center' }}>
                <Typography variant="body2" color="textSecondary">
                  Giriş yapmak mı istiyorsunuz?{' '}
                  <Link
                    component={RouterLink}
                    to="/login"
                    color="primary"
                    sx={{ 
                      fontWeight: 'bold',
                      textDecoration: 'none',
                      '&:hover': { textDecoration: 'underline' }
                    }}
                  >
                    Giriş Sayfasına Dön
                  </Link>
                </Typography>
                <Typography variant="body2" color="textSecondary" sx={{ mt: 1 }}>
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
            </ForgotPasswordBox>
          </StyledPaper>
        </motion.div>
      </Container>
    </GradientBackground>
  );
};

export default ForgotPassword; 