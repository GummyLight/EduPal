// 因为xyy这个b写的代码实在太史山了，自己写不明白所以叫我过来，但我作为一个极具高素质审美的程序员，是必定会规范代码书写格式的，所以在这里创建quiz.ts，特意用来写createQuiz和modifyQuiz的路由，并和xyy运用对比的手法
import request from './request';
import axios from 'axios';

// 老师创建quiz请求
interface CreateQuizRequest {
    title: string
    subject: string
    contentType: string
    difficulty: string
    knowledgePoints: string[]
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
    knowledgePoints: string[]
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

// 导出接口类型，供其他组件使用
export type { CreateQuizRequest, CreateQuizResponse, ModifyQuizRequest, ModifyQuizResponse };