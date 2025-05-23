// src/api/auth.ts
import axios from 'axios';

const login = (userId: string, password: string, type: number) => {
  return axios.post(`/api/auth/login`, { userId, password, type });
};

const register = (userId: string, password: string, phoneNum: string, userType: number) => {
  return axios.post(`/api/auth/register`, { userId, password, phoneNum, userType });
};

export default {register,login};


