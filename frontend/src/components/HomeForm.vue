<!-- 本页面需要后端提供以下数据（括号中为数据来源表名）：用户名（学生）、已完成讲义数（学情信息）、已完成习题数（学情信息）、学情评级（学情信息）、答疑通过率（提问表）、待完成习题（习题）、提问通知（提问） -->

<!-- 后端返回数据应该是这个样子的
{
  "username": "张三同学",
  "finishedLectures": 10,
  "finishedExercises": 20,
  "gradeLevel": "A+",
  "answerRate": "90%",
  "todayTasks": [
    "完成《线性代数》第三章练习题（10题）",
    "观看《概率论》第五节视频（20分钟）"
  ],
  "notifications": [
    "2025-05-23：题库已更新，新增 300 题",
    "2025-05-22：新增“社区交流”模块，欢迎体验"
  ]
}
-->

<!-- HomeForm.vue -->
<template>
  <div class="home-form">
    <el-card class="welcome-card" shadow="hover">
      <h2>👋 欢迎你，{{ props.username }}！</h2>
      <p>今天是 {{ today }}，祝你学习愉快～</p>
    </el-card>

    <el-row :gutter="20" class="overview">
      <el-col :span="6">
        <el-card shadow="hover"><strong>📚 已完成讲义：</strong> {{ Finished_lectures }} 篇</el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover"><strong>📝 已完成习题数：</strong> {{ Finished_exercises }} 题</el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover"><strong>🕒 学情评级：</strong> {{ Grade_level }}</el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover"><strong>🎯 答疑通过率：</strong> {{ Answer_rate }}</el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="info-section">
      <el-col :span="12">
        <el-card shadow="always" class="task-card">
          <h3> 待完成习题</h3>
          <ul>
            <li v-for="task in todayTasks" :key="task">{{ task }}</li>
          </ul>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="always" class="notice-card">
          <h3>📣 提问通知 </h3>
          <ul>
            <li v-for="notice in notifications" :key="notice">{{ notice }}</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, defineProps } from 'vue';

const props = defineProps({
  username: {
    type: String,
    required: true
  }
});

const today = new Date().toLocaleDateString('zh-CN', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
});

const Finished_lectures = ref(10);
const Finished_exercises = ref(20);
const Grade_level = ref('A+');
const Answer_rate = ref('90%');

const todayTasks = ref<string[]>([
  "完成《线性代数》第三章练习题（10题）",
  "观看《概率论》第五节视频（20分钟）"
]);

const notifications = ref<string[]>([
  "2025-05-23：题库已更新，新增 300 题",
  "2025-05-22：新增“社区交流”模块，欢迎体验"
]);
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

