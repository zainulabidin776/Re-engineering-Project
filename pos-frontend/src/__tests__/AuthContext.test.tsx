import { renderHook, act } from '@testing-library/react';
import { describe, test, expect, beforeEach, vi } from 'vitest';
import { AuthProvider, useAuth } from '../context/AuthContext';
import * as authService from '../services/authService';
import { ReactNode } from 'react';

vi.mock('../services/authService', () => ({
  authService: {
    login: vi.fn()
  }
}));

describe('AuthContext', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
  });

  test('provides initial null user', () => {
    const wrapper = ({ children }: { children: ReactNode }) => (
      <AuthProvider>{children}</AuthProvider>
    );

    const { result } = renderHook(() => useAuth(), { wrapper });

    expect(result.current.user).toBeNull();
    expect(result.current.isAuthenticated).toBe(false);
  });

  test('handles login successfully', async () => {
    const mockResponse = {
      token: 'test-token',
      employeeId: '123',
      username: 'testuser',
      fullName: 'Test User',
      position: 'Cashier'
    };

    vi.mocked(authService.authService.login).mockResolvedValue(mockResponse);

    const wrapper = ({ children }: { children: ReactNode }) => (
      <AuthProvider>{children}</AuthProvider>
    );

    const { result } = renderHook(() => useAuth(), { wrapper });

    await act(async () => {
      await result.current.login('testuser', 'password123');
    });

    expect(result.current.user).not.toBeNull();
    expect(result.current.user?.username).toBe('testuser');
    expect(result.current.isAuthenticated).toBe(true);
    expect(localStorage.getItem('token')).toBe('test-token');
  });

  test('handles logout', () => {
    localStorage.setItem('token', 'test-token');
    localStorage.setItem('user', JSON.stringify({ username: 'testuser' }));

    const wrapper = ({ children }: { children: ReactNode }) => (
      <AuthProvider>{children}</AuthProvider>
    );

    const { result } = renderHook(() => useAuth(), { wrapper });

    act(() => {
      result.current.logout();
    });

    expect(result.current.user).toBeNull();
    expect(result.current.isAuthenticated).toBe(false);
    expect(localStorage.getItem('token')).toBeNull();
  });
});

