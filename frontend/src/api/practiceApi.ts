import axios from 'axios';
import { ElMessage } from 'element-plus'; // 引入 ElMessage 用于统一错误提示
interface ApiResponse<T = any> {
    code: number;
    message: string;
    data?: T;
}
// 配置一个 axios 实例，确保 baseURL 正确
const service = axios.create({
    baseURL: 'http://localhost:8080', // <-- 确保这是你的后端 API 基础 URL
    timeout: 10000, // 上传文件可能需要更长的超时时间
});

// 文件上传响应接口（与后端 /file/upload 接口的返回结构一致）
interface UploadFileResponse {
    code: number;
    message: string;
    data?: any; // 后端 /file/upload 返回的 data 字段，根据实际情况可能包含文件信息
}

// 数据类型定义
export interface PracticeSubmitData {
    quizId: number;
    userId: string;
    answerContent: string;
}

export interface PracticeSubmitResponse {
    code: number;
    message: string;
}

/**
 * 上传文件到后端服务器
 * @param file 要上传的文件对象
 * @param fileId 文件唯一ID (前端生成，不带扩展名，例如 UUID)
 * @param toPath 文件存储的目标子路径 (例如 "resource/")
 * @param path 文件存储的根路径 (例如 "E:/Postman/files")
 * @returns Promise<UploadFileResponse>
 */
export async function uploadFile(file: File, fileId: string, toPath: string, path: string): Promise<UploadFileResponse> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('fileId', fileId);
    formData.append('toPath', toPath);
    formData.append('path', path);

    try {
        const response = await service.post<UploadFileResponse>('/file/upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            },
        });
        return response.data;
    } catch (error: any) {
        let errorMessage = '文件上传失败，请检查网络或后端服务。';
        if (axios.isAxiosError(error)) {
            errorMessage = error.response?.data?.message || `文件上传失败：HTTP 错误 ${error.response?.status}`;
            console.error('HTTP 错误响应数据:', error.response?.data);
            console.error('HTTP 错误状态码:', error.response?.status);
        } else if (error.request) {
            errorMessage = '文件上传请求未收到响应，请检查后端服务是否运行。';
        }
        ElMessage.error(errorMessage); // 统一错误提示
        throw error; // 继续抛出错误，让调用方（组件）处理
    }
}

/**
 * 提交测验信息到后端服务器 (用于保存测验答案)
 * @param data 测验提交数据
 * @returns Promise<PracticeSubmitResponse>
 */
export async function submitPracticeInfo(data: PracticeSubmitData): Promise<PracticeSubmitResponse> {
    try {
        // 使用 GET 请求，传递查询参数
        const response = await service.get<PracticeSubmitResponse>('/quiz/submitQuiz', {
            params: {
                quizId: data.quizId,
                userId: localStorage.getItem('user_id') ,
                answerContent: data.answerContent,
            },
        });

        // 验证响应格式
        if (response.data.code === 200) {
            ElMessage.success(response.data.message || '测验提交成功');
            return response.data;
        } else {
            throw new Error(response.data.message || '测验提交失败');
        }
    } catch (error: any) {
        let errorMessage = '提交测验信息失败，请检查网络或后端服务。';
        if (axios.isAxiosError(error)) {
            errorMessage = error.response?.data?.message || `提交测验信息失败：HTTP 错误 ${error.response?.status}`;
            console.error('HTTP 错误响应数据:', error.response?.data);
            console.error('HTTP 错误状态码:', error.response?.status);
        } else if (error.request) {
            errorMessage = '提交测验信息请求未收到响应，请检查后端服务是否运行。';
        } else {
            errorMessage = error.message || errorMessage;
        }
        console.error('提交测验错误:', error);
        ElMessage.error(errorMessage);
        throw error;
    }
}


export const getMaxanswerId = async (): Promise<number> => {
    try {
        // 使用全局 service 实例发送请求
        const response = await service.get<ApiResponse<number>>('/quiz/max-answer-id');
        if (response.data.code === 200) {
            return response.data.data; // 返回最大 resource_id
        } else {
            throw new Error(response.data.message || '获取最大 answer-id 失败');
        }
    } catch (error: any) {
        let errorMessage = '获取最大 answer-id 失败，请检查网络或后端服务。';
        if (axios.isAxiosError(error)) {
            errorMessage = error.response?.data?.message || `获取最大 answer-id 失败：HTTP 错误 ${error.response?.status}`;
            console.error('HTTP 错误响应数据:', error.response?.data);
            console.error('HTTP 错误状态码:', error.response?.status);
        } else if (error.request) {
            errorMessage = '获取最大 answer-id 请求未收到响应，请检查后端服务是否运行。';
        }
        ElMessage.error(errorMessage); // 统一错误提示
        throw error; // 继续抛出错误
    }
};