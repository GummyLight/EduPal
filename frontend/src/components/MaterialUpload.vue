<template>
  <div class="material-upload-page">
    <el-card class="upload-card" shadow="always">
      <template #header>
        <div class="card-header">
          <span>文件上传</span>
          <el-button class="back-button" type="info" text @click="goBack">返回资料库</el-button>
        </div>
      </template>

      <el-form :model="uploadForm" :rules="rules" ref="uploadFormRef" label-width="100px">
        <el-form-item label="资料名称" prop="name">
          <el-input v-model="uploadForm.name" placeholder="请输入资料名称" />
        </el-form-item>

        <el-form-item label="学科分类" prop="subject">
          <el-select v-model="uploadForm.subject" placeholder="请选择学科">
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

        <el-form-item label="资源描述" prop="description">
          <el-select v-model="uploadForm.description" placeholder="请选择描述" clearable>
            <el-option label="讲义" value="讲义" />
            <el-option label="习题" value="习题" />
            <el-option label="试卷" value="试卷" />
            <el-option label="答案" value="答案" />
          </el-select>
        </el-form-item>

        <el-form-item label="班级ID (可选)">
          <el-input v-model="uploadForm.classId" placeholder="请输入班级ID，可留空" />
        </el-form-item>

        <el-form-item label="文件根路径" prop="uploadPath">
          <el-input v-model="uploadForm.uploadPath" placeholder="后端服务器的绝对根路径" />
          <el-alert type="warning" show-icon :closable="false" style="margin-top: 10px;">
            <template #title>生产环境请谨慎配置此项，通常由后端或环境变量决定。</template>
          </el-alert>
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
                支持常见文档和媒体格式，文件大小不超过 500MB
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
import { ref, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElNotification, ElMessageBox, FormInstance, UploadFile, UploadFiles, ElUpload } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue';
// 从 materialUploadApi.ts 导入上传和提交资料信息的函数
import { uploadFile, submitMaterialInfo, MaterialSubmitData ,getMaxResourceId } from '../api/materialUploadApi';
import { onMounted } from 'vue';


const router = useRouter();

// props 用于获取用户信息，从父组件 Home.vue 传递而来
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
  },
});

// 计算属性，方便在模板中使用 props
const userType = computed(() => props.usertype);
const username = computed(() => props.username);
const userId = computed(() => props.userid); // 上传人ID

const uploadFormRef = ref<FormInstance>();
const elUploadRef = ref<InstanceType<typeof ElUpload> | null>(null);
const maxResourceId = ref<number | null>(null);

const fetchMaxResourceId = async () => {
  try {
    const maxId = await getMaxResourceId();
    maxResourceId.value = maxId;
    ElMessage.success(`最大 resource_id: ${maxId}`);
  } catch (error) {
    ElMessage.error('获取最大 resource_id 失败');
    console.error('获取最大 resource_id 错误:', error);
  }
};

onMounted(async () => {
  try {
    await fetchMaxResourceId();
  } catch (error) {
    ElMessage.error('获取最大 resource_id 失败，请刷新页面或联系管理员');
    console.error('获取最大 resource_id 错误:', error);
  }
});

const uploadForm = reactive({
  name: '', // 资料名称，应由用户填写
  subject: '', // 学科分类
  description: '' as string | null, // 资源描述，可能为 null
  classId: '' as string | null, // 班级ID，可能为 null
  file: null as File | null, // 待上传的文件对象
  uploadPath: 'E:/downloadsedge/test', // 服务器文件存储的绝对根路径，请根据实际情况修改
  toPath: 'resource/', // 文件在根路径下的子目录，例如 'resource/'
  fileId: '', // 文件在服务器上的唯一标识 (不带扩展名)
  originalFileName: '', // 存储原始文件名 (含扩展名)，用于后续元数据保存
});

const fileList = ref<UploadFile[]>([]);

// 表单验证规则
const rules = reactive({
  name: [{ required: true, message: '请输入资料名称', trigger: 'blur' }],
  subject: [{ required: true, message: '请选择学科分类', trigger: 'change' }],
  // description 和 classId 可以是可选的，根据后端 ResourceRequest 的定义
  // description: [{ required: true, message: '请选择资源描述', trigger: 'change' }],
  uploadPath: [{ required: true, message: '请输入文件存储根路径', trigger: 'blur' }],
  toPath: [{ required: true, message: '请输入文件存储子目录', trigger: 'blur' }],
  file: [{ required: true, message: '请选择上传文件', trigger: 'change' }],
});

// 文件选择或变更时触发
const handleFileChange = (uploadFile: UploadFile, uploadFiles: UploadFiles) => {
  uploadForm.file = uploadFile.raw || null;
  fileList.value = [uploadFile]; // 确保只保留一个文件

  if (uploadForm.file) {
    uploadForm.fileId = maxResourceId.value?.toString() || '';
    uploadForm.originalFileName = uploadFile.name; // 保存原始文件名，包含扩展名

    // 尝试根据文件名填充资料名称
    if (!uploadForm.name) {
      uploadForm.name = uploadFile.name.substring(0, uploadFile.name.lastIndexOf('.')) || uploadFile.name;
    }
  } else {
    uploadForm.fileId = '';
    uploadForm.originalFileName = '';
  }

  // 触发表单验证，确保文件已选择
  if (uploadFormRef.value) {
    uploadFormRef.value.validateField('file');
  }
};

// 文件移除时触发
const handleFileRemove = () => {
  uploadForm.file = null;
  uploadForm.fileId = '';
  uploadForm.originalFileName = '';
  fileList.value = [];
  if (uploadFormRef.value) {
    uploadFormRef.value.validateField('file');
  }
};

// 超过文件限制时触发
const handleExceed = () => {
  ElMessage.warning('只能上传一个文件，请先移除当前文件再上传。');
};

// 实际执行文件上传到服务器的函数，由 el-upload 的 :http-request 属性调用
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
  if (!uploadForm.uploadPath || !uploadForm.toPath) {
    ElMessage.error('请填写文件根路径和子目录！');
    options.onError(new Error('Missing path or toPath'), file);
    return;
  }

  const fileUploadNotification = ElNotification({
    title: '文件上传中',
    message: `正在上传文件：${file.name}`,
    type: 'info',
    duration: 0, // 不自动关闭
    showClose: false,
  });

  try {
    // 调用 materialUploadApi.ts 中定义的 uploadFile 函数
    // 参数顺序：file, fileId, toPath, path (与 materialUploadApi.ts 中的定义一致)
    console.log('准备上传文件:',  uploadForm.fileId, uploadForm.toPath, uploadForm.uploadPath);
    const uploadResponse = await uploadFile(

        file,
        uploadForm.fileId,
        uploadForm.toPath,
        uploadForm.uploadPath
    );

    fileUploadNotification.close(); // 关闭上传中的通知

    if (uploadResponse.code === 200) {
      ElMessage.success(`文件 ${file.name} 上传成功！`);
      console.log('文件上传成功，服务器返回数据:', uploadResponse);
      options.onSuccess(uploadResponse, file); // 告知 el-upload 成功

      // **文件上传成功后，提交资料元数据**
      await submitMaterialMetadata(); // 传递上传响应以便获取可能的文件信息

    } else {
      ElMessage.error(uploadResponse.message || `文件 ${file.name} 上传失败！`);
      console.error('文件上传失败，服务器返回数据:', uploadResponse);
      options.onError(new Error(uploadResponse.message || '文件上传失败'), file);
    }
  } catch (error: any) {
    fileUploadNotification.close(); // 关闭通知
    console.error('文件上传请求失败:', error);
    
    // 检查是否是500错误（服务器内部错误）
    if (error.response?.status === 500) {
      ElMessage.error('服务器内部错误，可能是FTP服务未启动或路径配置问题。请联系管理员或稍后重试。');
      
      // 提供重试选项
      ElMessageBox.confirm(
        '上传失败，可能是服务器配置问题。是否尝试使用备用路径重新上传？',
        '上传失败',
        {
          confirmButtonText: '重试',
          cancelButtonText: '取消',
          type: 'warning',
        }
      ).then(() => {
        // 使用备用路径重试
        retryUploadWithBackupPath(file, options);
      }).catch(() => {
        options.onError(error, file);
      });
    } else {
      // 其他错误正常处理
      options.onError(error, file);
    }
  }
};

// 使用备用路径重试上传
const retryUploadWithBackupPath = async (file: File, options: any) => {
  const backupNotification = ElNotification({
    title: '正在重试上传',
    message: `使用备用路径重新上传文件：${file.name}`,
    type: 'info',
    duration: 0,
    showClose: false,
  });

  try {
    // 尝试使用备用路径
    const backupPath = '/tmp/uploads'; // 备用路径
    const uploadResponse = await uploadFile(
      file,
      uploadForm.fileId,
      uploadForm.toPath,
      backupPath
    );

    backupNotification.close();

    if (uploadResponse.code === 200) {
      ElMessage.success(`文件 ${file.name} 重试上传成功！`);
      options.onSuccess(uploadResponse, file);
      await submitMaterialMetadata();
    } else {
      ElMessage.error('重试上传失败：' + uploadResponse.message);
      options.onError(new Error(uploadResponse.message || '重试上传失败'), file);
    }
  } catch (retryError: any) {
    backupNotification.close();
    console.error('重试上传也失败了:', retryError);
    ElMessage.error('重试上传也失败了，请检查网络连接或联系管理员');
    options.onError(retryError, file);
  }
};

// 提交资料元数据的函数
const submitMaterialMetadata = async () => {
  if (!uploadForm.file) {
    ElMessage.error('无法提交资料元数据：文件信息缺失。');
    return;
  }

  // 构造 resource_content：toPath + fileId + 文件扩展名
  const fileExtension = getFileExtension(uploadForm.originalFileName);
  const resourceContent = `${uploadForm.toPath}${uploadForm.fileId}${fileExtension}`;

  // **关键：构建符合后端 ResourceRequest 结构的数据**
  const materialData: MaterialSubmitData = {
    resource_id: uploadForm.fileId, // 使用 UUID 作为 resource_id
    subject: uploadForm.subject,
    teacher_id: userId.value, // 从 props 获取当前用户的 ID
    resource_content: resourceContent, // 文件在服务器上的完整路径
    class_id: uploadForm.classId || null, // 如果为空字符串，则发送 null
    name: uploadForm.name,
    // upload_time 使用 dataTime(6)格式的字符串
    upload_time: new Date().toISOString(),
    description: uploadForm.description || null, // 如果为空字符串，则发送 null
  };

  try {
    const submitResponse = await submitMaterialInfo(materialData);

    if (submitResponse.code === 200) {
      ElMessage.success('资料信息提交成功，资料已完整创建！');
      console.log('资料信息提交成功:', submitResponse);
      resetForm(); // 资料和文件都上传成功后，重置表单
      // 跳转回资料库页面
      router.push('/home/materials');
    } else {
      ElMessage.error(submitResponse.message || '资料信息提交失败！');
      console.error('资料信息提交失败:', submitResponse);
    }
  } catch (error) {
    console.error('提交资料信息请求失败:', error);
    // 错误信息已经在 materialUploadApi.ts 中通过 ElMessage 统一处理了
  }
};


// 触发上传操作
const submitUpload = async () => {
  if (!uploadFormRef.value) return;

  await uploadFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      if (!uploadForm.file) {
        ElMessage.error('请选择要上传的文件！');
        return;
      }
      ElMessage.info('正在准备上传文件...');
      try {
        if (elUploadRef.value) {
          elUploadRef.value.submit(); // 这会调用 :http-request 绑定的 uploadFileToServer 函数
        } else {
          throw new Error('文件上传组件未就绪。');
        }
      } catch (e) {
        console.error("文件上传触发失败:", e);
        // 这里的错误通常是组件内部触发失败，而非后端返回
        ElMessage.error('文件上传失败，请修正后重试。');
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
    uploadForm.originalFileName = '';
    fileList.value = [];
    if (elUploadRef.value) {
      elUploadRef.value.clearFiles();
    }
    // 恢复为默认值或空白
    uploadForm.name = '';
    uploadForm.subject = '';
    uploadForm.description = null; // 确保为 null
    uploadForm.classId = null; // 确保为 null
    // 恢复为默认路径，请确保这是你的实际路径
    uploadForm.uploadPath = 'E:/Postman/files/';
    uploadForm.toPath = 'resource/';
  }
};

// 返回资料库页面
const goBack = () => {
  ElMessage.info('正在返回资料库...');
  router.push('/home/materials');
};

// 辅助函数：从文件名中提取扩展名 (包含点)
function getFileExtension(filename: string): string {
  if (!filename || typeof filename !== 'string') {
    return '';
  }
  const lastDotIndex = filename.lastIndexOf('.');
  if (lastDotIndex === -1 || lastDotIndex === 0) {
    return ''; // 没有扩展名或以点开头的文件
  }
  return filename.substring(lastDotIndex); // 包含点，如 ".pdf"
}

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