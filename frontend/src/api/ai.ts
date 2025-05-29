// src/api/ai.ts
import request from './request';

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
  questionSubject: string = 'other'
): Promise<AIQuestionResponse> => {
  try {
    const requestData: AIQuestionRequest = {
      studentId,
      questionContent,
      questionType: 0, // 固定为0表示问AI
      questionSubject
    };

    console.log('发送AI问答请求:', requestData);

    const response = await request.post<AIQuestionResponse>('/ai/ask', requestData);
    
    console.log('AI回答响应:', response.data);
    
    return response.data;
  } catch (error: any) {
    console.error('AI问答请求失败:', error);
    
    if (error.response?.data) {
      throw new Error(error.response.data.message || 'AI服务暂时不可用');
    }
    
    throw new Error('网络错误，请检查网络连接');
  }
};

// 获取历史对话（如果后端支持）
export const getConversationHistory = async (studentId: string): Promise<any[]> => {
  try {
    const response = await request.get(`/ai/history/${studentId}`);
    return response.data || [];
  } catch (error) {
    console.error('获取历史对话失败:', error);
    return [];
  }
};

export default {
  askAI,
  getConversationHistory
};
