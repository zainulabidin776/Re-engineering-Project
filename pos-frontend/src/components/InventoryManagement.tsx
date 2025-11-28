import { useState, useEffect } from 'react';
import {
  Box,
  TextField,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Typography,
  Alert
} from '@mui/material';
import { itemService, Item } from '../services/itemService';
import api from '../services/api';

const InventoryManagement = () => {
  const [items, setItems] = useState<Item[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [message, setMessage] = useState('');

  useEffect(() => {
    loadItems();
  }, []);

  const loadItems = async () => {
    try {
      const data = await itemService.getAllItems();
      setItems(data);
    } catch (error) {
      console.error('Failed to load items:', error);
    }
  };

  const updateQuantity = async (itemId: string, newQuantity: number) => {
    try {
      await api.put(`/inventory/items/${itemId}/quantity?quantity=${newQuantity}`);
      setMessage('Quantity updated successfully');
      loadItems();
    } catch (error: any) {
      setMessage(error.response?.data?.error || 'Update failed');
    }
  };

  const filteredItems = items.filter(item =>
    item.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    item.itemId.toString().includes(searchTerm)
  );

  return (
    <Box>
      <Typography variant="h5" gutterBottom>Inventory Management</Typography>
      
      {message && (
        <Alert severity={message.includes('success') ? 'success' : 'error'} sx={{ mb: 2 }} onClose={() => setMessage('')}>
          {message}
        </Alert>
      )}

      <Box sx={{ mb: 2 }}>
        <TextField
          label="Search Items"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          size="small"
          sx={{ width: 300 }}
        />
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Item ID</TableCell>
              <TableCell>Name</TableCell>
              <TableCell>Price</TableCell>
              <TableCell>Quantity</TableCell>
              <TableCell>Update Quantity</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filteredItems.map((item) => (
              <TableRow key={item.id}>
                <TableCell>{item.itemId}</TableCell>
                <TableCell>{item.name}</TableCell>
                <TableCell>${item.price.toFixed(2)}</TableCell>
                <TableCell>{item.quantity}</TableCell>
                <TableCell>
                  <TextField
                    type="number"
                    size="small"
                    defaultValue={item.quantity}
                    inputProps={{ min: 0 }}
                    sx={{ width: 100, mr: 1 }}
                    onKeyPress={(e) => {
                      if (e.key === 'Enter') {
                        const input = e.target as HTMLInputElement;
                        updateQuantity(item.id, parseInt(input.value));
                      }
                    }}
                  />
                  <Button
                    size="small"
                    variant="outlined"
                    onClick={(e) => {
                      const input = e.currentTarget.previousElementSibling as HTMLInputElement;
                      updateQuantity(item.id, parseInt(input.value));
                    }}
                  >
                    Update
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
};

export default InventoryManagement;

