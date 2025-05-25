<template>
  <div class="home-form">
    <el-card class="welcome-card" shadow="hover">
      <h2>👋 欢迎你，{{ username }}！</h2> <!-- 这里的用户名是根据用户表的外键关联到学生表或者教师表的姓名属性中获取的-->
      <p>今天是 {{ today }}，祝你学习愉快～</p> <!--这个不用管，会自动获取-->
    </el-card>

    <el-row :gutter="20" class="overview">
      <el-col :span="6">
        <el-card shadow="hover"><strong>📚 已完成课程：</strong> 12 门</el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover"><strong>📝 今日练习题数：</strong> 20 题</el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover"><strong>🕒 学习时长：</strong> 1小时45分钟</el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover"><strong>🎯 答疑通过率：</strong> 88%</el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="info-section">
      <el-col :span="12">
        <el-card shadow="always" class="task-card">
          <h3> 今日任务</h3>
          <ul>
            <li>完成《线性代数》第三章练习题（10题）</li>
            <li>观看《概率论》第五节视频（20分钟）</li>
          </ul>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="always" class="notice-card">
          <h3>📣 最新公告</h3>
          <ul>
            <li>2025-05-23：题库已更新，新增 300 题</li>
            <li>2025-05-22：新增“社区交流”模块，欢迎体验</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const username = ref('张三同学');
const today = new Date().toLocaleDateString('zh-CN', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
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
