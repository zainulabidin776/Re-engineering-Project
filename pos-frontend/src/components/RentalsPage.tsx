import { useState } from 'react';
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
  IconButton,
  Alert
} from '@mui/material';
import { Add, Delete } from '@mui/icons-material';
import { itemService, Item } from '../services/itemService';
import api from '../services/api';

interface RentalItem {
  itemId: number;
  quantity: number;
  item?: Item;
}

const RentalsPage = () => {
  const [customerPhone, setCustomerPhone] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [cart, setCart] = useState<RentalItem[]>([]);
  const [itemId, setItemId] = useState('');
  const [quantity, setQuantity] = useState('1');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);

  const addToCart = async () => {
    if (!itemId || !quantity) {
      setMessage('Please enter item ID and quantity');
      return;
    }

    try {
      const item = await itemService.getItemByItemId(parseInt(itemId));
      if (item.quantity < parseInt(quantity)) {
        setMessage('Insufficient inventory');
        return;
      }

      const existingIndex = cart.findIndex(c => c.itemId === item.itemId);
      if (existingIndex >= 0) {
        const newCart = [...cart];
        newCart[existingIndex].quantity += parseInt(quantity);
        setCart(newCart);
      } else {
        setCart([...cart, { itemId: item.itemId, quantity: parseInt(quantity), item }]);
      }

      setItemId('');
      setQuantity('1');
      setMessage('');
    } catch (error) {
      setMessage('Item not found');
    }
  };

  const removeFromCart = (itemId: number) => {
    setCart(cart.filter(item => item.itemId !== itemId));
  };

  const processRental = async () => {
    if (!customerPhone) {
      setMessage('Please enter customer phone number');
      return;
    }
    if (!dueDate) {
      setMessage('Please enter due date');
      return;
    }
    if (cart.length === 0) {
      setMessage('Cart is empty');
      return;
    }

    setLoading(true);
    try {
      await api.post('/rentals', {
        customerPhone,
        dueDate,
        items: cart.map(c => ({ itemId: c.itemId, quantity: c.quantity }))
      });
      setMessage('Rental processed successfully!');
      setCart([]);
      setCustomerPhone('');
      setDueDate('');
    } catch (error: any) {
      setMessage(error.response?.data?.error || 'Failed to process rental');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      <Typography variant="h5" gutterBottom>Process Rental</Typography>
      
      {message && (
        <Alert severity={message.includes('success') ? 'success' : 'error'} sx={{ mb: 2 }}>
          {message}
        </Alert>
      )}

      <Paper sx={{ p: 2, mb: 2 }}>
        <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
          <TextField
            label="Customer Phone"
            value={customerPhone}
            onChange={(e) => setCustomerPhone(e.target.value)}
            size="small"
            required
          />
          <TextField
            label="Due Date"
            type="date"
            value={dueDate}
            onChange={(e) => setDueDate(e.target.value)}
            size="small"
            InputLabelProps={{ shrink: true }}
            required
          />
        </Box>

        <Box sx={{ display: 'flex', gap: 2 }}>
          <TextField
            label="Item ID"
            value={itemId}
            onChange={(e) => setItemId(e.target.value)}
            type="number"
            size="small"
          />
          <TextField
            label="Quantity"
            value={quantity}
            onChange={(e) => setQuantity(e.target.value)}
            type="number"
            size="small"
            inputProps={{ min: 1 }}
          />
          <Button variant="contained" onClick={addToCart} startIcon={<Add />}>
            Add to Cart
          </Button>
        </Box>
      </Paper>

      {cart.length > 0 && (
        <>
          <TableContainer component={Paper} sx={{ mb: 2 }}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Item ID</TableCell>
                  <TableCell>Name</TableCell>
                  <TableCell>Price</TableCell>
                  <TableCell>Quantity</TableCell>
                  <TableCell>Action</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {cart.map((cartItem) => (
                  <TableRow key={cartItem.itemId}>
                    <TableCell>{cartItem.itemId}</TableCell>
                    <TableCell>{cartItem.item?.name}</TableCell>
                    <TableCell>${cartItem.item?.price.toFixed(2)}</TableCell>
                    <TableCell>{cartItem.quantity}</TableCell>
                    <TableCell>
                      <IconButton onClick={() => removeFromCart(cartItem.itemId)} color="error" size="small">
                        <Delete />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>

          <Button
            variant="contained"
            color="primary"
            size="large"
            fullWidth
            onClick={processRental}
            disabled={loading}
          >
            {loading ? 'Processing...' : 'Complete Rental'}
          </Button>
        </>
      )}
    </Box>
  );
};

export default RentalsPage;

