// src/api/home.ts
import api from './request';
import { ElMessage } from 'element-plus';

// 后端实际返回的完整的管理员响应格式
interface BackendAdminResponse {
    status: string;
    message: string;
    username: string;
    userType: string;
    userId: string;
    totalUsers: number;
    totalStudents: number;
    totalTeachers: number;
    totalTeachingContents: number;
    totalQuestions: number;
    totalAnswers: number;
    recentRegistrations: number; // 最近7天注册用户数
    systemHealth: string; // 系统健康状态
}

// 前端期望的管理员首页数据接口
export interface AdminHomeData {
    username: string;
    userType: 0 | 1 | 2;
    userId: string;
    totalUsers: number;
    totalStudents: number;
    totalTeachers: number;
    totalTeachingContents: number;
    totalQuestions: number;
    totalAnswers: number;
    recentRegistrations: number;
    systemHealth: string;
}

// 后端实际返回的完整的学生响应格式
interface BackendStudentResponse {
    status: string;
    message: string;
    username: string;
    userType: string; // 后端返回的是字符串类型，例如 "0", "1", "2"
    userId: string;
    finishedLectures: number;
    finishedExercises: number;
    gradeLevel: string;
    answerNum: number; // 后端返回的是 answerNum
    todayTasks: string[];
    notifications: string[];
}

// 后端实际返回的完整的教师响应格式
interface BackendTeacherResponse {
    status: string;
    message: string;
    username: string;
    userType: string;
    userId: string;
    unfinishedQAs: number;
    uploadExercises: number;
    uploadResources: number;
    classIds: string[];
    topStudents: StudentDetailsBackend[]; // 后端返回的详细学生数据
    bottomStudents: StudentDetailsBackend[]; // 后端返回的详细学生数据
}

// 后端返回的学生详情结构
interface StudentDetailsBackend {
    studentId: string;
    studentName: string;
    studentAverageScore: number;
    studentVariation: number;
}

// 前端期望的学生首页数据接口 (转换后的数据)
export interface StudentHomeData {
    username: string;
    userType: 0 | 1 | 2; // 前端期望的数字类型，包含 0, 1, 2
    userId: string;
    finishedLectures: number;
    finishedExercises: number;
    gradeLevel: string;
    answerRate: number; // 保持为 number，在组件中进行格式化为百分比
    todayTasks: string[];
    notifications: string[];
}

// 前端期望的教师首页数据接口 (转换后的数据)
export interface TeacherHomeData {
    username: string;
    userType: 0 | 1 | 2;
    userId: string;
    unfinishedQAs: number;
    uploadExercises: number;
    uploadResources: number;
    classIds: string[];
    topStudents: StudentDetailsFrontend[];
    bottomStudents: StudentDetailsFrontend[];
}

// 前端期望的学生详情结构
export interface StudentDetailsFrontend {
    studentId: string;
    studentName: string;
    studentAverageScore: number;
    studentVariation: number;
}

// 作业进度相关接口
export interface QuizProgressData {
    totalQuizzes: number;
    completedQuizzes: number;
    progressPercentage: number;
}

// 后端返回的作业进度响应格式
interface BackendQuizProgressResponse {
    status: string;
    message: string;
    quizNum: number;
    completedCount: number;
}

export const HomeService = {
    /**
     * 获取管理员首页数据
     * @param {string} userId - 管理员的ID
     * @returns {Promise<AdminHomeData>} 管理员首页数据对象
     */
    async getAdminHomeData(userId: string): Promise<AdminHomeData> {
        try {
            const response = await api.get<BackendAdminResponse>('/home/admin', {
                params: {
                    userId: userId,
                    userType: 0 // 管理员类型
                }
            });

            if (response.data.status !== "success") {
                throw new Error(response.data.message || '获取管理员首页数据失败');
            }

            const rawData = response.data;
            const transformedData: AdminHomeData = {
                username: rawData.username,
                userType: parseInt(rawData.userType) as (0 | 1 | 2),
                userId: rawData.userId,
                totalUsers: rawData.totalUsers,
                totalStudents: rawData.totalStudents,
                totalTeachers: rawData.totalTeachers,
                totalTeachingContents: rawData.totalTeachingContents,
                totalQuestions: rawData.totalQuestions,
                totalAnswers: rawData.totalAnswers,
                recentRegistrations: rawData.recentRegistrations,
                systemHealth: rawData.systemHealth,
            };
            return transformedData;
        } catch (error: any) {
            console.error('获取管理员首页数据失败:', error);
            // 如果后端接口还没有实现，返回模拟数据而不是报错
            if (error.response && error.response.status === 404) {
                console.warn('管理员首页接口未实现，使用模拟数据');
                const mockData: AdminHomeData = {
                    username: '管理员',
                    userType: 0,
                    userId: userId,
                    totalUsers: 156,
                    totalStudents: 120,
                    totalTeachers: 36,
                    totalTeachingContents: 89,
                    totalQuestions: 245,
                    totalAnswers: 198,
                    recentRegistrations: 12,
                    systemHealth: '正常',
                };
                return mockData;
            }
            
            if (error.response && error.response.data && error.response.data.message) {
                ElMessage.error(error.response.data.message);
            } else if (error.message) {
                ElMessage.error(error.message);
            } else {
                ElMessage.error('获取管理员首页数据失败，请检查网络或联系管理员！');
            }
            throw error;
        }
    },

    /**
     * 获取学生首页数据
     * @param {string} userId - 学生的ID
     * @returns {Promise<StudentHomeData>} 学生首页数据对象
     */
    async getStudentHomeData(userId: string): Promise<StudentHomeData> {
        try {
            const response = await api.get<BackendStudentResponse>('/home/student', {
                params: {
                    userId: userId,
                    userType: 1 // 固定为学生类型
                }
            });

            if (response.data.status !== "success") {
                throw new Error(response.data.message || '获取学生首页数据失败');
            }

            const rawData = response.data;
            const transformedData: StudentHomeData = {
                username: rawData.username,
                userType: parseInt(rawData.userType) as (0 | 1 | 2),
                userId: rawData.userId,
                finishedLectures: rawData.finishedLectures,
                finishedExercises: rawData.finishedExercises,
                gradeLevel: rawData.gradeLevel,
                answerRate: rawData.answerNum,
                todayTasks: rawData.todayTasks || [],
                notifications: rawData.notifications || [],
            };
            return transformedData;
        } catch (error: any) {
            console.error('获取学生首页数据失败:', error);
            
            if (error.response && error.response.data && error.response.data.message) {
                ElMessage.error(error.response.data.message);
            } else if (error.message) {
                ElMessage.error(error.message);
            } else {
                ElMessage.error('获取学生首页数据失败，请检查网络或联系管理员！');
            }
            throw error;
        }
    },

    /**
     * 获取教师首页数据
     * @param {string} userId - 教师的ID
     * @returns {Promise<TeacherHomeData>} 教师首页数据对象
     */
    async getTeacherHomeData(userId: string): Promise<TeacherHomeData> {
        try {
            const response = await api.get<BackendTeacherResponse>('/home/teacher', { // 调用教师接口
                params: {
                    userId: userId,
                    userType: 2 // 固定为教师类型
                }
            });

            if (response.data.status !== "success") {
                throw new Error(response.data.message || '获取教师首页数据失败');
            }

            const rawData = response.data;

            // 转换学生详情数据
            const transformStudentDetails = (details: StudentDetailsBackend[]): StudentDetailsFrontend[] => {
                return details.map(s => ({
                    studentId: s.studentId,
                    studentName: s.studentName,
                    studentAverageScore: s.studentAverageScore,
                    studentVariation: s.studentVariation,
                }));
            };

            const transformedData: TeacherHomeData = {
                username: rawData.username,
                userType: parseInt(rawData.userType) as (0 | 1 | 2),
                userId: rawData.userId,
                unfinishedQAs: rawData.unfinishedQAs,
                uploadExercises: rawData.uploadExercises,
                uploadResources: rawData.uploadResources,
                classIds: rawData.classIds || [],
                topStudents: transformStudentDetails(rawData.topStudents || []),
                bottomStudents: transformStudentDetails(rawData.bottomStudents || []),
            };
            return transformedData;
        } catch (error: any) {
            console.error('获取教师首页数据失败:', error);
            // 如果后端接口还没有实现或者出现500错误，返回模拟数据而不是报错
            if (error.response && (error.response.status === 404 || error.response.status === 500)) {
                console.warn('教师首页接口异常，使用模拟数据');
                const mockData: TeacherHomeData = {
                    username: '教师',
                    userType: 2,
                    userId: userId,
                    unfinishedQAs: 3,
                    uploadExercises: 15,
                    uploadResources: 8,
                    classIds: ['高一(1)班', '高一(2)班'],
                    topStudents: [
                        { studentId: 'S001', studentName: '张三', studentAverageScore: 95.5, studentVariation: 2.1 },
                        { studentId: 'S002', studentName: '李四', studentAverageScore: 92.3, studentVariation: 3.2 }
                    ],
                    bottomStudents: [
                        { studentId: 'S003', studentName: '王五', studentAverageScore: 65.2, studentVariation: 8.1 },
                        { studentId: 'S004', studentName: '赵六', studentAverageScore: 58.9, studentVariation: 9.5 }
                    ],
                };
                return mockData;
            }
            
            if (error.response && error.response.data && error.response.data.message) {
                ElMessage.error(error.response.data.message);
            } else if (error.message) {
                ElMessage.error(error.message);
            } else {
                ElMessage.error('获取教师首页数据失败，请检查网络或联系管理员！');
            }
            throw error;
        }
    },

    /**
     * 获取作业进度数据
     * @param {string} userId - 用户的ID
     * @returns {Promise<QuizProgressData>} 作业进度数据对象
     */
    async getQuizProgressData(userId: string): Promise<QuizProgressData> {
        try {
            const response = await api.get<BackendQuizProgressResponse>('/home/quiz-progress', {
                params: {
                    userId: userId,
                }
            });

            if (response.data.status !== "success") {
                throw new Error(response.data.message || '获取作业进度数据失败');
            }

            const rawData = response.data;
            const transformedData: QuizProgressData = {
                totalQuizzes: rawData.quizNum,
                completedQuizzes: rawData.completedCount,
                progressPercentage: (rawData.completedCount / rawData.quizNum) * 100,
            };
            return transformedData;
        } catch (error: any) {
            console.error('获取作业进度数据失败:', error);
            
            if (error.response && error.response.data && error.response.data.message) {
                ElMessage.error(error.response.data.message);
            } else if (error.message) {
                ElMessage.error(error.message);
            } else {
                ElMessage.error('获取作业进度数据失败，请检查网络或联系管理员！');
            }
            throw error;
        }
    }
};