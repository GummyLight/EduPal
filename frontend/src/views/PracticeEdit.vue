<template>
  <div class="practice-edit-page">
    <el-header class="navbar">
      <div class="title">{{ isNew ? '添加练习' : '编辑练习' }} - {{ practiceForm.content }}</div>
      <el-button type="info" @click="router.back()">返回练习列表</el-button>
    </el-header>

    <el-main>
      <el-card class="edit-card">
        <el-form :model="practiceForm" :rules="rules" ref="practiceFormRef" label-width="120px" class="edit-form">
          <el-form-item label="练习主题" prop="content">
            <el-input v-model="practiceForm.content" placeholder="请输入练习主题" />
          </el-form-item>
          <el-form-item label="习题编号" prop="quiz_id">
            <el-input v-model="practiceForm.quiz_id" :disabled="!isNew" placeholder="请输入习题编号" />
          </el-form-item>
          <el-form-item label="所属科目" prop="subject">
            <el-select v-model="practiceForm.subject" placeholder="请选择科目">
              <el-option label="数学" value="数学" />
              <el-option label="物理" value="物理" />
              <el-option label="英语" value="英语" />
              <el-option label="化学" value="化学" />
              <el-option label="语文" value="语文" />
              <el-option label="生物" value="生物" />
              <el-option label="历史" value="历史" />
              <el-option label="地理" value="地理" />
              <el-option label="政治" value="政治" />
            </el-select>
          </el-form-item>
          <el-form-item label="题目类型" prop="type">
            <el-input v-model="practiceForm.type" placeholder="例如：选择题、填空题" />
          </el-form-item>
          <el-form-item label="难度等级" prop="difficulty">
            <el-select v-model="practiceForm.difficulty" placeholder="请选择难度">
              <el-option label="简单" value="简单" />
              <el-option label="中等" value="中等" />
              <el-option label="困难" value="困难" />
            </el-select>
          </el-form-item>
          <el-form-item label="关联知识点" prop="knowledge_point">
            <el-input v-model="practiceForm.knowledge_point" placeholder="请输入关联知识点" />
          </el-form-item>
          <el-form-item label="发布日期" prop="publish_time">
            <el-date-picker
                v-model="practiceForm.publish_time"
                type="datetime"
                placeholder="选择发布日期时间"
                value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
          <el-form-item label="截止日期" prop="deadline">
            <el-date-picker
                v-model="practiceForm.deadline"
                type="datetime"
                placeholder="选择截止日期时间"
                value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
          <el-form-item label="习题文件" prop="file">
            <el-upload
                class="upload-demo"
                drag
                action="#"
                :auto-upload="false"
                :on-change="handleFileChange"
                :on-remove="handleFileRemove"
                :http-request="uploadFileToServer"
                :file-list="fileList"
                :limit="1"
                :on-exceed="handleExceed"
                ref="elUploadRef"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">将文件拖到此处，或 <em>点击上传</em></div>
              <template #tip>
                <div class="el-upload__tip">
                  {{ isNew ? '支持常见文档和媒体格式，文件大小不超过 500MB' : '可选择上传新文件替换原文件。支持常见文档和媒体格式，文件大小不超过 500MB' }}
                </div>
              </template>
            </el-upload>
            <div v-if="practiceForm.resource_content" style="margin-top: 5px;">
              当前文件: <a :href="getPreviewUrl(practiceForm.resource_content)" target="_blank">{{ practiceForm.resource_content.split('/').pop() }}</a>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSubmit">保存练习</el-button>
            <el-button @click="router.back()">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElNotification, FormInstance, UploadFile, UploadFiles, ElUpload } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue';
import axios from 'axios';
import { uploadFile, submitMaterialInfo, getMaxResourceId } from '../api/materialUploadApi';
import { getPreviewFileUrl } from '../api/materialsApi';
import { getMaxQuizId, getTeacherQuiz } from '../api/quiz';

const API_BASE = 'http://localhost:8080';
const SERVER_FILE_ROOT_PATH = 'quiz/';
const SERVER_UPLOAD_ROOT_PATH = 'E:/Postman/files/';

const route = useRoute();
const router = useRouter();
const exerciseId = route.params.exerciseId as string | undefined;
const exerciseData = (route.params as any).exercise || (history.state as any)?.exercise; // 获取 state 中的 exercise 数据
const isNew = computed(() => !exerciseId);

const practiceFormRef = ref<FormInstance>();
const elUploadRef = ref<InstanceType<typeof ElUpload> | null>(null);
const fileList = ref<UploadFile[]>([]);
const maxResourceId = ref<number | null>(null);

const practiceForm = ref({
  quiz_id: '',
  subject: '',
  content: '',
  publish_time: '',
  deadline: '',
  publisher: '',
  resource_content: '',
  type: '',
  difficulty: '',
  knowledge_point: '',
  file: null as File | null,
  fileId: '',
  originalFileName: '',
});

// 动态验证规则，根据是否为新建模式调整文件上传要求
const rules = computed(() => ({
  quiz_id: [{ required: true, message: '请输入习题编号', trigger: 'blur' }],
  subject: [{ required: true, message: '请选择科目', trigger: 'change' }],
  content: [{ required: true, message: '请输入练习主题', trigger: 'blur' }],
  publish_time: [{ required: true, message: '请选择发布日期', trigger: 'change' }],
  deadline: [{ required: true, message: '请选择截止日期', trigger: 'change' }],
  type: [{ required: true, message: '请输入题目类型', trigger: 'blur' }],
  difficulty: [{ required: true, message: '请选择难度等级', trigger: 'change' }],
  knowledge_point: [{ required: true, message: '请输入关联知识点', trigger: 'blur' }],
  // 编辑模式下文件上传为可选，新建模式下为必需
  file: isNew.value 
    ? [{ required: true, message: '请选择上传文件', trigger: 'change' }]
    : [],
}));

onMounted(async () => {
  try {
    if (isNew.value) {
      // 新增练习：获取 quiz_id
      const userId = localStorage.getItem('user_id') || 'unknown';
      const quizId = await fetchQuizId(userId);
      practiceForm.value.quiz_id = quizId;
      practiceForm.value.publisher = localStorage.getItem('username') || '当前教师';
      practiceForm.value.publish_time = new Date().toISOString().slice(0, 19).replace('T', ' ');
    } else if (exerciseId) {
      // 编辑练习：通过API获取quiz详情
      await loadQuizData(exerciseId);
    } else {
      // 边缘情况：缺少必要参数
      ElMessage.error('无法加载练习数据，请从练习列表重新进入');
      router.push({ name: 'PracticeList' }); // 返回练习列表
    }
  } catch (error) {
    ElMessage.error('初始化失败，请检查网络或后端服务');
    console.error('Initialization error:', error);
    router.push({ name: 'PracticeList' });
  }
});

async function fetchQuizId(userId: string): Promise<string> {
  try {
    const maxId = await getMaxQuizId();
    return (maxId + 1).toString();
  } catch (error) {
    ElMessage.error('获取习题编号失败，使用默认编号');
    console.error('Fetch quiz_id error:', error);
    // 返回一个基于时间戳的临时ID
    return Date.now().toString();
  }
}

// 通过API加载quiz数据用于编辑
async function loadQuizData(quizId: string) {
  try {
    const userId = localStorage.getItem('user_id') || '';
    const quizList = await getTeacherQuiz(userId);
    
    // 查找对应的quiz
    const quiz = quizList.find((q: any) => q.quizId.toString() === quizId);
    if (!quiz) {
      throw new Error(`未找到ID为${quizId}的练习`);
    }
    
    // 映射quiz数据到表单
    practiceForm.value = {
      quiz_id: quiz.quizId.toString(),
      subject: quiz.subject || '',
      content: quiz.title || '',
      publish_time: quiz.createTime || '',
      deadline: quiz.deadline || '',
      publisher: quiz.teacherName || localStorage.getItem('username') || '当前教师',
      resource_content: '', // 暂时为空，需要根据实际情况设置
      type: quiz.contentType || '',
      difficulty: quiz.difficulty || '',
      knowledge_point: quiz.knowledgePoints || '',
      file: null,
      fileId: quiz.quizId.toString(),
      originalFileName: '',
    };
    
    ElMessage.success('练习数据加载成功');
  } catch (error) {
    ElMessage.error(`加载练习数据失败: ${error}`);
    console.error('Load quiz data error:', error);
    throw error;
  }
}

const handleFileChange = (uploadFile: UploadFile, uploadFiles: UploadFiles) => {
  practiceForm.value.file = uploadFile.raw || null;
  fileList.value = [uploadFile];
  if (practiceForm.value.file) {
    practiceForm.value.fileId = isNew.value ? practiceForm.value.quiz_id : practiceForm.value.quiz_id;
    practiceForm.value.originalFileName = uploadFile.name;
    if (!practiceForm.value.content) {
      practiceForm.value.content = uploadFile.name.substring(0, uploadFile.name.lastIndexOf('.')) || uploadFile.name;
    }
  } else {
    practiceForm.value.fileId = '';
    practiceForm.value.originalFileName = '';
  }
  practiceFormRef.value?.validateField('file');
};

const handleFileRemove = () => {
  practiceForm.value.file = null;
  practiceForm.value.fileId = isNew.value ? practiceForm.value.quiz_id : practiceForm.value.quiz_id;
  practiceForm.value.originalFileName = '';
  practiceForm.value.resource_content = '';
  fileList.value = [];
  practiceFormRef.value?.validateField('file');
};

const handleExceed = () => {
  ElMessage.warning('只能上传一个文件，请先移除当前文件再上传。');
};

const uploadFileToServer = async (options: any) => {
  const file = options.file;
  if (!file || !practiceForm.value.fileId || !SERVER_FILE_ROOT_PATH || !SERVER_UPLOAD_ROOT_PATH) {
    ElMessage.error('文件上传参数缺失！');
    options.onError(new Error('Missing parameters'));
    return;
  }

  const fileUploadNotification = ElNotification({
    title: '文件上传中',
    message: `正在上传文件：${file.name}`,
    type: 'info',
    duration: 0,
    showClose: false,
  });

  try {
    const uploadResponse = await uploadFile(
        file,
        practiceForm.value.fileId,
        SERVER_FILE_ROOT_PATH,
        SERVER_UPLOAD_ROOT_PATH
    );
    fileUploadNotification.close();

    if (uploadResponse.code === 200) {
      ElMessage.success(`文件 ${file.name} 上传成功！`);
      options.onSuccess(uploadResponse, file);
      const fileExtension = getFileExtension(file.name);
      practiceForm.value.resource_content = `${SERVER_FILE_ROOT_PATH}${practiceForm.value.fileId}${fileExtension}`;
      await submitMaterialMetadata();
    } else {
      ElMessage.error(uploadResponse.message || `文件 ${file.name} 上传失败！`);
      options.onError(new Error(uploadResponse.message || 'Upload failed'));
    }
  } catch (error: any) {
    fileUploadNotification.close();
    ElMessage.error('文件上传失败，请检查网络或后端服务');
    console.error('File upload error:', error);
    options.onError(error);
  }
};

const submitMaterialMetadata = async () => {
  const materialData = {
    resource_id: practiceForm.value.fileId,
    subject: practiceForm.value.subject,
    teacher_id: localStorage.getItem('userid') || 'unknown',
    resource_content: practiceForm.value.resource_content,
    class_id: null,
    name: practiceForm.value.content,
    upload_time: new Date().toISOString(),
    description: practiceForm.value.type,
  };

  try {
    const submitResponse = await submitMaterialInfo(materialData);
    if (submitResponse.code === 200) {
      ElMessage.success('资料信息提交成功！');
    } else {
      ElMessage.error(submitResponse.message || '资料信息提交失败！');
    }
  } catch (error) {
    ElMessage.error('提交资料信息失败，请检查网络或后端服务');
    console.error('Submit material info error:', error);
  }
};

async function handleSubmit() {
  if (!practiceFormRef.value) return;

  await practiceFormRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.error('请检查表单填写，确保所有必填项已填写！');
      return;
    }

    // 处理文件上传逻辑
    if (practiceForm.value.file) {
      // 有新文件需要上传
      await elUploadRef.value?.submit();
    } else if (isNew.value && !practiceForm.value.resource_content) {
      // 新建模式下必须有文件
      ElMessage.error('请上传习题文件！');
      return;
    }
    // 编辑模式下，如果没有新文件但有现有文件，可以继续提交

    const quizData: any = {
      title: practiceForm.value.content, // 使用content作为title
      subject: practiceForm.value.subject,
      contentType: practiceForm.value.type,
      difficulty: practiceForm.value.difficulty,
      knowledgePoints: practiceForm.value.knowledge_point,
      description: `练习：${practiceForm.value.content}`,
      teacherId: localStorage.getItem('user_id') || '',
      teacherName: practiceForm.value.publisher,
      createTime: practiceForm.value.publish_time.split(' ')[0], // 只发送日期部分
      deadline: practiceForm.value.deadline.split(' ')[0], // 只发送日期部分
      class1: '', // 这里需要根据实际班级信息填充
      class2: '',
    };

    // 如果是编辑模式，添加quizId
    if (!isNew.value && practiceForm.value.quiz_id) {
      quizData.quizId = parseInt(practiceForm.value.quiz_id.toString());
    }

    try {
      const url = isNew.value ? `${API_BASE}/quiz/createQuiz` : `${API_BASE}/quiz/modifyQuiz`;
      const response = await axios.post(url, quizData, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
      });

      if (response.data.code === 200) {
        ElMessage.success(isNew.value ? '练习添加成功！' : '练习更新成功！');
        router.push({ name: 'PracticeList' }); // 返回练习列表
      } else {
        ElMessage.error(response.data.message || '保存练习失败！');
      }
    } catch (error: any) {
      ElMessage.error(`保存失败：${error.response?.data?.message || '请检查网络或后端服务'}`);
      console.error('Save quiz error:', error);
    }
  });
}

function getPreviewUrl(resourceContent: string): string {
  if (!resourceContent) return '';
  const fileId = resourceContent.split('/').pop()?.split('.')[0] || '';
  return getPreviewFileUrl({ fileId, path: SERVER_FILE_ROOT_PATH });
}

function getFileExtension(filename: string): string {
  if (!filename || typeof filename !== 'string') {
    return '.pdf';
  }
  const lastDotIndex = filename.lastIndexOf('.');
  if (lastDotIndex === -1 || lastDotIndex === 0) {
    return '.pdf';
  }
  return filename.substring(lastDotIndex);
}
</script>

<style scoped>
.practice-edit-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
}
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: #409EFF;
  color: white;
  border-bottom: 1px solid #ddd;
}
.title {
  font-weight: bold;
  font-size: 18px;
  color: white;
}
.el-main {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
  background-color: #f4f6f8;
}
.edit-card {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  background-color: #fff;
}
.edit-form .el-form-item {
  margin-bottom: 20px;
}
.upload-demo {
  width: 100%;
}
.upload-demo .el-upload-dragger {
  width: 100%;
  padding: 20px 0;
}
.el-upload__text {
  font-size: 15px;
  color: #606266;
}
.el-upload__text em {
  color: #409EFF;
  font-style: normal;
}
.el-upload__tip {
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
}
</style>