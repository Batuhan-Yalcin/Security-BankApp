import React, { useState, useEffect } from 'react';
import { Box, Container, Typography, Paper, Button, Divider, useTheme, Avatar, CircularProgress } from '@mui/material';
import { styled } from '@mui/material/styles';
import { motion } from 'framer-motion';
import { 
  BarChart, Bar, LineChart, Line, PieChart, Pie, AreaChart, Area,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
  Cell
} from 'recharts';
import { 
  AccountBalance, 
  TrendingUp, 
  CreditCard, 
  ArrowUpward, 
  ArrowDownward,
  MonetizationOn,
  Receipt,
  CallMade,
  CallReceived,
} from '@mui/icons-material';

import { useAuth } from '../contexts/AuthContext';
import Navigation from '../components/Navigation';
import axiosInstance from '../services/axiosConfig';

// Animasyonlu kart bileşeni
const AnimatedCard = styled(motion(Paper))(({ theme }) => ({
  padding: theme.spacing(3),
  borderRadius: 16,
  boxShadow: '0 8px 16px rgba(0, 0, 0, 0.05)',
  height: '100%',
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'space-between',
  transition: 'transform 0.3s ease, box-shadow 0.3s ease',
  '&:hover': {
    boxShadow: '0 12px 24px rgba(0, 0, 0, 0.1)',
    transform: 'translateY(-4px)'
  }
}));

// İşlem butonu bileşeni
const ActionButton = styled(Button)(({ theme }) => ({
  padding: theme.spacing(1.5, 2),
  borderRadius: 12,
  textTransform: 'none',
  fontWeight: 600,
  boxShadow: '0 4px 8px rgba(0, 0, 0, 0.05)',
}));

// Grid bileşenleri
const GridContainer = styled(Box)(({ theme }) => ({
  display: 'grid',
  gap: theme.spacing(3),
  marginBottom: theme.spacing(4),
}));

const CardGrid = styled(GridContainer)(({ theme }) => ({
  gridTemplateColumns: 'repeat(1, 1fr)',
  [theme.breakpoints.up('md')]: {
    gridTemplateColumns: 'repeat(3, 1fr)',
  },
}));

const ActionGrid = styled(GridContainer)(({ theme }) => ({
  gridTemplateColumns: 'repeat(2, 1fr)',
  gap: theme.spacing(2),
  [theme.breakpoints.up('sm')]: {
    gridTemplateColumns: 'repeat(4, 1fr)',
  },
}));

const ChartsGrid = styled(GridContainer)(({ theme }) => ({
  gridTemplateColumns: 'repeat(1, 1fr)',
  [theme.breakpoints.up('md')]: {
    gridTemplateColumns: 'repeat(2, 1fr)',
  },
}));

// Veri tipi tanımlamaları
interface AccountBalance {
  total: number;
  change: number;
  accounts: { 
    id: number; 
    name: string; 
    balance: number; 
    currency: string 
  }[];
}

interface Transaction {
  id: number;
  description: string;
  amount: number;
  date: string;
  category: string;
  merchant: string;
}

interface MonthlyExpense {
  name: string;
  gelir: number;
  gider: number;
}

interface CategoryDistribution {
  name: string;
  value: number;
  color: string;
}

interface SavingsGoal {
  name: string;
  miktar: number;
}

interface BalanceHistory {
  tarih: string;
  bakiye: number;
}

const Dashboard: React.FC = () => {
  const theme = useTheme();
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [dataLoaded, setDataLoaded] = useState({
    balance: false,
    transactions: false,
    monthlyExpenses: false,
    categoryDistribution: false,
    savingsGoal: false,
    balanceHistory: false
  });
  
  // Durum değişkenleri
  const [accountBalance, setAccountBalance] = useState<AccountBalance>({
    total: 0,
    change: 0,
    accounts: []
  });
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [monthlyExpenses, setMonthlyExpenses] = useState<MonthlyExpense[]>([]);
  const [categoryDistribution, setCategoryDistribution] = useState<CategoryDistribution[]>([]);
  const [savingsGoal, setSavingsGoal] = useState<SavingsGoal[]>([]);
  const [balanceHistory, setBalanceHistory] = useState<BalanceHistory[]>([]);

  // Sayfa yüklenirken API çağrılarını yap
  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      console.log("Dashboard verilerini yüklemeye çalışıyoruz...");

      try {
        // Token kontrolü
        const token = localStorage.getItem('accessToken');
        if (!token) {
          console.error("Kimlik doğrulama token'ı bulunamadı!");
          setLoading(false);
          return;
        }

        // Kullanıcı kontrolü
        if (!user || !user.id) {
          console.error("Kullanıcı bilgileri bulunamadı!");
          setLoading(false);
          return;
        }

        // Bakiye bilgilerini getir
        try {
          const balanceResponse = await axiosInstance.get('/accounts/balance');
          if (balanceResponse.data.success && balanceResponse.data.data) {
            setAccountBalance(balanceResponse.data.data);
            setDataLoaded(prev => ({ ...prev, balance: true }));
            console.log("Bakiye bilgileri başarıyla alındı:", balanceResponse.data.data);
          }
        } catch (error) {
          console.error("Bakiye bilgileri alınırken hata oluştu:", error);
        }

        // Kullanıcının hesaplarını ve işlemlerini getir
        try {
          // Kullanıcının hesaplarını getir
          const accountsResponse = await axiosInstance.get(`/accounts/customer/${user.id}`);
          
          if (accountsResponse.data.success && accountsResponse.data.data && accountsResponse.data.data.length > 0) {
            const accounts = accountsResponse.data.data;
            
            // Hesaplardan birinin işlemlerini getir
            if (accounts.length > 0 && accounts[0].accountNumber) {
              const transactionsResponse = await axiosInstance.get(`/transactions/account/${accounts[0].accountNumber}`, {
                params: { page: 0, size: 5 } // İlk 5 işlemi getir
              });
              
              if (transactionsResponse.data.success && transactionsResponse.data.data) {
                const transactionData = transactionsResponse.data.data.map((item: any) => ({
                  id: item.id || Math.random(),
                  description: item.description || 'İşlem',
                  amount: item.amount || 0,
                  date: item.transactionDate || new Date().toISOString(),
                  category: item.type || 'TRANSFER',
                  merchant: item.merchant || 'Bilinmeyen'
                }));
                setTransactions(transactionData);
                setDataLoaded(prev => ({ ...prev, transactions: true }));
                console.log("İşlemler başarıyla alındı:", transactionData);
              }
            }
          }
        } catch (error) {
          console.error("İşlemler alınırken hata oluştu:", error);
        }
      } catch (mainError) {
        console.error("Dashboard verilerini getirirken genel hata oluştu:", mainError);
      } finally {
        // Veri yükleme tamamlandı
        setLoading(false);
      }
    };

    if (user && localStorage.getItem('accessToken')) {
      fetchData();
    } else {
      setLoading(false);
    }
  }, [user]);

  // Kart Animasyon Varyantları
  const cardVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: (custom: number) => ({
      opacity: 1,
      y: 0,
      transition: {
        delay: custom * 0.1,
        duration: 0.5,
        ease: "easeOut"
      }
    })
  };

  // Para transferi işlemi
  const handleTransfer = async () => {
    try {
      console.log('Para transferi işlemi başlatıldı');
      // Transfer işlemini gerçekleştirecek kodlar buraya eklenecek
    } catch (error) {
      console.error('Transfer işlemi sırasında hata:', error);
    }
  };

  // Fatura ödeme işlemi
  const handlePayBill = async () => {
    try {
      console.log('Fatura ödeme işlemi başlatıldı');
      // Fatura ödeme işlemini gerçekleştirecek kodlar buraya eklenecek
    } catch (error) {
      console.error('Fatura ödeme işlemi sırasında hata:', error);
    }
  };

  // Para yatırma işlemi
  const handleDeposit = async () => {
    try {
      console.log('Para yatırma işlemi başlatıldı');
      // Para yatırma işlemini gerçekleştirecek kodlar buraya eklenecek
    } catch (error) {
      console.error('Para yatırma işlemi sırasında hata:', error);
    }
  };

  // Para çekme işlemi
  const handleWithdraw = async () => {
    try {
      console.log('Para çekme işlemi başlatıldı');
      // Para çekme işlemini gerçekleştirecek kodlar buraya eklenecek
    } catch (error) {
      console.error('Para çekme işlemi sırasında hata:', error);
    }
  };

  if (loading) {
    return (
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '100vh'
        }}
      >
        <motion.div
          animate={{
            scale: [1, 1.2, 1],
            opacity: [0.5, 1, 0.5]
          }}
          transition={{
            duration: 1.5,
            repeat: Infinity,
            repeatType: "loop"
          }}
        >
          <AccountBalance sx={{ fontSize: 60, color: theme.palette.primary.main }} />
        </motion.div>
      </Box>
    );
  }

  const NoDataMessage = () => (
    <Box sx={{ p: 3, textAlign: 'center' }}>
      <Typography variant="body1" color="text.secondary">
        Şu anda veri bulunmuyor.
      </Typography>
    </Box>
  );

  return (
    <>
      <Navigation />
      <Container maxWidth="xl" sx={{ py: 4 }}>
        {/* Hoşgeldin Mesajı */}
        <Box mb={4}>
          <motion.div
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
          >
            <Typography variant="h4" fontWeight="bold" gutterBottom>
              Merhaba, {user?.firstName || 'Değerli Müşterimiz'}!
            </Typography>
            <Typography variant="body1" color="text.secondary">
              İşte finansal bilgilerinizin güncel özeti. Harika bir gün geçirmeniz dileğiyle.
            </Typography>
          </motion.div>
        </Box>

        {/* Hesap Bakiye Özeti */}
        <CardGrid>
          <motion.div
            variants={cardVariants}
            initial="hidden"
            animate="visible"
            custom={0}
          >
            <AnimatedCard>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Typography variant="subtitle2" color="text.secondary">Toplam Varlık</Typography>
                <AccountBalance color="primary" />
              </Box>
              {dataLoaded.balance ? (
                <>
                  <Typography variant="h4" fontWeight="bold">
                    {new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(accountBalance.total)}
                  </Typography>
                  <Box sx={{ display: 'flex', alignItems: 'center', mt: 1 }}>
                    {accountBalance.change >= 0 ? (
                      <ArrowUpward fontSize="small" sx={{ color: 'success.main', mr: 0.5 }} />
                    ) : (
                      <ArrowDownward fontSize="small" sx={{ color: 'error.main', mr: 0.5 }} />
                    )}
                    <Typography 
                      variant="body2" 
                      sx={{ 
                        color: accountBalance.change >= 0 ? 'success.main' : 'error.main' 
                      }}
                    >
                      {Math.abs(accountBalance.change)}% geçen aya göre
                    </Typography>
                  </Box>
                </>
              ) : (
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100px' }}>
                  <CircularProgress size={30} />
                </Box>
              )}
            </AnimatedCard>
          </motion.div>

          <motion.div
            variants={cardVariants}
            initial="hidden"
            animate="visible"
            custom={1}
          >
            <AnimatedCard>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Typography variant="subtitle2" color="text.secondary">Aylık Gelir</Typography>
                <TrendingUp sx={{ color: 'success.main' }} />
              </Box>
              {false /* Bu veri henüz API'den gelmiyor */ ? (
                <>
                  <Typography variant="h4" fontWeight="bold">
                    {new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(monthlyExpenses.length > 0 ? monthlyExpenses[monthlyExpenses.length - 1].gelir : 0)}
                  </Typography>
                  <Box sx={{ display: 'flex', alignItems: 'center', mt: 1 }}>
                    <ArrowUpward fontSize="small" sx={{ color: 'success.main', mr: 0.5 }} />
                    <Typography variant="body2" sx={{ color: 'success.main' }}>
                      {monthlyExpenses.length >= 2 
                        ? ((monthlyExpenses[monthlyExpenses.length - 1].gelir - monthlyExpenses[monthlyExpenses.length - 2].gelir) / monthlyExpenses[monthlyExpenses.length - 2].gelir * 100).toFixed(1)
                        : 0}% geçen aya göre
                    </Typography>
                  </Box>
                </>
              ) : (
                <NoDataMessage />
              )}
            </AnimatedCard>
          </motion.div>

          <motion.div
            variants={cardVariants}
            initial="hidden"
            animate="visible"
            custom={2}
          >
            <AnimatedCard>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Typography variant="subtitle2" color="text.secondary">Aylık Harcama</Typography>
                <CreditCard sx={{ color: 'error.main' }} />
              </Box>
              {false /* Bu veri henüz API'den gelmiyor */ ? (
                <>
                  <Typography variant="h4" fontWeight="bold">
                    {new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(monthlyExpenses.length > 0 ? monthlyExpenses[monthlyExpenses.length - 1].gider : 0)}
                  </Typography>
                  <Box sx={{ display: 'flex', alignItems: 'center', mt: 1 }}>
                    <ArrowUpward fontSize="small" sx={{ color: 'error.main', mr: 0.5 }} />
                    <Typography variant="body2" sx={{ color: 'error.main' }}>
                      {monthlyExpenses.length >= 2 
                        ? ((monthlyExpenses[monthlyExpenses.length - 1].gider - monthlyExpenses[monthlyExpenses.length - 2].gider) / monthlyExpenses[monthlyExpenses.length - 2].gider * 100).toFixed(1)
                        : 0}% geçen aya göre
                    </Typography>
                  </Box>
                </>
              ) : (
                <NoDataMessage />
              )}
            </AnimatedCard>
          </motion.div>
        </CardGrid>

        {/* Hızlı İşlemler */}
        <Box mb={4}>
          <Typography variant="h6" fontWeight="bold" mb={2}>
            Hızlı İşlemler
          </Typography>
          <ActionGrid>
            <motion.div
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
            >
              <ActionButton
                variant="contained"
                fullWidth
                startIcon={<CallMade />}
                color="primary"
                onClick={handleTransfer}
              >
                Para Transferi
              </ActionButton>
            </motion.div>
            <motion.div
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
            >
              <ActionButton
                variant="contained"
                fullWidth
                startIcon={<Receipt />}
                color="secondary"
                onClick={handlePayBill}
              >
                Fatura Öde
              </ActionButton>
            </motion.div>
            <motion.div
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
            >
              <ActionButton
                variant="contained"
                fullWidth
                startIcon={<CallReceived />}
                color="success"
                onClick={handleDeposit}
              >
                Para Yatır
              </ActionButton>
            </motion.div>
            <motion.div
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
            >
              <ActionButton
                variant="contained"
                fullWidth
                startIcon={<MonetizationOn />}
                color="warning"
                onClick={handleWithdraw}
              >
                Para Çek
              </ActionButton>
            </motion.div>
          </ActionGrid>
        </Box>

        {/* Grafikler ve İşlemler */}
        <ChartsGrid>
          {/* Grafik alanları için */}
          <motion.div variants={cardVariants} initial="hidden" animate="visible" custom={3}>
            <AnimatedCard>
              <Typography variant="h6" fontWeight="bold" mb={2}>Gelir/Gider Analizi</Typography>
              <Box height={300} display="flex" alignItems="center" justifyContent="center">
                <NoDataMessage />
              </Box>
            </AnimatedCard>
          </motion.div>
          
          <motion.div variants={cardVariants} initial="hidden" animate="visible" custom={4}>
            <AnimatedCard>
              <Typography variant="h6" fontWeight="bold" mb={2}>Harcama Kategorileri</Typography>
              <Box height={300} display="flex" alignItems="center" justifyContent="center">
                <NoDataMessage />
              </Box>
            </AnimatedCard>
          </motion.div>
        </ChartsGrid>

        {/* Son İşlemler */}
        <motion.div
          variants={cardVariants}
          initial="hidden"
          animate="visible"
          custom={7}
        >
          <AnimatedCard sx={{ mb: 3 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
              <Typography variant="h6" fontWeight="bold">
                Son İşlemler
              </Typography>
              <Button variant="text" color="primary">
                Tümünü Gör
              </Button>
            </Box>
            
            {dataLoaded.transactions && transactions.length > 0 ? (
              transactions.map((transaction, index) => (
                <React.Fragment key={transaction.id}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', py: 1.5 }}>
                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                      <Avatar
                        sx={{ 
                          bgcolor: transaction.amount > 0 ? 'success.light' : 'error.light',
                          mr: 2,
                          width: 40,
                          height: 40
                        }}
                      >
                        {transaction.amount > 0 ? (
                          <ArrowUpward fontSize="small" />
                        ) : (
                          <ArrowDownward fontSize="small" />
                        )}
                      </Avatar>
                      <Box>
                        <Typography variant="body1" fontWeight={500}>
                          {transaction.description}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          {transaction.merchant} • {new Date(transaction.date).toLocaleDateString('tr-TR')}
                        </Typography>
                      </Box>
                    </Box>
                    <Typography
                      variant="body1"
                      fontWeight="bold"
                      sx={{ color: transaction.amount > 0 ? 'success.main' : 'error.main' }}
                    >
                      {transaction.amount > 0 ? '+' : ''}
                      {new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(transaction.amount)}
                    </Typography>
                  </Box>
                  {index < transactions.length - 1 && <Divider />}
                </React.Fragment>
              ))
            ) : (
              <NoDataMessage />
            )}
          </AnimatedCard>
        </motion.div>
      </Container>
    </>
  );
};

export default Dashboard; 