// src/api/ai.ts
import request from './request';
import axios from 'axios';

// AI问答请求接口 - 与后端QuestionRequest匹配
interface AIQuestionRequest {
  studentId: string;        // 学生ID（账号）
  questionContent: string;  // 提问内容
  questionType: number;     // 问AI 0，问老师 1
  questionSubject: string;  // 问题所属的科目
}

// AI问答响应接口 - 与后端AnswerResponse匹配
interface AIQuestionResponse {
  answerId: string;         // 回答ID
  questionId: string;       // 问题ID  
  answerContent: string;    // 回答内容
}

// 历史对话响应接口 - 与后端HistoryResponse匹配
interface HistoryResponse {
  status: string;
  message: string;
  studentId: string;
  questionNum: number;
  questionSet: QA[];
}

interface QA {
  questionContent: string;
  questionSubject: string;
  questionTime: string;     // Date 转换为字符串
  answerNum: number;
  answers: AnswerDetail[];
}

interface AnswerDetail {
  answerContent: string;
  answerType: number;       // 0-AI回答，1-教师回答
  teacherId?: string;       // 教师ID（可能为空）
  answerTime: string;       // Date 转换为字符串
}

// 学科映射
const subjectMap: Record<string, number> = {
  'math': 0,
  'physics': 1,
  'chemistry': 2,
  'programming': 3,
  'other': 4
};

// 发送AI问答请求
export const askAI = async (
  studentId: string,
  questionContent: string,
  questionSubject: string = 'other',
  timeoutMs: number = 60000, // 默认60秒超时
  cancelTokenSource?: any
): Promise<AIQuestionResponse> => {
  try {
    const requestData: AIQuestionRequest = {
      studentId,
      questionContent,
      questionType: 0, // 固定为0表示问AI
      questionSubject
    };

    console.log('发送AI问答请求:', requestData);

    // 创建取消令牌
    const source = cancelTokenSource || axios.CancelToken.source();
    
    // 设置超时定时器
    const timeoutId = setTimeout(() => {
      if (!cancelTokenSource) {
        source.cancel('请求超时，AI响应时间过长，请稍后重试');
      }
    }, timeoutMs);

    const response = await request.post<AIQuestionResponse>('/ai/ask', requestData, {
      timeout: timeoutMs,
      cancelToken: source.token
    });
    
    // 清除超时定时器
    clearTimeout(timeoutId);
    
    console.log('AI回答响应:', response.data);
    
    return response.data;
  } catch (error: any) {
    console.error('AI问答请求失败:', error);
    
    // 处理取消请求
    if (axios.isCancel(error)) {
      throw new Error('请求已取消: ' + error.message);
    }
    
    // 处理超时
    if (error.code === 'ECONNABORTED' || error.message.includes('timeout')) {
      throw new Error('AI响应超时，请稍后重试');
    }
    
    if (error.response?.data) {
      throw new Error(error.response.data.message || 'AI服务暂时不可用');
    }
    
    throw new Error('网络错误，请检查网络连接');
  }
};

// 获取历史对话
export const getConversationHistory = async (studentId: string): Promise<HistoryResponse> => {
  try {
    const response = await request.get(`/ai/history/${studentId}`);
    console.log('获取历史对话成功:', response.data);
    return response.data;
  } catch (error: any) {
    console.error('获取历史对话失败:', error);
    
    // 如果是404错误，说明还没有历史对话，返回空的HistoryResponse
    if (error.response?.status === 404) {
      return {
        status: 'success',
        message: '暂无历史对话',
        studentId: studentId,
        questionNum: 0,
        questionSet: []
      };
    }
    
    // 其他错误也返回空的HistoryResponse，不影响用户使用
    return {
      status: 'error',
      message: error.message || '获取历史对话失败',
      studentId: studentId,
      questionNum: 0,
      questionSet: []
    };
  }
};

export default {
  askAI,
  getConversationHistory
};

// 导出类型定义
export type { 
  AIQuestionRequest, 
  AIQuestionResponse, 
  HistoryResponse, 
  QA, 
  AnswerDetail 
};
