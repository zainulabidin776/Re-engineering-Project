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
  IconButton,
  Alert,
  Chip
} from '@mui/material';
import { Add, Delete } from '@mui/icons-material';
import { itemService, Item } from '../services/itemService';
import { saleService, SaleItemRequest } from '../services/saleService';

const SalesPage = () => {
  const [items, setItems] = useState<Item[]>([]);
  const [cart, setCart] = useState<SaleItemRequest[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [itemId, setItemId] = useState('');
  const [quantity, setQuantity] = useState('1');
  const [couponCode, setCouponCode] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);

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
        setCart([...cart, { itemId: item.itemId, quantity: parseInt(quantity) }]);
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

  const calculateTotal = () => {
    let subtotal = 0;
    cart.forEach(cartItem => {
      const item = items.find(i => i.itemId === cartItem.itemId);
      if (item) {
        subtotal += item.price * cartItem.quantity;
      }
    });
    const tax = subtotal * 0.06;
    const discount = couponCode ? subtotal * 0.10 : 0;
    return {
      subtotal: subtotal.toFixed(2),
      tax: tax.toFixed(2),
      discount: discount.toFixed(2),
      total: (subtotal + tax - discount).toFixed(2)
    };
  };

  const processSale = async () => {
    if (cart.length === 0) {
      setMessage('Cart is empty');
      return;
    }

    setLoading(true);
    try {
      await saleService.createSale({
        items: cart,
        couponCode: couponCode || undefined
      });
      setMessage('Sale processed successfully!');
      setCart([]);
      setCouponCode('');
      loadItems();
    } catch (error: any) {
      setMessage(error.response?.data?.error || 'Failed to process sale');
    } finally {
      setLoading(false);
    }
  };

  const totals = calculateTotal();
  const cartItems = cart.map(cartItem => {
    const item = items.find(i => i.itemId === cartItem.itemId);
    return { ...cartItem, item };
  }).filter(ci => ci.item);

  return (
    <Box>
      <Typography variant="h5" gutterBottom>Process Sale</Typography>
      
      {message && (
        <Alert severity={message.includes('success') ? 'success' : 'error'} sx={{ mb: 2 }}>
          {message}
        </Alert>
      )}

      <Paper sx={{ p: 2, mb: 2 }}>
        <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
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

        <TextField
          label="Coupon Code (optional)"
          value={couponCode}
          onChange={(e) => setCouponCode(e.target.value)}
          size="small"
          sx={{ width: 300 }}
        />
      </Paper>

      {cartItems.length > 0 && (
        <>
          <TableContainer component={Paper} sx={{ mb: 2 }}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Item ID</TableCell>
                  <TableCell>Name</TableCell>
                  <TableCell>Price</TableCell>
                  <TableCell>Quantity</TableCell>
                  <TableCell>Subtotal</TableCell>
                  <TableCell>Action</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {cartItems.map((cartItem) => (
                  <TableRow key={cartItem.itemId}>
                    <TableCell>{cartItem.itemId}</TableCell>
                    <TableCell>{cartItem.item?.name}</TableCell>
                    <TableCell>${cartItem.item?.price.toFixed(2)}</TableCell>
                    <TableCell>{cartItem.quantity}</TableCell>
                    <TableCell>${((cartItem.item?.price || 0) * cartItem.quantity).toFixed(2)}</TableCell>
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

          <Paper sx={{ p: 2, mb: 2 }}>
            <Typography variant="h6">Summary</Typography>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 1 }}>
              <Typography>Subtotal:</Typography>
              <Typography>${totals.subtotal}</Typography>
            </Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
              <Typography>Tax (6%):</Typography>
              <Typography>${totals.tax}</Typography>
            </Box>
            {couponCode && (
              <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                <Typography>Discount (10%):</Typography>
                <Typography>-${totals.discount}</Typography>
              </Box>
            )}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 1, pt: 1, borderTop: 1 }}>
              <Typography variant="h6">Total:</Typography>
              <Typography variant="h6">${totals.total}</Typography>
            </Box>
          </Paper>

          <Button
            variant="contained"
            color="primary"
            size="large"
            fullWidth
            onClick={processSale}
            disabled={loading}
          >
            {loading ? 'Processing...' : 'Complete Sale'}
          </Button>
        </>
      )}
    </Box>
  );
};

export default SalesPage;

