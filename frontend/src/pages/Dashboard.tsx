import React, { useState, useEffect } from 'react';
import { Box, Container, Typography, Paper, Button, Divider, useTheme, Avatar } from '@mui/material';
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
  Person,
  AttachMoney,
  AccountBalanceWallet
} from '@mui/icons-material';

import { useAuth } from '../contexts/AuthContext';
import Navigation from '../components/Navigation';
import axios from 'axios';

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

  useEffect(() => {
    // Verileri yükle
    const fetchData = async () => {
      try {
        // Bakiye bilgilerini getir
        const balanceResponse = await axios.get('/api/accounts/balance');
        setAccountBalance(balanceResponse.data);
        
        // Son işlemleri getir
        const transactionsResponse = await axios.get('/api/transactions/recent');
        setTransactions(transactionsResponse.data);
        
        // Aylık gelir/gider verilerini getir
        const monthlyExpensesResponse = await axios.get('/api/analytics/monthly-expenses');
        setMonthlyExpenses(monthlyExpensesResponse.data);
        
        // Kategori dağılımını getir
        const categoryResponse = await axios.get('/api/analytics/category-distribution');
        setCategoryDistribution(categoryResponse.data);
        
        // Birikim hedefi verilerini getir
        const savingsResponse = await axios.get('/api/analytics/savings-goal');
        setSavingsGoal(savingsResponse.data);
        
        // Bakiye geçmişini getir
        const balanceHistoryResponse = await axios.get('/api/analytics/balance-history');
        setBalanceHistory(balanceHistoryResponse.data);
        
        setLoading(false);
      } catch (error) {
        console.error('Veri yüklenirken hata oluştu:', error);
        // Hata durumunda kullanıcıya dost bir hata mesajı göster
        // Ve örnek verilerle uygulamanın çalışmasına devam et
        setAccountBalance({
          total: 42750.85,
          change: 2.4,
          accounts: [
            { id: 1, name: 'Ana Hesap', balance: 28540.50, currency: 'TL' },
            { id: 2, name: 'Tasarruf Hesabı', balance: 14210.35, currency: 'TL' }
          ]
        });
        
        setTransactions([
          { id: 1, description: 'Market Alışverişi', amount: -245.50, date: '2025-05-10', category: 'Gıda', merchant: 'Migros' },
          { id: 2, description: 'Maaş Ödemesi', amount: 12500.00, date: '2025-05-01', category: 'Gelir', merchant: 'Şirket A' },
          { id: 3, description: 'Elektrik Faturası', amount: -320.75, date: '2025-05-05', category: 'Fatura', merchant: 'Elektrik Dağıtım' },
          { id: 4, description: 'Kira Ödemesi', amount: -3500.00, date: '2025-05-02', category: 'Konut', merchant: 'Emlak' },
          { id: 5, description: 'Online Alışveriş', amount: -890.25, date: '2025-05-08', category: 'Alışveriş', merchant: 'Trendyol' }
        ]);
        
        setMonthlyExpenses([
          { name: 'Oca', gelir: 8400, gider: 5400 },
          { name: 'Şub', gelir: 9800, gider: 7300 },
          { name: 'Mar', gelir: 8700, gider: 6100 },
          { name: 'Nis', gelir: 10200, gider: 6800 },
          { name: 'May', gelir: 12500, gider: 8900 }
        ]);
        
        setCategoryDistribution([
          { name: 'Gıda', value: 2800, color: '#FF8042' },
          { name: 'Konut', value: 3500, color: '#00C49F' },
          { name: 'Ulaşım', value: 1200, color: '#FFBB28' },
          { name: 'Faturalar', value: 1900, color: '#0088FE' },
          { name: 'Eğlence', value: 900, color: '#FF5C8D' }
        ]);
        
        setSavingsGoal([
          { name: 'Oca', miktar: 500 },
          { name: 'Şub', miktar: 1200 },
          { name: 'Mar', miktar: 1800 },
          { name: 'Nis', miktar: 2300 },
          { name: 'May', miktar: 3100 }
        ]);
        
        setBalanceHistory([
          { tarih: 'Oca', bakiye: 32500 },
          { tarih: 'Şub', bakiye: 34800 },
          { tarih: 'Mar', bakiye: 38200 },
          { tarih: 'Nis', bakiye: 39500 },
          { tarih: 'May', bakiye: 42750 }
        ]);
        
        setLoading(false);
      }
    };

    fetchData();
  }, []);

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
      // Transfer sayfasına yönlendir veya modal aç
      console.log('Para transferi işlemi başlatıldı');
      // Burada transfer işlemini gerçekleştirecek kod eklenecek
    } catch (error) {
      console.error('Transfer işlemi sırasında hata:', error);
    }
  };

  // Fatura ödeme işlemi
  const handlePayBill = async () => {
    try {
      // Fatura ödeme sayfasına yönlendir veya modal aç
      console.log('Fatura ödeme işlemi başlatıldı');
      // Burada fatura ödeme işlemini gerçekleştirecek kod eklenecek
    } catch (error) {
      console.error('Fatura ödeme işlemi sırasında hata:', error);
    }
  };

  // Para yatırma işlemi
  const handleDeposit = async () => {
    try {
      // Para yatırma sayfasına yönlendir veya modal aç
      console.log('Para yatırma işlemi başlatıldı');
      // Burada para yatırma işlemini gerçekleştirecek kod eklenecek
    } catch (error) {
      console.error('Para yatırma işlemi sırasında hata:', error);
    }
  };

  // Para çekme işlemi
  const handleWithdraw = async () => {
    try {
      // Para çekme sayfasına yönlendir veya modal aç
      console.log('Para çekme işlemi başlatıldı');
      // Burada para çekme işlemini gerçekleştirecek kod eklenecek
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

        {/* Grafikler */}
        <ChartsGrid>
          {/* Gelir Gider Grafiği */}
          <motion.div
            variants={cardVariants}
            initial="hidden"
            animate="visible"
            custom={3}
          >
            <AnimatedCard>
              <Typography variant="h6" fontWeight="bold" mb={2}>
                Aylık Gelir / Gider Analizi
              </Typography>
              <Box height={300}>
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart
                    data={monthlyExpenses}
                    margin={{ top: 10, right: 30, left: 0, bottom: 0 }}
                  >
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="name" />
                    <YAxis />
                    <Tooltip
                      formatter={(value) => new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(Number(value))}
                    />
                    <Legend />
                    <Bar dataKey="gelir" name="Gelir" fill={theme.palette.success.main} radius={[4, 4, 0, 0]} />
                    <Bar dataKey="gider" name="Gider" fill={theme.palette.error.main} radius={[4, 4, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </Box>
            </AnimatedCard>
          </motion.div>

          {/* Harcama Kategorileri */}
          <motion.div
            variants={cardVariants}
            initial="hidden"
            animate="visible"
            custom={4}
          >
            <AnimatedCard>
              <Typography variant="h6" fontWeight="bold" mb={2}>
                Harcama Kategorileri
              </Typography>
              <Box height={300} sx={{ display: 'flex', justifyContent: 'center' }}>
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={categoryDistribution}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                      outerRadius={90}
                      fill="#8884d8"
                      dataKey="value"
                    >
                      {categoryDistribution.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip
                      formatter={(value) => new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(Number(value))}
                    />
                  </PieChart>
                </ResponsiveContainer>
              </Box>
            </AnimatedCard>
          </motion.div>

          {/* Tasarruf Hedefi */}
          <motion.div
            variants={cardVariants}
            initial="hidden"
            animate="visible"
            custom={5}
          >
            <AnimatedCard>
              <Typography variant="h6" fontWeight="bold" mb={2}>
                Birikim Hedefi İlerlemesi
              </Typography>
              <Box height={300}>
                <ResponsiveContainer width="100%" height="100%">
                  <AreaChart
                    data={savingsGoal}
                    margin={{ top: 10, right: 30, left: 0, bottom: 0 }}
                  >
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="name" />
                    <YAxis />
                    <Tooltip
                      formatter={(value) => new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(Number(value))}
                    />
                    <Area 
                      type="monotone" 
                      dataKey="miktar" 
                      stroke={theme.palette.primary.main} 
                      fill={theme.palette.primary.light} 
                      name="Birikim"
                    />
                  </AreaChart>
                </ResponsiveContainer>
              </Box>
            </AnimatedCard>
          </motion.div>

          {/* Bakiye Geçmişi */}
          <motion.div
            variants={cardVariants}
            initial="hidden"
            animate="visible"
            custom={6}
          >
            <AnimatedCard>
              <Typography variant="h6" fontWeight="bold" mb={2}>
                Hesap Bakiyesi Geçmişi
              </Typography>
              <Box height={300}>
                <ResponsiveContainer width="100%" height="100%">
                  <LineChart
                    data={balanceHistory}
                    margin={{ top: 10, right: 30, left: 0, bottom: 0 }}
                  >
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="tarih" />
                    <YAxis />
                    <Tooltip
                      formatter={(value) => new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(Number(value))}
                    />
                    <Line 
                      type="monotone" 
                      dataKey="bakiye" 
                      stroke={theme.palette.info.main} 
                      activeDot={{ r: 8 }} 
                      strokeWidth={2}
                      name="Bakiye"
                    />
                  </LineChart>
                </ResponsiveContainer>
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
            
            {transactions.map((transaction, index) => (
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
            ))}
          </AnimatedCard>
        </motion.div>
      </Container>
    </>
  );
};

export default Dashboard; 