import React, { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import {
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Box,
  Avatar,
  Menu,
  MenuItem,
  Divider,
  useTheme,
  useMediaQuery
} from '@mui/material';
import {
  Menu as MenuIcon,
  Dashboard,
  AccountBalance,
  Payment,
  Settings,
  ExitToApp,
  Person
} from '@mui/icons-material';
import { motion } from 'framer-motion';
import { useAuth } from '../contexts/AuthContext';

const Navigation: React.FC = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [userMenuAnchor, setUserMenuAnchor] = useState<null | HTMLElement>(null);

  const menuItems = [
    { text: 'Ana Panel', icon: <Dashboard />, path: '/dashboard' },
    { text: 'Hesaplar', icon: <AccountBalance />, path: '/accounts' },
    { text: 'Transferler', icon: <Payment />, path: '/transactions' },
    { text: 'Profil', icon: <Person />, path: '/profile' },
    { text: 'Ayarlar', icon: <Settings />, path: '/settings' }
  ];

  const handleLogout = async () => {
    try {
      await logout();
      navigate('/login');
    } catch (error) {
      console.error('Çıkış yaparken hata oluştu:', error);
    }
  };

  const toggleDrawer = () => {
    setDrawerOpen(!drawerOpen);
  };

  const handleUserMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setUserMenuAnchor(event.currentTarget);
  };

  const handleUserMenuClose = () => {
    setUserMenuAnchor(null);
  };

  const isActive = (path: string) => {
    return location.pathname === path;
  };

  const appBarVariants = {
    hidden: { y: -100 },
    visible: { y: 0, transition: { type: 'spring', stiffness: 100 } }
  };

  const listItemVariants = {
    hover: { x: 10, transition: { duration: 0.2 } }
  };

  return (
    <>
      <motion.div
        initial="hidden"
        animate="visible"
        variants={appBarVariants}
      >
        <AppBar position="sticky" elevation={3} sx={{ backgroundColor: 'background.paper' }}>
          <Toolbar>
            <IconButton
              edge="start"
              color="primary"
              aria-label="menu"
              onClick={toggleDrawer}
              sx={{ mr: 2 }}
            >
              <MenuIcon />
            </IconButton>
            
            <Typography variant="h6" color="primary" fontWeight="bold" sx={{ flexGrow: 1 }}>
              Banka Uygulaması
            </Typography>
            
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <IconButton onClick={handleUserMenuOpen}>
                <Avatar 
                  sx={{ bgcolor: theme.palette.primary.main }}
                >
                  {user?.firstName?.charAt(0) || "K"}
                </Avatar>
              </IconButton>
              
              <Menu
                anchorEl={userMenuAnchor}
                open={Boolean(userMenuAnchor)}
                onClose={handleUserMenuClose}
                PaperProps={{
                  elevation: 3,
                  sx: { 
                    borderRadius: 2,
                    mt: 1,
                    minWidth: 200
                  }
                }}
              >
                <Box sx={{ px: 2, py: 1 }}>
                  <Typography variant="subtitle1" fontWeight="bold">
                    {user?.firstName} {user?.lastName}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {user?.email}
                  </Typography>
                </Box>
                <Divider />
                <MenuItem component={Link} to="/profile">
                  <ListItemIcon>
                    <Person fontSize="small" />
                  </ListItemIcon>
                  <ListItemText>Profilim</ListItemText>
                </MenuItem>
                <MenuItem component={Link} to="/settings">
                  <ListItemIcon>
                    <Settings fontSize="small" />
                  </ListItemIcon>
                  <ListItemText>Ayarlar</ListItemText>
                </MenuItem>
                <Divider />
                <MenuItem onClick={handleLogout}>
                  <ListItemIcon>
                    <ExitToApp fontSize="small" />
                  </ListItemIcon>
                  <ListItemText>Çıkış Yap</ListItemText>
                </MenuItem>
              </Menu>
            </Box>
          </Toolbar>
        </AppBar>
      </motion.div>
      
      <Drawer
        anchor="left"
        open={drawerOpen}
        onClose={toggleDrawer}
        PaperProps={{
          sx: {
            width: isMobile ? '75%' : 250,
            borderTopRightRadius: 16,
            borderBottomRightRadius: 16,
            padding: 2,
            boxShadow: '0 8px 16px rgba(0, 0, 0, 0.1)'
          }
        }}
      >
        <Box sx={{ 
          display: 'flex', 
          flexDirection: 'column', 
          height: '100%' 
        }}>
          <Box sx={{ p: 2, mb: 2 }}>
            <Typography variant="h6" color="primary" fontWeight="bold">
              Banka Uygulaması
            </Typography>
          </Box>
          
          <Divider sx={{ mb: 2 }} />
          
          <List sx={{ flexGrow: 1 }}>
            {menuItems.map((item) => {
              const active = isActive(item.path);
              return (
                <motion.div key={item.text} whileHover="hover" variants={listItemVariants}>
                  <ListItem 
                    component={Link} 
                    to={item.path}
                    sx={{
                      borderRadius: 2,
                      mb: 1,
                      backgroundColor: active ? 'rgba(0, 0, 0, 0.04)' : 'transparent',
                      cursor: 'pointer'
                    }}
                  >
                    <ListItemIcon sx={{ color: active ? 'primary.main' : 'inherit' }}>
                      {item.icon}
                    </ListItemIcon>
                    <ListItemText 
                      primary={item.text} 
                      primaryTypographyProps={{
                        fontWeight: active ? 'bold' : 'regular',
                        color: active ? 'primary.main' : 'inherit'
                      }}
                    />
                  </ListItem>
                </motion.div>
              );
            })}
          </List>
          
          <Divider sx={{ mt: 'auto', mb: 2 }} />
          
          <List>
            <ListItem 
              onClick={handleLogout}
              sx={{ borderRadius: 2, cursor: 'pointer' }}
            >
              <ListItemIcon>
                <ExitToApp />
              </ListItemIcon>
              <ListItemText primary="Çıkış Yap" />
            </ListItem>
          </List>
        </Box>
      </Drawer>
    </>
  );
};

export default Navigation; 