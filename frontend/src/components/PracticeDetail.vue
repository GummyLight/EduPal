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
          <el-button type="primary" icon="el-icon-download" @click="downloadExerciseFile(exerciseDetail?.习题文件路径)">
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
            <el-button type="text" @click="downloadSubmissionFile(mySubmission.submissionFilePath)">下载我的提交</el-button>
          </p>
        </div>
        <div v-else-if="mySubmission.status === '已提交'">
          <p><strong>提交状态:</strong> <el-tag type="info">已提交</el-tag> (等待批改)</p>
          <p v-if="mySubmission.submissionFilePath">
            <strong>我的提交:</strong>
            <el-button type="text" @click="downloadSubmissionFile(mySubmission.submissionFilePath)">下载我的提交</el-button>
          </p>
          <p>您已提交作业，如需重新提交请联系老师。</p>
        </div>
        <div v-else>
          <p><strong>提交状态:</strong> <el-tag type="danger">未提交</el-tag></p>
          <el-upload
              class="upload-demo"
              action="/api/submit-homework"
              :headers="uploadHeaders"
              :data="{ exerciseId: exerciseId }"
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
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElNotification } from 'element-plus'; // 导入 Element Plus 消息提示

const route = useRoute();
const router = useRouter();
const exerciseId = route.params.exerciseId as string; // 获取练习ID

const exerciseDetail = ref<any>(null); // 存储练习详情
const mySubmission = ref<any>({ status: '未提交', score: null, submissionFilePath: null }); // 存储学生自己的提交状态和分数

const uploadHeaders = ref({}); // 用于存储上传文件的认证头部信息，例如 Authorization token
const fileList = ref<any[]>([]); // 上传文件列表

onMounted(() => {
  fetchExerciseDetail();
  fetchMySubmissionStatus();
  // 实际项目中，这里需要从 localStorage 或 Vuex 获取用户的认证 token
  // uploadHeaders.value = { 'Authorization': `Bearer ${localStorage.getItem('token')}` };
});

/**
 * 获取练习详情。
 * @param {string} id - 习题号
 */
async function fetchExerciseDetail() {
  console.log(`fetching exercise detail for ${exerciseId}`);
  // 实际：调用后端API获取练习详情，包含习题文件路径
  // try {
  //   const response = await axios.get(`/api/exercises/${exerciseId}`);
  //   exerciseDetail.value = response.data;
  // } catch (error) {
  //   ElMessage.error('获取练习详情失败');
  //   console.error('Error fetching exercise detail:', error);
  // }

  // 模拟数据
  exerciseDetail.value = {
    习题号: exerciseId,
    科目: 'math',
    内容: '函数与图像基础练习',
    发布时间: '2025-05-20 10:00:00',
    截止时间: '2025-06-01 23:59:59',
    发布人: '张老师',
    习题文件路径: `/files/exercises/${exerciseId}.pdf`, // 模拟路径
    类型: '选择题',
    难度: '中等',
    知识点: '函数图像',
  };
}

/**
 * 获取当前学生对该练习的提交状态和分数。
 * @param {string} exerciseId - 习题号
 * @param {string} studentId - 学生ID
 */
async function fetchMySubmissionStatus() {
  console.log(`fetching my submission status for ${exerciseId}`);
  // 实际：调用后端API获取当前学生的提交状态和分数
  // 假设学生ID可以从用户状态中获取
  const studentId = 'student123'; // 模拟学生ID
  // try {
  //   const response = await axios.get(`/api/submissions/<span class="math-inline">\{exerciseId\}/</span>{studentId}`);
  //   mySubmission.value = response.data; // 包含 status, score, submissionFilePath
  // } catch (error) {
  //   // 如果没有提交记录，后端可能返回404或空数据，此时设为未提交
  //   mySubmission.value = { status: '未提交', score: null, submissionFilePath: null };
  //   console.warn('No submission found or error fetching submission:', error);
  // }

  // 模拟数据
  if (exerciseId === 'EX20250503') {
    mySubmission.value = { status: '已批改', score: 95, submissionFilePath: `/files/submissions/EX20250503_student123.pdf` };
  } else if (exerciseId === 'EX20250502') {
    mySubmission.value = { status: '已提交', score: null, submissionFilePath: `/files/submissions/EX20250502_student123.pdf` };
  } else {
    mySubmission.value = { status: '未提交', score: null, submissionFilePath: null };
  }
}


/**
 * 下载练习题文件。
 * @param {string} filePath - 文件在服务器上的路径
 */
function downloadExerciseFile(filePath: string) {
  console.log('Downloading exercise file from:', filePath);
  // 实际：通过后端提供的下载接口或直接文件URL下载
  window.open(filePath, '_blank'); // 直接打开或下载文件
  // 或者通过后端接口下载：axios.get(`/api/download-exercise?path=${filePath}`, { responseType: 'blob' })
}

/**
 * 下载学生提交的作业文件。
 * @param {string} filePath - 文件在服务器上的路径
 */
function downloadSubmissionFile(filePath: string) {
  console.log('Downloading submission file from:', filePath);
  window.open(filePath, '_blank');
}

/**
 * 文件上传前校验。
 * @param {File} file - 待上传的文件
 */
function beforeUpload(file: File) {
  const isLt5M = file.size / 1024 / 1024 < 5; // 5MB限制
  if (!isLt5M) {
    ElMessage.error('上传作业文件大小不能超过 5MB!');
  }
  return isLt5M;
}

/**
 * 文件上传成功回调。
 * @param {any} response - 后端返回的响应数据
 * @param {File} file - 上传成功的文件
 */
function handleUploadSuccess(response: any, file: File) {
  console.log('Upload success:', response);
  if (response && response.code === 200) { // 假设后端返回 code 200 表示成功
    ElMessage.success('作业提交成功！等待老师批改。');
    mySubmission.value.status = '已提交';
    mySubmission.value.submissionFilePath = response.filePath; // 假设后端返回文件路径
    // 重新获取提交状态以更新UI
    fetchMySubmissionStatus();
  } else {
    ElMessage.error('作业提交失败：' + (response.message || '未知错误'));
  }
}

/**
 * 文件上传失败回调。
 * @param {any} error - 错误信息
 * @param {File} file - 失败的文件
 */
function handleUploadError(error: any, file: File) {
  console.error('Upload error:', error);
  ElMessage.error('作业提交失败，请重试！');
}

/**
 * 文件列表超出限制回调。
 */
function handleExceed() {
  ElMessage.warning('只能上传一份作业文件，请先移除现有文件。');
}

/**
 * 移除文件回调。
 * @param {File} file - 被移除的文件
 */
function handleRemove(file: File) {
  console.log('File removed:', file);
  // 实际项目中，如果文件已经上传到服务器，这里可能需要调用后端接口删除文件
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