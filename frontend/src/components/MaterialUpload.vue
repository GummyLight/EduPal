<template>
  <div class="material-upload-page">
    <el-card class="upload-card" shadow="always">
      <template #header>
        <div class="card-header">
          <span>上传资料</span>
          <el-button class="back-button" type="info" text @click="goBack">返回资料库</el-button>
        </div>
      </template>

      <el-form :model="uploadForm" :rules="rules" ref="uploadFormRef" label-width="100px">
        <el-form-item label="资料名称" prop="name">
          <el-input v-model="uploadForm.name" placeholder="请输入资料名称" />
        </el-form-item>

        <el-form-item label="学科分类" prop="subject">
          <el-select v-model="uploadForm.subject" placeholder="请选择学科">
            <el-option label="数学" value="math" />
            <el-option label="物理" value="physics" />
            <el-option label="英语" value="english" />
            <el-option label="化学" value="chemistry" />
          </el-select>
        </el-form-item>

        <el-form-item label="资源描述" prop="description">
          <el-select v-model="uploadForm.description" placeholder="请选择资源描述">
            <el-option label="讲义" value="讲义" />
            <el-option label="习题" value="习题" />
            <el-option label="试卷" value="试卷" />
            <el-option label="答案" value="答案" />
          </el-select>
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
          <el-button type="primary" @click="submitUpload">立即上传</el-button>
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
import axios from 'axios'; // 确保 axios 导入
import { v4 as uuidv4 } from 'uuid'; // 确保 uuid 导入
import { uploadFile, submitMaterialInfo } from '../api/materialUploadApi'; // 从 API 文件中导入函数

const router = useRouter();

// 表单引用
const uploadFormRef = ref<FormInstance>();
const elUploadRef = ref<InstanceType<typeof ElUpload> | null>(null);

// 上传表单数据
const uploadForm = reactive({
  name: '',
  subject: '',
  description: '',
  file: null as File | null,
  uploadPath: '/resource/', // 固定上传路径
  fileId: '' // 用于存储生成的 fileId
});

// 文件列表
const fileList = ref<UploadFile[]>([]);

// 表单验证规则
const rules = reactive({
  name: [{ required: true, message: '请输入资料名称', trigger: 'blur' }],
  subject: [{ required: true, message: '请选择学科分类', trigger: 'change' }],
  description: [{ required: true, message: '请选择资源描述', trigger: 'change' }],
  file: [{ required: true, message: '请选择上传文件', trigger: 'change' }],
});

// 文件选择改变时的回调
const handleFileChange = (uploadFile: UploadFile, uploadFiles: UploadFiles) => {
  uploadForm.file = uploadFile.raw || null;
  fileList.value = [uploadFile];
  // 在选择文件时生成 fileId
  if (uploadForm.file) {
    uploadForm.fileId = uuidv4(); // 生成 UUID 作为 fileId
  } else {
    uploadForm.fileId = '';
  }
  // 触发表单验证
  if (uploadFormRef.value) {
    uploadFormRef.value.validateField('file');
  }
};

// 文件移除时的回调
const handleFileRemove = () => {
  uploadForm.file = null;
  uploadForm.fileId = '';
  fileList.value = [];
  // 触发表单验证
  if (uploadFormRef.value) {
    uploadFormRef.value.validateField('file');
  }
};

// 文件超出限制时的回调
const handleExceed = () => {
  ElMessage.warning('只能上传一个文件，请先移除当前文件再上传。');
};

// 自定义文件上传逻辑
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

  const fileUploadNotification = ElNotification({
    title: '文件上传中',
    message: `正在上传文件：${file.name}`,
    type: 'info',
    duration: 0,
    showClose: false,
  });

  try {
    // 调用 API 文件中的上传函数
    const responseData = await uploadFile(file, uploadForm.uploadPath, uploadForm.fileId);

    fileUploadNotification.close();

    if (responseData.code === 200) {
      ElMessage.success(`文件 ${file.name} 上传成功！`);
      console.log('文件上传成功，服务器返回数据:', responseData);
      options.onSuccess(responseData, file); // 通知 el-upload 组件上传成功
    } else {
      ElMessage.error(responseData.message || `文件 ${file.name} 上传失败！`);
      console.error('文件上传失败，服务器返回数据:', responseData);
      options.onError(new Error(responseData.message || '文件上传失败'), file); // 通知 el-upload 组件上传失败
    }
  } catch (error: any) {
    fileUploadNotification.close();
    console.error('文件上传请求失败:', error);
    let errorMessage = '文件上传失败，请检查网络或联系管理员。';
    if (axios.isAxiosError(error)) { // 判断是否是 Axios 错误
      errorMessage = error.response?.data?.message || `文件上传失败：${error.response?.status}`;
      console.error('HTTP 错误响应数据:', error.response?.data);
      console.error('HTTP 错误状态码:', error.response?.status);
    } else if (error.request) {
      errorMessage = '文件上传请求未收到响应，请检查后端服务是否运行。';
      console.error('请求无响应:', error.request);
    }
    ElMessage.error(errorMessage);
    options.onError(error, file);
  }
};

// 提交上传 (资料信息)
const submitUpload = async () => {
  if (!uploadFormRef.value) return;

  await uploadFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      if (!uploadForm.file) { // 检查是否选择了文件
        ElMessage.error('请选择要上传的文件！');
        return;
      }

      // 1. 首先触发文件上传
      ElMessage.info('正在上传文件...');
      try {
        if (elUploadRef.value) {
          await elUploadRef.value.submit(); // 触发 http-request，执行 uploadFileToServer
        } else {
          throw new Error('文件上传组件未就绪。');
        }
      } catch (e) {
        console.error("文件上传前置失败:", e);
        ElMessage.error('文件上传失败，请修正后重试。');
        return; // 文件上传失败则不再继续提交资料信息
      }

      // 2. 确保文件上传成功后，再提交资料信息到 /materials 接口
      // 这里依赖于 uploadFileToServer 成功后，uploadForm.fileId 已经被正确设置
      if (!uploadForm.fileId) {
        ElMessage.error('文件上传成功，但未获取到文件ID，无法提交资料信息。');
        return;
      }

      console.log('文件上传成功，正在提交资料信息:', uploadForm);

      // 开启资料信息提交通知
      const materialSubmitNotification = ElNotification({
        title: '提交资料信息中',
        message: `正在保存资料《${uploadForm.name}》的信息...`,
        type: 'info',
        duration: 0,
        showClose: false,
      });

      try {
        // 调用 API 文件中的提交资料信息函数
        const responseData = await submitMaterialInfo({
          name: uploadForm.name,
          subject: uploadForm.subject,
          description: uploadForm.description,
          resourceId: uploadForm.fileId // 将生成的 fileId 作为 resourceId 传递
        });

        materialSubmitNotification.close(); // 关闭通知

        if (responseData.code === 200) {
          ElNotification({
            title: '资料保存成功',
            message: `资料《${uploadForm.name}》已成功保存！`,
            type: 'success',
            duration: 3000,
          });
          resetForm(); // 上传成功后重置表单
          router.push('/home/materials'); // 返回资料库页面
        } else {
          ElNotification({
            title: '资料保存失败',
            message: responseData.message || '资料信息保存失败，请稍后再试。',
            type: 'error',
            duration: 5000,
          });
        }
      } catch (error: any) {
        materialSubmitNotification.close(); // 关闭通知
        console.error('资料信息提交失败:', error);
        let errorMessage = '资料信息提交失败，请检查网络或联系管理员。';
        if (axios.isAxiosError(error)) {
          errorMessage = error.response?.data?.message || `资料信息提交失败：${error.response?.status}`;
          console.error('HTTP 错误响应数据:', error.response?.data);
          console.error('HTTP 错误状态码:', error.response?.status);
        } else if (error.request) {
          errorMessage = '资料信息提交请求未收到响应。';
        }
        ElNotification({
          title: '资料保存失败',
          message: errorMessage,
          type: 'error',
          duration: 5000,
        });
      }

    } else {
      console.log('表单验证失败', fields);
      ElMessage.error('请检查表单填写，确保所有必填项已填写并选择文件。');
    }
  });
};

// 重置表单
const resetForm = () => {
  if (uploadFormRef.value) {
    uploadFormRef.value.resetFields();
    uploadForm.file = null;
    uploadForm.fileId = '';
    fileList.value = [];
    if (elUploadRef.value) {
      elUploadRef.value.clearFiles(); // 清空 el-upload 组件的文件列表
    }
  }
};

// 返回资料库页面
const goBack = () => {
  router.push('/home/materials');
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