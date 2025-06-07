// src/api/teachingContent.ts
import axios from 'axios';
import { ElMessage } from 'element-plus';

const API_BASE_URL = 'http://localhost:8080';

// 创建axios实例
const service = axios.create({
    baseURL: API_BASE_URL,
    timeout: 5000,
});

// 定义通用的API响应结构
interface ApiResponse<T = any> {
    code: number;
    message: string;
    data?: T;
}

// 定义教学内容请求接口（与后端TeachingContentRequest对应）
export interface TeachingContentRequest {
    teaching_content_id: string;
    subject: string;
    teacher_id: string;
    teaching_content_content: string;
    class_id?: string;
    name: string;
    upload_time?: string | null;
    description?: string;
}

// 定义教学内容响应接口（与后端TeachingContent实体对应）
export interface TeachingContentResponse {
    teaching_content_id: string;
    subject: string;
    teacher_id: string;
    teaching_content_content: string;
    class_id?: string;
    name: string;
    upload_time: string;
    description?: string;
}

/**
 * 获取所有教学内容列表
 */
export const fetchAllTeachingContent = async (): Promise<TeachingContentResponse[]> => {
    try {
        console.log('获取教学内容列表...');
        const response = await service.get<ApiResponse<TeachingContentResponse[]>>('/teaching-content/find');
        console.log('获取响应:', response.data);
        
        if (response.data && response.data.code === 200) {
            return response.data.data || [];
        } else {
            console.error('获取教学内容失败:', response.data?.message);
            return [];
        }
    } catch (error: any) {
        const errorMessage = error?.response?.data?.message || error?.message || '请求教学内容失败，请检查网络或后端服务';
        ElMessage.error(errorMessage);
        console.error('请求教学内容失败:', error);
        console.error('错误详情:', error?.response?.data);
        return [];
    }
};

/**
 * 按名称搜索教学内容
 */
export const searchTeachingContentByName = async (name: string): Promise<TeachingContentResponse[]> => {
    try {
        const response = await service.get<ApiResponse<TeachingContentResponse[]>>('/teaching-content/search', {
            params: { name }
        });
        if (response.data && response.data.code === 200) {
            return response.data.data || [];
        } else {
            ElMessage.error(response.data?.message || '搜索教学内容失败');
            console.error('搜索教学内容失败:', response.data?.message);
            return [];
        }
    } catch (error) {
        ElMessage.error('搜索教学内容失败，请检查网络或后端服务');
        console.error('搜索教学内容失败:', error);
        return [];
    }
};

/**
 * 创建新的教学内容
 */
export const createTeachingContent = async (data: TeachingContentRequest): Promise<boolean> => {
    try {
        // 创建请求数据副本，移除upload_time字段（后端会自动设置）
        const requestData = {
            teaching_content_id: data.teaching_content_id,
            subject: data.subject,
            teacher_id: data.teacher_id,
            teaching_content_content: data.teaching_content_content,
            class_id: data.class_id || null,
            name: data.name,
            description: data.description || null
        };
        
        const response = await service.post<ApiResponse>('/teaching-content/create', requestData);
        if (response.data && response.data.code === 200) {
            ElMessage.success('创建教学内容成功');
            return true;
        } else {
            ElMessage.error(response.data?.message || '创建教学内容失败');
            console.error('创建教学内容失败:', response.data?.message);
            return false;
        }
    } catch (error) {
        ElMessage.error('创建教学内容失败，请检查网络或后端服务');
        console.error('创建教学内容失败:', error);
        return false;
    }
};

/**
 * 删除教学内容
 */
export const deleteTeachingContent = async (id: string): Promise<boolean> => {
    try {
        console.log('删除教学内容，ID:', id);
        const response = await service.delete<ApiResponse>(`/teaching-content/delete/${id}`);
        console.log('删除响应:', response.data);
        
        if (response.data && response.data.code === 200) {
            ElMessage.success('删除教学内容成功');
            return true;
        } else {
            ElMessage.error(response.data?.message || '删除教学内容失败');
            console.error('删除教学内容失败:', response.data?.message);
            return false;
        }
    } catch (error: any) {
        console.error('删除API调用失败，完整错误:', error);
        console.error('错误响应数据:', error?.response?.data);
        console.error('错误状态码:', error?.response?.status);
        console.error('错误消息:', error?.message);
        
        let errorMessage = '删除教学内容失败';
        if (error?.response?.data?.message) {
            errorMessage = `删除失败: ${error.response.data.message}`;
        } else if (error?.response?.status === 500) {
            errorMessage = '服务器内部错误，删除失败';
        } else if (error?.message) {
            errorMessage = `删除失败: ${error.message}`;
        }
        
        ElMessage.error(errorMessage);
        return false;
    }
};