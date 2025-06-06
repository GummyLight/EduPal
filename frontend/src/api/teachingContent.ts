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

// 定义教学内容响应接口
export interface TeachingContentResponse {
    id: string;
    title: string;
    content: string;
    teacher_id: string;
    class_id?: string;
    subject: string;
    create_time: string;
    update_time?: string;
    description?: string;
    file_path?: string;
    status?: number; // 状态：0-草稿，1-已发布，2-已归档
}

/**
 * 获取所有教学内容列表
 * @returns {Promise<TeachingContentResponse[]>} 教学内容列表数组
 */
export const fetchAllTeachingContent = async (): Promise<TeachingContentResponse[]> => {
    try {
        const response = await service.get<ApiResponse<TeachingContentResponse[]>>('/teaching-content/find');
        if (response.data && response.data.code === 200) {
            return response.data.data || [];
        } else {
            ElMessage.error(response.data?.message || '获取所有教学内容失败: 未知错误');
            console.error('获取所有教学内容失败:', response.data?.message || '未知错误');
            return [];
        }
    } catch (error) {
        ElMessage.error('请求获取所有教学内容失败，请检查网络或后端服务。');
        console.error('请求获取所有教学内容失败:', error);
        return [];
    }
};

/**
 * 根据标题模糊查询教学内容
 * @param title 教学内容标题关键字
 * @returns {Promise<TeachingContentResponse[]>} 符合条件的教学内容列表数组
 */
export const searchTeachingContentByTitle = async (title: string): Promise<TeachingContentResponse[]> => {
    try {
        const response = await service.get<ApiResponse<TeachingContentResponse[]>>('/teaching-content/search', {
            params: { title }
        });
        if (response.data && response.data.code === 200) {
            return response.data.data || [];
        } else {
            ElMessage.error(response.data?.message || '按标题查询教学内容失败: 未知错误');
            console.error('按标题查询教学内容失败:', response.data?.message || '未知错误');
            return [];
        }
    } catch (error) {
        ElMessage.error('请求按标题查询教学内容失败，请检查网络或后端服务。');
        console.error('请求按标题查询教学内容失败:', error);
        return [];
    }
};

/**
 * 根据学科查询教学内容
 * @param subject 学科名称
 * @returns {Promise<TeachingContentResponse[]>} 符合条件的教学内容列表数组
 */
export const searchTeachingContentBySubject = async (subject: string): Promise<TeachingContentResponse[]> => {
    try {
        const response = await service.get<ApiResponse<TeachingContentResponse[]>>('/teaching-content/subject', {
            params: { subject }
        });
        if (response.data && response.data.code === 200) {
            return response.data.data || [];
        } else {
            ElMessage.error(response.data?.message || '按学科查询教学内容失败: 未知错误');
            console.error('按学科查询教学内容失败:', response.data?.message || '未知错误');
            return [];
        }
    } catch (error) {
        ElMessage.error('请求按学科查询教学内容失败，请检查网络或后端服务。');
        console.error('请求按学科查询教学内容失败:', error);
        return [];
    }
};

/**
 * 删除教学内容记录
 * @param contentId 教学内容ID
 * @returns {Promise<ApiResponse>} 删除操作的响应
 */
export const deleteTeachingContentById = async (contentId: string): Promise<ApiResponse> => {
    try {
        const response = await service.delete<ApiResponse>(`/teaching-content/delete/${contentId}`);
        return response.data;
    } catch (error) {
        ElMessage.error(`请求删除教学内容 (ID: ${contentId}) 失败，请检查网络或后端服务。`);
        console.error(`请求删除教学内容 (ID: ${contentId}) 失败:`, error);
        return { code: 500, message: `请求删除教学内容 (ID: ${contentId}) 失败`, data: null };
    }
};

/**
 * 根据教师ID查询教学内容
 * @param teacherId 教师ID
 * @returns {Promise<TeachingContentResponse[]>} 符合条件的教学内容列表数组
 */
export const getTeachingContentByTeacher = async (teacherId: string): Promise<TeachingContentResponse[]> => {
    try {
        const response = await service.get<ApiResponse<TeachingContentResponse[]>>('/teaching-content/teacher', {
            params: { teacherId }
        });
        if (response.data && response.data.code === 200) {
            return response.data.data || [];
        } else {
            ElMessage.error(response.data?.message || '按教师查询教学内容失败: 未知错误');
            console.error('按教师查询教学内容失败:', response.data?.message || '未知错误');
            return [];
        }
    } catch (error) {
        ElMessage.error('请求按教师查询教学内容失败，请检查网络或后端服务。');
        console.error('请求按教师查询教学内容失败:', error);
        return [];
    }
};

/**
 * 根据班级ID查询教学内容
 * @param classId 班级ID
 * @returns {Promise<TeachingContentResponse[]>} 符合条件的教学内容列表数组
 */
export const getTeachingContentByClass = async (classId: string): Promise<TeachingContentResponse[]> => {
    try {
        const response = await service.get<ApiResponse<TeachingContentResponse[]>>('/teaching-content/class', {
            params: { classId }
        });
        if (response.data && response.data.code === 200) {
            return response.data.data || [];
        } else {
            ElMessage.error(response.data?.message || '按班级查询教学内容失败: 未知错误');
            console.error('按班级查询教学内容失败:', response.data?.message || '未知错误');
            return [];
        }
    } catch (error) {
        ElMessage.error('请求按班级查询教学内容失败，请检查网络或后端服务。');
        console.error('请求按班级查询教学内容失败:', error);
        return [];
    }
};