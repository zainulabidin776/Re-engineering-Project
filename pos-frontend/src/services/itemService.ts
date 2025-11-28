import api from './api';

export interface Item {
  id: string;
  itemId: number;
  name: string;
  price: number;
  quantity: number;
}

export const itemService = {
  async getAllItems(): Promise<Item[]> {
    const response = await api.get<Item[]>('/items');
    return response.data;
  },
  
  async getItemById(id: string): Promise<Item> {
    const response = await api.get<Item>(`/items/${id}`);
    return response.data;
  },
  
  async getItemByItemId(itemId: number): Promise<Item> {
    const response = await api.get<Item>(`/items/item-id/${itemId}`);
    return response.data;
  },
  
  async searchItems(name: string): Promise<Item[]> {
    const response = await api.get<Item[]>(`/items/search?name=${name}`);
    return response.data;
  }
};

