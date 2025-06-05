<template>
  <div class="practice-detail-page">
    <el-header class="navbar">
      <div class="title">练习详情 - {{ exerciseDetail?.内容 }}</div>
      <el-button type="info" @click="router.back()">返回练习列表</el-button>
    </el-header>

    <el-main>
      <el-card class="detail-card">
        <h3>{{ exerciseDetail?.内容 }} (编号: {{ exerciseDetail?.习题号 }})</h3>
        <p><strong>科目:</strong> {{ exerciseDetail?.科目 }}</p>
        <p><strong>难度:</strong> {{ exerciseDetail?.难度 }}</p>
        <p><strong>发布日期:</strong> {{ exerciseDetail?.发布时间 }}</p>
        <p><strong>截止日期:</strong> {{ exerciseDetail?.截止时间 }}</p>
        <p><strong>发布教师:</strong> {{ exerciseDetail?.发布人 }}</p>

        <el-divider>练习文件</el-divider>
        <div v-if="exerciseDetail?.习题号">
          <el-button type="primary" icon="el-icon-download" @click="downloadExerciseFile">
            下载练习题文件
          </el-button>
        </div>
        <div v-else>
          <p>暂无练习题文件可供下载。</p>
        </div>

        <el-divider>我的作业</el-divider>
        <div v-if="mySubmission.status === '已批改'">
          <p><strong>提交状态:</strong> <el-tag type="success">已批改</el-tag></p>
          <p><strong>我的得分:</strong> <el-tag type="warning" size="large">{{ mySubmission.score }}</el-tag></p>
          <p v-if="mySubmission.status === '已批改'">
            <strong>我的提交:</strong>
            <el-button type="text" @click="downloadSubmissionFile">下载我的提交</el-button>
          </p>
          <p><strong>教师评语:</strong> <el-tag type="warning" size="large">{{mySubmission.feedback}}</el-tag></p>
        </div>
        <div v-else-if="mySubmission.status === '已提交'">
          <p><strong>提交状态:</strong> <el-tag type="info">已提交</el-tag> (等待批改)</p>
          <p v-if="mySubmission.status === '已提交'">
            <strong>我的提交:</strong>
            <el-button type="text" @click="downloadSubmissionFile">下载我的提交</el-button>
          </p>
          <p>您已提交作业，如需重新提交请联系老师。</p>
        </div>
        <div v-else>
          <p><strong>提交状态:</strong> <el-tag type="danger">未提交</el-tag></p>
          <el-upload
              class="upload-demo"
              action="/api/submit-homework"
              :headers="uploadHeaders"
              :data="{ exerciseId: exerciseId, studentId: props.userid }"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              :file-list="fileList"
              :limit="1"
              :on-exceed="handleExceed"
              :on-remove="handleRemove"
          >
            <el-button type="success" icon="el-icon-upload" @click="handleUpload">上传资料</el-button>
            <template #tip>
              <div class="el-upload__tip">只能上传一份作业文件，文件大小不超过5MB。</div>
            </template>
          </el-upload>
        </div>
      </el-card>
    </el-main>

    <!-- 下载设置弹窗 -->
    <el-dialog
        v-model="downloadDialogVisible"
        title="下载设置"
        width="30%"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <el-form :model="downloadForm" label-width="120px">
        <el-form-item label="原始文件名">
          <el-input v-model="downloadForm.originalName" disabled />
        </el-form-item>
        <el-form-item label="下载文件名">
          <el-input v-model="downloadForm.fileName" placeholder="请输入下载文件名" />
        </el-form-item>
        <el-form-item label="保存到文件夹">
          <el-input v-model="downloadForm.outFile" placeholder="请输入服务器上的保存文件夹路径" />
          <el-alert type="warning" show-icon :closable="false" style="margin-top: 10px;">
            <template #title>注意：此处路径指服务器上的临时下载文件夹。</template>
          </el-alert>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="downloadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmDownload">确定下载</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 上传设置弹窗 -->
    <el-dialog
        v-model="uploadDialogVisible"
        title="上传作业文件"
        width="30%"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <el-form :model="uploadForm" label-width="120px">
        <el-form-item label="文件名">
          <el-input v-model="uploadForm.fileName" placeholder="请输入文件名（包含扩展名）" />
        </el-form-item>
        <el-form-item label="选择文件">
          <el-upload
              class="upload-demo"
              action="#"
              :auto-upload="false"
              :file-list="uploadForm.fileList"
              :limit="1"
              :on-change="handleUploadFileChange"
              :on-remove="handleUploadFileRemove"
              :on-exceed="handleExceed"
              :before-upload="beforeUpload"
              ref="uploadRef"
          >
            <el-button size="small" type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">只能上传一份文件，最大5MB</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmUpload">确认上传</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {ElAlert, ElButton, ElDialog, ElForm, ElFormItem, ElInput, ElMessage} from 'element-plus';
import axios from 'axios';
import {
  ResourceResponse,
  getPreviewFileUrl,
  getDownloadFileUrl,
  deleteFile,
  fetchAllResources,
  searchResourcesByName,
  deleteResourceById
} from '../api/materialsApi';
// API 基地址
const API_BASE = 'http://localhost:8080';

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

const route = useRoute();
const router = useRouter();
const exerciseId = route.params.exerciseId as string;

const exerciseDetail = ref<any>(null);
const mySubmission = ref<any>({ status: '未提交', score: null, submissionFilePath: null,feedback: null });
const uploadHeaders = ref({});
const fileList = ref<any[]>([]);

// 状态映射函数
const mapQuizStatus = (status: number | null) => {
  if (status === null) return '未提交';
  switch (status) {
    case 0:
      return '未提交';
    case 1:
      return '已提交';
    case 2:
      return '已批改';
    default:
      return '未知';
  }
};

// 从路由状态或后端获取练习详情
const fetchExerciseDetail = async () => {
  if (route.state?.exercise) {
    // 优先从路由状态获取
    exerciseDetail.value = {
      习题号: route.state.exercise.习题号,
      内容: route.state.exercise.内容,
      科目: route.state.exercise.科目,
      类型: route.state.exercise.类型,
      难度: route.state.exercise.难度,
      知识点: route.state.exercise.知识点,
      发布时间: route.state.exercise.发布时间,
      截止时间: route.state.exercise.截止时间,
      发布人: route.state.exercise.发布人,
      习题文件路径: route.state.exercise.习题文件路径 || '',
    };
    ElMessage.success('练习详情加载成功');
  } else {
    // 路由状态丢失，尝试从后端获取
    try {
      const response = await axios.get(`${API_BASE}/quiz/getMyQuiz`, {
        params: {
          userId: props.userid,
          quizId: exerciseId,
        },
      });
      if (response.data.status === 'success') {
        exerciseDetail.value = {
          习题号: response.data.quizId,
          内容: response.data.title,
          科目: response.data.subject,
          类型: response.data.contentType,
          难度: response.data.difficulty,
          知识点: response.data.knowledgePoints,
          发布时间: response.data.createTime,
          截止时间: response.data.deadline,
          发布人: response.data.teacherName,
          习题文件路径: response.data.answerId,
          教师评语: response.data.feedback,
        };
        ElMessage.success('练习详情加载成功');
      } else {
        ElMessage.error('获取练习详情失败');
        router.push('/practice');
      }
    } catch (error) {
      ElMessage.error('请求失败，请稍后重试');
      console.error(error);
      router.push('/practice');
    }
  }
};

// 获取提交状态
const fetchMySubmissionStatus = async () => {
  try {
    const response = await axios.get(`${API_BASE}/quiz/getMyQuiz`, {
      params: {
        userId: props.userid,
        quizId: exerciseId,
      },
    });
    if (response.data.status === 'success') {
      mySubmission.value = {
        status: mapQuizStatus(response.data.quizStatus),
        score: response.data.score === -1 ? null : response.data.score,
        submissionFilePath: response.data.answerId || '',
        feedback: response.data.feedback ,
      };
      ElMessage.success('提交状态加载成功');
    } else {
      ElMessage.error('获取提交状态失败');
    }
  } catch (error) {
    ElMessage.error('请求失败，请稍后重试');
    console.error(error);
  }
};

const downloadDialogVisible = ref(false);
const currentDownloadRow = ref<{ id: string; name: string; resource_content: string } | null>(null);
const downloadForm = ref({
  originalName: '', // 显示原始的资料名称
  fileName: '',     // 用户可编辑的下载文件名 (包含扩展名)
  outFile: 'E:/downloadsedge/test/', // 默认下载到服务器上的这个路径（示例），用户可修改
});

const openDownloadDialog = () => {
  downloadForm.value = {
    originalName:  `${exerciseId}.pdf`,
    fileName: `${exerciseId}.pdf`,
    outFile: 'E:/downloadsedge/test/',
  };

  downloadDialogVisible.value = true;
};
const openSubmissionDownloadDialog = () => {
  downloadForm.value = {
    originalName:  `${mySubmission.value.submissionFilePath}.pdf`,
    fileName: `${mySubmission.value.submissionFilePath}.pdf`,
    outFile: 'E:/downloadsedge/test/',
  };

  downloadDialogVisible.value = true;
};
// 确认下载
const confirmDownload = () => {
  if (!downloadForm.value.fileName) {
    ElMessage.warning('下载文件名不能为空！');
    return;
  }
  if (!downloadForm.value.outFile) {
    ElMessage.warning('服务器保存路径不能为空！');
    return;
  }
  downloadDialogVisible.value = false;
  handleDownload(
      exerciseId,
      downloadForm.value.fileName,
      downloadForm.value.outFile
  );
};

// 以下为未修改的文件相关函数，保持原样
const handleDownload=async(resourceId: string, fileName: string, outFile: string)=> {
  try {
    const url = getDownloadFileUrl({
      fileId: resourceId,
      path: 'quiz/',
      fileName: fileName,
      outFile: outFile,
    });

    const res = await axios.get(url);
    if (res.data.code === 200) {
      ElMessage.success(`文件 ${downloadForm.value.fileName} 下载成功！`);
    } else {
      ElMessage.error(res.data.message || '下载失败');
    }
  } catch (err) {
    ElMessage.error('下载失败');
    console.error(err);
  }
}

const downloadExerciseFile = () => {
  openDownloadDialog();
};

function downloadSubmissionFile() {
  openSubmissionDownloadDialog()
}

const handleUpload = () => {
  console.log('跳转到上传作业界面',exerciseId);
  localStorage.setItem('quizId', exerciseId);
  router.push('/home/practice/upload');
};
// 组件挂载时加载数据
onMounted(() => {
  if (props.usertype !== 1) {
    ElMessage.error('仅学生可访问此页面');
    router.push('/practice');
    return;
  }
  fetchExerciseDetail();
  fetchMySubmissionStatus();
});
</script>

<style scoped>
.practice-detail-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f4f6f8;
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
.detail-card {
  margin: 20px;
  padding: 20px;
  background-color: #fff;
}
.detail-card h3 {
  font-size: 20px;
  margin-bottom: 15px;
}
.detail-card p {
  line-height: 1.8;
}
.el-divider {
  margin: 25px 0;
}
.upload-demo {
  margin-top: 15px;
}
</style>