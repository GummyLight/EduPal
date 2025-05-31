// src/api/community.ts

// 导入统一的 axios 实例
import api from './request'; // 假设 request.ts 在同一目录下

import { ElMessage } from 'element-plus'; // 导入 Element Plus 的消息提示组件

// ==============================
// 1. 定义数据接口 (与后端返回和前端模拟的数据结构保持一致)
// ==============================

/**
 * 附件信息接口
 */
export interface AttachedFile {
    name: string;
    url: string;
}

/**
 * 回复接口
 */
export interface Reply {
    id: string;
    authorId: string; // author 的 ID
    authorName: string; // 回复人的姓名
    content: string;
    publishTime: string; // 回复时间
    attachedFile?: AttachedFile; // 回复可能带附件
}

/**
 * 帖子接口
 */
export interface Post {
    id: string;
    title: string;
    content: string;
    authorId: string; // 发布者 ID
    authorName: string; // 发布者姓名
    publishTime: string; // 发布时间
    isCollected?: boolean; // 标记用户是否已收藏 (这个通常由前端逻辑或后端根据用户返回)
    attachedFile?: AttachedFile; // 帖子可能带附件
    replies: Reply[]; // 帖子的回复列表
}

/**
 * 帖子表单接口 (用于创建/更新帖子时发送给后端)
 */
export interface PostForm {
    title: string;
    content: string;
    // authorId 和 authorName 通常由后端从认证信息中获取，无需前端传递
    // file: File | null; // 文件对象，用于 FormData 上传
}

/**
 * 回复表单接口 (用于创建回复时发送给后端)
 */
export interface ReplyForm {
    content: string;
    // authorId 和 authorName 通常由后端从认证信息中获取，无需前端传递
    // file: File | null; // 文件对象，用于 FormData 上传
}


// ==============================
// 2. 封装 API 请求函数
// ==============================

export const CommunityService = {

    /**
     * 获取所有帖子列表
     * @returns {Promise<Post[]>} 帖子数组
     */
    async getPosts(): Promise<Post[]> {
        try {
            const response = await api.get<Post[]>('/community/posts');
            return response.data;
        } catch (error) {
            console.error('获取帖子列表失败:', error);
            throw error;
        }
    },

    /**
     * 获取单个帖子详情
     * @param postId 帖子ID
     * @returns {Promise<Post>} 帖子详情
     */
    async getPostDetail(postId: string): Promise<Post> {
        try {
            const response = await api.get<Post>(`/community/posts/${postId}`);
            return response.data;
        } catch (error) {
            console.error(`获取帖子 ${postId} 详情失败:`, error);
            throw error;
        }
    },

    /**
     * 发布新帖子
     * @param postForm 帖子表单数据 (包含 title, content, 以及可选的文件)
     * @param file 可选的文件对象
     * @returns {Promise<Post>} 新创建的帖子数据
     */
    async createPost(postForm: PostForm, file?: File | null): Promise<Post> {
        try {
            const formData = new FormData();
            formData.append('title', postForm.title);
            formData.append('content', postForm.content);
            if (file) {
                formData.append('file', file); // 'file' 是后端接收文件字段的名称，请根据后端API调整
            }

            const response = await api.post<Post>('/community/posts', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data', // 确保文件上传时使用 multipart/form-data
                },
            });
            ElMessage.success('帖子发布成功！');
            return response.data;
        } catch (error) {
            console.error('发布帖子失败:', error);
            throw error;
        }
    },

    /**
     * 删除帖子
     * @param postId 帖子ID
     * @returns {Promise<void>}
     */
    async deletePost(postId: string): Promise<void> {
        try {
            await api.delete(`/community/posts/${postId}`);
            ElMessage.success('帖子删除成功！');
        } catch (error) {
            console.error(`删除帖子 ${postId} 失败:`, error);
            throw error;
        }
    },

    /**
     * 提交回复
     * @param postId 帖子ID
     * @param replyForm 回复表单数据 (包含 content, 以及可选的文件)
     * @param file 可选的文件对象
     * @returns {Promise<Reply>} 新创建的回复数据
     */
    async createReply(postId: string, replyForm: ReplyForm, file?: File | null): Promise<Reply> {
        try {
            const formData = new FormData();
            formData.append('content', replyForm.content);
            if (file) {
                formData.append('file', file); // 'file' 是后端接收文件字段的名称，请根据后端API调整
            }

            const response = await api.post<Reply>(`/community/posts/${postId}/replies`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            ElMessage.success('回复成功！');
            return response.data;
        } catch (error) {
            console.error(`回复帖子 ${postId} 失败:`, error);
            throw error;
        }
    },

    /**
     * 删除回复
     * @param postId 帖子ID (如果后端删除回复需要帖子ID作为路径参数)
     * @param replyId 回复ID
     * @returns {Promise<void>}
     */
    async deleteReply(postId: string, replyId: string): Promise<void> {
        try {
            // 假设后端删除回复的路径是 /community/posts/{postId}/replies/{replyId}
            await api.delete(`/community/posts/${postId}/replies/${replyId}`);
            ElMessage.success('回复删除成功！');
        } catch (error) {
            console.error(`删除回复 ${replyId} 失败:`, error);
            throw error;
        }
    },

    /**
     * 收藏/取消收藏帖子
     * @param postId 帖子ID
     * @param collectStatus true 为收藏，false 为取消收藏
     * @returns {Promise<void>}
     */
    async toggleCollect(postId: string, collectStatus: boolean): Promise<void> {
        try {
            if (collectStatus) {
                // 假设收藏接口是 PUT /community/posts/{postId}/collect
                await api.put(`/community/posts/${postId}/collect`);
                ElMessage.success('帖子收藏成功！');
            } else {
                // 假设取消收藏接口是 DELETE /community/posts/{postId}/collect
                await api.delete(`/community/posts/${postId}/collect`);
                ElMessage.success('帖子已取消收藏！');
            }
        } catch (error) {
            console.error(`更新帖子 ${postId} 收藏状态失败:`, error);
            throw error;
        }
    },

    /**
     * (可选) 通用文件上传接口
     * 假设后端有一个 /api/upload 的通用文件上传接口
     * @param file 要上传的文件
     * @returns {Promise<AttachedFile>} 上传成功后的文件信息 (包含 URL)
     */
    async uploadFile(file: File): Promise<AttachedFile> {
        try {
            const formData = new FormData();
            formData.append('file', file); // 'file' 是后端接收文件字段的名称

            const response = await api.post<AttachedFile>('/upload', formData, { // 注意这里是 /upload，不是 /community/upload
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            // 假设后端返回的直接就是 AttachedFile 格式
            return response.data;
        } catch (error) {
            console.error('文件上传失败:', error);
            throw error;
        }
    },

    /**
     * (可选) 获取用户收藏的帖子
     * 假设后端有一个 /api/community/users/{userId}/collected-posts 接口
     * @param userId 用户ID
     * @returns {Promise<Post[]>} 用户收藏的帖子数组
     */
    async getCollectedPosts(userId: string): Promise<Post[]> {
        try {
            const response = await api.get<Post[]>(`/community/users/${userId}/collected-posts`);
            return response.data;
        } catch (error) {
            console.error(`获取用户 ${userId} 收藏的帖子失败:`, error);
            throw error;
        }
    }
};