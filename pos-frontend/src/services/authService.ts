import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api';

export interface LoginResponse {
  token: string;
  employeeId: string;
  username: string;
  fullName: string;
  position: string;
}

export const authService = {
  async login(username: string, password: string): Promise<LoginResponse> {
    const response = await axios.post<LoginResponse>(`${API_BASE_URL}/auth/login`, {
      username,
      password
    });
    return response.data;
  }
};

