import React, { useState, useEffect } from 'react';
import { Box, Container, Typography, Paper, Button, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Dialog, DialogActions, DialogContent, DialogTitle, IconButton, Tab, Tabs, CircularProgress } from '@mui/material';
import { Delete, Edit, PersonAdd, AccountBalance } from '@mui/icons-material';
import Navigation from '../components/Navigation';
import { useAuth } from '../contexts/AuthContext';
import axiosInstance from '../services/axiosConfig';

// İnterface tanımları
interface Customer {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  address: string;
  roles: string[];
}

interface Account {
  id: number;
  accountNumber: string;
  accountType: string;
  balance: number;
  currency: string;
  customerId: number;
  customerName?: string;
}

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

// TabPanel bileşeni
function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`admin-tabpanel-${index}`}
      aria-labelledby={`admin-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box sx={{ p: 3 }}>
          {children}
        </Box>
      )}
    </div>
  );
}

const AdminPanel: React.FC = () => {
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [tabValue, setTabValue] = useState(0);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [selectedCustomer, setSelectedCustomer] = useState<Customer | null>(null);
  const [selectedAccount, setSelectedAccount] = useState<Account | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [accountDialogOpen, setAccountDialogOpen] = useState(false);
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    phoneNumber: '',
    address: '',
    isAdmin: false
  });
  const [accountFormData, setAccountFormData] = useState({
    accountNumber: '',
    accountType: 'CHECKING',
    balance: 0,
    currency: 'TL',
    customerId: 0
  });

  // Tab değiştirme işleyicisi
  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  // Verileri yükle
  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      if (!user || !user.roles || !user.roles.includes('ROLE_ADMIN')) {
        setLoading(false);
        return;
      }

      try {
        // Müşterileri getir
        const customersResponse = await axiosInstance.get('/admin/customers');
        if (customersResponse.data.success) {
          setCustomers(customersResponse.data.data);
        }

        // Hesapları getir
        const accountsResponse = await axiosInstance.get('/admin/accounts');
        if (accountsResponse.data.success) {
          setAccounts(accountsResponse.data.data);
        }
      } catch (error) {
        console.error('Admin verilerini yüklerken hata:', error);
        // Hata durumunda örnek veriler
        setCustomers([
          {
            id: 1,
            firstName: 'Admin',
            lastName: 'Kullanıcı',
            email: 'admin@bankapp.com',
            phoneNumber: '555-123-4567',
            address: 'Admin Adresi',
            roles: ['ROLE_ADMIN', 'ROLE_USER']
          },
          {
            id: 2,
            firstName: 'Test',
            lastName: 'Kullanıcı',
            email: 'user@bankapp.com',
            phoneNumber: '555-987-6543',
            address: 'Kullanıcı Adresi',
            roles: ['ROLE_USER']
          }
        ]);
        
        setAccounts([
          {
            id: 1,
            accountNumber: '1001001',
            accountType: 'CHECKING',
            balance: 28540.50,
            currency: 'TL',
            customerId: 1,
            customerName: 'Admin Kullanıcı'
          },
          {
            id: 2,
            accountNumber: '1001002',
            accountType: 'SAVINGS',
            balance: 14210.35,
            currency: 'TL',
            customerId: 1,
            customerName: 'Admin Kullanıcı'
          },
          {
            id: 3,
            accountNumber: '2001001',
            accountType: 'CHECKING',
            balance: 5000.00,
            currency: 'TL',
            customerId: 2,
            customerName: 'Test Kullanıcı'
          }
        ]);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [user]);

  // Form değişiklikleri için işleyici
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : value
    });
  };

  // Hesap formu değişiklikleri için işleyici
  const handleAccountInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setAccountFormData({
      ...accountFormData,
      [name]: name === 'balance' ? parseFloat(value) : value
    });
  };

  // Müşteri kaydetme işlevi
  const handleSaveCustomer = async () => {
    try {
      if (selectedCustomer) {
        // Güncelleme
        const response = await axiosInstance.put(`/admin/customers/${selectedCustomer.id}`, {
          ...formData,
          roles: formData.isAdmin ? ['ROLE_ADMIN', 'ROLE_USER'] : ['ROLE_USER']
        });
        
        if (response.data.success) {
          setCustomers(customers.map(c => c.id === selectedCustomer.id ? response.data.data : c));
        }
      } else {
        // Yeni müşteri oluşturma
        const response = await axiosInstance.post('/admin/customers', {
          ...formData,
          roles: formData.isAdmin ? ['ROLE_ADMIN', 'ROLE_USER'] : ['ROLE_USER']
        });
        
        if (response.data.success) {
          setCustomers([...customers, response.data.data]);
        }
      }
      setDialogOpen(false);
    } catch (error) {
      console.error('Müşteri kaydı sırasında hata:', error);
      alert('Müşteri kaydedilemedi. Lütfen tekrar deneyin.');
    }
  };

  // Hesap kaydetme işlevi
  const handleSaveAccount = async () => {
    try {
      if (selectedAccount) {
        // Güncelleme
        const response = await axiosInstance.put(`/admin/accounts/${selectedAccount.id}`, accountFormData);
        
        if (response.data.success) {
          setAccounts(accounts.map(a => a.id === selectedAccount.id ? response.data.data : a));
        }
      } else {
        // Yeni hesap oluşturma
        const response = await axiosInstance.post('/admin/accounts', accountFormData);
        
        if (response.data.success) {
          setAccounts([...accounts, response.data.data]);
        }
      }
      setAccountDialogOpen(false);
    } catch (error) {
      console.error('Hesap kaydı sırasında hata:', error);
      alert('Hesap kaydedilemedi. Lütfen tekrar deneyin.');
    }
  };

  // Müşteri düzenleme
  const handleEditCustomer = (customer: Customer) => {
    setSelectedCustomer(customer);
    setFormData({
      firstName: customer.firstName,
      lastName: customer.lastName,
      email: customer.email,
      password: '', // Şifre alanını boş bırak
      phoneNumber: customer.phoneNumber,
      address: customer.address,
      isAdmin: customer.roles.includes('ROLE_ADMIN')
    });
    setDialogOpen(true);
  };

  // Hesap düzenleme
  const handleEditAccount = (account: Account) => {
    setSelectedAccount(account);
    setAccountFormData({
      accountNumber: account.accountNumber,
      accountType: account.accountType,
      balance: account.balance,
      currency: account.currency,
      customerId: account.customerId
    });
    setAccountDialogOpen(true);
  };

  // Yeni müşteri
  const handleNewCustomer = () => {
    setSelectedCustomer(null);
    setFormData({
      firstName: '',
      lastName: '',
      email: '',
      password: '',
      phoneNumber: '',
      address: '',
      isAdmin: false
    });
    setDialogOpen(true);
  };

  // Yeni hesap
  const handleNewAccount = () => {
    setSelectedAccount(null);
    setAccountFormData({
      accountNumber: '',
      accountType: 'CHECKING',
      balance: 0,
      currency: 'TL',
      customerId: customers.length > 0 ? customers[0].id : 0
    });
    setAccountDialogOpen(true);
  };

  // Müşteri silme
  const handleDeleteCustomer = async (id: number) => {
    if (window.confirm('Müşteriyi silmek istediğinizden emin misiniz?')) {
      try {
        const response = await axiosInstance.delete(`/admin/customers/${id}`);
        if (response.data.success) {
          setCustomers(customers.filter(c => c.id !== id));
        }
      } catch (error) {
        console.error('Müşteri silme sırasında hata:', error);
        alert('Müşteri silinemedi. Lütfen tekrar deneyin.');
      }
    }
  };

  // Hesap silme
  const handleDeleteAccount = async (id: number) => {
    if (window.confirm('Hesabı silmek istediğinizden emin misiniz?')) {
      try {
        const response = await axiosInstance.delete(`/admin/accounts/${id}`);
        if (response.data.success) {
          setAccounts(accounts.filter(a => a.id !== id));
        }
      } catch (error) {
        console.error('Hesap silme sırasında hata:', error);
        alert('Hesap silinemedi. Lütfen tekrar deneyin.');
      }
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
        <CircularProgress />
      </Box>
    );
  }

  // Admin yetkisi kontrolü
  if (!user || !user.roles || !user.roles.includes('ROLE_ADMIN')) {
    return (
      <>
        <Navigation />
        <Container maxWidth="md" sx={{ mt: 4 }}>
          <Paper sx={{ p: 4, textAlign: 'center' }}>
            <Typography variant="h5" color="error" gutterBottom>
              Erişim Reddedildi
            </Typography>
            <Typography variant="body1">
              Bu sayfayı görüntülemek için admin yetkisine sahip olmanız gerekiyor.
            </Typography>
          </Paper>
        </Container>
      </>
    );
  }

  return (
    <>
      <Navigation />
      <Container maxWidth="xl" sx={{ mt: 4 }}>
        <Paper sx={{ p: 3, mb: 4 }}>
          <Typography variant="h4" gutterBottom>
            Admin Paneli
          </Typography>
          
          <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
            <Tabs value={tabValue} onChange={handleTabChange} aria-label="admin panel tabs">
              <Tab label="Müşteriler" id="admin-tab-0" aria-controls="admin-tabpanel-0" />
              <Tab label="Hesaplar" id="admin-tab-1" aria-controls="admin-tabpanel-1" />
            </Tabs>
          </Box>
          
          {/* Müşteriler Sekmesi */}
          <TabPanel value={tabValue} index={0}>
            <Box display="flex" justifyContent="flex-end" mb={2}>
              <Button 
                variant="contained" 
                color="primary" 
                startIcon={<PersonAdd />}
                onClick={handleNewCustomer}
              >
                Yeni Müşteri
              </Button>
            </Box>
            
            <TableContainer component={Paper}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>ID</TableCell>
                    <TableCell>Ad</TableCell>
                    <TableCell>Soyad</TableCell>
                    <TableCell>E-posta</TableCell>
                    <TableCell>Telefon</TableCell>
                    <TableCell>Roller</TableCell>
                    <TableCell>İşlemler</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {customers.map((customer) => (
                    <TableRow key={customer.id}>
                      <TableCell>{customer.id}</TableCell>
                      <TableCell>{customer.firstName}</TableCell>
                      <TableCell>{customer.lastName}</TableCell>
                      <TableCell>{customer.email}</TableCell>
                      <TableCell>{customer.phoneNumber}</TableCell>
                      <TableCell>{customer.roles.join(', ')}</TableCell>
                      <TableCell>
                        <IconButton color="primary" onClick={() => handleEditCustomer(customer)}>
                          <Edit />
                        </IconButton>
                        <IconButton color="error" onClick={() => handleDeleteCustomer(customer.id)}>
                          <Delete />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </TabPanel>
          
          {/* Hesaplar Sekmesi */}
          <TabPanel value={tabValue} index={1}>
            <Box display="flex" justifyContent="flex-end" mb={2}>
              <Button 
                variant="contained" 
                color="primary" 
                startIcon={<AccountBalance />}
                onClick={handleNewAccount}
              >
                Yeni Hesap
              </Button>
            </Box>
            
            <TableContainer component={Paper}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>ID</TableCell>
                    <TableCell>Hesap No</TableCell>
                    <TableCell>Tür</TableCell>
                    <TableCell>Bakiye</TableCell>
                    <TableCell>Para Birimi</TableCell>
                    <TableCell>Müşteri ID</TableCell>
                    <TableCell>Müşteri Adı</TableCell>
                    <TableCell>İşlemler</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {accounts.map((account) => (
                    <TableRow key={account.id}>
                      <TableCell>{account.id}</TableCell>
                      <TableCell>{account.accountNumber}</TableCell>
                      <TableCell>{account.accountType}</TableCell>
                      <TableCell>
                        {new Intl.NumberFormat('tr-TR', { 
                          style: 'currency', 
                          currency: account.currency 
                        }).format(account.balance)}
                      </TableCell>
                      <TableCell>{account.currency}</TableCell>
                      <TableCell>{account.customerId}</TableCell>
                      <TableCell>
                        {account.customerName || 
                          customers.find(c => c.id === account.customerId)?.firstName + ' ' + 
                          customers.find(c => c.id === account.customerId)?.lastName}
                      </TableCell>
                      <TableCell>
                        <IconButton color="primary" onClick={() => handleEditAccount(account)}>
                          <Edit />
                        </IconButton>
                        <IconButton color="error" onClick={() => handleDeleteAccount(account.id)}>
                          <Delete />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </TabPanel>
        </Paper>
      </Container>

      {/* Müşteri Ekleme/Düzenleme Dialog */}
      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{selectedCustomer ? 'Müşteriyi Düzenle' : 'Yeni Müşteri Ekle'}</DialogTitle>
        <DialogContent>
          <TextField
            margin="dense"
            name="firstName"
            label="Ad"
            type="text"
            fullWidth
            value={formData.firstName}
            onChange={handleInputChange}
          />
          <TextField
            margin="dense"
            name="lastName"
            label="Soyad"
            type="text"
            fullWidth
            value={formData.lastName}
            onChange={handleInputChange}
          />
          <TextField
            margin="dense"
            name="email"
            label="E-posta"
            type="email"
            fullWidth
            value={formData.email}
            onChange={handleInputChange}
          />
          <TextField
            margin="dense"
            name="password"
            label="Şifre"
            type="password"
            fullWidth
            value={formData.password}
            onChange={handleInputChange}
            helperText={selectedCustomer ? "Değiştirmeyecekseniz boş bırakın" : ""}
          />
          <TextField
            margin="dense"
            name="phoneNumber"
            label="Telefon"
            type="text"
            fullWidth
            value={formData.phoneNumber}
            onChange={handleInputChange}
          />
          <TextField
            margin="dense"
            name="address"
            label="Adres"
            type="text"
            fullWidth
            value={formData.address}
            onChange={handleInputChange}
          />
          <Box display="flex" alignItems="center" mt={2}>
            <Typography>Admin Yetkisi:</Typography>
            <input
              type="checkbox"
              name="isAdmin"
              checked={formData.isAdmin}
              onChange={handleInputChange}
              style={{ marginLeft: '10px' }}
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)} color="primary">
            İptal
          </Button>
          <Button onClick={handleSaveCustomer} color="primary" variant="contained">
            Kaydet
          </Button>
        </DialogActions>
      </Dialog>

      {/* Hesap Ekleme/Düzenleme Dialog */}
      <Dialog open={accountDialogOpen} onClose={() => setAccountDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{selectedAccount ? 'Hesabı Düzenle' : 'Yeni Hesap Ekle'}</DialogTitle>
        <DialogContent>
          <TextField
            margin="dense"
            name="accountNumber"
            label="Hesap Numarası"
            type="text"
            fullWidth
            value={accountFormData.accountNumber}
            onChange={handleAccountInputChange}
          />
          <TextField
            select
            margin="dense"
            name="accountType"
            label="Hesap Türü"
            fullWidth
            value={accountFormData.accountType}
            onChange={handleAccountInputChange}
            SelectProps={{
              native: true,
            }}
          >
            <option value="CHECKING">Vadesiz Hesap</option>
            <option value="SAVINGS">Tasarruf Hesabı</option>
            <option value="CREDIT">Kredi Hesabı</option>
          </TextField>
          <TextField
            margin="dense"
            name="balance"
            label="Bakiye"
            type="number"
            fullWidth
            value={accountFormData.balance}
            onChange={handleAccountInputChange}
          />
          <TextField
            select
            margin="dense"
            name="currency"
            label="Para Birimi"
            fullWidth
            value={accountFormData.currency}
            onChange={handleAccountInputChange}
            SelectProps={{
              native: true,
            }}
          >
            <option value="TL">TL</option>
            <option value="USD">USD</option>
            <option value="EUR">EUR</option>
          </TextField>
          <TextField
            select
            margin="dense"
            name="customerId"
            label="Müşteri"
            fullWidth
            value={accountFormData.customerId}
            onChange={handleAccountInputChange}
            SelectProps={{
              native: true,
            }}
          >
            {customers.map((customer) => (
              <option key={customer.id} value={customer.id}>
                {customer.firstName} {customer.lastName} ({customer.email})
              </option>
            ))}
          </TextField>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setAccountDialogOpen(false)} color="primary">
            İptal
          </Button>
          <Button onClick={handleSaveAccount} color="primary" variant="contained">
            Kaydet
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default AdminPanel; 