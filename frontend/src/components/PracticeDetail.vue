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
        <div v-if="exerciseDetail?.习题文件路径">
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
          <p v-if="mySubmission.submissionFilePath">
            <strong>我的提交:</strong>
            <el-button type="text" @click="downloadSubmissionFile">下载我的提交</el-button>
          </p>
        </div>
        <div v-else-if="mySubmission.status === '已提交'">
          <p><strong>提交状态:</strong> <el-tag type="info">已提交</el-tag> (等待批改)</p>
          <p v-if="mySubmission.submissionFilePath">
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
            <el-button size="small" type="success">上传作业文件</el-button>
            <template #tip>
              <div class="el-upload__tip">只能上传一份作业文件，文件大小不超过5MB。</div>
            </template>
          </el-upload>
        </div>
      </el-card>
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, defineProps } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getDownloadFileUrl } from '../api/materialsApi';

const props = defineProps({
  usertype: Number,
  username: String,
  userid: String,
});

const route = useRoute();
const router = useRouter();
const exerciseId = route.params.exerciseId as string;

const exerciseDetail = ref<any>(null);
const mySubmission = ref<any>({ status: '未提交', score: null, submissionFilePath: null });
const uploadHeaders = ref({});
const fileList = ref<any[]>([]);

onMounted(() => {
  fetchExerciseDetail();
  fetchMySubmissionStatus();
});

function fetchExerciseDetail() {
  exerciseDetail.value = {
    习题号: exerciseId,
    科目: 'math',
    内容: '函数与图像基础练习',
    发布时间: '2025-05-20 10:00:00',
    截止时间: '2025-06-01 23:59:59',
    发布人: '张老师',
    习题文件路径: `/files/exercises/${exerciseId}.pdf`,
    类型: '选择题',
    难度: '中等',
    知识点: '函数图像',
  };
}

function fetchMySubmissionStatus() {
  const studentId = props.userid;
  if (exerciseId === 'EX20250503') {
    mySubmission.value = { status: '已批改', score: 95, submissionFilePath: `/files/submissions/${exerciseId}_${studentId}.pdf` };
  } else if (exerciseId === 'EX20250502') {
    mySubmission.value = { status: '已提交', score: null, submissionFilePath: `/files/submissions/${exerciseId}_${studentId}.pdf` };
  } else {
    mySubmission.value = { status: '未提交', score: null, submissionFilePath: null };
  }
}

function handleDownload(resourceId: string, fileName: string, outFile: string) {
  try {
    const url = getDownloadFileUrl({
      fileId: resourceId,
      path: '',
      fileName: fileName,
      outFile: outFile,
    });
    const link = document.createElement('a');
    link.href = url;
    link.style.display = 'none';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    ElMessage.success(`文件 ${fileName} 正在下载！`);
  } catch (err) {
    ElMessage.error('下载失败');
    console.error(err);
  }
}

function downloadExerciseFile() {
  const fileName = `${exerciseId}.pdf`;
  handleDownload(exerciseId, fileName, `exercises/${fileName}`);
}

function downloadSubmissionFile() {
  const fileName = `${exerciseId}_${props.userid}.pdf`;
  handleDownload(`${exerciseId}_${props.userid}`, fileName, `submissions/${fileName}`);
}

function beforeUpload(file: File) {
  const isLt5M = file.size / 1024 / 1024 < 5;
  if (!isLt5M) ElMessage.error('上传作业文件大小不能超过 5MB!');
  return isLt5M;
}

function handleUploadSuccess(response: any) {
  if (response?.code === 200) {
    ElMessage.success('作业提交成功！等待老师批改。');
    mySubmission.value = {
      status: '已提交',
      score: null,
      submissionFilePath: response.filePath,
    };
  } else {
    ElMessage.error('作业提交失败：' + (response.message || '未知错误'));
  }
}

function handleUploadError(error: any) {
  console.error('Upload error:', error);
  ElMessage.error('作业提交失败，请重试！');
}

function handleExceed() {
  ElMessage.warning('只能上传一份作业文件，请先移除现有文件。');
}

function handleRemove(file: File) {
  console.log('File removed:', file);
}
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
