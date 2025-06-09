// src/api/auth.ts
//注册相关先不要写，因为注册功能是给管理员用的，而我们没有管理员
import axios from 'axios';
import request from './request'; // 导入配置好的axios实例

/*auth.ts用于实现后端和前端的数据交接*/

// 来自后端的实际响应格式
export interface ApiResponse<T = any> {
  code: number
  message: string
  data?: T
}

// 登录请求数据类型
interface LoginRequest {
  userInput: string //将userId和email合并为同一项，即，只检测用户输入的内容是什么，不区分账号还是有邮箱
  password: string
  type: number //0表示用户名登录，1表示邮箱登录
}

// 登录响应数据类型 - 直接返回格式
interface LoginResponseData {
  message: string // "登录成功"
  success: boolean // true/false
  userId: string // "s20250112"
  userName: string // "李昶谨"
  userType: number // 0 (学生) 或 1 (教师)
}

// 统一的登录响应类型，兼容两种可能的返回格式
export type LoginResponse = ApiResponse<LoginResponseData> | LoginResponseData;

/*
 注册功能放到admin.ts了，因为管理员的接口比较多，
 需要单独一个文件管理，而注册又恰好是管理员的功能，
 所以就放在admin.ts了
*/

// 忘记密码请求数据类型
interface ForgetPasswordRequest {
  email: string
  code: string
  newPassword: string
}

// 忘记密码响应数据类型
interface ForgetPasswordResponse {
  code: number
  message: string
}

// 验证码请求数据类型
interface EmailCodeRequest {
  email: string
}

// 验证码响应数据类型
interface EmailCodeResponse {
  code: number
  message: string
}

// 登录函数, 分类处理账号和邮箱登录
export const login = async (userInput: string, password: string, type: number): Promise<LoginResponse> => {
  try {

    //发送请求部分
    let requestData: LoginRequest;
    
    requestData = {
      userInput: userInput,
      password: password,
      type: type
    }

    console.log('发送登录请求:', {
      url: '/auth/login',
      data: requestData,
      type: type === 0 ? '账号登录' : '邮箱登录'
    });
    
    //返回响应部分
    const response = await request.post<any>('/auth/login', requestData, {
      headers: {
        'Content-Type': 'application/json',
      }
    });
    
    console.log('登录响应:', response.data);
    return response.data;
  } catch (error: any) {
    console.error('登录请求失败:', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      url: error.config?.url,
      message: error.message
    });
    
    if (error.response?.data) {
      return error.response.data;
    }
    throw error;
  }
}

// 发送邮箱验证码函数
export const sendEmailCode = async (email: string): Promise<ApiResponse> => {
  try {
    // 后端期望URL参数而不是请求体
    const response = await request.post<ApiResponse>(`/auth/send-code?email=${encodeURIComponent(email)}`);
    return response.data;
  } catch (error: any) {
    if (error.response?.data) {
      return error.response.data;
    }
    throw error;
  }
};

// 忘记密码函数
export const forgetPassword = async (email: string, code: string, newPassword: string): Promise<ApiResponse> => {
  try {
    const requestData: ForgetPasswordRequest = {
      email,
      code,
      newPassword
    };
    const response = await request.post<ApiResponse>('/auth/reset-password', requestData);
    return response.data;
  } catch (error: any) {
    if (error.response?.data) {
      return error.response.data;
    }
    throw error;
  }
};

export default { login, sendEmailCode, forgetPassword };