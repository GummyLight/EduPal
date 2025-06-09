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
  questionId: string;       // 问题ID
  questionContent: string;
  questionSubject: string;
  questionTime: string;     // Date 转换为字符串
  answerNum: number;
  answers: AnswerDetail[];
}

interface AnswerDetail {
  answerId: string;         // 回答ID
  answerContent: string;
  answerType: number;       // 0-AI回答，1-教师回答
  teacherId?: string;       // 教师ID（可能为空）
  answerTime: string;       // Date 转换为字符串
}

// 学生获取转接教师列表请求
interface GetMyTeacherRequest {
  userId: string
}

// 教师列表元素
interface TItem {
  teacherId: string
  teacherName: string
  teacherSubject: string
}
// 学生获取转接教师列表响应
interface GetMyTeacherResponse {
  status: string
  message: string
  teacherNum: number
  teachers: TItem[]
}

// 学科映射：将前端学科字符串映射为后端兼容的短字符串
// 主要使用中文学科名称，同时保持向后兼容性
const subjectMap: Record<string, string> = {
  // 中文学科名称（新标准）
  '数学': 'math',
  '物理': 'physics',
  '化学': 'chem',
  '英语': 'engl',
  '语文': 'chin',
  '生物': 'biol',
  '历史': 'hist',
  '地理': 'geog',
  '政治': 'poli',
  '其他': 'other',
  // 英文学科名称（向后兼容）
  'math': 'math',
  'physics': 'physics', 
  'chemistry': 'chem',
  'english': 'engl',
  'chinese': 'chin',
  'biology': 'biol',
  'history': 'hist',
  'geography': 'geog',
  'politics': 'poli',
  'programming': 'prog',
  'other': 'other'
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
    // 将学科字符串转换为数据库兼容的格式
    const mappedSubject = subjectMap[questionSubject] || questionSubject;
    
    const requestData: AIQuestionRequest = {
      studentId,
      questionContent,
      questionType: 0, // 固定为0表示问AI
      questionSubject: mappedSubject
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
    const response = await request.post(`/ai/history?userId=${studentId}`);
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

// 上传本地对话到云端（为将来扩展准备）
export const uploadLocalConversation = async (
  studentId: string,
  questionContent: string,
  questionSubject: string,
  answerContent?: string,
  questionId?: string,
  answerId?: string
): Promise<void> => {
  try {
    // 注意：这个功能需要后端支持，目前只是预留接口
    // 实际上，当前的AI问答流程已经自动将对话保存到后端了
    console.log('本地对话已通过AI问答自动同步到云端');
  } catch (error: any) {
    console.error('同步本地对话失败:', error);
    // 不抛出错误，避免影响用户体验
  }
};

// 删除历史对话
export const deleteConversation = async (
  userId: string, 
  questionId: string
): Promise<{ status: string; message: string }> => {
  try {
    console.log('开始发送删除请求:', { userId, questionId });
    const url = `/ai/delete?userId=${userId}&questionId=${questionId}`;
    console.log('请求URL:', url);
    
    const response = await request.post(url);
    console.log('删除历史对话成功:', response);
    console.log('响应数据:', response.data);
    return response.data;
  } catch (error: any) {
    console.error('删除历史对话失败 - 详细错误:', error);
    console.error('错误响应:', error.response);
    console.error('错误状态码:', error.response?.status);
    console.error('错误数据:', error.response?.data);
    
    if (error.response?.data) {
      throw new Error(error.response.data.message || '删除对话失败');
    }
    
    throw new Error('删除对话失败，请稍后重试');
  }
};

// 获取学生可转接的教师列表
export const getMyTeacher = async (userId: string): Promise<GetMyTeacherResponse> => {
  try {
    // 后端使用 @RequestParam，需要使用 URLSearchParams 格式
    const params = new URLSearchParams();
    params.append('userId', userId);
    
    const response = await request.post<GetMyTeacherResponse>('/ai/getMyTeacher', params, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
    
    console.log('获取教师列表成功:', response.data);
    return response.data;
  } catch (error: any) {
    console.error('获取教师列表失败:', error);
    
    if (error.response?.data) {
      throw new Error(error.response.data.message || '获取教师列表失败');
    }
    
    throw new Error('网络错误或服务器内部错误，请稍后重试');
  }
};

export default {
  askAI,
  getConversationHistory,
  deleteConversation,
  getMyTeacher
};

// 导出类型定义
export type { 
  AIQuestionRequest, 
  AIQuestionResponse, 
  HistoryResponse, 
  QA, 
  AnswerDetail,
  GetMyTeacherRequest,
  GetMyTeacherResponse,
  TItem
};
