<template>
  <div class="material-upload-page">
    <el-card class="upload-card" shadow="always">
      <template #header>
        <div class="card-header">
          <span>测试文件上传功能</span>
          <el-button class="back-button" type="info" text @click="goBack">返回资料库</el-button>
        </div>
      </template>

      <el-form :model="uploadForm" :rules="rules" ref="uploadFormRef" label-width="100px">
        <el-form-item label="资料名称" prop="name">
          <el-input v-model="uploadForm.name" placeholder="可以随意填写或留空" />
        </el-form-item>

        <el-form-item label="学科分类" prop="subject">
          <el-select v-model="uploadForm.subject" placeholder="可以随意选择">
            <el-option label="数学" value="math" />
            <el-option label="物理" value="physics" />
            <el-option label="英语" value="english" />
            <el-option label="化学" value="chemistry" />
          </el-select>
        </el-form-item>

        <el-form-item label="资源描述" prop="description">
          <el-select v-model="uploadForm.description" placeholder="可以随意选择">
            <el-option label="讲义" value="讲义" />
            <el-option label="习题" value="习题" />
            <el-option label="试卷" value="试卷" />
            <el-option label="答案" value="答案" />
          </el-select>
        </el-form-item>

        <el-form-item label="文件根路径" prop="uploadPath">
          <el-input v-model="uploadForm.uploadPath" placeholder="后端服务器的绝对根路径" />
        </el-form-item>
        <el-form-item label="文件子目录" prop="toPath">
          <el-input v-model="uploadForm.toPath" placeholder="例如：resource/ 或 materials/" />
        </el-form-item>

        <el-form-item label="选择文件" prop="file">
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
                支持 PDF / DOCX / MP4 / MP3 / ZIP 等格式，文件大小不超过 500MB
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitUpload">立即上传文件</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElNotification, FormInstance, UploadFile, UploadFiles, ElUpload } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue';
import axios from 'axios';
import { v4 as uuidv4 } from 'uuid';
import { uploadFile } from '../api/materialUploadApi';

const router = useRouter();

const uploadFormRef = ref<FormInstance>();
const elUploadRef = ref<InstanceType<typeof ElUpload> | null>(null);

const uploadForm = reactive({
  name: '临时测试资料',
  subject: 'math',
  description: '讲义',
  file: null as File | null,
  uploadPath: 'E:/Postman/files',
  toPath: 'resource/', // 'resource/'
  fileId: ''
});

const fileList = ref<UploadFile[]>([]);

// 简化验证规则，只验证文件和新的路径字段
const rules = reactive({
  uploadPath: [{ required: true, message: '请输入文件存储根路径', trigger: 'blur' }],
  toPath: [{ required: true, message: '请输入文件存储子目录', trigger: 'blur' }],
  file: [{ required: true, message: '请选择上传文件', trigger: 'change' }],
});

const handleFileChange = (uploadFile: UploadFile, uploadFiles: UploadFiles) => {
  uploadForm.file = uploadFile.raw || null;
  fileList.value = [uploadFile];
  if (uploadForm.file) {
    uploadForm.fileId = uuidv4();
  } else {
    uploadForm.fileId = '';
  }
  if (uploadFormRef.value) {
    uploadFormRef.value.validateField('file');
  }
};

const handleFileRemove = () => {
  uploadForm.file = null;
  uploadForm.fileId = '';
  fileList.value = [];
  if (uploadFormRef.value) {
    uploadFormRef.value.validateField('file');
  }
};

const handleExceed = () => {
  ElMessage.warning('只能上传一个文件，请先移除当前文件再上传。');
};

const uploadFileToServer = async (options: any) => {
  const file = options.file;
  if (!file) {
    ElMessage.error('没有文件可上传！');
    options.onError(new Error('没有文件可上传'), file);
    return;
  }
  if (!uploadForm.fileId) {
    ElMessage.error('未生成文件唯一标识符，请重新选择文件。');
    options.onError(new Error('Missing fileId'), file);
    return;
  }
  // 额外验证 path 和 toPath 是否已填写
  if (!uploadForm.uploadPath || !uploadForm.toPath) {
    ElMessage.error('请填写文件根路径和子目录！');
    options.onError(new Error('Missing path or toPath'), file);
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
    // 调用 API 文件中的上传函数，传入所有必要的参数
    const responseData = await uploadFile(file, uploadForm.uploadPath, uploadForm.toPath, uploadForm.fileId);

    fileUploadNotification.close();

    if (responseData.code === 200) {
      ElMessage.success(`文件 ${file.name} 上传成功！`);
      console.log('文件上传成功，服务器返回数据:', responseData);
      options.onSuccess(responseData, file);
    } else {
      ElMessage.error(responseData.message || `文件 ${file.name} 上传失败！`);
      console.error('文件上传失败，服务器返回数据:', responseData);
      options.onError(new Error(responseData.message || '文件上传失败'), file);
    }
  } catch (error: any) {
    fileUploadNotification.close();
    console.error('文件上传请求失败:', error);
    let errorMessage = '文件上传失败，请检查网络或联系管理员。';
    if (axios.isAxiosError(error)) {
      errorMessage = error.response?.data?.message || `文件上传失败：${error.response?.status}`;
      console.error('HTTP 错误响应数据:', error.response?.data);
      console.error('HTTP 错误状态码:', error.response?.status);
    } else if (error.request) {
      errorMessage = '文件上传请求未收到响应，请检查后端服务是否运行。';
    }
    ElMessage.error(errorMessage);
    options.onError(error, file);
  }
};

const submitUpload = async () => {
  if (!uploadFormRef.value) return;

  await uploadFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      if (!uploadForm.file) {
        ElMessage.error('请选择要上传的文件！');
        return;
      }
      // 触发文件上传
      ElMessage.info('正在触发文件上传...');
      try {
        if (elUploadRef.value) {
          await elUploadRef.value.submit();
        } else {
          throw new Error('文件上传组件未就绪。');
        }
      } catch (e) {
        console.error("文件上传触发失败:", e);
        ElMessage.error('文件上传失败，请修正后重试。');
      }
    } else {
      console.log('表单验证失败', fields);
      ElMessage.error('请检查表单填写，确保所有必填项已填写并选择文件。');
    }
  });
};

const resetForm = () => {
  if (uploadFormRef.value) {
    uploadFormRef.value.resetFields();
    // 确保手动清空文件相关状态
    uploadForm.file = null;
    uploadForm.fileId = '';
    fileList.value = [];
    if (elUploadRef.value) {
      elUploadRef.value.clearFiles();
    }
    // 手动设置回初始值或空白，因为 resetFields 可能只针对 prop 绑定的值
    uploadForm.name = '';
    uploadForm.subject = '';
    uploadForm.description = '';
    // 恢复为默认或空白值
    uploadForm.uploadPath = '/opt/edupal_files/'; // <-- 再次提醒，请修改为你的实际路径！
    uploadForm.toPath = 'resource/';
  }
};

const goBack = () => {
  ElMessage.info('测试模式，不执行页面跳转。');
};
</script>

<style scoped>
/* 样式与之前保持一致 */
.material-upload-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 60px);
  background-color: #f4f6f8;
  padding: 20px;
}

.upload-card {
  width: 600px;
  max-width: 90%;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.card-header .back-button {
  font-size: 14px;
}

.el-form-item {
  margin-bottom: 22px;
}

.upload-demo {
  width: 100%;
}
.upload-demo .el-upload-dragger {
  width: 100%;
  padding: 30px 0;
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