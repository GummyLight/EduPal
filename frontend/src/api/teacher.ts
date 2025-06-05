// src/api/teacher.ts
import request from './request';
import axios from 'axios';

// 学生转交问题给老师请求
interface TransTeacherRequest {
  userId: string;
  questionId: string;
  teacherId: string;
}

// 学生转交给老师响应
interface TransTeacherResponse {
  code: number
  message: string
  data?: any
}

export const transferQuestionToTeacher = async (
    data: TransTeacherRequest
): Promise<TransTeacherResponse> => {
    try {
        // 后端期望的是表单参数，不是JSON
        const formData = new URLSearchParams();
        formData.append('userId', data.userId);
        formData.append('questionId', data.questionId);
        formData.append('teacherId', data.teacherId);
        
        const response = await request.post<TransTeacherResponse>('/ai/transTeacher', formData, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        });
        console.log('转交问题给老师成功:', response.data);
        return response.data;
    } catch (error: any) {
        console.error('转交问题给老师失败', error);
        if (error.response?.data) {
            throw new Error(error.response.data.message || '转交问题失败');
        }
        throw new Error('网络错误或服务器内部错误，请稍后重试')
    }
};


// 老师查看所有转交的问题请求
interface TeacherViewRequest {
  teacherId: string;
}

// 老师对问题的回答
export interface AnswerDetail {
    answerType: number // 回答类型，0-AI回答，1-教师回答
    answerContent: string // 答案内容
    answerTime: string // 回答时间
}

// 和ai.ts的QA定义不同，所以单独拿出一个ts来写）
export interface QA {
    questionId: string
    studentName: string
    studentId: string
    studentClass: string
    questionContent: string
    transferTime: string
    teacherAnswers: AnswerDetail[]
}

// 老师查看所有转交的问题响应
interface TeacherViewResponse {
  status: string
  message: string
  teacherId: string
  qaNum: number
  questionSet: QA[]
}

export const TeacherViewQuestions = async (
    params: TeacherViewRequest
): Promise<TeacherViewResponse> => {
    try {
        const response = await request.get<TeacherViewResponse>('/ai/teacherView', { params });
        console.log('老师查看转交问题成功:', response.data);
        return response.data;
    } catch (error: any) {
        console.error('老师查看转交问题失败', error);
        if (error.response?.data) {
            throw new Error(error.response.data.message || '获取转交问题列表失败');
        }
        throw new Error('网络错误或服务器内部错误，请稍后重试');
    }
};

// 老师回答问题请求
interface TeacherAnswerRequest {
    teacherId: string
    questionId: string
    answerContent: string
}

// 老师回答问题响应
interface TeacherAnswerResponse {
    code: number
    message: string
}

export const TeacherAnswer = async (
    data: TeacherAnswerRequest
): Promise<TeacherAnswerResponse> => {
    try {
        // 后端期望的是表单参数，不是JSON
        const formData = new URLSearchParams();
        formData.append('teacherId', data.teacherId);
        formData.append('questionId', data.questionId);
        formData.append('answerContent', data.answerContent);
        
        const response = await request.post<TeacherAnswerResponse>('/ai/teacherAnswer', formData, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        });
        console.log('老师回答问题成功:', response.data);
        return response.data;
    } catch (error: any) {
        console.error('老师回答问题失败', error);
        if (error.response?.data) {
            throw new Error(error.response.data.message || '老师回答问题失败');
        }
        throw new Error('网络错误或服务器内部错误，请稍后重试');
    }
};