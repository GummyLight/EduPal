// src/api/materialUploadApi.ts

import axios from 'axios';
import { ElMessage } from 'element-plus'; // 引入 ElMessage 用于统一错误提示

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

// **更新：对应后端 ResourceRequest 的接口**
export interface MaterialSubmitData {
    resource_id: string;      // 对应 ResourceRequest 的 resource_id
    subject: string;          // 对应 ResourceRequest 的 subject
    teacher_id: string;       // 对应 ResourceRequest 的 teacher_id
    resource_content: string; // 对应 ResourceRequest 的 resource_content (文件在服务器上的完整路径)
    class_id?: string | null; // 对应 ResourceRequest 的 class_id，可能为空或 null
    name: string;             // 对应 ResourceRequest 的 name
    upload_time: string;      // 对应 ResourceRequest 的 upload_time (LocalDateTime 对应前端的 ISO string)
    description: string | null; // 对应 ResourceRequest 的 description，可能为空或 null
}

// 资料信息提交响应接口（与后端 /create 接口的返回结构一致）
interface MaterialSubmitResponse {
    code: number;
    message: string;
    data?: any; // 后端 ApiResponse 的 data 字段
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
 * 提交资料信息到后端服务器 (用于保存资料元数据)
 * @param data 资料信息对象 (ResourceRequest 结构)
 * @returns Promise<MaterialSubmitResponse>
 */
export async function submitMaterialInfo(data: MaterialSubmitData): Promise<MaterialSubmitResponse> {
    try {

        const response = await service.post<MaterialSubmitResponse>('/resource/create', data, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
        return response.data;
    } catch (error: any) {
        let errorMessage = '提交资料信息失败，请检查网络或后端服务。';
        if (axios.isAxiosError(error)) {
            errorMessage = error.response?.data?.message || `提交资料信息失败：HTTP 错误 ${error.response?.status}`;
            console.error('HTTP 错误响应数据:', error.response?.data);
            console.error('HTTP 错误状态码:', error.response?.status);
        } else if (error.request) {
            errorMessage = '提交资料信息请求未收到响应，请检查后端服务是否运行。';
        }
        ElMessage.error(errorMessage); // 统一错误提示
        throw error; // 继续抛出错误
    }


}

export const getMaxResourceId = async (): Promise<number> => {
    try {
        const response = await axios.get<ApiResponse<number>>('/resource/max-id');
        if (response.data.code === 200) {
            return response.data.data; // 返回最大 resource_id
        } else {
            throw new Error(response.data.message || '获取最大 resource_id 失败');
        }
    } catch (error) {
        console.error('获取最大 resource_id 错误:', error);
        throw error;
    }
};