import { createTheme } from '@mui/material/styles';

// Türkiye'nin finansal kurumlarına uygun renk paletleri
const colors = {
  primary: {
    main: '#1D3557', // Koyu mavi
    light: '#457B9D',
    dark: '#0C1B2C',
    contrastText: '#FFFFFF',
  },
  secondary: {
    main: '#E63946', // Kırmızı aksan
    light: '#FF616D',
    dark: '#B02A33',
    contrastText: '#FFFFFF',
  },
  success: {
    main: '#2A9D8F',
    light: '#57C9BB',
    dark: '#1E726A',
  },
  background: {
    default: '#F5F9FF',
    paper: '#FFFFFF',
    dark: '#EEF5FF',
  },
  text: {
    primary: '#1D3557',
    secondary: '#5F7696',
    disabled: '#A2AABD',
  },
  grey: {
    50: '#F8FAFC',
    100: '#F1F5F9',
    200: '#E2E8F0',
    300: '#CBD5E1',
    400: '#94A3B8',
    500: '#64748B',
    600: '#475569',
    700: '#334155',
    800: '#1E293B',
    900: '#0F172A',
  },
};

// MUI temalarının shadows dizisi 25 eleman bekler
const shadowArray = [
  'none',
  '0px 2px 4px rgba(29, 53, 87, 0.05)',
  '0px 4px 8px rgba(29, 53, 87, 0.08)',
  '0px 6px 12px rgba(29, 53, 87, 0.1)',
  '0px 8px 16px rgba(29, 53, 87, 0.12)',
  '0px 10px 20px rgba(29, 53, 87, 0.14)',
  '0px 12px 24px rgba(29, 53, 87, 0.16)',
  '0px 14px 28px rgba(29, 53, 87, 0.18)',
  '0px 16px 32px rgba(29, 53, 87, 0.2)',
  '0px 18px 36px rgba(29, 53, 87, 0.22)',
  '0px 20px 40px rgba(29, 53, 87, 0.24)',
  '0px 22px 44px rgba(29, 53, 87, 0.26)',
  '0px 24px 48px rgba(29, 53, 87, 0.28)',
  '0px 26px 52px rgba(29, 53, 87, 0.3)',
  '0px 28px 56px rgba(29, 53, 87, 0.32)',
  '0px 30px 60px rgba(29, 53, 87, 0.34)',
  '0px 32px 64px rgba(29, 53, 87, 0.36)',
  '0px 34px 68px rgba(29, 53, 87, 0.38)',
  '0px 36px 72px rgba(29, 53, 87, 0.4)',
  '0px 38px 76px rgba(29, 53, 87, 0.42)',
  '0px 40px 80px rgba(29, 53, 87, 0.44)',
  '0px 42px 84px rgba(29, 53, 87, 0.46)',
  '0px 44px 88px rgba(29, 53, 87, 0.48)',
  '0px 46px 92px rgba(29, 53, 87, 0.5)',
  '0px 48px 96px rgba(29, 53, 87, 0.52)', // Son element eklendi
] as const;

const theme = createTheme({
  palette: {
    primary: colors.primary,
    secondary: colors.secondary,
    success: {
      main: colors.success.main,
      light: colors.success.light,
      dark: colors.success.dark,
    },
    background: {
      default: colors.background.default,
      paper: colors.background.paper,
    },
    text: {
      primary: colors.text.primary,
      secondary: colors.text.secondary,
    },
    grey: colors.grey,
  },
  typography: {
    fontFamily: "'Poppins', 'Roboto', 'Helvetica', 'Arial', sans-serif",
    h1: {
      fontWeight: 700,
      fontSize: '2.5rem',
    },
    h2: {
      fontWeight: 600,
      fontSize: '2rem',
    },
    h3: {
      fontWeight: 600,
      fontSize: '1.75rem',
    },
    h4: {
      fontWeight: 600,
      fontSize: '1.5rem',
    },
    h5: {
      fontWeight: 500,
      fontSize: '1.25rem',
    },
    h6: {
      fontWeight: 500,
      fontSize: '1rem',
    },
    subtitle1: {
      fontWeight: 500,
      fontSize: '1rem',
    },
    body1: {
      fontSize: '1rem',
    },
    button: {
      fontWeight: 600,
      textTransform: 'none',
    },
  },
  shape: {
    borderRadius: 8,
  },
  shadows: shadowArray as any,
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          padding: '10px 24px',
          boxShadow: '0px 4px 12px rgba(29, 53, 87, 0.15)',
          transition: 'all 0.3s ease',
          '&:hover': {
            transform: 'translateY(-2px)',
            boxShadow: '0px 6px 16px rgba(29, 53, 87, 0.2)',
          },
        },
        contained: {
          '&:hover': {
            backgroundColor: colors.primary.light,
          },
        },
        containedSecondary: {
          '&:hover': {
            backgroundColor: colors.secondary.light,
          },
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          boxShadow: '0px 4px 20px rgba(29, 53, 87, 0.08)',
          borderRadius: 12,
          transition: 'transform 0.3s ease, box-shadow 0.3s ease',
          '&:hover': {
            boxShadow: '0px 8px 30px rgba(29, 53, 87, 0.12)',
          },
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            '&.Mui-focused fieldset': {
              borderColor: colors.primary.main,
              borderWidth: '2px',
            },
          },
        },
      },
    },
  },
});

export default theme; 