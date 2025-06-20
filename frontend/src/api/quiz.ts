// Quiz API 接口定义
import request from './request';

// 老师创建quiz请求
interface CreateQuizRequest {
    title: string
    subject: string
    contentType: string
    difficulty: string
    knowledgePoints: string  // 字符串类型，与后端匹配
    description: string
    teacherId: string
    teacherName: string
    createTime: string
    deadline: string
    class1: string
    class2: string
}

// 老师创建quiz响应
interface CreateQuizResponse<T = any> {
    code: number
    message: string
    data: T
}

// 老师修改quiz请求
interface ModifyQuizRequest {
    quizId: number
    title: string
    subject: string
    contentType: string
    difficulty: string
    knowledgePoints: string  // 字符串类型，与后端匹配
    description: string
    teacherId: string
    teacherName: string
    createTime: string
    deadline: string
    class1: string
    class2: string
}

// 老师修改quiz响应
interface ModifyQuizResponse<T = any> {
    code: number
    message: string
    data: T
}

// 创建quiz
export const createQuiz = async (data: CreateQuizRequest): Promise<CreateQuizResponse> => {
    try {
        const response = await request.post('/quiz/createQuiz', data, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
        return response.data;
    } catch (error) {
        console.error('创建quiz失败:', error);
        throw error;
    }
};

// 修改quiz
export const modifyQuiz = async (data: ModifyQuizRequest): Promise<ModifyQuizResponse> => {
    try {
        const response = await request.post('/quiz/modifyQuiz', data, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
        return response.data;
    } catch (error) {
        console.error('修改quiz失败:', error);
        throw error;
    }
};

// 获取最大quiz ID
export const getMaxQuizId = async (): Promise<number> => {
    try {
        const response = await request.get('/quiz/max-quiz-id');
        if (response.data.code === 200) {
            return response.data.data; // 返回最大 quiz_id
        } else {
            throw new Error(response.data.message || '获取最大 quiz_id 失败');
        }
    } catch (error) {
        console.error('获取最大 quiz_id 错误:', error);
        throw error;
    }
};

// 获取教师的quiz列表
export const getTeacherQuiz = async (userId: string) => {
    try {
        const response = await request.get('/quiz/getTeacherQuiz', {
            params: { userId }
        });
        if (response.data.quizzes) {
            return response.data.quizzes; // 返回quiz数组
        } else {
            return []; // 如果没有quiz则返回空数组
        }
    } catch (error) {
        console.error('获取教师quiz列表失败:', error);
        throw error;
    }
};

// 导出接口类型，供其他组件使用
export type { CreateQuizRequest, CreateQuizResponse, ModifyQuizRequest, ModifyQuizResponse };