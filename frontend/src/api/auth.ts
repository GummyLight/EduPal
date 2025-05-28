// src/api/auth.ts
import axios from 'axios';

/*auth.ts用于实现后端和前端的数据交接*/

// 来自后端的实际响应格式
interface ApiResponse<T = any> {
  code: number
  message: string
  data?: T
}

// 登录响应数据类型
interface LoginResponseData {
  userInfo?: {
    id: number
    userId: string
    phoneNum: string
    userType: number
  }
}

// 注册请求数据类型
interface RegisterRequest {
  userId: string
  password: string
  phoneNum: string
  userType: number //0表示学生，1表示教师
}

// 登录请求数据类型
interface LoginRequest {
  userId: string
  password: string
  type: number //0表示用户名登录，1表示手机号登录
}

// 统一的成功响应格式（用于前端组件）
interface UnifiedResponse<T = any> {
  code: number //状态码，用于表示请求的处理结果，302啊404啊什么的
  message: string //信息，接收到后端的提示信息
  data?: T //数据，即json文件
}

// 响应格式转换函数，将ApiResponse转换为UnifiedResponse
const transformResponse = <T>(response: ApiResponse<T>): UnifiedResponse<T> => {
  return {
    code: response.code,
    message: response.message,
    data: response.data
  }
}

//登录函数
export const login = async (userId: string, password: string, type: number): Promise<UnifiedResponse<LoginResponseData>> => {
  try {
    const response = await axios.post<ApiResponse<LoginResponseData>>('/api/auth/login', {
      userId,
      password,
      type
    });
    return transformResponse(response.data);
  } catch (error: any) {
    if (error.response?.data) {
      return transformResponse(error.response.data);
    }
    throw error;
  }
}

// 注册函数
export const register = async (userId: string, password: string, phoneNum: string, userType: number): Promise<UnifiedResponse> => {
  try {
    const response = await axios.post<ApiResponse>(`/api/auth/register`, {
      userId,
      password,
      phoneNum,
      userType
    });
    return transformResponse(response.data);
  } catch (error: any) {
    if (error.response?.data) {
      return transformResponse(error.response.data);
    }
    throw error;
  }
};

export default {register,login};
//  const login = (userId: string, password: string, type: number) => {
//    return axios.post(`/api/auth/login`, { userId, password, type });
//  };

//  const register = (userId: string, password: string, phoneNum: string, userType: number) => {
//    return axios.post(`/api/auth/register`, { userId, password, phoneNum, userType });
//  };



