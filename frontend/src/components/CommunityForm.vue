<template>
  <div class="community-content-wrapper">
    <!-- 帖子列表 -->
    <el-card class="post-list-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>社区帖子</span>
          <el-button type="primary" size="small" @click="showPostDialog = true">发布帖子</el-button>
        </div>
      </template>

      <div class="post-list">
        <el-card v-for="post in posts" :key="post.id" class="post-item" shadow="hover">
          <div class="post-header">
            <span class="post-title" @click="viewPostDetail(post)">{{ post.title }}</span>
            <span class="post-author">发布人: {{ post.authorName }}</span>
            <span class="post-time">{{ formatTime(post.publishTime) }}</span>
          </div>
          <div class="post-content">{{ post.content.substring(0, 100) }}...</div>
          <div class="post-actions">
            <el-button type="primary" text size="small" @click="viewPostDetail(post)">
              查看详情 ({{ post.replies ? post.replies.length : 0 }})
            </el-button>
            <el-button type="info" text size="small" @click="toggleCollect(post)">
              {{ post.isCollected ? '取消收藏' : '收藏' }}
            </el-button>
            <el-button v-if="post.attachedFileUrl" type="success" text size="small">
              <el-link :href="post.attachedFileUrl" target="_blank">查看附件</el-link>
            </el-button>
            <el-button
                v-if="userType === 0 || post.authorId === userId"
                type="danger"
                text
                size="small"
                @click="deletePost(post)"
            >
              删除
            </el-button>
          </div>
        </el-card>
        <el-empty v-if="posts.length === 0" description="暂无帖子"></el-empty>
      </div>
    </el-card>

    <!-- 收藏夹 -->
    <el-card class="collection-card" shadow="never" style="margin-top: 15px;">
      <template #header>
        <div class="card-header">
          <span>我的收藏夹</span>
          <el-button type="info" text size="small" @click="toggleCollectedView">
            {{ showCollectedPosts ? '隐藏' : '查看' }}
          </el-button>
        </div>
      </template>
      <div v-if="showCollectedPosts">
        <el-empty v-if="collectedPosts.length === 0" description="暂无收藏的帖子"></el-empty>
        <el-card v-for="post in collectedPosts" :key="post.id" class="post-item" shadow="hover">
          <div class="post-header">
            <span class="post-title" @click="viewPostDetail(post)">{{ post.title }}</span>
            <span class="post-author">发布人: {{ post.authorName }}</span>
            <span class="post-time">{{ formatTime(post.publishTime) }}</span>
          </div>
          <div class="post-content">{{ post.content.substring(0, 50) }}...</div>
          <div class="post-actions">
            <el-button type="primary" text size="small" @click="viewPostDetail(post)">
              查看详情 ({{ post.replies ? post.replies.length : 0 }})
            </el-button>
            <el-button type="info" text size="small" @click="toggleCollect(post)">取消收藏</el-button>
          </div>
        </el-card>
      </div>
    </el-card>

    <!-- 发布帖子对话框 -->
    <el-dialog v-model="showPostDialog" title="发布新帖子" width="600px">
      <el-form :model="postForm" :rules="postRules" ref="postFormRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="postForm.title" placeholder="请输入帖子标题"></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
              type="textarea"
              v-model="postForm.content"
              :rows="5"
              placeholder="请输入帖子内容"
          ></el-input>
        </el-form-item>
        <el-form-item label="上传文件">
          <el-upload
              class="upload-demo"
              drag
              action="#"
              :auto-upload="false"
              :on-change="handleNewPostFileChange"
              :on-remove="handleNewPostFileRemove"
              :file-list="newPostFileList"
              :limit="1"
              :on-exceed="handleExceed"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">拖拽文件到此处，或 <em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">仅支持图片、PDF、DOCX 等文件，大小不超过 50MB</div>
            </template>
          </el-upload>
          <el-progress v-if="newPostUploadProgress > 0 && newPostUploadProgress < 100" :percentage="newPostUploadProgress"></el-progress>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPostDialog = false; resetPostForm()">取消</el-button>
        <el-button type="primary" @click="submitPost" :loading="isPostSubmitting">发布</el-button>
      </template>
    </el-dialog>

    <!-- 帖子详情对话框 -->
    <el-dialog v-model="showPostDetailDialog" :title="detailPost.title" width="800px">
      <div class="detail-post-content">
        <div class="post-meta">
          <span>发布人: {{ detailPost.authorName }}</span>
          <span>时间: {{ formatTime(detailPost.publishTime) }}</span>
          <span v-if="detailPost.attachedFileUrl" class="attached-file">
            附件: <el-link type="primary" :href="detailPost.attachedFileUrl" target="_blank">查看附件</el-link>
          </span>
        </div>
        <p class="post-body">{{ detailPost.content }}</p>

        <el-divider>回复</el-divider>
        <div class="replies-section">
          <div v-for="reply in detailPost.replies" :key="reply.id" class="reply-item">
            <div class="reply-header">
              <span class="reply-author">{{ reply.authorName }}</span>
              <span class="reply-time">{{ formatTime(reply.publishTime) }}</span>
            </div>
            <p class="reply-content">{{ reply.content }}</p>
            <div v-if="reply.attachedFileUrl" class="attached-file">
              附件: <el-link type="primary" :href="reply.attachedFileUrl" target="_blank">查看附件</el-link>
            </div>
            <el-button
                v-if="userType === 2 || reply.authorId === userId"
                type="danger"
                text
                size="small"
                @click="deleteReply(detailPost, reply)"
            >
              删除
            </el-button>
          </div>
          <el-empty v-if="detailPost.replies && detailPost.replies.length === 0" description="暂无回复"></el-empty>
        </div>

        <el-form :model="replyForm" :rules="replyRules" ref="replyFormRef" class="reply-form">
          <el-form-item prop="content">
            <el-input
                type="textarea"
                v-model="replyForm.content"
                :rows="3"
                placeholder="发表你的回复..."
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-upload
                class="upload-demo"
                action="#"
                :auto-upload="false"
                :on-change="handleReplyFileChange"
                :on-remove="handleReplyFileRemove"
                :file-list="replyFileList"
                :limit="1"
                :on-exceed="handleExceed"
            >
              <el-button size="small" type="primary" :icon="UploadFilled">上传附件</el-button>
              <template #tip>
                <div class="el-upload__tip">支持图片、PDF、DOCX 等文件，大小不超过 50MB</div>
              </template>
            </el-upload>
            <el-progress v-if="replyUploadProgress > 0 && replyUploadProgress < 100" :percentage="replyUploadProgress"></el-progress>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitReply(detailPost)" :loading="isReplySubmitting">回复</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, defineProps } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox, FormInstance, UploadFile } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue';

import {
  CommunityService,
  PostResponse,
  ReplyResponse,
  PostForm,
  ReplyForm
} from '../api/community';

// Props 定义
const props = defineProps({
  usertype: {
    type: Number,
    required: true,
  },
  username: {
    type: String,
    required: true,
  },
  userid: {
    type: String,
    required: true,
  }
});

const router = useRouter();
const userType = computed(() => props.usertype);
const username = computed(() => props.username);
const userId = computed(() => props.userid);

console.log('userType:', userType.value, 'username:', username.value, 'userId:', userId.value);

// 响应式数据
const posts = ref<PostResponse[]>([]);
const collectedPosts = ref<PostResponse[]>([]);
const showCollectedPosts = ref(false);

// 发布帖子相关
const showPostDialog = ref(false);
const postFormRef = ref<FormInstance>();
const postForm = reactive<PostForm>({
  title: '',
  content: '',
  attachedFileUrl: undefined,
});
const newPostSelectedFile = ref<File | null>(null);
const newPostFileList = ref<UploadFile[]>([]);
const newPostUploadProgress = ref(0);
const isPostSubmitting = ref(false);

const postRules = reactive({
  title: [{ required: true, message: '请输入帖子标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入帖子内容', trigger: 'blur' }],
});

// 帖子详情相关
const showPostDetailDialog = ref(false);
const detailPost = ref<PostResponse>({} as PostResponse);

// 回复相关
const replyFormRef = ref<FormInstance>();
const replyForm = reactive<ReplyForm>({
  content: '',
  attachedFileUrl: undefined,
});
const replySelectedFile = ref<File | null>(null);
const replyFileList = ref<UploadFile[]>([]);
const replyUploadProgress = ref(0);
const isReplySubmitting = ref(false);

const replyRules = reactive({
  content: [{ required: true, message: '请输入回复内容', trigger: 'blur' }],
});

// 初始化
onMounted(() => {
  fetchPosts();
});

// 方法定义
const formatTime = (timeString: string): string => {
  const date = new Date(timeString);
  return date.toLocaleString('zh-CN');
};

const fetchPosts = async () => {
  try {
    const fetchedPosts = await CommunityService.getPosts();
    posts.value = fetchedPosts.map(post => ({
      ...post,
      isCollected: false // 默认设置为未收藏，实际项目中应该根据用户状态判断
    }));
  } catch (error) {
    console.error('获取帖子列表失败:', error);
    ElMessage.error('获取帖子列表失败');
  }
};

const fetchCollectedPosts = async () => {
  try {
    const collected = await CommunityService.getCollectedPosts(userId.value);
    collectedPosts.value = collected;

    // 更新主列表中的收藏状态
    posts.value.forEach(post => {
      post.isCollected = collected.some(cp => cp.id === post.id);
    });
  } catch (error) {
    console.error('获取收藏列表失败:', error);
    ElMessage.error('获取收藏列表失败');
  }
};

const toggleCollectedView = async () => {
  showCollectedPosts.value = !showCollectedPosts.value;
  if (showCollectedPosts.value) {
    await fetchCollectedPosts();
  }
};

// 文件上传限制处理
const handleExceed = () => {
  ElMessage.warning('最多只能上传一个文件');
};

// 帖子文件处理
const handleNewPostFileChange = (uploadFile: UploadFile) => {
  newPostSelectedFile.value = uploadFile.raw || null;
  newPostFileList.value = [uploadFile];
  newPostUploadProgress.value = 0;
};

const handleNewPostFileRemove = () => {
  newPostSelectedFile.value = null;
  newPostFileList.value = [];
  postForm.attachedFileUrl = undefined;
  newPostUploadProgress.value = 0;
};

const submitPost = async () => {
  if (!postFormRef.value) return;

  await postFormRef.value.validate(async (valid) => {
    if (valid) {
      isPostSubmitting.value = true;
      try {
        let finalAttachedFileUrl: string | undefined = undefined;

        // 如果有文件选中，先上传文件
        if (newPostSelectedFile.value) {
          ElMessage.info('正在上传文件，请稍候...');
          newPostUploadProgress.value = 10;
          try {
            const uploadResult = await CommunityService.uploadFile(newPostSelectedFile.value);
            finalAttachedFileUrl = uploadResult.url;
            newPostUploadProgress.value = 100;
            ElMessage.success('文件上传完成，正在发布帖子...');
          } catch (uploadError) {
            ElMessage.error('文件上传失败，请重试！');
            console.error('文件上传失败:', uploadError);
            isPostSubmitting.value = false;
            return;
          }
        }

        postForm.attachedFileUrl = finalAttachedFileUrl;

        const newPost = await CommunityService.createPost(postForm, userId.value);

        posts.value.unshift({ ...newPost, isCollected: false });
        showPostDialog.value = false;
        resetPostForm();
        ElMessage.success('帖子发布成功！');
        await fetchPosts();

      } catch (error) {
        ElMessage.error('帖子发布失败，请检查填写内容！');
        console.error('帖子发布失败:', error);
      } finally {
        isPostSubmitting.value = false;
      }
    } else {
      ElMessage.error('请填写帖子标题和内容！');
    }
  });
};

const resetPostForm = () => {
  if (postFormRef.value) {
    postFormRef.value.resetFields();
  }
  postForm.title = '';
  postForm.content = '';
  postForm.attachedFileUrl = undefined;
  newPostSelectedFile.value = null;
  newPostFileList.value = [];
  newPostUploadProgress.value = 0;
};

const viewPostDetail = async (post: PostResponse) => {
  try {
    const fetchedDetail = await CommunityService.getPostDetail(post.id);
    detailPost.value = { ...fetchedDetail, isCollected: post.isCollected };
    showPostDetailDialog.value = true;
  } catch (error) {
    console.error('获取帖子详情失败:', error);
    ElMessage.error('获取帖子详情失败');
  }
};

// 回复文件处理
const handleReplyFileChange = (uploadFile: UploadFile) => {
  replySelectedFile.value = uploadFile.raw || null;
  replyFileList.value = [uploadFile];
  replyUploadProgress.value = 0;
};

const handleReplyFileRemove = () => {
  replySelectedFile.value = null;
  replyFileList.value = [];
  replyForm.attachedFileUrl = undefined;
  replyUploadProgress.value = 0;
};

const submitReply = async (post: PostResponse) => {
  if (!replyFormRef.value) return;

  await replyFormRef.value.validate(async (valid) => {
    if (valid) {
      isReplySubmitting.value = true;
      try {
        let finalAttachedFileUrl: string | undefined = undefined;

        if (replySelectedFile.value) {
          ElMessage.info('正在上传文件，请稍候...');
          replyUploadProgress.value = 10;
          try {
            const uploadResult = await CommunityService.uploadFile(replySelectedFile.value);
            finalAttachedFileUrl = uploadResult.url;
            replyUploadProgress.value = 100;
            ElMessage.success('文件上传完成，正在提交回复...');
          } catch (uploadError) {
            ElMessage.error('文件上传失败，请重试！');
            console.error('文件上传失败:', uploadError);
            isReplySubmitting.value = false;
            return;
          }
        }

        replyForm.attachedFileUrl = finalAttachedFileUrl;

        const newReply = await CommunityService.createReply(post.id, replyForm, userId.value);

        const targetPost = posts.value.find(p => p.id === post.id);
        if (targetPost && targetPost.replies) {
          targetPost.replies.push(newReply);
        }
        if (detailPost.value.replies) {
          detailPost.value.replies.push(newReply);
        }

        resetReplyForm();
        ElMessage.success('回复成功！');

      } catch (error) {
        ElMessage.error('回复失败，请检查填写内容！');
        console.error('回复失败:', error);
      } finally {
        isReplySubmitting.value = false;
      }
    } else {
      ElMessage.error('请输入回复内容！');
    }
  });
};

const resetReplyForm = () => {
  if (replyFormRef.value) {
    replyFormRef.value.resetFields();
  }
  replyForm.content = '';
  replyForm.attachedFileUrl = undefined;
  replySelectedFile.value = null;
  replyFileList.value = [];
  replyUploadProgress.value = 0;
};

const deleteReply = async (post: PostResponse, replyToDelete: ReplyResponse) => {
  ElMessageBox.confirm(`确定删除这条回复吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await CommunityService.deleteReply(replyToDelete.id);

      const targetPost = posts.value.find(p => p.id === post.id);
      if (targetPost && targetPost.replies) {
        targetPost.replies = targetPost.replies.filter(reply => reply.id !== replyToDelete.id);
      }
      if (detailPost.value.replies) {
        detailPost.value.replies = detailPost.value.replies.filter(reply => reply.id !== replyToDelete.id);
      }

      ElMessage.success('回复删除成功');
    } catch (error) {
      console.error('删除回复失败:', error);
      ElMessage.error('删除回复失败');
    }
  }).catch(() => {
    // 用户取消删除
  });
};

const toggleCollect = async (post: PostResponse) => {
  try {
    const shouldCollect = !post.isCollected;
    await CommunityService.toggleCollect(post.id, shouldCollect, userId.value);

    post.isCollected = shouldCollect;

    // 如果当前显示收藏列表，需要更新收藏列表
    if (showCollectedPosts.value) {
      if (shouldCollect) {
        collectedPosts.value.push(post);
      } else {
        collectedPosts.value = collectedPosts.value.filter(cp => cp.id !== post.id);
      }
    }

    ElMessage.success(shouldCollect ? '收藏成功' : '取消收藏成功');
  } catch (error) {
    console.error('更新收藏状态失败:', error);
    ElMessage.error('操作失败，请重试');
  }
};

const deletePost = (postToDelete: PostResponse) => {
  ElMessageBox.confirm(`确定删除帖子"${postToDelete.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await CommunityService.deletePost(postToDelete.id);

      // 从帖子列表中移除
      posts.value = posts.value.filter(post => post.id !== postToDelete.id);

      // 如果在收藏列表中也要移除
      if (showCollectedPosts.value) {
        collectedPosts.value = collectedPosts.value.filter(post => post.id !== postToDelete.id);
      }

      ElMessage.success('帖子删除成功');
    } catch (error) {
      console.error('删除帖子失败:', error);
      ElMessage.error('删除帖子失败');
    }
  }).catch(() => {
    // 用户取消删除
  });
};
</script>

<style scoped>
.community-content-wrapper {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-list-card, .collection-card {
  margin-bottom: 20px;
}

.post-item {
  margin-bottom: 15px;
  border-left: 4px solid #409eff;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  flex-wrap: wrap;
  gap: 10px;
}

.post-title {
  font-weight: bold;
  font-size: 16px;
  color: #409eff;
  cursor: pointer;
  flex: 1;
  min-width: 200px;
}

.post-title:hover {
  text-decoration: underline;
}

.post-author {
  color: #666;
  font-size: 14px;
}

.post-time {
  color: #999;
  font-size: 12px;
}

.post-content {
  color: #333;
  line-height: 1.6;
  margin-bottom: 15px;
}

.post-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.detail-post-content {
  max-height: 70vh;
  overflow-y: auto;
}

.post-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
  color: #666;
  font-size: 14px;
  flex-wrap: wrap;
}

.post-body {
  line-height: 1.8;
  margin-bottom: 20px;
  color: #333;
}

.replies-section {
  margin-bottom: 20px;
}

.reply-item {
  border-left: 3px solid #e4e7ed;
  padding-left: 15px;
  margin-bottom: 15px;
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 4px;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.reply-author {
  font-weight: bold;
  color: #409eff;
}

.reply-time {
  color: #999;
  font-size: 12px;
}

.reply-content {
  line-height: 1.6;
  margin-bottom: 10px;
  color: #333;
}

.attached-file {
  margin-top: 10px;
  padding: 5px 0;
}

.reply-form {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.upload-demo :deep(.el-upload-dragger) {
  width: 100%;
}

@media (max-width: 768px) {
  .community-content-wrapper {
    padding: 10px;
  }

  .post-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .post-actions {
    justify-content: flex-start;
  }

  .post-meta {
    flex-direction: column;
    gap: 5px;
  }
}
</style>