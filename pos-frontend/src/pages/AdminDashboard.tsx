import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
  Tabs,
  Tab,
  Paper
} from '@mui/material';
import { useAuth } from '../context/AuthContext';
import EmployeeManagement from '../components/EmployeeManagement';
import InventoryManagement from '../components/InventoryManagement';

const AdminDashboard = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [tabValue, setTabValue] = useState(0);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            POS System - Admin Dashboard
          </Typography>
          <Typography variant="body1" sx={{ mr: 2 }}>
            {user?.fullName}
          </Typography>
          <Button color="inherit" onClick={handleLogout}>
            Logout
          </Button>
        </Toolbar>
      </AppBar>

      <Container maxWidth="lg" sx={{ mt: 4 }}>
        <Paper>
          <Tabs value={tabValue} onChange={(e, newValue) => setTabValue(newValue)}>
            <Tab label="Employee Management" />
            <Tab label="Inventory Management" />
          </Tabs>

          <Box sx={{ p: 3 }}>
            {tabValue === 0 && <EmployeeManagement />}
            {tabValue === 1 && <InventoryManagement />}
          </Box>
        </Paper>
      </Container>
    </Box>
  );
};

export default AdminDashboard;

