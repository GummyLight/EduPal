// src/api/materialUploadApi.ts

import axios from 'axios';

interface UploadFileResponse {
    code: number;
    message: string;
    // data?: {
    //   resourceId: string;
    // };
}

interface MaterialSubmitData {
    name: string;
    subject: string;
    description: string;
    resourceId: string;
}

interface MaterialSubmitResponse {
    code: number;
    message: string;
}

/**
 * 上传文件到后端服务器
 * @param file 要上传的文件对象
 * @param path 文件存储的根路径
 * @param toPath 文件存储的目标子路径 (新增)
 * @param fileId 文件唯一ID (前端生成)
 * @returns Promise<UploadFileResponse>
 */
export async function uploadFile(file: File, path: string, toPath: string, fileId: string): Promise<UploadFileResponse> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('path', path);
    formData.append('toPath', toPath);
    formData.append('fileId', fileId);

    const response = await axios.post<UploadFileResponse>('/file/upload', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        },
    });
    return response.data;
}

/**
 * 提交资料信息到后端服务器
 * @param data 资料信息对象
 * @returns Promise<MaterialSubmitResponse>
 */
export async function submitMaterialInfo(data: MaterialSubmitData): Promise<MaterialSubmitResponse> {
    const response = await axios.post<MaterialSubmitResponse>('/materials', data, {
        headers: {
            'Content-Type': 'application/json'
        }
    });
    return response.data;
}