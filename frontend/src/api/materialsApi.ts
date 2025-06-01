// src/api/materialsApi.ts
import axios from 'axios';

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

/**
 * 预览文件
 * @param params 包含 fileId 和 path 的对象
 * @returns {string} 预览文件的完整 URL
 */
export function getPreviewFileUrl(params: FileOperationParams): string {
    // 注意：这里直接构建 URL，因为预览通常是直接在浏览器新标签页打开
    // encodeURIComponent 用于编码 URL 参数，防止路径中的特殊字符导致问题
    return `/file/previewFile?fileId=${params.fileId}&path=${encodeURIComponent(params.path)}`;
}

/**
 * 下载文件
 * @param params 包含 fileName, path, fileId, outFile 的对象
 * @returns {string} 下载文件的完整 URL
 */
export function getDownloadFileUrl(params: DownloadFileParams): string {
    // 注意：这里直接构建 URL，因为下载通常也是通过超链接或 window.open 实现
    return `/file/download?fileName=${encodeURIComponent(params.fileName)}&path=${encodeURIComponent(params.path)}&fileId=${params.fileId}&outFile=${encodeURIComponent(params.outFile)}`;
}

/**
 * 删除文件
 * @param params 包含 fileId 和 path 的对象
 * @returns {Promise<ApiResponse>} 删除操作的响应
 */
export async function deleteFile(params: FileOperationParams): Promise<ApiResponse> {
    const response = await axios.post<ApiResponse>('/file/delete', null, { // POST 请求通常将参数放在 body 中，但这里使用 params
        params: {
            fileId: params.fileId,
            path: params.path,
        }
    });
    return response.data;
}

// 假设你可能有一个获取资料列表的接口
// interface MaterialListItem {
//   id: string;
//   name: string;
//   uploader: string;
//   subject: string;
//   size: string;
//   rating: number;
//   uploadTime: string;
//   format: string;
//   description: string;
//   fileId: string; // 从后端获取的 fileId
//   // 其他后端可能返回的字段，例如实际存储的 toPath
// }

// interface GetMaterialsListParams {
//   name?: string;
//   uploader?: string;
//   subject?: string;
//   format?: string;
//   description?: string;
//   // 其他分页参数等
// }

// export async function getMaterialsList(params: GetMaterialsListParams): Promise<ApiResponse<MaterialListItem[]>> {
//   const response = await axios.get<ApiResponse<MaterialListItem[]>>('/materials/list', {
//     params: params
//   });
//   return response.data;
// }