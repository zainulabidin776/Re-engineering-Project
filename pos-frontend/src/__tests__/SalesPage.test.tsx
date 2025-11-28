import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import SalesPage from '../components/SalesPage';
import * as itemService from '../services/itemService';
import * as saleService from '../services/saleService';

vi.mock('../services/itemService', () => ({
  itemService: {
    getAllItems: vi.fn(),
    getItemByItemId: vi.fn()
  }
}));

vi.mock('../services/saleService', () => ({
  saleService: {
    createSale: vi.fn()
  }
}));

describe('SalesPage Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('renders sales page', () => {
    vi.mocked(itemService.itemService.getAllItems).mockResolvedValue([]);
    render(<SalesPage />);
    expect(screen.getByText(/process sale/i)).toBeInTheDocument();
  });

  test('displays items when loaded', async () => {
    const mockItems = [
      { id: '1', itemId: 1001, name: 'Test Item', price: 10.00, quantity: 100 }
    ];
    vi.mocked(itemService.itemService.getAllItems).mockResolvedValue(mockItems);
    
    render(<SalesPage />);
    
    await waitFor(() => {
      expect(itemService.itemService.getAllItems).toHaveBeenCalled();
    });
  });
});

