// src/api/auth.ts
//注册相关先不要写，因为注册功能是给管理员用的，而我们没有管理员
import axios from 'axios';

/*auth.ts用于实现后端和前端的数据交接*/

// 来自后端的实际响应格式
interface ApiResponse<T = any> {
  code: number
  message: string
  data?: T
}

// 登录请求数据类型
interface LoginRequest {
  userId?: string
  password: string
  type: number //0表示用户名登录，1表示邮箱登录
}

// 登录响应数据类型
interface LoginResponseData {
  message: string // "登录成功"
  success: boolean // true/false
  userId: string // "s20250112"
  userName: string // "李昶谨"
  userType: number // 1 (教师) 或 0 (学生)
}

// 注册请求数据类型
interface RegisterRequest {
  userId: string
  password: string
  address: string
  userType: number //0表示学生，1表示教师
}

// 注册响应数据类型其实和后端实际相应类型一致，但这是一种代码规范。
interface RegisterResponse {
  code: number
  message: string
}

interface ForgetPasswordRequest {

}

interface ForgetPasswordResponse {
  
}

// 登录函数, 分类处理账号和邮箱登录
export const login = async (userInput: string, password: string, type: number): Promise<ApiResponse<LoginResponseData>> => {
  try {
    let requestData: LoginRequest;
    
    if (type === 0) {
      // 账号登录
      requestData = {
        userId: userInput,    // 发送账号
        password: password,
        type: type
      };
      console.log('账号登录:', { userId: userInput, type });
    } else if (type === 1) {
      // 邮箱登录
      requestData = {
        userId: userInput,  // 发送邮箱
        password: password,
        type: type
      };
      console.log('邮箱登录:', { phoneNum: userInput, type });
    } else {
      throw new Error('无效的登录类型');
    }
    
    const response = await axios.post<ApiResponse<LoginResponseData>>('/api/auth/login', requestData);
    return response.data;
  } catch (error: any) {
    if (error.response?.data) {
      return error.response.data;
    }
    throw error;
  }
}

// 注册函数
export const register = async (userId: string, password: string, email: string, userType: number): Promise<ApiResponse> => {
  try {
    const response = await axios.post<ApiResponse>(`/api/auth/register`, {
      userId,
      password,
      phoneNum: email,  // 暂时保持后端参数名，发送邮箱作为值
      userType
    });
    return response.data;  // 直接返回，不需要转换
  } catch (error: any) {
    if (error.response?.data) {
      return error.response.data;  // 直接返回，不需要转换
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



