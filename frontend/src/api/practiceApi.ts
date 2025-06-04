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
//下载专用api
interface FileOperationParams {
    fileId: string;
    path: string;
}

interface DownloadFileParams extends FileOperationParams {
    fileName: string;
    outFile: string; // 后端接口要求，但前端通常不关心实际保存路径
}

interface UploadFileResponse {
    code: number;
    message: string;
    data?: any; // 后端 /file/upload 返回的 data 字段，根据实际情况可能包含文件信息
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