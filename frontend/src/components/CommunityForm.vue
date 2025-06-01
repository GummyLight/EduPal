<template>
  <div class="community-content-wrapper">
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
            <span class="post-time">{{ post.publishTime }}</span>
          </div>
          <div class="post-content">{{ post.content.substring(0, 100) }}...</div>
          <div class="post-actions">
            <el-button type="primary" text size="small" @click="viewPostDetail(post)">查看详情 ({{ post.replies ? post.replies.length : 0 }})</el-button>
            <el-button type="info" text size="small" @click="toggleCollect(post)">
              {{ post.isCollected ? '取消收藏' : '收藏' }}
            </el-button>
            <el-button v-if="post.attachedFile" type="success" text size="small">
              <el-link :href="post.attachedFile.url" target="_blank">查看附件</el-link>
            </el-button>
            <el-button v-if="userType === 'teacher' || post.authorId === userId" type="danger" text size="small" @click="deletePost(post)">删除</el-button>
          </div>
        </el-card>
        <el-empty v-if="posts.length === 0" description="暂无帖子"></el-empty>
      </div>
    </el-card>

    <el-card class="collection-card" shadow="never" style="margin-top: 15px;">
      <template #header>
        <div class="card-header">
          <span>我的收藏夹</span>
          <el-button type="info" text size="small" @click="showCollectedPosts = !showCollectedPosts">
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
            <span class="post-time">{{ post.publishTime }}</span>
          </div>
          <div class="post-content">{{ post.content.substring(0, 50) }}...</div>
          <div class="post-actions">
            <el-button type="primary" text size="small" @click="viewPostDetail(post)">查看详情 ({{ post.replies ? post.replies.length : 0 }})</el-button>
            <el-button type="info" text size="small" @click="toggleCollect(post)">取消收藏</el-button>
          </div>
        </el-card>
      </div>
    </el-card>

    <el-dialog v-model="showPostDialog" :title="currentPost ? '编辑帖子' : '发布新帖子'" width="600px">
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

    <el-dialog v-model="showPostDetailDialog" :title="detailPost.title" width="800px">
      <div class="detail-post-content">
        <div class="post-meta">
          <span>发布人: {{ detailPost.authorName }}</span>
          <span>时间: {{ detailPost.publishTime }}</span>
          <span v-if="detailPost.attachedFile" class="attached-file">
            附件: <el-link type="primary" :href="detailPost.attachedFile.url" target="_blank">{{ detailPost.attachedFile.name }}</el-link>
          </span>
        </div>
        <p class="post-body">{{ detailPost.content }}</p>

        <el-divider>回复</el-divider>
        <div class="replies-section">
          <div v-for="reply in detailPost.replies" :key="reply.id" class="reply-item">
            <div class="reply-header">
              <span class="reply-author">{{ reply.authorName }}</span>
              <span class="reply-time">{{ reply.publishTime }}</span>
            </div>
            <p class="reply-content">{{ reply.content }}</p>
            <div v-if="reply.attachedFile" class="attached-file">
              附件: <el-link type="primary" :href="reply.attachedFile.url" target="_blank">{{ reply.attachedFile.name }}</el-link>
            </div>
            <el-button v-if="userType === 'teacher' || reply.authorId === userId" type="danger" text size="small" @click="deleteReply(detailPost, reply)">删除</el-button>
          </div>
          <el-empty v-if="detailPost.replies.length === 0" description="暂无回复"></el-empty>
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
import { ref, reactive, computed, onMounted, defineProps} from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElNotification, ElMessageBox, FormInstance, UploadFile } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue';

import { CommunityService, Post, Reply, PostForm, ReplyForm, AttachedFile } from '../api/community'; // 确保导入 AttachedFile

const props = defineProps({
  usertype: {
    type: String as () => 'teacher' | 'student',
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

const userType = computed(() => props.usertype);
const username = computed(() => props.username);
const userId = computed(() => props.userid);

const posts = ref<Post[]>([]);
const collectedPosts = computed(() => posts.value.filter(post => post.isCollected));
const showCollectedPosts = ref(false);

// 发布/编辑帖子相关
const showPostDialog = ref(false);
const postFormRef = ref<FormInstance>();
const postForm = reactive<PostForm>({ // PostForm 接口现在只包含 title, content, attachedFileUrl
  title: '',
  content: '',
  attachedFileUrl: undefined, // 初始化为 undefined
});
const newPostSelectedFile = ref<File | null>(null); // 存储新帖子待上传的文件对象
const newPostFileList = ref<UploadFile[]>([]); // Element Plus 上传组件的文件列表
const newPostUploadProgress = ref(0); // 新帖子文件上传进度
const isPostSubmitting = ref(false); // 发布帖子按钮加载状态

const currentPost = ref<Post | null>(null);

const postRules = reactive({
  title: [{ required: true, message: '请输入帖子标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入帖子内容', trigger: 'blur' }],
});

onMounted(() => {
  fetchPosts();
});

const fetchPosts = async () => {
  try {
    const fetchedPosts = await CommunityService.getPosts();
    posts.value = fetchedPosts;
  } catch (error) {
    console.error('获取帖子列表失败:', error);
  }
};

// 帖子文件选择处理
const handleNewPostFileChange = (uploadFile: UploadFile) => {
  newPostSelectedFile.value = uploadFile.raw || null;
  newPostFileList.value = [uploadFile];
  newPostUploadProgress.value = 0; // 重置进度
};
const handleNewPostFileRemove = () => {
  newPostSelectedFile.value = null;
  newPostFileList.value = [];
  postForm.attachedFileUrl = undefined; // 清空已上传的 URL
  newPostUploadProgress.value = 0;
};

const submitPost = async () => {
  if (!postFormRef.value) return;
  await postFormRef.value.validate(async (valid) => {
    if (valid) {
      isPostSubmitting.value = true; // 开始加载
      try {
        let finalAttachedFileUrl: string | undefined = undefined;

        // 如果有文件选中，先上传文件
        if (newPostSelectedFile.value) {
          ElMessage.info('正在上传文件，请稍候...');
          newPostUploadProgress.value = 10; // 模拟开始上传
          try {
            const uploadedFileInfo: AttachedFile = await CommunityService.uploadFile(newPostSelectedFile.value);
            finalAttachedFileUrl = uploadedFileInfo.url; // 获取后端返回的文件URL
            newPostUploadProgress.value = 100; // 上传完成
            ElMessage.success('文件上传完成，正在发布帖子...');
          } catch (uploadError) {
            ElMessage.error('文件上传失败，请重试！');
            console.error('文件上传失败:', uploadError);
            isPostSubmitting.value = false;
            return; // 文件上传失败，不继续发布帖子
          }
        }

        // 将文件URL绑定到帖子数据中
        postForm.attachedFileUrl = finalAttachedFileUrl;

        // 调用 CommunityService 的创建帖子方法，只传递 PostForm 对象
        const newPost = await CommunityService.createPost(postForm);

        posts.value.unshift({ ...newPost, isCollected: false }); // 假设后端返回的 Post 对象，默认未收藏
        ElMessage.success('帖子发布成功！');
        showPostDialog.value = false;
        resetPostForm();
        await fetchPosts(); // 重新加载帖子列表以确保最新数据

      } catch (error) {
        ElMessage.error('帖子发布失败，请检查填写内容！');
        console.error('帖子发布失败:', error);
      } finally {
        isPostSubmitting.value = false; // 结束加载
      }
    } else {
      ElMessage.error('请填写帖子标题和内容！');
    }
  });
};

const resetPostForm = () => {
  if (postFormRef.value) {
    postFormRef.value.resetFields();
    postForm.title = '';
    postForm.content = '';
    postForm.attachedFileUrl = undefined;
    newPostSelectedFile.value = null;
    newPostFileList.value = [];
    newPostUploadProgress.value = 0;
  }
  currentPost.value = null;
};

// 帖子详情相关
const showPostDetailDialog = ref(false);
const detailPost = ref<Post>({} as Post);

const viewPostDetail = async (post: Post) => {
  try {
    const fetchedDetail = await CommunityService.getPostDetail(post.id);
    detailPost.value = { ...fetchedDetail, isCollected: post.isCollected };
    showPostDetailDialog.value = true;
  } catch (error) {
    console.error('获取帖子详情失败:', error);
  }
};

// 回复相关
const replyFormRef = ref<FormInstance>();
const replyForm = reactive<ReplyForm>({ // ReplyForm 接口现在只包含 content, attachedFileUrl
  content: '',
  attachedFileUrl: undefined,
});
const replySelectedFile = ref<File | null>(null); // 存储回复待上传的文件对象
const replyFileList = ref<UploadFile[]>([]);
const replyUploadProgress = ref(0); // 回复文件上传进度
const isReplySubmitting = ref(false); // 回复按钮加载状态

const replyRules = reactive({
  content: [{ required: true, message: '请输入回复内容', trigger: 'blur' }],
});

// 回复文件选择处理
const handleReplyFileChange = (uploadFile: UploadFile) => {
  replySelectedFile.value = uploadFile.raw || null;
  replyFileList.value = [uploadFile];
  replyUploadProgress.value = 0; // 重置进度
};
const handleReplyFileRemove = () => {
  replySelectedFile.value = null;
  replyFileList.value = [];
  replyForm.attachedFileUrl = undefined; // 清空已上传的 URL
  replyUploadProgress.value = 0;
};

const submitReply = async (post: Post) => {
  if (!replyFormRef.value) return;
  await replyFormRef.value.validate(async (valid) => {
    if (valid) {
      isReplySubmitting.value = true; // 开始加载
      try {
        let finalAttachedFileUrl: string | undefined = undefined;

        // 如果有文件选中，先上传文件
        if (replySelectedFile.value) {
          ElMessage.info('正在上传文件，请稍候...');
          replyUploadProgress.value = 10; // 模拟开始上传
          try {
            const uploadedFileInfo: AttachedFile = await CommunityService.uploadFile(replySelectedFile.value);
            finalAttachedFileUrl = uploadedFileInfo.url; // 获取后端返回的文件URL
            replyUploadProgress.value = 100; // 上传完成
            ElMessage.success('文件上传完成，正在提交回复...');
          } catch (uploadError) {
            ElMessage.error('文件上传失败，请重试！');
            console.error('文件上传失败:', uploadError);
            isReplySubmitting.value = false;
            return; // 文件上传失败，不继续提交回复
          }
        }

        // 将文件URL绑定到回复数据中
        replyForm.attachedFileUrl = finalAttachedFileUrl;

        // 调用 CommunityService 的创建回复方法，只传递 ReplyForm 对象
        const newReply = await CommunityService.createReply(
            post.id,
            replyForm
        );

        const targetPost = posts.value.find(p => p.id === post.id);
        if (targetPost) {
          targetPost.replies.push(newReply);
          detailPost.value.replies.push(newReply);
        }
        ElMessage.success('回复成功！');
        resetReplyForm();
        await viewPostDetail(post); // 重新加载帖子详情，确保回复列表是最新的

      } catch (error) {
        ElMessage.error('回复失败，请检查填写内容！');
        console.error('回复失败:', error);
      } finally {
        isReplySubmitting.value = false; // 结束加载
      }
    } else {
      ElMessage.error('请输入回复内容！');
    }
  });
};

const resetReplyForm = () => {
  if (replyFormRef.value) {
    replyFormRef.value.resetFields();
    replyForm.content = '';
    replyForm.attachedFileUrl = undefined;
    replySelectedFile.value = null;
    replyFileList.value = [];
    replyUploadProgress.value = 0;
  }
};

const deleteReply = async (post: Post, replyToDelete: Reply) => {
  ElMessageBox.confirm(`确定删除这条回复吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await CommunityService.deleteReply(post.id, replyToDelete.id);
      const targetPost = posts.value.find(p => p.id === post.id);
      if (targetPost) {
        targetPost.replies = targetPost.replies.filter(reply => reply.id !== replyToDelete.id);
        detailPost.value.replies = detailPost.value.replies.filter(reply => reply.id !== replyToDelete.id);
      }
      ElMessage.success('回复删除成功！');
    } catch (error) {
      console.error('删除回复失败:', error);
    }
  }).catch(() => {
    // 用户取消删除
  });
};

const toggleCollect = async (post: Post) => {
  try {
    await CommunityService.toggleCollect(post.id, !post.isCollected);
    post.isCollected = !post.isCollected;
    ElMessage.success(post.isCollected ? `已收藏帖子：${post.title}` : `已取消收藏帖子：${post.title}`);
  } catch (error) {
    console.error('更新收藏状态失败:', error);
  }
};

const deletePost = (postToDelete: Post) => {
  ElMessageBox.confirm(`确定删除帖子《${postToDelete.title}》吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await CommunityService.deletePost(postToDelete.id);
      posts.value = posts.value.filter(post => post.id !== postToDelete.id);
      ElMessage.success('帖子删除成功！');
    } catch (error) {
      console.error('删除帖子失败:', error);
    }
  }).catch(() => {
    // 用户取消删除
  });
};

const handleExceed = () => {
  ElMessage.warning('只能上传一个文件，请先移除当前文件再上传。');
};

</script>

<style scoped>
/* 样式保持不变 */
.community-content-wrapper {
  flex: 1;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.post-list-card, .collection-card {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.post-list {
  padding-top: 10px;
}

.post-item {
  margin-bottom: 15px;
  border-radius: 4px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.05);
}
.post-item:last-child {
  margin-bottom: 0;
}

.post-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}
.post-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  cursor: pointer;
  margin-right: 15px;
}
.post-title:hover {
  color: #409EFF; /* Hover 效果 */
}
.post-author {
  font-size: 13px;
  color: #606266;
  margin-right: 15px;
}
.post-time {
  font-size: 12px;
  color: #909399;
}
.post-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 10px;
}
.post-actions {
  display: flex;
  gap: 10px;
  border-top: 1px solid #ebeef5;
  padding-top: 10px;
  margin-top: 10px;
}
.post-actions .el-button {
  padding: 5px 0;
}

/* 帖子详情对话框样式 */
.detail-post-content {
  padding: 0 10px;
}
.detail-post-content .post-meta {
  font-size: 13px;
  color: #909399;
  margin-bottom: 15px;
}
.detail-post-content .post-meta span {
  margin-right: 20px;
}
.detail-post-content .post-body {
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
  margin-bottom: 20px;
}
.detail-post-content .attached-file {
  margin-left: 10px;
}
.replies-section {
  max-height: 300px; /* 限制回复区域高度，超出滚动 */
  overflow-y: auto;
  padding-right: 10px; /* 留出滚动条空间 */
}
.reply-item {
  border-bottom: 1px dashed #ebeef5;
  padding: 10px 0;
}
.reply-item:last-child {
  border-bottom: none;
}
.reply-header {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}
.reply-author {
  font-weight: bold;
  font-size: 13px;
  color: #409EFF;
  margin-right: 10px;
}
.reply-time {
  font-size: 12px;
  color: #909399;
}
.reply-content {
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  margin-bottom: 5px;
}
.reply-form {
  margin-top: 20px;
}

/* 文件上传组件样式 */
.upload-demo {
  width: 100%;
}
.upload-demo .el-upload-dragger {
  width: 100%;
  padding: 20px 0;
}
.el-upload__text {
  font-size: 14px;
}
.el-upload__tip {
  font-size: 12px;
  color: #909399;
}
</style>