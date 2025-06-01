<template>
  <div class="home-form">
    <el-card class="welcome-card" shadow="hover">
      <h2>👋 欢迎你，{{ props.username }}！</h2>
      <p>今天是 {{ today }}，祝你学习愉快～</p>
    </el-card>

    <el-row :gutter="20" class="overview">
      <el-col :span="6">
        <el-card shadow="hover"><strong>📚 已完成讲义：</strong> {{ finishedLectures }} 篇</el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover"><strong>📝 已完成习题数：</strong> {{ finishedExercises }} 题</el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover"><strong>🕒 学情评级：</strong> {{ gradeLevel }}</el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover"><strong>🎯 提问数量：</strong> {{ formattedAnswerRate }} 个</el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="info-section">
      <el-col :span="12">
        <el-card shadow="always" class="task-card">
          <h3> 待完成习题</h3>
          <ul>
            <li v-for="(task, index) in todayTasks" :key="index">{{ task }}</li>
          </ul>
          <el-empty v-if="todayTasks.length === 0" description="暂无待完成习题"></el-empty>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="always" class="notice-card">
          <h3>📣 提问通知 </h3>
          <ul>
            <li v-for="(notice, index) in notifications" :key="index">{{ notice }}</li>
          </ul>
          <el-empty v-if="notifications.length === 0" description="暂无通知"></el-empty>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, defineProps, onMounted, watch, computed } from 'vue';
import { HomeService, HomeData } from '../api/home';

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

const finishedLectures = ref(0);
const finishedExercises = ref(0);
const gradeLevel = ref('');
const rawAnswerRate = ref(0); // 存储原始数字，因为 HomeData 中是 number
const todayTasks = ref<string[]>([]);
const notifications = ref<string[]>([]);

// 使用 computed 属性来格式化 answerRate 以便显示
const formattedAnswerRate = computed(() => {
  return `${rawAnswerRate.value}`;
});

// **关键改动：将 loadHomeData 函数移动到 watch 和 onMounted 之前**
const loadHomeData = async (userId: string, userType: 0 | 1 | 2) => {
  try {
    const data: HomeData = await HomeService.getHomeData(userId, userType);

    finishedLectures.value = data.finishedLectures;
    finishedExercises.value = data.finishedExercises;
    gradeLevel.value = data.gradeLevel;
    rawAnswerRate.value = data.answerRate;
    todayTasks.value = data.todayTasks;
    notifications.value = data.notifications;

  } catch (error) {
    console.error('HomeForm.vue - 加载首页数据失败:', error);
    finishedLectures.value = 0;
    finishedExercises.value = 0;
    gradeLevel.value = 'N/A';
    rawAnswerRate.value = 0;
    todayTasks.value = ['加载失败，请刷新'];
    notifications.value = ['加载失败，请刷新'];
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
.notice-card ul {
  padding-left: 20px;
}
.task-card li,
.notice-card li {
  line-height: 1.8;
}
</style>

