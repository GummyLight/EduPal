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
              :file-list="fileList"
              :limit="1"
              :on-exceed="handleExceed"
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
import { ElMessage, ElNotification, FormInstance, UploadFile, UploadFiles } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue';

const router = useRouter();

// 表单引用
const uploadFormRef = ref<FormInstance>();

// 上传表单数据
const uploadForm = reactive({
  name: '',
  subject: '',
  description: '',
  file: null as File | null, // 用于存储选中的文件对象
});

// 文件列表，用于 el-upload
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
  fileList.value = [uploadFile]; // 确保 fileList 只有一个文件
  // 触发表单验证，因为文件是异步添加的
  if (uploadFormRef.value) {
    uploadFormRef.value.validateField('file');
  }
};

// 文件移除时的回调
const handleFileRemove = () => {
  uploadForm.file = null;
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

// 提交上传
const submitUpload = async () => {
  if (!uploadFormRef.value) return;

  await uploadFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      ElMessage.success('表单验证成功，准备上传！');
      console.log('上传资料数据:', uploadForm);

      // 模拟文件上传过程
      ElNotification({
        title: '上传中',
        message: `正在上传资料：${uploadForm.name}`,
        type: 'info',
        duration: 0, // 不自动关闭
        showClose: false,
      });

      // 实际：这里应该调用后端API进行文件上传
      // 例如：使用 FormData
      // const formData = new FormData();
      // formData.append('name', uploadForm.name);
      // formData.append('subject', uploadForm.subject);
      // formData.append('description', uploadForm.description);
      // if (uploadForm.file) {
      //   formData.append('file', uploadForm.file);
      // }
      // await axios.post('/api/upload-material', formData); // 假设的API

      setTimeout(() => {
        ElNotification.closeAll(); // 关闭上传中的通知
        ElNotification({
          title: '上传成功',
          message: `资料《${uploadForm.name}》已成功上传！`,
          type: 'success',
          duration: 3000,
        });
        resetForm(); // 上传成功后重置表单
        router.push('/materials'); // 返回资料库页面
      }, 2000); // 模拟2秒上传时间

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
    uploadForm.file = null; // 手动重置文件对象
    fileList.value = [];    // 清空文件列表
  }
};

// 返回资料库页面
const goBack = () => {
  // 返回资料列表页
  router.push('/home/materials'); // 或者 router.push({ name: 'MaterialsList' });
};
</script>

<style scoped>
.material-upload-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 60px); /* 减去顶部导航栏的高度，保持居中 */
  background-color: #f4f6f8;
  padding: 20px; /* 增加一些内边距 */
}

.upload-card {
  width: 600px; /* 卡片宽度 */
  max-width: 90%; /* 最大宽度限制 */
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

/* 调整文件上传区域样式 */
.upload-demo {
  width: 100%; /* 让上传区域占满宽度 */
}
.upload-demo .el-upload-dragger {
  width: 100%; /* 拖拽区域宽度 */
  padding: 30px 0; /* 增加上下内边距 */
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