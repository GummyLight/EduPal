import api from './request';
import { ElMessage } from 'element-plus';
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
    teacherId: string
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

// 教师具体信息 - 匹配后端ListTeacherResponse.TeacherDetail
interface TeacherDetail {
    teacherId: string // 教师ID
    teacherName: string // 教师姓名
    teachingSubject: string // 教学科目
    teacherClass: string[] // 授课班级数组
}

// 列出所有老师 - 匹配后端ListTeacherResponse
interface ListTeachers {
    status: string // 响应状态
    message: string // 响应信息
    teacherNum: number // 教师总数
    teachers: TeacherDetail[] // 教师集合
}

// 学生具体信息 - 匹配后端ListStudentResponse.StudentDetail
interface StudentDetail {
    studentId: string // 学生ID
    studentName: string // 学生姓名
    studentGender: number // 学生性别
    studentClass: string // 学生班级
}

// 列出所有学生 - 匹配后端ListStudentResponse
interface ListStudents {
    status: string // 响应状态
    message: string // 响应信息
    studentNum: number // 学生总数
    students: StudentDetail[] // 学生集合
}

// 用户具体信息 - 匹配后端ListUserResponse.UserDetail
interface UserDetail {
    userId: string // 用户ID
    userEmail: string // 用户邮箱
    userType: number // 用户类型
    userName: string // 用户名
    createTime: Date // 创建时间
    loginTime: Date // 登录时间
}

// 列出所有用户 - 匹配后端ListUserResponse
interface ListUsers {
    status: string // 响应状态
    message: string // 响应信息
    userNum: number // 用户总数
    users: UserDetail[] // 用户集合
}

// 注册函数 - 注意：注册是在/admin/register而不是/auth/register
export const register = async (userId: string, userName: string, password: string, email: string, userType: number, additionalData?: any): Promise<ApiResponse> => {
  try {
    // 后端接受表单数据而不是JSON，所以使用URLSearchParams
    const params = new URLSearchParams();
    params.append('userId', userId);
    params.append('userName', userName);
    params.append('password', password);
    params.append('email', email);
    params.append('userType', userType.toString());

    // 根据用户类型添加特定字段
    if (userType === 1 && additionalData) {
      // 学生
      if (additionalData.studentClass) {
        params.append('studentClass', additionalData.studentClass);
      }
      if (additionalData.studentGender !== undefined) {
        params.append('studentGender', additionalData.studentGender.toString());
      }
    } else if (userType === 2 && additionalData) {
      // 教师
      if (additionalData.teachingSubject) {
        params.append('teachingSubject', additionalData.teachingSubject);
      }
      if (additionalData.teacherClass && Array.isArray(additionalData.teacherClass)) {
        // 对于数组参数，需要多次append相同的key
        additionalData.teacherClass.forEach((classItem: string) => {
          params.append('teacherClass', classItem);
        });
      }
    }

    const response = await api.post<ApiResponse>('/admin/register', params, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
    return response.data;
  } catch (error: any) {
    if (error.response?.data) {
      return error.response.data;
    }
    throw error;
  }
};

// 注销账户函数
export const deleteAccount = async (userType: number, userId: string): Promise<ApiResponse> => {
  try {
    // 后端使用 @RequestParam，所以需要使用 URLSearchParams
    const params = new URLSearchParams();
    params.append('userType', userType.toString());
    params.append('userId', userId);
    
    const response = await api.post<ApiResponse>('/admin/delete', params, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
    return response.data;
  } catch (error: any) {
    if (error.response?.data) {
      return error.response.data;
    }
    throw error;
  }
};

// 更新账户信息函数
export const updateAccount = async (userId: string, newUserName: string, newEmail: string, newPassword: string): Promise<ApiResponse> => {
  try {
    // 后端使用 @RequestParam，所以需要使用 URLSearchParams
    const params = new URLSearchParams();
    params.append('userId', userId);
    params.append('newUserName', newUserName);
    params.append('newEmail', newEmail);
    params.append('newPassword', newPassword);
    
    const response = await api.post<ApiResponse>('/admin/update', params, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
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
    // 后端使用 @RequestParam，所以需要使用 URLSearchParams
    const params = new URLSearchParams();
    params.append('studentId', studentId);
    params.append('newClassId', newClassId);
    
    const response = await api.post<ApiResponse>('/admin/updateStudentClass', params, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
    return response.data;
  } catch (error: any) {
    if (error.response?.data) {
      return error.response.data;
    }
    throw error;
  }
};

// 给老师换班函数
export const updateTeacherClass = async (teacherId: string, newClass: string[]): Promise<ApiResponse> => {
  try {
    // 后端期望的是 @RequestParam("teacherId") String teacherId, @RequestParam("newClass") String[] newClass
    // 但是由于URLSearchParams不能直接发送数组，我们需要使用FormData或其他方式
    // 根据后端代码，它接收String[]数组，我们需要发送数组参数
    const params = new URLSearchParams();
    params.append('teacherId', teacherId);
    
    // 对于数组参数，需要多次append相同的key
    newClass.forEach(classItem => {
      params.append('newClass', classItem);
    });
    
    const response = await api.post<ApiResponse>('/admin/updateTeacherClass', params, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
    return response.data;
  } catch (error: any) {
    if (error.response?.data) {
      return error.response.data;
    }
    throw error;
  }
};

// 给老师换科目函数
export const updateTeacherSubject = async (teacherId: string, newSubject: string): Promise<ApiResponse> => {
  try {
    // 后端使用 @RequestParam，所以需要使用 URLSearchParams
    const params = new URLSearchParams();
    params.append('teacherId', teacherId);
    params.append('newSubject', newSubject);
    
    const response = await api.post<ApiResponse>('/admin/updateTeacherSubject', params, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
    return response.data;
  } catch (error: any) {
    if (error.response?.data) {
      return error.response.data;
    }
    throw error;
  }
};

// 列出所有老师函数
export const listTeachers = async (): Promise<ListTeachers> => {
  try {
    // 后端路由是 /admin/listTeachers，直接返回 ListTeacherResponse 对象
    const response = await api.get<ListTeachers>('/admin/listTeachers');
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
export const listStudents = async (): Promise<ListStudents> => {
  try {
    // 后端路由是 /admin/listStudents，直接返回 ListStudentResponse 对象
    const response = await api.get<ListStudents>('/admin/listStudents');
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
export const listUsers = async (): Promise<ListUsers> => {
  try {
    // 后端路由是 /admin/listUsers，直接返回 ListUserResponse 对象
    const response = await api.get<ListUsers>('/admin/listUsers');
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