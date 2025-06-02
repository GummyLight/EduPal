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
  userInput: string //将userId和email合并为同一项，即，只检测用户输入的内容是什么，不区分账号还是有邮箱
  password: string
  type: number //0表示用户名登录，1表示邮箱登录
}

// 登录响应数据类型
interface LoginResponseData {
  message: string // "登录成功"
  success: boolean // true/false
  userId: string // "s20250112"
  userName: string // "李昶谨"
  userType: number // 1 (学生) 或 2 (教师)
}

// 注册请求数据类型
interface RegisterRequest {
  userName: string
  userId: string
  password: string
  email: string
  userType: number //1表示学生，2表示教师
  // 教师特有变量
  teacherClass?: string[] //授课班级
  teachingSubject?: string //教学科目
  // 学生特有变量
  studentClass?: string //学生所在班级
  studentGender?: number //学生性别
}

// 注册响应数据类型其实和后端实际相应类型一致，但这是一种代码规范。
interface RegisterResponse {
  code: number
  message: string
}

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
export const login = async (userInput: string, password: string, type: number): Promise<ApiResponse<LoginResponseData>> => {
  try {

    //发送请求部分
    let requestData: LoginRequest;
    
    requestData = {
      userInput: userInput,
      password: password,
      type: type
    }

    if (type === 0) {
      console.log('账号登录:', { userId: userInput, type });
    } else if (type === 1) {
      console.log('邮箱登录:', { email: userInput, type });
    }
    
    //返回响应部分
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
      email,  // 暂时保持后端参数名，发送邮箱作为值
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

// 发送邮箱验证码函数
export const sendEmailCode = async (email: string): Promise<ApiResponse> => {
  try {
    const requestData: EmailCodeRequest = { email };
    const response = await axios.post<ApiResponse>('/api/auth/send-code', requestData);
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
    const response = await axios.post<ApiResponse>('/api/auth/reset-password', requestData);
    return response.data;
  } catch (error: any) {
    if (error.response?.data) {
      return error.response.data;
    }
    throw error;
  }
};

export default { register, login, sendEmailCode, forgetPassword };
//  const login = (userId: string, password: string, type: number) => {
//    return axios.post(`/api/auth/login`, { userId, password, type });
//  };

//  const register = (userId: string, password: string, phoneNum: string, userType: number) => {
//    return axios.post(`/api/auth/register`, { userId, password, phoneNum, userType });
//  };



