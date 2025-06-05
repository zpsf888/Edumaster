import axios from 'axios';

const API_BASE_URL = '/api';

export interface LoginData {
  username: string;
  password: string;
}

export interface RegisterData extends LoginData {
  email: string;
}

// 模拟注册
export const register = async (data: RegisterData) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const users = JSON.parse(localStorage.getItem('users') || '[]');
      if (users.find((u: any) => u.username === data.username)) {
        reject({ response: { data: { message: '用户名已存在' } } });
      } else {
        users.push(data);
        localStorage.setItem('users', JSON.stringify(users));
        resolve({ userId: Date.now(), message: '注册成功' });
      }
    }, 500);
  });
};

// 模拟登录
export const login = async (data: LoginData) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const users = JSON.parse(localStorage.getItem('users') || '[]');
      const user = users.find((u: any) => u.username === data.username && u.password === data.password);
      if (user) {
        resolve({ token: 'fake-jwt-token', username: user.username });
      } else {
        reject({ response: { data: { message: '用户名或密码错误' } } });
      }
    }, 500);
  });
};

// 配置axios拦截器
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
); 