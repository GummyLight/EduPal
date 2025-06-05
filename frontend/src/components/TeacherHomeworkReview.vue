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
              <el-button v-if="scope.row.submissionFilePath" type="text" @click="downloadSubmissionFile(scope.row)">下载</el-button>
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
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElNotification } from 'element-plus';
import JSZip from 'jszip'; // 引入 JSZip 库用于文件打包下载

const route = useRoute();
const router = useRouter();
const exerciseId = route.params.exerciseId as string;
const exerciseTitle = route.params.exerciseTitle as string; // 从路由获取练习标题

const studentSubmissions = ref<any[]>([]); // 存储学生提交列表

onMounted(() => {
  fetchStudentSubmissions();
});

/**
 * 获取所有学生的提交列表。
 * @param {string} exerciseId - 习题号
 * @param {string} classId - 班级ID (如果教师筛选了班级)
 */
async function fetchStudentSubmissions() {
  console.log(`fetching student submissions for exercise ${exerciseId}`);
  // 实际：调用后端API获取学生提交列表，包含学生姓名、提交时间、提交文件路径、分数等
  // try {
  //   const response = await axios.get(`/api/exercises/${exerciseId}/submissions`, {
  //     params: { classId: route.query.classId } // 如果有班级筛选
  //   });
  //   studentSubmissions.value = response.data.map(item => ({ ...item, isScoring: false })); // 添加评分状态
  // } catch (error) {
  //   ElMessage.error('获取学生提交列表失败');
  //   console.error('Error fetching submissions:', error);
  // }

  // 模拟数据
  studentSubmissions.value = [
    {
      submissionId: 'sub001',
      studentId: 'S2023001',
      studentName: '王小明',
      submissionTime: '2025-05-28 14:30:00',
      submissionFilePath: `/files/submissions/${exerciseId}_S2023001.pdf`,
      score: null,
      comment: '', // 添加教师评语字段
      isScoring: false,
    },
    {
      submissionId: 'sub002',
      studentId: 'S2023002',
      studentName: '李华',
      submissionTime: '2025-05-28 15:00:00',
      submissionFilePath: `/files/submissions/${exerciseId}_S2023002.docx`,
      score: 88,
      comment: '非常好，继续保持！', // 示例评语
      isScoring: false,
    },
    {
      submissionId: 'sub003',
      studentId: 'S2023003',
      studentName: '张丽',
      submissionTime: '2025-05-29 09:00:00',
      submissionFilePath: null,
      score: null,
      comment: '', // 添加教师评语字段
      isScoring: false,
    },
    {
      submissionId: 'sub004',
      studentId: 'S2023004',
      studentName: '赵强',
      submissionTime: '2025-05-29 10:00:00',
      submissionFilePath: `/files/submissions/${exerciseId}_S2023004.zip`,
      score: null,
      comment: '', // 添加教师评语字段
      isScoring: false,
    },
  ];
}

/**
 * 下载单个学生提交的作业文件。
 * @param {object} submission - 提交记录对象
 */
function downloadSubmissionFile(submission: any) {
  if (submission.submissionFilePath) {
    console.log('Downloading submission file:', submission.submissionFilePath);
    window.open(submission.submissionFilePath, '_blank');
  } else {
    ElMessage.warning('该学生未提交作业文件。');
  }
}

/**
 * 一键下载所有学生作业。
 * 需要后端提供一个打包下载所有作业的接口，或者前端逐个下载后打包。
 * 建议后端提供打包接口，前端实现较为复杂，容易出现内存问题或下载超时。
 */
async function handleDownloadAllSubmissions() {
  ElNotification({
    title: '开始下载',
    message: '正在准备打包所有作业文件，请稍候...',
    type: 'info',
    duration: 0, // 不自动关闭
    id: 'download-progress'
  });

  // 实际：建议调用后端接口进行打包下载
  // try {
  //   const response = await axios.get(`/api/exercises/${exerciseId}/download-all-submissions`, {
  //     responseType: 'blob' // 接收二进制数据
  //   });
  //   const blob = new Blob([response.data], { type: 'application/zip' });
  //   const url = window.URL.createObjectURL(blob);
  //   const link = document.createElement('a');
  //   link.href = url;
  //   link.setAttribute('download', `${exerciseTitle}_所有作业.zip`);
  //   document.body.appendChild(link);
  //   link.click();
  //   document.body.removeChild(link);
  //   window.URL.revokeObjectURL(url);
  //   ElNotification.close('download-progress');
  //   ElMessage.success('所有作业已下载！');
  // } catch (error) {
  //   ElNotification.close('download-progress');
  //   ElMessage.error('下载所有作业失败，请重试！');
  //   console.error('Error downloading all submissions:', error);
  // }


  // 模拟前端打包下载（仅供演示，实际不推荐大量文件）
  const zip = new JSZip();
  let filesToDownload = studentSubmissions.value.filter(s => s.submissionFilePath);
  let downloadedCount = 0;

  for (const submission of filesToDownload) {
    try {
      const fileName = `<span class="math-inline">\{submission\.studentName\}\_</span>{submission.studentId}_${submission.submissionFilePath.split('/').pop()}`;
      // 模拟文件下载 (实际应使用 axios.get(submission.submissionFilePath, {responseType: 'arraybuffer'}))
      const fileContent = await fetch(submission.submissionFilePath).then(res => res.blob());
      zip.file(fileName, fileContent);
      downloadedCount++;
      ElNotification.close('download-progress');
      ElNotification({
        title: '下载进度',
        message: `正在打包: <span class="math-inline">\{downloadedCount\}/</span>{filesToDownload.length} 个文件`,
        type: 'info',
        duration: 0,
        id: 'download-progress'
      });
    } catch (error) {
      console.error(`Failed to add ${submission.submissionFilePath} to zip:`, error);
      ElMessage.warning(`文件 ${submission.studentName} 下载失败，跳过。`);
    }
  }

  if (downloadedCount > 0) {
    zip.generateAsync({ type: "blob" })
        .then(function(content) {
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
        .catch(error => {
          ElNotification.close('download-progress');
          ElMessage.error('打包文件失败！');
          console.error('Error generating zip:', error);
        });
  } else {
    ElNotification.close('download-progress');
    ElMessage.info('没有可下载的作业文件。');
  }
}


/**
 * 提交学生分数。
 * @param {object} submission - 学生提交记录对象
 */
async function submitScore(submission: any) {
  if (submission.score === null || submission.score === undefined) {
    ElMessage.warning('请输入分数！');
    return;
  }
  submission.isScoring = true; // 设置加载状态
  console.log(`Submitting score ${submission.score} for submission ${submission.submissionId}`);
  // 实际：调用后端API更新学生提交表的分数
  // try {
  //   await axios.post(`/api/submissions/${submission.submissionId}/score`, { score: submission.score });
  //   ElMessage.success(`${submission.studentName} 的分数提交成功！`);
  //   submission.isScoring = false;
  //   fetchStudentSubmissions(); // 重新获取数据以更新状态
  // } catch (error) {
  //   ElMessage.error('提交分数失败，请重试！');
  //   console.error('Error submitting score:', error);
  //   submission.isScoring = false;
  // }

  // 模拟成功
  setTimeout(() => {
    ElMessage.success(`${submission.studentName} 的分数提交成功！`);
    submission.isScoring = false;
    submission.score = submission.score; // 确保数据模型更新
    fetchStudentSubmissions(); // 重新加载以更新表格状态，例如 '已批改'
  }, 1000);
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
  justify-content: flex-end; /* 右对齐 */
  margin-bottom: 15px;
}
</style>