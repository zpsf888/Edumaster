import { defineStore } from 'pinia';
import { login as apiLogin } from '../api/auth';

interface UserState {
  username: string;
  isLoggedIn: boolean;
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    username: '',
    isLoggedIn: false,
  }),

  actions: {
    async login(username: string, password: string) {
      try {
        const response = await apiLogin({ username, password });
        localStorage.setItem('token', response.token);
        this.username = username;
        this.isLoggedIn = true;
        return true;
      } catch (error) {
        console.error('Login failed:', error);
        return false;
      }
    },

    logout() {
      localStorage.removeItem('token');
      this.username = '';
      this.isLoggedIn = false;
    },

    checkAuth() {
      const token = localStorage.getItem('token');
      this.isLoggedIn = !!token;
      return this.isLoggedIn;
    },
  },
}); 