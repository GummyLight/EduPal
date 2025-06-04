// src/api/materialsApi.ts
import axios from 'axios';
import { ResourceResponse } from '../types/resource.d'; // 确保这个路径正确
import { ElMessage } from 'element-plus';

const API_BASE_URL = 'http://localhost:8080'; // <-- 确保这是你的后端地址

// 核心：将 service 实例定义在文件顶部，确保它是全局可用的
const service = axios.create({
    baseURL: API_BASE_URL,
    timeout: 5000, // 请求超时时间
});

// 定义通用的 API 响应结构
interface ApiResponse<T = any> {
    code: number;
    message: string;
    data?: T;
}

// 定义下载和预览的参数接口
interface FileOperationParams {
    fileId: string;
    path: string;
}

interface DownloadFileParams extends FileOperationParams {
    fileName: string;
    outFile: string; // 后端接口要求，但前端通常不关心实际保存路径
}

// **重要：ResourceResponse 接口与后端返回的 JSON 字段名严格对齐 (下划线命名)**
export interface ResourceResponse {
    resource_id: string;
    subject: string;
    teacher_id: string;
    resource_content: string;
    class_id?: string; // class_id 可能为空
    name: string | null; // name 可能为 null
    upload_time: string | null; // upload_time 可能为 null
    description: string | null; // description 可能为 null
}

// ----------------------------------------------------
// 文件操作接口 (你提供的现有代码)
// ----------------------------------------------------
/**
 * 预览文件
 * @param params 包含 fileId 和 path 的对象
 * @returns {string} 预览文件的完整 URL
 */
export function getPreviewFileUrl(params: FileOperationParams): string {
    // 注意：这里直接构建 URL，因为预览通常是直接在浏览器新标签页打开
    // encodeURIComponent 用于编码 URL 参数，防止路径中的特殊字符导致问题
    // 假设你的后端接口路径是 /file/previewFile
    // 这里使用 service.defaults.baseURL 来确保与 service 实例的 baseURL 一致
    const baseUrl = service.defaults.baseURL || ''; // 获取 service 的 baseURL
    const url= `${baseUrl}/file/previewFile?fileId=${params.fileId}&path=${encodeURIComponent(params.path)}`;

    axios
        .get(url, { responseType: 'blob' })
        .then((response) => {
            const file = new Blob([response.data], { type: 'application/pdf' });
            const fileURL = URL.createObjectURL(file);
            window.open(fileURL);
        })
        .catch((error) => {
            console.error('预览文件失败:', error);
        });
}

/**
 * 下载文件
 * @param params 包含 fileName, path, fileId, outFile 的对象
 * @returns {string} 下载文件的完整 URL
 */
export function getDownloadFileUrl(params: DownloadFileParams): string {
    // 注意：这里直接构建 URL，因为下载通常也是通过超链接或 window.open 实现
    // 假设你的后端接口路径是 /file/download
    const baseUrl = service.defaults.baseURL || ''; // 获取 service 的 baseURL
    return `${baseUrl}/file/download?fileName=${encodeURIComponent(params.fileName)}&path=${encodeURIComponent(params.path)}&fileId=${params.fileId}&outFile=${encodeURIComponent(params.outFile)}`;
}

/**
 * 删除文件
 * @param params 包含 fileId 和 path 的对象
 * @returns {Promise<ApiResponse>} 删除操作的响应
 */
export async function deleteFile(params: FileOperationParams): Promise<ApiResponse> {
    // 假设你的后端接口路径是 /file/delete，并且是 POST 请求带参数
    // **这里使用 service 实例**
    const response = await service.post<ApiResponse>('/file/delete', null, { // POST 请求通常将参数放在 body 中，但这里使用 params
        params: {
            fileId: params.fileId,
            path: params.path,
        }
    });
    return response.data;
}

// ----------------------------------------------------
// 资料资源操作接口 (新增或从 Materials.vue 中提取)
// ----------------------------------------------------

/**
 * 获取所有资料列表
 * @returns {Promise<ResourceResponse[]>} 资料列表数组
 */
export const fetchAllResources = async (): Promise<ResourceResponse[]> => {
    try {
        // **这里使用 service 实例**
        const response = await service.get<ApiResponse<ResourceResponse[]>>('/resource/find');
        if (response.data && response.data.code === 200) {
            return response.data.data || []; // 如果 data 为 null，返回空数组
        } else {
            ElMessage.error(response.data?.message || '获取所有资源失败: 未知错误');
            console.error('获取所有资源失败:', response.data?.message || '未知错误');
            return [];
        }
    } catch (error) {
        ElMessage.error('请求获取所有资源失败，请检查网络或后端服务。');
        console.error('请求获取所有资源失败:', error);
        return [];
    }
};

/**
 * 根据名称模糊查询资料
 * @param name 资料名称关键字
 * @returns {Promise<ResourceResponse[]>} 符合条件的资料列表数组
 */
export const searchResourcesByName = async (name: string): Promise<ResourceResponse[]> => {
    try {
        // 假设你的后端接口路径是 /resource/search
        // **这里使用 service 实例**
        const response = await service.get<ApiResponse<ResourceResponse[]>>('/resource/search', {
            params: { name }
        });
        if (response.data && response.data.code === 200) {
            return response.data.data || [];
        } else {
            ElMessage.error(response.data?.message || '按名称查询资源失败: 未知错误'); // 统一添加 ElMessage 提示
            console.error('按名称查询资源失败:', response.data?.message || '未知错误');
            return [];
        }
    } catch (error) {
        ElMessage.error('请求按名称查询资源失败，请检查网络或后端服务。'); // 统一添加 ElMessage 提示
        console.error('请求按名称查询资源失败:', error);
        return [];
    }
};

/**
 * 删除资源记录 (后端 ResourceController 的 /resource/delete/{id} 接口)
 * @param resourceId 资源ID
 * @returns {Promise<ApiResponse>} 删除操作的响应
 */
export const deleteResourceById = async (resourceId: string): Promise<ApiResponse> => {
    try {
        // 假设你的后端接口路径是 /resource/delete/{id}
        // **这里使用 service 实例**
        const response = await service.delete<ApiResponse>(`/resource/delete/${resourceId}`);
        return response.data;
    } catch (error) {
        ElMessage.error(`请求删除资源 (ID: ${resourceId}) 失败，请检查网络或后端服务。`); // 统一添加 ElMessage 提示
        console.error(`请求删除资源 (ID: ${resourceId}) 失败:`, error);
        // 这里可以根据需要决定是 re-throw error 还是返回一个失败的 ApiResponse 结构
        // 为了和 deleteFile 保持一致，这里也返回一个模拟的失败响应
        return { code: 500, message: `请求删除资源 (ID: ${resourceId}) 失败`, data: null };
    }
};