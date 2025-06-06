```vue
<template>
  <div class="teacher-homework-review-page">
    <el-header class="navbar">
      <div class="title">作业批改 - {{ exerciseTitle }}</div>
      <el-button type="info" @click="router.back()">返回练习列表</el-button>
    </el-header>

    <el-main>
      <el-card class="review-card">
        <h3>学生提交列表</h3>
        <div class="actions" style="margin-bottom: 15px;">
          <el-button type="primary" icon="el-icon-download" @click="handleDownloadAllSubmissions">一键下载所有作业</el-button>
        </div>

        <el-table :data="studentSubmissions" border style="width: 100%">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="studentName" label="学生姓名" />
          <el-table-column prop="studentId" label="学号" />
          <el-table-column prop="submissionTime" label="提交时间" />
          <el-table-column prop="submissionStatus" label="提交状态">
            <template #default="scope">
              <el-tag :type="scope.row.score !== null ? 'success' : (scope.row.submissionFilePath ? 'info' : 'danger')">
                {{ scope.row.score !== null ? '已批改' : (scope.row.submissionFilePath ? '已提交' : '未提交') }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="作业文件">
            <template #default="scope">
              <el-button v-if="scope.row.submissionFilePath" type="text" @click="openDownloadDialog(scope.row)">下载</el-button>
              <span v-else>无</span>
            </template>
          </el-table-column>
          <el-table-column label="分数" width="180">
            <template #default="scope">
              <el-input-number
                  v-model="scope.row.score"
                  :min="0"
                  :max="100"
                  controls-position="right"
                  :disabled="scope.row.isScoring"
              />
            </template>
          </el-table-column>
          <el-table-column label="教师评语" width="250">
            <template #default="scope">
              <el-input
                  v-model="scope.row.comment"
                  placeholder="请输入评语"
                  type="textarea"
                  :autosize="{ minRows: 2, maxRows: 4 }"
                  :disabled="scope.row.isScoring"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button
                  type="primary"
                  size="huge"
                  @click="submitScore(scope.row)"
                  :loading="scope.row.isScoring"
              >
                提交
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

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
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElNotification, ElDialog, ElForm, ElFormItem, ElInput, ElAlert } from 'element-plus';
import JSZip from 'jszip';
import axios from 'axios';
import { getDownloadFileUrl } from '../api/materialsApi'; // 导入 getDownloadFileUrl

const API_BASE = 'http://localhost:8080';
const SERVER_FILE_ROOT_PATH = 'answer/';

const route = useRoute();
const router = useRouter();
const exerciseId = route.params.exerciseId as string;
const exerciseTitle = route.params.exerciseTitle as string;

const studentSubmissions = ref<any[]>([]);

// 下载弹窗相关状态
const downloadDialogVisible = ref(false);
const currentDownloadRow = ref<any>(null);
const downloadForm = ref({
  originalName: '',
  fileName: '',
  outFile: 'E:/downloadsedge/test/',
});

// 获取文件扩展名
function getFileExtension(filename: string): string {
  if (!filename || typeof filename !== 'string') {
    return '';
  }
  const lastDotIndex = filename.lastIndexOf('.');
  if (lastDotIndex === -1 || lastDotIndex === 0) {
    return '';
  }
  return filename.substring(lastDotIndex);
}

onMounted(() => {
  fetchStudentSubmissions();
});

/**
 * 获取所有学生的提交列表。
 */
async function fetchStudentSubmissions() {
  try {
    const response = await axios.get(`${API_BASE}/quiz/getQuizStudent`, {
      params: { quizId: exerciseId },
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
    });
    if (response.data.status === 'success') {
      // 防御性检查：确保 answers 存在且是数组
      const answers = response.data.answers || [];
      if (Array.isArray(answers)) {
        studentSubmissions.value = answers.map((item: any) => ({
          submissionId: item.answerId,
          studentId: item.studentId,
          studentName: item.userName,
          submissionTime: item.answerTime || null,
          submissionFilePath: item.answerContent || null,
          score: item.score || null,
          comment: item.feedback || '',
          isScoring: false,
        }));
        if (answers.length === 0) {
          ElMessage.info('该测验暂无学生提交');
        } else {
          ElMessage.success(`学生提交列表加载成功，共 ${answers.length} 条提交`);
        }
      } else {
        studentSubmissions.value = [];
        ElMessage.warning('返回的数据格式异常，请联系管理员');
        console.warn('Expected array but got:', typeof answers, answers);
      }
    } else {
      ElMessage.error(`获取学生提交列表失败：${response.data.message}`);
    }
  } catch (error: any) {
    if (error.response?.status === 403) {
      ElMessage.error('无权限访问该作业');
      router.push('/login');
    } else {
      ElMessage.error(`请求失败：${error.response?.data?.message || '请稍后重试'}`);
    }
    console.error('Error fetching submissions:', error);
  }
}

/**
 * 打开下载对话框。
 */
const openDownloadDialog = (row: any) => {
  if (!row.submissionId || row.submissionId === -1 || !row.submissionFilePath) {
    ElMessage.warning('该学生未提交作业文件。');
    return;
  }
  currentDownloadRow.value = row;
  downloadForm.value.originalName = row.submissionFilePath;
  downloadForm.value.fileName = `${row.studentName}_${row.studentId}${getFileExtension(row.submissionFilePath)}`;
  downloadForm.value.outFile = 'E:/downloadsedge/test/';
  downloadDialogVisible.value = true;
};

/**
 * 确认下载。
 */
const confirmDownload = () => {
  if (!downloadForm.value.fileName) {
    ElMessage.warning('下载文件名不能为空。');
    return;
  }
  if (!downloadForm.value.outFile) {
    ElMessage.warning('服务器保存文件夹路径不能为空。');
    return;
  }

  downloadDialogVisible.value = false;

  if (currentDownloadRow.value) {
    handleDownload(
        currentDownloadRow.value.submissionId,
        downloadForm.value.fileName,
        downloadForm.value.outFile
    );
  } else {
    ElMessage.error('无法获取下载作业信息。');
  }
};

/**
 * 下载单个学生提交的作业文件。
 */
async function handleDownload(submissionId: string, customFileName: string, customOutFile: string) {
  try {
    const downloadUrl = getDownloadFileUrl({
      fileId: submissionId,
      path: SERVER_FILE_ROOT_PATH,
      fileName: customFileName+'.pdf',
      outFile: customOutFile,
    });
    const response = await axios.get(downloadUrl);

    ElMessage.success(`文件 ${customFileName} 下载成功！`);
  } catch (error: any) {
    ElMessage.error(`下载失败：${error.response?.data?.message || '请检查网络或后端服务'}`);
    console.error('下载失败:', error);
  }
}

/**
 * 一键下载所有学生作业。
 */

import pLimit from 'p-limit'; // 使用 p-limit 限制并发

async function handleDownloadAllSubmissions() {
  ElNotification({
    title: '开始下载',
    message: '正在准备打包所有作业文件，请稍候...',
    type: 'info',
    duration: 0,
    id: 'download-progress',
  });

  const zip = new JSZip();
  const limit = pLimit(1); // 限制最大并发数为 5
  let downloadedCount = 0;
  const filesToDownload = studentSubmissions.value.filter((s) => s.submissionId);

  const downloadPromises = filesToDownload.map((submission) =>
      limit(async () => {
        try {
          const fileName = `${submission.studentName}.pdf`;
          const downloadUrl = getDownloadFileUrl({
            fileId: submission.submissionId,
            path: SERVER_FILE_ROOT_PATH,
            fileName: fileName,
            outFile: 'E:/downloadsedge/test/',
          });
          const response = await axios.get(downloadUrl, {
            responseType: 'arraybuffer',
          });
          if (response.data.byteLength === 0) {
            throw new Error(`Empty file received for ${fileName}`);
          }
          zip.file(fileName, response.data);
          downloadedCount++;
          ElNotification.close('download-progress');
          ElNotification({
            title: '下载进度',
            message: `正在打包: ${downloadedCount}/${filesToDownload.length} 个文件`,
            type: 'info',
            duration: 0,
            id: 'download-progress',
          });
        } catch (error) {
          console.error(`Failed to add ${submission.studentName}.pdf to zip:`, error);
          ElMessage.warning(`文件 ${submission.studentName} 下载失败，跳过。`);
        }
      })
  );

  await Promise.all(downloadPromises);

  if (downloadedCount > 0) {
    zip
        .generateAsync({ type: 'blob' })
        .then((content) => {
          const url = window.URL.createObjectURL(content);
          const link = document.createElement('a');
          link.href = url;
          link.setAttribute('download', `${exerciseTitle}_所有作业.zip`);
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);
          ElNotification.close('download-progress');
          ElMessage.success('所有作业已下载！');
        })
        .catch((error) => {
          ElNotification.close('download-progress');
          ElMessage.error('打包文件失败！');
          console.error('Error generating zip:', error);
        });
  } else {
    ElNotification.close('download-progress');
    ElMessage.info('没有可下载的作业文件。');
  }
}
// async function handleDownloadAllSubmissions() {
//   ElNotification({
//     title: '开始下载',
//     message: '正在准备打包所有作业文件，请稍候...',
//     type: 'info',
//     duration: 0,
//     id: 'download-progress'
//   });
//
//   const zip = new JSZip();
//   let filesToDownload = studentSubmissions.value.filter(s => s.submissionId);
//   let downloadedCount = 0;
//
//   for (const submission of filesToDownload) {
//     try {
//       const fileName = `${submission.studentName}.pdf`;
//       const downloadUrl = getDownloadFileUrl({
//         fileId: submission.submissionId,
//         path: SERVER_FILE_ROOT_PATH,
//         fileName: fileName,
//         outFile: 'E:/downloadsedge/test/',
//       });
//       const response = await axios.get(downloadUrl);
//       zip.file(fileName, response.data);
//       downloadedCount++;
//       ElNotification.close('download-progress');
//       ElNotification({
//         title: '下载进度',
//         message: `正在打包: ${downloadedCount}/${filesToDownload.length} 个文件`,
//         type: 'info',
//         duration: 0,
//         id: 'download-progress'
//       });
//     } catch (error) {
//       console.error(`Failed to add ${submission.submissionFilePath} to zip:`, error);
//       ElMessage.warning(`文件 ${submission.studentName} 下载失败，跳过。`);
//     }
//   }
//
//   if (downloadedCount > 0) {
//     zip.generateAsync({ type: "blob" })
//         .then(function(content) {
//           const url = window.URL.createObjectURL(content);
//           const link = document.createElement('a');
//           link.href = url;
//           link.setAttribute('download', `${exerciseTitle}_所有作业.zip`);
//           document.body.appendChild(link);
//           link.click();
//           document.body.removeChild(link);
//           window.URL.revokeObjectURL(url);
//           ElNotification.close('download-progress');
//           ElMessage.success('所有作业已下载！');
//         })
//         .catch(error => {
//           ElNotification.close('download-progress');
//           ElMessage.error('打包文件失败！');
//           console.error('Error generating zip:', error);
//         });
//   } else {
//     ElNotification.close('download-progress');
//     ElMessage.info('没有可下载的作业文件。');
//   }
// }

/**
 * 提交学生分数。
 */
async function submitScore(submission: any) {
  if (submission.score === null || submission.score === undefined) {
    ElMessage.warning('请输入分数！');
    return;
  }
  if (!Number.isInteger(submission.score) || submission.score < 0 || submission.score > 100) {
    ElMessage.warning('分数必须为 0-100 之间的整数！');
    return;
  }
  if (submission.comment && submission.comment.length > 500) {
    ElMessage.warning('评语不能超过500字！');
    return;
  }
  submission.isScoring = true;
  try {
    const response = await axios.post(`${API_BASE}/quiz/gradeQuiz`, null, {
      params: {
        answerId: submission.submissionId,
        score: submission.score,
        feedback: submission.comment || '',
      },
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
    });
    if (response.data.code === 200) {
      ElMessage.success(`${submission.studentName} 的分数提交成功！`);
      const index = studentSubmissions.value.findIndex(s => s.submissionId === submission.submissionId);
      if (index !== -1) {
        studentSubmissions.value[index] = {
          ...studentSubmissions.value[index],
          score: submission.score,
          comment: submission.comment || '',
          isScoring: false,
        };
      }
    } else {
      ElMessage.error(`提交分数失败：${response.data.message}`);
    }
  } catch (error: any) {
    ElMessage.error(`提交分数失败：${error.response?.data?.message || '请重试'}`);
    console.error('Error submitting score:', error);
  } finally {
    submission.isScoring = false;
  }
}
</script>

<style scoped>
.teacher-homework-review-page {
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
.review-card {
  margin: 20px;
  padding: 20px;
  background-color: #fff;
}
.review-card h3 {
  font-size: 20px;
  margin-bottom: 15px;
}
.actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 15px;
}
</style>
```