import React from 'react';
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
  Stack,
  Paper,
  Link,
  InputAdornment,
  Stepper,
  Step,
  StepLabel,
  useTheme,
} from '@mui/material';
import {
  PersonOutline,
  EmailOutlined,
  LockOutlined,
  PhoneOutlined,
  HomeOutlined,
} from '@mui/icons-material';
import { motion } from 'framer-motion';
import styled from 'styled-components';
import { RegisterRequest } from '../interfaces/auth';
import { useSnackbar } from 'notistack';

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

const RegisterBox = styled(Box)`
  padding: 40px;
  
  @media (max-width: 600px) {
    padding: 20px;
  }
`;

const ImageBox = styled(Box)`
  background-image: url('/images/banking-register.jpg');
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
  firstName: Yup.string()
    .min(2, 'İsim en az 2 karakter olmalıdır')
    .max(50, 'İsim en fazla 50 karakter olabilir')
    .required('İsim gereklidir'),
  lastName: Yup.string()
    .min(2, 'Soyisim en az 2 karakter olmalıdır')
    .max(50, 'Soyisim en fazla 50 karakter olabilir')
    .required('Soyisim gereklidir'),
  email: Yup.string()
    .email('Geçerli bir e-posta adresi girin')
    .required('E-posta adresi gereklidir'),
  password: Yup.string()
    .min(6, 'Şifre en az 6 karakter olmalıdır')
    .matches(/[a-z]/, 'Şifrede en az bir küçük harf olmalıdır')
    .matches(/[A-Z]/, 'Şifrede en az bir büyük harf olmalıdır')
    .matches(/[0-9]/, 'Şifrede en az bir rakam olmalıdır')
    .required('Şifre gereklidir'),
  passwordConfirm: Yup.string()
    .oneOf([Yup.ref('password')], 'Şifreler eşleşmiyor')
    .required('Şifre onayı gereklidir'),
  phoneNumber: Yup.string()
    .matches(/^[0-9]{10,11}$/, 'Telefon numarası 10-11 rakamdan oluşmalıdır')
    .required('Telefon numarası gereklidir'),
  address: Yup.string()
    .max(255, 'Adres en fazla 255 karakter olabilir')
    .required('Adres gereklidir'),
});

// Kayıt adımları
const steps = ['Kişisel Bilgiler', 'Hesap Bilgileri', 'İletişim Bilgileri'];

const Register: React.FC = () => {
  const { register } = useAuth();
  // ESLint uyarısını önlemek için kullanılmayan navigate değişkenini yoruma alıyoruz
  // const navigate = useNavigate();
  const theme = useTheme();
  const [activeStep, setActiveStep] = React.useState(0);
  const { enqueueSnackbar } = useSnackbar();

  // Form gönderimi
  const handleSubmit = async (values: any) => {
    try {
      // passwordConfirm alanını backende göndermiyoruz
      const { passwordConfirm, ...registerData } = values;
      
      // RegisterRequest tipine uygun veri oluştur
      const registerRequest: RegisterRequest = {
        firstName: registerData.firstName.trim(),
        lastName: registerData.lastName.trim(),
        email: registerData.email.trim(),
        password: registerData.password,
        phoneNumber: registerData.phoneNumber.trim(),
        address: registerData.address.trim()
        // roles alanını tamamen kaldırıyoruz, backend otomatik olarak ROLE_USER ekleyecek
      };
      
      // Tüm alanların dolu olduğunu kontrol et
      const missingFields = Object.entries(registerRequest)
        .filter(([key, value]) => !value)
        .map(([key]) => key);
      
      if (missingFields.length > 0) {
        console.error("Eksik alanlar:", missingFields);
        enqueueSnackbar(`Lütfen tüm alanları doldurun: ${missingFields.join(', ')}`, { variant: 'error' });
        return;
      }
      
      console.log("Kayıt verileri:", JSON.stringify(registerRequest, null, 2));
      await register(registerRequest);
    } catch (error) {
      console.error("Form submit hatası:", error);
    }
  };

  // Sonraki adıma geç
  const handleNext = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  // Önceki adıma dön
  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
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
              {/* Sol taraf (kayıt formu) */}
              <Box sx={{ flex: { xs: '1', md: '2' } }}>
                <RegisterBox>
                  <motion.div variants={itemVariants}>
                    <Typography 
                      variant="h4" 
                      color="primary" 
                      gutterBottom 
                      fontWeight="bold"
                      sx={{ mb: 2 }}
                    >
                      Yeni Hesap Oluşturun
                    </Typography>
                  </motion.div>

                  <motion.div variants={itemVariants} style={{ marginBottom: '20px' }}>
                    <Stepper activeStep={activeStep} alternativeLabel>
                      {steps.map((label) => (
                        <Step key={label}>
                          <StepLabel>{label}</StepLabel>
                        </Step>
                      ))}
                    </Stepper>
                  </motion.div>

                  <Formik
                    initialValues={{
                      firstName: '',
                      lastName: '',
                      email: '',
                      password: '',
                      passwordConfirm: '',
                      phoneNumber: '',
                      address: '',
                    }}
                    validationSchema={validationSchema}
                    onSubmit={handleSubmit}
                  >
                    {({ values, errors, touched, isValid, isSubmitting, handleChange, handleBlur }) => (
                      <Form>
                        {activeStep === 0 && (
                          <motion.div 
                            variants={itemVariants}
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            exit={{ opacity: 0 }}
                            transition={{ duration: 0.5 }}
                          >
                            <FormInput
                              name="firstName"
                              label="İsim"
                              placeholder="İsminizi girin"
                              InputProps={{
                                startAdornment: (
                                  <InputAdornment position="start">
                                    <PersonOutline color="primary" />
                                  </InputAdornment>
                                ),
                              }}
                            />
                            
                            <FormInput
                              name="lastName"
                              label="Soyisim"
                              placeholder="Soyisminizi girin"
                              InputProps={{
                                startAdornment: (
                                  <InputAdornment position="start">
                                    <PersonOutline color="primary" />
                                  </InputAdornment>
                                ),
                              }}
                            />
                          </motion.div>
                        )}

                        {activeStep === 1 && (
                          <motion.div 
                            variants={itemVariants}
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            exit={{ opacity: 0 }}
                            transition={{ duration: 0.5 }}
                          >
                            <FormInput
                              name="email"
                              label="E-posta Adresi"
                              type="email"
                              placeholder="E-posta adresinizi girin"
                              InputProps={{
                                startAdornment: (
                                  <InputAdornment position="start">
                                    <EmailOutlined color="primary" />
                                  </InputAdornment>
                                ),
                              }}
                            />
                            
                            <FormInput
                              name="password"
                              label="Şifre"
                              type="password"
                              placeholder="Şifrenizi girin"
                              InputProps={{
                                startAdornment: (
                                  <InputAdornment position="start">
                                    <LockOutlined color="primary" />
                                  </InputAdornment>
                                ),
                              }}
                            />
                            
                            <FormInput
                              name="passwordConfirm"
                              label="Şifre Tekrar"
                              type="password"
                              placeholder="Şifrenizi tekrar girin"
                              InputProps={{
                                startAdornment: (
                                  <InputAdornment position="start">
                                    <LockOutlined color="primary" />
                                  </InputAdornment>
                                ),
                              }}
                            />
                          </motion.div>
                        )}

                        {activeStep === 2 && (
                          <motion.div 
                            variants={itemVariants}
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            exit={{ opacity: 0 }}
                            transition={{ duration: 0.5 }}
                          >
                            <FormInput
                              name="phoneNumber"
                              label="Telefon Numarası"
                              placeholder="Telefon numaranızı girin (5XX...)"
                              InputProps={{
                                startAdornment: (
                                  <InputAdornment position="start">
                                    <PhoneOutlined color="primary" />
                                  </InputAdornment>
                                ),
                              }}
                            />
                            
                            <FormInput
                              name="address"
                              label="Adres"
                              placeholder="Adresinizi girin"
                              multiline
                              rows={4}
                              InputProps={{
                                startAdornment: (
                                  <InputAdornment position="start">
                                    <HomeOutlined color="primary" />
                                  </InputAdornment>
                                ),
                              }}
                            />
                          </motion.div>
                        )}

                        <motion.div 
                          variants={itemVariants} 
                          style={{ 
                            display: 'flex', 
                            justifyContent: 'space-between', 
                            marginTop: '20px' 
                          }}
                        >
                          <Button
                            disabled={activeStep === 0}
                            onClick={handleBack}
                            variant="outlined"
                            color="primary"
                            sx={{ mr: 1 }}
                          >
                            Geri
                          </Button>
                          
                          <Box sx={{ flex: '1 1 auto' }} />
                          
                          {activeStep === steps.length - 1 ? (
                            <Button
                              type="submit"
                              variant="contained"
                              color="primary"
                              disabled={isSubmitting}
                              sx={{
                                py: 1.2,
                                px: 4,
                                fontWeight: 'bold',
                                boxShadow: theme.shadows[4],
                                '&:hover': {
                                  boxShadow: theme.shadows[8],
                                },
                              }}
                            >
                              {isSubmitting ? 'Kaydediliyor...' : 'Kaydol'}
                            </Button>
                          ) : (
                            <Button
                              variant="contained"
                              color="primary"
                              onClick={handleNext}
                              sx={{
                                py: 1.2,
                                px: 4,
                                fontWeight: 'bold',
                                boxShadow: theme.shadows[4],
                                '&:hover': {
                                  boxShadow: theme.shadows[8],
                                },
                              }}
                            >
                              İleri
                            </Button>
                          )}
                        </motion.div>
                      </Form>
                    )}
                  </Formik>

                  <motion.div variants={itemVariants} style={{ textAlign: 'center', marginTop: '20px' }}>
                    <Typography variant="body1" color="textSecondary">
                      Zaten bir hesabınız var mı?{' '}
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
                        Giriş Yapın
                      </Link>
                    </Typography>
                  </motion.div>
                </RegisterBox>
              </Box>

              {/* Sağ taraf (görsel) */}
              <Box 
                sx={{ 
                  flex: { xs: '1', md: '1' }, 
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
                      Bankacılık Deneyimi
                    </Typography>
                    <Typography variant="body1" sx={{ mb: 2 }}>
                      Modern bankacılık dünyasına adım atın.
                      Hesabınızı oluşturun ve tüm bankacılık işlemlerinizi kolayca yönetin.
                    </Typography>
                    <Typography variant="body2" sx={{ opacity: 0.8 }}>
                      Güvenli, hızlı ve kullanıcı dostu bir deneyim sizi bekliyor.
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

export default Register;