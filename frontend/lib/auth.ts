import api from './api';
import Cookies from 'js-cookie';
import { UserRole } from '@/types';

export interface AuthResponse {
  token: string;
  userId: string;
  email: string;
  role: UserRole;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export const authService = {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await api.post('/api/auth/login', credentials);
    const authData = response.data;
    
    // Store token in cookie
    Cookies.set('auth-token', authData.token, { expires: 1 });
    Cookies.set('user-role', authData.role, { expires: 1 });
    Cookies.set('user-id', authData.userId, { expires: 1 });
    
    return authData;
  },

  logout() {
    Cookies.remove('auth-token');
    Cookies.remove('user-role');
    Cookies.remove('user-id');
    window.location.href = '/login';
  },

  getToken(): string | undefined {
    return Cookies.get('auth-token');
  },

  getUserRole(): UserRole | undefined {
    const role = Cookies.get('user-role');
    return role as UserRole;
  },

  getUserId(): string | undefined {
    return Cookies.get('user-id');
  },

  isAuthenticated(): boolean {
    return !!this.getToken();
  },

  isManager(): boolean {
    return this.getUserRole() === 'MANAGER';
  }
};