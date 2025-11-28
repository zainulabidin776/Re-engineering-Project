import api from './api';

export interface SaleItemRequest {
  itemId: number;
  quantity: number;
}

export interface SaleRequest {
  items: SaleItemRequest[];
  couponCode?: string;
}

export interface Sale {
  id: string;
  employeeId: string;
  totalAmount: number;
  taxAmount: number;
  discountAmount: number;
  finalTotal: number;
  couponCode?: string;
  transactionDate: string;
}

export const saleService = {
  async createSale(request: SaleRequest): Promise<Sale> {
    const response = await api.post<Sale>('/sales', request);
    return response.data;
  },
  
  async getSaleById(id: string): Promise<Sale> {
    const response = await api.get<Sale>(`/sales/${id}`);
    return response.data;
  }
};

