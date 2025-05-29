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
            <span class="post-author">发布人: {{ post.author }}</span>
            <span class="post-time">{{ post.publishTime }}</span>
          </div>
          <div class="post-content">{{ post.content.substring(0, 100) }}...</div>
          <div class="post-actions">
            <el-button type="primary" text size="small" @click="viewPostDetail(post)">查看详情 ({{ post.replies.length }})</el-button>
            <el-button type="info" text size="small" @click="toggleCollect(post)">
              {{ post.isCollected ? '取消收藏' : '收藏' }}
            </el-button>
            <el-button type="success" text size="small" @click="handleUploadFileForPost(post)">上传文件</el-button>
            <el-button v-if="userType === 'teacher'" type="danger" text size="small" @click="deletePost(post)">删除</el-button>
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
            <span class="post-author">发布人: {{ post.author }}</span>
            <span class="post-time">{{ post.publishTime }}</span>
          </div>
          <div class="post-content">{{ post.content.substring(0, 50) }}...</div>
          <div class="post-actions">
            <el-button type="primary" text size="small" @click="viewPostDetail(post)">查看详情 ({{ post.replies.length }})</el-button>
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
        <el-form-item label="上传文件" v-if="userType === 'teacher' || userType === 'student'">
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
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPostDialog = false">取消</el-button>
        <el-button type="primary" @click="submitPost">发布</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showPostDetailDialog" :title="detailPost.title" width="800px">
      <div class="detail-post-content">
        <div class="post-meta">
          <span>发布人: {{ detailPost.author }}</span>
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
              <span class="reply-author">{{ reply.author }}</span>
              <span class="reply-time">{{ reply.time }}</span>
            </div>
            <p class="reply-content">{{ reply.content }}</p>
            <div v-if="reply.attachedFile" class="attached-file">
              附件: <el-link type="primary" :href="reply.attachedFile.url" target="_blank">{{ reply.attachedFile.name }}</el-link>
            </div>
            <el-button v-if="userType === 'teacher'" type="success" text size="small" @click="handleUploadFileForReply(reply)">上传文件</el-button>
            <el-button v-if="userType === 'teacher' || reply.author === username" type="danger" text size="small" @click="deleteReply(detailPost, reply)">删除</el-button>
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
          <el-form-item v-if="userType === 'teacher' || userType === 'student'">
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
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitReply(detailPost)">回复</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>

    <el-dialog v-model="showFileUploadDialog" title="上传文件" width="500px">
      <el-upload
          class="upload-demo"
          drag
          action="#"
          :auto-upload="false"
          :on-change="handleFileUploadChange"
          :on-remove="handleFileUploadRemove"
          :file-list="currentFileUploadList"
          :limit="1"
          :on-exceed="handleExceed"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">拖拽文件到此处，或 <em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">支持图片、PDF、DOCX 等文件，大小不超过 50MB</div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="showFileUploadDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmFileUpload">确认上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed,defineProps} from 'vue';
import { ElMessage, ElNotification, ElMessageBox, FormInstance, UploadFile, UploadFiles } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue';

const props = defineProps({
  usertype: {
    type: String as () => 'teacher' | 'student', // 明确类型
    required: true,
  },
  username: { // 确保也接收 username，因为您在模板中也使用了它
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
const userId = computed(() => props.userid); // 同样处理 username
// 模拟用户类型和用户名
//const userType = ref<'teacher' | 'student'>('student'); // 默认为 student，可以根据登录状态动态设置
// const userType = ref('student');
// const username = ref('学生A'); // 模拟当前登录用户名

// 帖子数据结构
interface Post {
  id: string;
  title: string;
  content: string;
  author: string;
  publishTime: string;
  isCollected: boolean;
  attachedFile?: { name: string; url: string; raw: File | null };
  replies: Reply[];
}

// 回复数据结构
interface Reply {
  id: string;
  author: string;
  time: string;
  content: string;
  attachedFile?: { name: string; url: string; raw: File | null };
}

// 模拟帖子数据
const posts = ref<Post[]>([
  {
    id: 'p001',
    title: '关于期末考试复习资料的讨论',
    content: '同学们，大家觉得期末考试重点在哪里？有没有好的复习资料可以分享一下？',
    author: '学生A',
    publishTime: '2025-05-25 10:30',
    isCollected: false,
    replies: [
      { id: 'r001', author: '学生B', time: '2025-05-25 10:45', content: '我觉得线性代数是重点，老师之前划过几个大题类型。' },
      { id: 'r002', author: '张老师', time: '2025-05-25 11:00', content: '大家可以多关注平时作业中的易错点，那些很可能出题。', attachedFile: { name: '易错点汇总.pdf', url: '/mock-materials/yicuodian_huizong.pdf', raw: null } },
    ],
  },
  {
    id: 'p002',
    title: '求助：物理实验报告怎么写？',
    content: '第一次写物理实验报告，有点不知道从何下手，有模板或者范例可以参考吗？',
    author: '学生C',
    publishTime: '2025-05-24 15:00',
    isCollected: false,
    replies: [],
  },
  {
    id: 'p003',
    title: '数学建模竞赛组队',
    content: '有没有对数学建模感兴趣的同学？我想组个队，我们还缺一个擅长编程的。',
    author: '王老师',
    publishTime: '2025-05-23 09:00',
    isCollected: false,
    attachedFile: { name: '竞赛介绍.docx', url: '/mock-materials/jingsai_jieshao.docx', raw: null },
    replies: [
      { id: 'r003', author: '学生D', time: '2025-05-23 09:30', content: '老师，我对编程比较熟悉，可以加入吗？' },
    ],
  },
]);

// 模拟已收藏的帖子
const collectedPosts = computed(() => posts.value.filter(post => post.isCollected));
const showCollectedPosts = ref(false); // 控制收藏夹显示/隐藏

// 发布/编辑帖子相关
const showPostDialog = ref(false);
const postFormRef = ref<FormInstance>();
const postForm = reactive({
  title: '',
  content: '',
  attachedFile: null as File | null,
});
const newPostFileList = ref<UploadFile[]>([]);
const currentPost = ref<Post | null>(null); // 用于判断是发布新帖还是编辑旧帖

const postRules = reactive({
  title: [{ required: true, message: '请输入帖子标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入帖子内容', trigger: 'blur' }],
});

// 帖子文件上传处理
const handleNewPostFileChange = (uploadFile: UploadFile) => {
  postForm.attachedFile = uploadFile.raw || null;
  newPostFileList.value = [uploadFile];
};
const handleNewPostFileRemove = () => {
  postForm.attachedFile = null;
  newPostFileList.value = [];
};

const submitPost = async () => {
  if (!postFormRef.value) return;
  await postFormRef.value.validate((valid) => {
    if (valid) {
      const now = new Date();
      const newPost: Post = {
        id: `p${Date.now()}`, // 简单的ID生成
        title: postForm.title,
        content: postForm.content,
        author: username.value,
        publishTime: `${now.getFullYear()}-${(now.getMonth() + 1).toString().padStart(2, '0')}-${now.getDate().toString().padStart(2, '0')} ${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`,
        isCollected: false,
        replies: [],
      };

      if (postForm.attachedFile) {
        newPost.attachedFile = {
          name: postForm.attachedFile.name,
          url: URL.createObjectURL(postForm.attachedFile), // 创建一个临时的URL用于预览
          raw: postForm.attachedFile,
        };
      }

      posts.value.unshift(newPost); // 新帖子放在最前面
      ElMessage.success('帖子发布成功！');
      showPostDialog.value = false;
      resetPostForm();
    } else {
      ElMessage.error('请填写帖子标题和内容！');
    }
  });
};

const resetPostForm = () => {
  if (postFormRef.value) {
    postFormRef.value.resetFields();
    postForm.attachedFile = null;
    newPostFileList.value = [];
  }
};

// 帖子详情相关
const showPostDetailDialog = ref(false);
const detailPost = ref<Post>({} as Post); // 当前查看详情的帖子

const viewPostDetail = (post: Post) => {
  detailPost.value = { ...post }; // 复制一份，避免直接修改原始数据
  showPostDetailDialog.value = true;
};

// 回复相关
const replyFormRef = ref<FormInstance>();
const replyForm = reactive({
  content: '',
  attachedFile: null as File | null,
});
const replyFileList = ref<UploadFile[]>([]);
const replyRules = reactive({
  content: [{ required: true, message: '请输入回复内容', trigger: 'blur' }],
});

// 回复文件上传处理
const handleReplyFileChange = (uploadFile: UploadFile) => {
  replyForm.attachedFile = uploadFile.raw || null;
  replyFileList.value = [uploadFile];
};
const handleReplyFileRemove = () => {
  replyForm.attachedFile = null;
  replyFileList.value = [];
};

const submitReply = async (post: Post) => {
  if (!replyFormRef.value) return;
  await replyFormRef.value.validate((valid) => {
    if (valid) {
      const now = new Date();
      const newReply: Reply = {
        id: `r${Date.now()}`,
        author: username.value,
        time: `${now.getFullYear()}-${(now.getMonth() + 1).toString().padStart(2, '0')}-${now.getDate().toString().padStart(2, '0')} ${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`,
        content: replyForm.content,
      };

      if (replyForm.attachedFile) {
        newReply.attachedFile = {
          name: replyForm.attachedFile.name,
          url: URL.createObjectURL(replyForm.attachedFile),
          raw: replyForm.attachedFile,
        };
      }

      // 找到原帖并添加回复
      const targetPost = posts.value.find(p => p.id === post.id);
      if (targetPost) {
        targetPost.replies.push(newReply);
        // 更新 detailPost，以在详情对话框中显示新回复
        detailPost.value.replies.push(newReply);
        ElMessage.success('回复成功！');
        resetReplyForm();
      }
    } else {
      ElMessage.error('请输入回复内容！');
    }
  });
};

const resetReplyForm = () => {
  if (replyFormRef.value) {
    replyFormRef.value.resetFields();
    replyForm.attachedFile = null;
    replyFileList.value = [];
  }
};

const deleteReply = (post: Post, replyToDelete: Reply) => {
  ElMessageBox.confirm(`确定删除这条回复吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    const targetPost = posts.value.find(p => p.id === post.id);
    if (targetPost) {
      targetPost.replies = targetPost.replies.filter(reply => reply.id !== replyToDelete.id);
      detailPost.value.replies = detailPost.value.replies.filter(reply => reply.id !== replyToDelete.id);
      ElMessage.success('回复删除成功！');
    }
  }).catch(() => {
    // 用户取消删除
  });
};


// 收藏功能
const toggleCollect = (post: Post) => {
  post.isCollected = !post.isCollected;
  ElMessage.success(post.isCollected ? `已收藏帖子：${post.title}` : `已取消收藏帖子：${post.title}`);
};

// 删除帖子功能 (仅教师)
const deletePost = (postToDelete: Post) => {
  ElMessageBox.confirm(`确定删除帖子《${postToDelete.title}》吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    posts.value = posts.value.filter(post => post.id !== postToDelete.id);
    ElMessage.success('帖子删除成功！');
  }).catch(() => {
    // 用户取消删除
  });
};

// 通用文件上传处理（用于帖子内或回复内的文件上传）
const showFileUploadDialog = ref(false);
const currentFileUploadList = ref<UploadFile[]>([]);
let currentUploadTarget: Post | Reply | null = null; // 标记当前文件上传的目标是帖子还是回复

const handleUploadFileForPost = (post: Post) => {
  currentUploadTarget = post;
  currentFileUploadList.value = post.attachedFile ? [{ name: post.attachedFile.name, url: post.attachedFile.url }] as UploadFile[] : [];
  showFileUploadDialog.value = true;
};

const handleUploadFileForReply = (reply: Reply) => {
  currentUploadTarget = reply;
  currentFileUploadList.value = reply.attachedFile ? [{ name: reply.attachedFile.name, url: reply.attachedFile.url }] as UploadFile[] : [];
  showFileUploadDialog.value = true;
};


const handleFileUploadChange = (uploadFile: UploadFile) => {
  if (currentUploadTarget) {
    // 这里实际上应该将文件上传到服务器，并获取到 URL
    // 模拟：直接使用临时 URL
    currentFileUploadList.value = [uploadFile];
  }
};

const handleFileUploadRemove = () => {
  currentFileUploadList.value = [];
};

const confirmFileUpload = () => {
  if (currentUploadTarget && currentFileUploadList.value.length > 0) {
    const uploadedFile = currentFileUploadList.value[0].raw;
    if (uploadedFile) {
      const fileInfo = {
        name: uploadedFile.name,
        url: URL.createObjectURL(uploadedFile), // 实际应该是服务器返回的URL
        raw: uploadedFile,
      };

      currentUploadTarget.attachedFile = fileInfo;
      ElMessage.success(`文件上传成功: ${fileInfo.name}`);
      showFileUploadDialog.value = false;
    }
  } else {
    ElMessage.warning('请选择要上传的文件。');
  }
};

// 通用文件超出限制处理
const handleExceed = () => {
  ElMessage.warning('只能上传一个文件，请先移除当前文件再上传。');
};

// 移除 CourseForm 中已有的 logout 函数，因为这里不再有顶栏
// const logout = () => {
//   console.log('退出登录');
//   ElMessage.info('您已退出登录。');
//   // 实际：执行退出登录操作，如清除 token，跳转登录页
// };
</script>

<style scoped>
/* 移除 .community-page-container 的样式，因为它现在是子组件，不需要自己的全局容器 */
/* .community-page-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f4f6f8;
} */

/* 新增一个 wrapper 来替代原 el-main 的作用 */
.community-content-wrapper {
  flex: 1; /* 让内容填充可用空间 */
  /* padding 和 background-color 由父组件 views/Community.vue 提供 */
}

/* 顶部导航栏样式已移除，不再需要 .navbar 样式 */
/* .navbar { ... } */


/* 主要内容区样式调整 */
/* .main-content { ... } */ /* 这个类可以移除，直接在 .community-content-wrapper 中管理布局 */


/* 卡片头部样式统一 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 帖子列表卡片 - 沿用 MaterialsForm.vue 的卡片风格 */
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