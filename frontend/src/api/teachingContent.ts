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
    upload_time?: string;
    description?: string;
}

// 定义教学内容响应接口（与后端TeachingContentResponse对应）
export interface TeachingContentResponse {
    teachingContentId: string;
    subject: string;
    teacherId: string;
    teachingContentContent: string;
    classId?: string;
    name: string;
    uploadTime: string;
    description?: string;
}

/**
 * 获取所有教学内容列表
 */
export const fetchAllTeachingContent = async (): Promise<TeachingContentResponse[]> => {
    try {
        const response = await service.get<ApiResponse<TeachingContentResponse[]>>('/teaching-content/find');
        if (response.data && response.data.code === 200) {
            return response.data.data || [];
        } else {
            ElMessage.error(response.data?.message || '获取教学内容失败');
            console.error('获取教学内容失败:', response.data?.message);
            return [];
        }
    } catch (error) {
        ElMessage.error('请求教学内容失败，请检查网络或后端服务');
        console.error('请求教学内容失败:', error);
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
        const response = await service.post<ApiResponse>('/teaching-content/create', data);
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
 * 更新教学内容
 */
export const updateTeachingContent = async (id: string, data: TeachingContentRequest): Promise<boolean> => {
    try {
        const response = await service.put<ApiResponse>(`/teaching-content/update/${id}`, data);
        if (response.data && response.data.code === 200) {
            ElMessage.success('更新教学内容成功');
            return true;
        } else {
            ElMessage.error(response.data?.message || '更新教学内容失败');
            console.error('更新教学内容失败:', response.data?.message);
            return false;
        }
    } catch (error) {
        ElMessage.error('更新教学内容失败，请检查网络或后端服务');
        console.error('更新教学内容失败:', error);
        return false;
    }
};

/**
 * 删除教学内容
 */
export const deleteTeachingContent = async (id: string): Promise<boolean> => {
    try {
        const response = await service.delete<ApiResponse>(`/teaching-content/delete/${id}`);
        if (response.data && response.data.code === 200) {
            ElMessage.success('删除教学内容成功');
            return true;
        } else {
            ElMessage.error(response.data?.message || '删除教学内容失败');
            console.error('删除教学内容失败:', response.data?.message);
            return false;
        }
    } catch (error) {
        ElMessage.error('删除教学内容失败，请检查网络或后端服务');
        console.error('删除教学内容失败:', error);
        return false;
    }
};