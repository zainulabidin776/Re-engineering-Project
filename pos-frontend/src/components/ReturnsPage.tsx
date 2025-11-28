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
  Alert
} from '@mui/material';
import api from '../services/api';

interface OutstandingRental {
  id: string;
  itemId: number;
  itemName: string;
  quantity: number;
  daysOverdue: number;
  returned: boolean;
}

const ReturnsPage = () => {
  const [customerPhone, setCustomerPhone] = useState('');
  const [outstandingRentals, setOutstandingRentals] = useState<OutstandingRental[]>([]);
  const [selectedItems, setSelectedItems] = useState<{ rentalItemId: string; quantity: number }[]>([]);
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);

  const lookupRentals = async () => {
    if (!customerPhone) {
      setMessage('Please enter customer phone number');
      return;
    }

    try {
      const response = await api.get(`/rentals/outstanding/${customerPhone}`);
      setOutstandingRentals(response.data);
      setMessage('');
    } catch (error: any) {
      setMessage(error.response?.data?.error || 'Failed to lookup rentals');
      setOutstandingRentals([]);
    }
  };

  const toggleItem = (rentalItemId: string, quantity: number) => {
    const existing = selectedItems.find(item => item.rentalItemId === rentalItemId);
    if (existing) {
      setSelectedItems(selectedItems.filter(item => item.rentalItemId !== rentalItemId));
    } else {
      setSelectedItems([...selectedItems, { rentalItemId, quantity }]);
    }
  };

  const processReturn = async () => {
    if (selectedItems.length === 0) {
      setMessage('Please select items to return');
      return;
    }

    setLoading(true);
    try {
      await api.post('/returns', {
        items: selectedItems.map(item => ({
          rentalItemId: item.rentalItemId,
          quantity: item.quantity
        }))
      });
      setMessage('Return processed successfully!');
      setSelectedItems([]);
      lookupRentals();
    } catch (error: any) {
      setMessage(error.response?.data?.error || 'Failed to process return');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      <Typography variant="h5" gutterBottom>Process Return</Typography>
      
      {message && (
        <Alert severity={message.includes('success') ? 'success' : 'error'} sx={{ mb: 2 }}>
          {message}
        </Alert>
      )}

      <Paper sx={{ p: 2, mb: 2 }}>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <TextField
            label="Customer Phone"
            value={customerPhone}
            onChange={(e) => setCustomerPhone(e.target.value)}
            size="small"
            required
          />
          <Button variant="contained" onClick={lookupRentals}>
            Lookup Rentals
          </Button>
        </Box>
      </Paper>

      {outstandingRentals.length > 0 && (
        <>
          <TableContainer component={Paper} sx={{ mb: 2 }}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Select</TableCell>
                  <TableCell>Item ID</TableCell>
                  <TableCell>Item Name</TableCell>
                  <TableCell>Quantity</TableCell>
                  <TableCell>Days Overdue</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {outstandingRentals.map((rental) => (
                  <TableRow key={rental.id}>
                    <TableCell>
                      <input
                        type="checkbox"
                        checked={selectedItems.some(item => item.rentalItemId === rental.id)}
                        onChange={() => toggleItem(rental.id, rental.quantity)}
                      />
                    </TableCell>
                    <TableCell>{rental.itemId}</TableCell>
                    <TableCell>{rental.itemName}</TableCell>
                    <TableCell>{rental.quantity}</TableCell>
                    <TableCell>
                      {rental.daysOverdue > 0 ? (
                        <Typography color="error">{rental.daysOverdue} days</Typography>
                      ) : (
                        <Typography color="success.main">On time</Typography>
                      )}
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
            onClick={processReturn}
            disabled={loading || selectedItems.length === 0}
          >
            {loading ? 'Processing...' : 'Process Return'}
          </Button>
        </>
      )}
    </Box>
  );
};

export default ReturnsPage;

