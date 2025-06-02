// src/api/practiceApi.ts
import axios from 'axios'; // 直接导入 axios
import { ElMessage } from 'element-plus';

// 你的后端基础 URL
const API_BASE_URL = 'http://localhost:8080'; // 在此文件中定义

// --- 定义通用的 API 响应结构 ---
interface ApiResponse<T = any> {
    code: number;
    message: string;
    data?: T;
}

// --- 定义练习相关的接口数据结构 (与后端实体/DTO对应) ---
export interface ExerciseResponse {
    exercise_id: string;
    title: string;
    subject: string;
    content_type: string;
    difficulty: string;
    knowledge_points?: string;
    file_path?: string;
    published_by_user_id: string;
    publisher_name?: string;
    publish_time: string;
    due_time: string;
    description?: string;
    chapter?: string;
    class_ids?: string[];

    submitted_count?: number;
    unsubmitted_count?: number;
    graded_count?: number;

    my_status?: '未提交' | '已提交' | '已批改';
    my_score?: number | null;
    my_submission_file_path?: string;
}

export interface FetchExercisesFilters {
    keyword?: string;
    chapter?: string;
    difficulty?: string;
    subject?: string;
    classId?: string;
    userType: number;
    userId: string;
}

// 注意：此接口用于 PracticeDetail.vue，但因你希望只关注 PracticeForm，
// 我们仍然放在此处，以便于未来的扩展。
export interface SubmissionRequest {
    exercise_id: string;
    student_id: string;
    submission_file_path: string;
    submission_time: string;
}

// --- 辅助函数：创建带有认证的 Axios 配置 ---
function getAuthAxiosConfig() {
    const token = localStorage.getItem('token');
    const headers: Record<string, string> = {
        'Content-Type': 'application/json',
    };
    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }
    return {
        baseURL: API_BASE_URL,
        timeout: 10000,
        headers: headers
    };
}

// --- 练习列表相关的 API 函数 ---

/**
 * 获取练习列表 (PracticeForm.vue 使用)
 * @param filters 筛选条件，包含用户类型和用户ID
 * @returns Promise<ExerciseResponse[]>
 */
export async function fetchExercises(filters: FetchExercisesFilters): Promise<ExerciseResponse[]> {
    try {
        const response = await axios.get<ApiResponse<ExerciseResponse[]>>('/exercise/list', {
            ...getAuthAxiosConfig(), // 应用认证配置
            params: filters
        });
        if (response.data.code !== 200) {
            ElMessage.error(response.data.message || '获取练习列表失败');
            return []; // 返回空数组
        }
        return response.data.data || [];
    } catch (error: any) {
        console.error('Error fetching exercises:', error.response?.data || error.message || error);
        ElMessage.error(error.response?.data?.message || error.message || '获取练习列表失败，请检查网络或后端服务。');
        return [];
    }
}

/**
 * 删除练习 (PracticeForm.vue 教师操作)
 * @param exerciseId 练习ID
 * @returns Promise<ApiResponse>
 */
export async function deleteExercise(exerciseId: string): Promise<ApiResponse> {
    try {
        const response = await axios.delete<ApiResponse>(`/exercise/delete/${exerciseId}`, getAuthAxiosConfig());
        if (response.data.code !== 200) {
            ElMessage.error(response.data.message || '删除练习失败');
            throw new Error(response.data.message || '删除练习失败');
        }
        ElMessage.success(response.data.message || '练习删除成功！'); // 删除操作需要明确成功提示
        return response.data;
    } catch (error: any) {
        console.error('Error deleting exercise:', error.response?.data || error.message || error);
        ElMessage.error(error.response?.data?.message || error.message || '删除练习失败，请检查网络或后端服务。');
        throw error;
    }
}

// --- 单个练习详情相关的 API 函数 (为 PracticeDetail.vue 提供) ---
// 这些函数将同样需要添加认证配置和错误处理
/**
 * 获取单个练习详情 (学生/教师查看详情)
 * @param exerciseId 练习ID
 * @param studentId 学生ID (用于获取学生自己的提交状态，教师调用可省略)
 * @returns Promise<ExerciseResponse | null>
 */
export async function fetchExerciseDetail(exerciseId: string, studentId?: string): Promise<ExerciseResponse | null> {
    try {
        const response = await axios.get<ApiResponse<ExerciseResponse>>(`/exercise/${exerciseId}`, {
            ...getAuthAxiosConfig(),
            params: { studentId }
        });
        if (response.data.code !== 200) {
            ElMessage.error(response.data.message || '获取练习详情失败');
            return null;
        }
        return response.data.data || null;
    } catch (error: any) {
        console.error('Error fetching exercise detail:', error.response?.data || error.message || error);
        ElMessage.error(error.response?.data?.message || error.message || '获取练习详情失败，请检查网络或后端服务。');
        return null;
    }
}

/**
 * 提交学生作业记录 (如果文件上传和记录是分开的两个步骤)
 * @param submissionData 作业提交数据
 * @returns Promise<ApiResponse>
 */
export async function submitStudentHomework(submissionData: SubmissionRequest): Promise<ApiResponse> {
    try {
        const response = await axios.post<ApiResponse>('/submission/submit', submissionData, getAuthAxiosConfig());
        if (response.data.code !== 200) {
            ElMessage.error(response.data.message || '提交作业记录失败');
            throw new Error(response.data.message || '提交作业记录失败');
        }
        ElMessage.success(response.data.message || '作业提交记录成功！');
        return response.data;
    } catch (error: any) {
        console.error('Error submitting student homework record:', error.response?.data || error.message || error);
        ElMessage.error(error.response?.data?.message || error.message || '提交作业记录失败，请检查网络或后端服务。');
        throw error;
    }
}