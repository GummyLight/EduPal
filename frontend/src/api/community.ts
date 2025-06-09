// src/api/community.ts

// 导入统一的 axios 实例
import api from './request';
import { ElMessage } from 'element-plus';

// ==============================
// 1. 定义数据接口 (与后端返回结构保持一致)
// ==============================

/**
 * 回复响应接口 (对应后端 ReplyResponse)
 */
export interface ReplyResponse {
    id: string;
    authorId: string;
    authorName: string;
    content: string;
    publishTime: string; // LocalDateTime 转换为字符串
    attachedFileUrl?: string;
}

/**
 * 帖子响应接口 (对应后端 PostResponse)
 */
export interface PostResponse {
    id: string;
    title: string;
    content: string;
    authorId: string;
    authorName: string;
    publishTime: string; // LocalDateTime 转换为字符串
    isCollected?: boolean;
    attachedFileUrl?: string;
    replies: ReplyResponse[];
}

// 为了保持向后兼容性，添加类型别名
export type Post = PostResponse;
export type Reply = ReplyResponse;

/**
 * 帖子请求接口 (对应后端 PostRequest)
 */
export interface PostRequest {
    authorId: string;
    title: string;
    content: string;
    attachedFileUrl?: string;
}

/**
 * 回复请求接口 (对应后端 ReplyRequest)
 */
export interface ReplyRequest {
    content: string;
    authorId: string;
    attachedFileUrl?: string;
}

/**
 * 前端表单接口 (用于表单绑定)
 */
export interface PostForm {
    title: string;
    content: string;
    attachedFileUrl?: string;
}

export interface ReplyForm {
    content: string;
    attachedFileUrl?: string;
}

/**
 * 文件上传响应接口
 */
export interface UploadResponse {
    url: string;
    fileName: string;
}

// ==============================
// 2. 封装 API 请求函数
// ==============================

export const CommunityService = {

    /**
     * 获取帖子列表
     * GET /community/posts
     */
    async getPosts(): Promise<PostResponse[]> {
        try {
            const response = await api.get<PostResponse[]>('/community/posts');
            return response.data;
        } catch (error) {
            console.error('获取帖子列表失败:', error);
            ElMessage.error('获取帖子列表失败');
            throw error;
        }
    },

    /**
     * 获取帖子详情
     * GET /community/posts/{postId}
     */
    async getPostDetail(postId: string): Promise<PostResponse> {
        try {
            const response = await api.get<PostResponse>(`/community/posts/${postId}`);
            return response.data;
        } catch (error) {
            console.error(`获取帖子 ${postId} 详情失败:`, error);
            ElMessage.error('获取帖子详情失败');
            throw error;
        }
    },

    /**
     * 创建帖子
     * POST /community/posts
     */
    async createPost(postForm: PostForm, authorId: string): Promise<PostResponse> {
        try {
            const postRequest: PostRequest = {
                authorId: authorId,
                title: postForm.title,
                content: postForm.content,
                attachedFileUrl: postForm.attachedFileUrl
            };

            const response = await api.post<PostResponse>('/community/posts', postRequest);
            ElMessage.success('帖子发布成功！');
            return response.data;
        } catch (error) {
            console.error('发布帖子失败:', error);
            ElMessage.error('发布帖子失败');
            throw error;
        }
    },

    /**
     * 删除帖子
     * DELETE /community/posts/{postId}
     */
    async deletePost(postId: string): Promise<void> {
        try {
            await api.delete(`/community/posts/${postId}`);
            ElMessage.success('帖子删除成功！');
        } catch (error) {
            console.error(`删除帖子 ${postId} 失败:`, error);
            ElMessage.error('删除帖子失败');
            throw error;
        }
    },

    /**
     * 创建回复
     * POST /community/posts/{postId}/replies
     */
    async createReply(postId: string, replyForm: ReplyForm, authorId: string): Promise<ReplyResponse> {
        try {
            const replyRequest: ReplyRequest = {
                content: replyForm.content,
                authorId: authorId,
                attachedFileUrl: replyForm.attachedFileUrl
            };

            const response = await api.post<ReplyResponse>(`/community/posts/${postId}/replies`, replyRequest);
            ElMessage.success('回复成功！');
            return response.data;
        } catch (error) {
            console.error(`回复帖子 ${postId} 失败:`, error);
            ElMessage.error('回复失败');
            throw error;
        }
    },

    /**
     * 删除回复
     * DELETE /community/posts/{postId}/replies/{replyId}
     */
    async deleteReply(replyId: string): Promise<void> {
        try {
            // 根据你的后端接口，这里只需要 replyId
            await api.delete(`/community/posts/*/replies/${replyId}`);
            ElMessage.success('回复删除成功！');
        } catch (error) {
            console.error(`删除回复 ${replyId} 失败:`, error);
            ElMessage.error('删除回复失败');
            throw error;
        }
    },

    /**
     * 收藏帖子
     * PUT /community/posts/{postId}/collect/{userId}
     */
    async collectPost(postId: string, userId: string): Promise<void> {
        try {
            await api.put(`/community/posts/${postId}/collect/${userId}`);
            ElMessage.success('帖子收藏成功！');
        } catch (error) {
            console.error(`收藏帖子 ${postId} 失败:`, error);
            ElMessage.error('收藏帖子失败');
            throw error;
        }
    },

    /**
     * 取消收藏帖子
     * DELETE /community/posts/{postId}/collect/{userId}
     */
    async uncollectPost(postId: string, userId: string): Promise<void> {
        try {
            await api.delete(`/community/posts/${postId}/collect/${userId}`);
            ElMessage.success('取消收藏成功！');
        } catch (error) {
            console.error(`取消收藏帖子 ${postId} 失败:`, error);
            ElMessage.error('取消收藏失败');
            throw error;
        }
    },

    /**
     * 切换收藏状态
     */
    async toggleCollect(postId: string, shouldCollect: boolean, userId: string): Promise<void> {
        if (shouldCollect) {
            await this.collectPost(postId, userId);
        } else {
            await this.uncollectPost(postId, userId);
        }
    },

    /**
     * 获取用户收藏的帖子列表
     * GET /community/users/{userId}/collected-posts
     */
    async getCollectedPosts(userId: string): Promise<PostResponse[]> {
        try {
            const response = await api.get<PostResponse[]>(`/community/users/${userId}/collected-posts`);
            return response.data;
        } catch (error) {
            console.error(`获取用户 ${userId} 收藏的帖子失败:`, error);
            ElMessage.error('获取收藏列表失败');
            throw error;
        }
    },

    /**
     * 文件上传接口 (通用)
     * 假设有一个通用的文件上传接口
     */
    async uploadFile(file: File): Promise<UploadResponse> {
        try {
            const formData = new FormData();
            formData.append('file', file);

            const response = await api.post<UploadResponse>('/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            return response.data;
        } catch (error) {
            console.error('文件上传失败:', error);
            ElMessage.error('文件上传失败');
            throw error;
        }
    }
};