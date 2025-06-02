import api from './request';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import type { ApiResponse } from './auth'; // 导入 ApiResponse 类型，因为都是从后端得到的注册数据

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

// 注销账户请求
interface DeleteRequest {
    userType: number
    userId: string
}

// 注销账户响应
interface DeleteResponse {
    code: number
    message: string
}

// 更新账户信息请求
interface UpdateRequest {
    userId: string
    newUserName: string
    newEmail: string
    newPassword: string
}

// 更新账户信息响应
interface UpdateResponse {
    code: number
    message: string
}

// 给学生换班请求
interface UpdateStudentClassRequest {
    studentId: string
    newClassId: string
}

// 给学生换班响应
interface UpdateStudentClassResponse {
    code: number
    message: string
}

// 给老师换班请求
interface UpdateTeacherClassRequest {
    teachaerId: string
    newClass: string[]
}

// 给老师换班响应
interface UpdateTeacherClassResponse {
    code: number
    message: string
}

// 给老师换科目请求
interface UpdateTeacherSubjectRequest {
    teacherId: string
    newSubject: string
}

// 给老师换科目响应
interface UpdateTeacherSubjectResponse {
    code: number
    message: string
}

// 教师具体信息
interface TeacherDetail {
    teacherId: string // 教师ID
    teacherName: string // 教师姓名
    teachingSubject: string // 教学科目
    teacherClass: string[] // 授课班级

}

// 列出所有老师，因为是Get类型，不需要Request再Response
interface ListTeachers {
    status: string // 响应状态
    message: string // 响应信息
    teacherNum: number // 教师总数
    teachers: TeacherDetail[] // 教师集合
}

// 学生具体信息
interface StudentDetail {
    studentId: string // 学生ID
    studentName: string // 学生姓名
    studentGender: number // 学生性别
    studentClass: string // 学生班级
}

// 列出所有学生
interface ListStudents {
    status: string // 响应状态
    message: string // 响应信息
    studentNum: number // 学生总数
    students: StudentDetail[] // 学生集合
}

// 用户具体信息
interface UserDetail {
    userId: string // 用户ID
    userEmail: string // 用户邮箱
    userType: number // 用户类型
    userName: string // 用户名
    createTime: Date // 创建时间
    loginTime: Date // 登录时间
}

// 列出所有用户
interface ListUsers {
    status: string // 响应状态
    message: string // 响应信息
    userNum: number // 用户总数
    users: UserDetail[] // 用户集合
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

// 注销账户函数
export const deleteAccount = async (userType: number, userId: string): Promise<ApiResponse> => {
  try {
    const requestData: DeleteRequest = {
      userType,
      userId
    };
    // 注意：实际的API路由是 /api/admin/delete， 而不是 /admin/delete，根据您项目结构中其他API调用推断
    // 如果后端确实是 /admin/delete，请移除 /api 前缀
    const response = await axios.post<ApiResponse>('/api/admin/delete', requestData);
    return response.data;
  } catch (error: any) {
    if (error.response?.data) {
      return error.response.data;
    }
    // 对于未知错误，可以返回一个标准的错误格式ApiResponse
    // 或者直接抛出错误，让调用者处理
    // return { code: 500, message: '操作失败，请稍后再试' };
    throw error;
  }
};

// 更新账户信息函数
export const updateAccount = async (userId: string, newUserName: string, newEmail: string, newPassword: string): Promise<ApiResponse> => {
  try {
    const requestData: UpdateRequest = {
      userId,
      newUserName,
      newEmail,
      newPassword
    };
    const response = await axios.post<ApiResponse>('/api/admin/update', requestData);
    return response.data;
  } catch (error: any) {
    if (error.response?.data) {
      return error.response.data;
    }
    throw error;
  }
};

// 给学生换班函数
export const updateStudentClass = async (studentId: string, newClassId: string): Promise<ApiResponse> => {
  try {
    const requestData: UpdateStudentClassRequest = {
      studentId,
      newClassId
    };
    
    const response = await axios.post<ApiResponse>('/api/admin/updateStudentClass', requestData);
    return response.data;
  } catch (error: any) {
    if (error.response?.data) {
      return error.response.data;
    }
    throw error;
  }
};

// 给老师换班函数
export const updateTeacherClass = async (teachaerId: string, newClass: string[]): Promise<ApiResponse> => {
  try {
    const requestData: UpdateTeacherClassRequest = {
      teachaerId, // 根据接口 UpdateTeacherClassRequest 定义的属性名
      newClass
    };
    // 假设 API 路由是 /api/admin/updateTeacherClass
    const response = await axios.post<ApiResponse>('/api/admin/updateTeacherClass', requestData);
    return response.data; // 返回后端响应的 data 部分
  } catch (error: any) {
    // 错误处理
    if (error.response?.data) {
      return error.response.data;
    }
    throw error; // 抛出原始错误或自定义错误
  }
};

// 给老师换科目函数
export const updateTeacherSubject = async (teacherId: string, newSubject: string): Promise<ApiResponse> => {
  try {
    const requestData: UpdateTeacherSubjectRequest = {
      teacherId,      // 教师ID
      newSubject      // 新的教学科目
    };
    // 假设 API 路由是 /api/admin/updateTeacherSubject
    const response = await axios.post<ApiResponse>('/api/admin/updateTeacherSubject', requestData);
    return response.data; // 返回后端响应的 data 部分
  } catch (error: any) {
    // 错误处理
    if (error.response?.data) {
      return error.response.data;
    }
    throw error; // 抛出原始错误或自定义错误
  }
};

// 列出所有老师函数
export const listTeachers = async (): Promise<ApiResponse<ListTeachers>> => {
  try {
    // 假设 API 路由是 /api/admin/listTeachers
    // 因为是 GET 请求，不需要请求体
    const response = await axios.get<ApiResponse<ListTeachers>>('/api/admin/listTeachers');
    return response.data; // 返回后端响应的 data 部分
  } catch (error: any) {
    // 错误处理
    if (error.response?.data) {
      return error.response.data;
    }
    throw error; // 抛出原始错误或自定义错误
  }
};

// 列出所有学生函数
export const listStudents = async (): Promise<ApiResponse<ListStudents>> => {
  try {
    // 假设 API 路由是 /api/admin/listStudents
    // 因为是 GET 请求，不需要请求体
    const response = await axios.get<ApiResponse<ListStudents>>('/api/admin/listStudents');
    return response.data; // 返回后端响应的 data 部分
  } catch (error: any) {
    // 错误处理
    if (error.response?.data) {
      return error.response.data;
    }
    throw error; // 抛出原始错误或自定义错误
  }
};

// 列出所有用户函数
export const listUsers = async (): Promise<ApiResponse<ListUsers>> => {
  try {
    // 假设 API 路由是 /api/admin/listUsers
    // 因为是 GET 请求，不需要请求体
    const response = await axios.get<ApiResponse<ListUsers>>('/api/admin/listUsers');
    return response.data; // 返回后端响应的 data 部分
  } catch (error: any) {
    // 错误处理
    if (error.response?.data) {
      return error.response.data;
    }
    throw error; // 抛出原始错误或自定义错误
  }
};

export default { register, deleteAccount, updateAccount, updateStudentClass, updateTeacherClass, updateTeacherSubject, listTeachers, listStudents, listUsers };