<template>
  <div class="home-form">
    <el-card class="welcome-card" shadow="hover">
      <h2>👋 欢迎你，{{ props.username }}！</h2>
      <p>今天是 {{ today }}，祝你学习愉快～</p>
    </el-card>

    <div v-if="props.usertype === 1">
      <el-row :gutter="20" class="overview">
        <el-col :span="6">
          <el-card shadow="hover"><strong>📚 已完成讲义：</strong> {{ studentData.finishedLectures }} 篇</el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover"><strong>📝 已完成习题数：</strong> {{ studentData.finishedExercises }} 题</el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover"><strong>🕒 学情评级：</strong> {{ studentData.gradeLevel }}</el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover"><strong>🎯 提问数量：</strong> {{ studentData.answerRate }} 个</el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="info-section">
        <el-col :span="12">
          <el-card shadow="always" class="task-card">
            <h3>待完成习题</h3>
            <ul>
              <li v-for="(task, index) in studentData.todayTasks" :key="index">{{ task }}</li>
            </ul>
            <el-empty v-if="studentData.todayTasks.length === 0" description="暂无待完成习题"></el-empty>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="always" class="notice-card">
            <h3>📣 提问通知</h3>
            <ul>
              <li v-for="(notice, index) in studentData.notifications" :key="index">{{ notice }}</li>
            </ul>
            <el-empty v-if="studentData.notifications.length === 0" description="暂无通知"></el-empty>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div v-else-if="props.usertype === 2">
      <el-row :gutter="20" class="overview">
        <el-col :span="6">
          <el-card shadow="hover"><strong>❓ 待回答问题：</strong> {{ teacherData.unfinishedQAs }} 个</el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover"><strong>📤 已上传的习题：</strong> {{ teacherData.uploadExercises }} 份</el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover"><strong>📖 已上传的资料：</strong> {{ teacherData.uploadResources }} 份</el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover"><strong>🏫 管理班级数：</strong> {{ teacherData.classIds.length }} 个</el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="info-section">
        <el-col :span="12">
          <el-card shadow="always" class="student-performance-card">
            <h3>学生学情 - 前五名</h3>
            <el-table :data="teacherData.topStudents" style="width: 100%" v-if="teacherData.topStudents.length > 0">
              <el-table-column prop="studentName" label="学生姓名"></el-table-column>
              <el-table-column prop="studentAverageScore" label="平均分"></el-table-column>
              <el-table-column prop="studentVariation" label="波动性"></el-table-column>
            </el-table>
            <el-empty v-else description="暂无前五名学生数据"></el-empty>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="always" class="student-performance-card">
            <h3>学生学情 - 后五名</h3>
            <el-table :data="teacherData.bottomStudents" style="width: 100%" v-if="teacherData.bottomStudents.length > 0">
              <el-table-column prop="studentName" label="学生姓名"></el-table-column>
              <el-table-column prop="studentAverageScore" label="平均分"></el-table-column>
              <el-table-column prop="studentVariation" label="波动性"></el-table-column>
            </el-table>
            <el-empty v-else description="暂无后五名学生数据"></el-empty>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div v-else>
      <el-empty description="当前用户类型无特定首页展示内容或加载中..."></el-empty>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, defineProps, onMounted, watch, computed } from 'vue';
import { HomeService, StudentHomeData, TeacherHomeData } from '../api/home'; // 导入 StudentHomeData 和 TeacherHomeData

const props = defineProps({
  username: {
    type: String,
    required: true
  },
  usertype: {
    type: Number as () => 0 | 1 | 2, // 确保这里也是 0 | 1 | 2
    required: true
  },
  userid: {
    type: String,
    required: true
  }
});

const today = new Date().toLocaleDateString('zh-CN', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
});

// 学生数据状态
const studentData = ref<StudentHomeData>({
  username: '',
  userType: 1,
  userId: '',
  finishedLectures: 0,
  finishedExercises: 0,
  gradeLevel: '',
  answerRate: 0,
  todayTasks: [],
  notifications: [],
});

// 教师数据状态
const teacherData = ref<TeacherHomeData>({
  username: '',
  userType: 2,
  userId: '',
  unfinishedQAs: 0,
  uploadExercises: 0,
  uploadResources: 0,
  classIds: [],
  topStudents: [],
  bottomStudents: [],
});


// **关键改动：根据 usertype 调用不同的服务方法**
const loadHomeData = async (userId: string, userType: 0 | 1 | 2) => {
  try {
    if (userType === 1) { // 学生
      const data: StudentHomeData = await HomeService.getStudentHomeData(userId); // 调用学生特有的接口
      studentData.value = data; // 直接赋值
    } else if (userType === 2) { // 教师
      const data: TeacherHomeData = await HomeService.getTeacherHomeData(userId); // 调用教师特有的接口
      teacherData.value = data; // 直接赋值
    }
  } catch (error) {
    console.error('HomeForm.vue - 加载首页数据失败:', error);
    // 根据用户类型设置不同的默认值或错误提示
    if (userType === 1) {
      studentData.value = {
        username: props.username,
        userType: 1,
        userId: props.userid,
        finishedLectures: 0,
        finishedExercises: 0,
        gradeLevel: 'N/A',
        answerRate: 0,
        todayTasks: ['加载失败，请刷新'],
        notifications: ['加载失败，请刷新'],
      };
    } else if (userType === 2) {
      teacherData.value = {
        username: props.username,
        userType: 2,
        userId: props.userid,
        unfinishedQAs: 0,
        uploadExercises: 0,
        uploadResources: 0,
        classIds: [],
        topStudents: [],
        bottomStudents: [],
      };
    }
  }
};

// 监听 props.userid 和 props.usertype 的变化，确保两者都有值才加载数据
watch(() => [props.userid, props.usertype], ([newUserId, newUsertype]) => {
  if (newUserId && (newUsertype === 0 || newUsertype === 1 || newUsertype === 2)) {
    loadHomeData(newUserId as string, newUsertype);
  }
}, { immediate: true });

// 在组件挂载时也尝试加载一次
onMounted(() => {
  if (props.userid && (props.usertype === 0 || props.usertype === 1 || props.usertype === 2)) {
    loadHomeData(props.userid, props.usertype);
  }
});
</script>

<style scoped>
.home-form {
  padding: 20px;
}
.welcome-card {
  margin-bottom: 20px;
}
.overview {
  margin-bottom: 20px;
}
.info-section .el-card {
  height: 100%;
}
.task-card ul,
.notice-card ul,
.student-performance-card ul { /* 添加教师部分的样式 */
  padding-left: 20px;
}
.task-card li,
.notice-card li {
  line-height: 1.8;
}

.student-performance-card .el-table {
  margin-top: 10px;
}
</style>