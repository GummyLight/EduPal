<template>
  <div class="qa-page">
    <!-- 学生端智能答疑 -->
    <QAForm 
      v-if="usertype === 1"
      :usertype="usertype"
      :username="username" 
      :userid="userid"
    />
    <!-- 教师端智能答疑 -->
    <TeacherQAForm 
      v-else-if="usertype === 2"
      :usertype="usertype"
      :username="username" 
      :userid="userid"
    />
    <!-- 未知用户类型 -->
    <div v-else class="error-message">
      <h2>访问受限</h2>
      <p>您没有权限访问智能答疑功能</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import QAForm from '@/components/QAForm.vue';
import TeacherQAForm from '@/components/TeacherQAForm.vue';

// 接收从父组件传递的props
interface Props {
  usertype: 0 | 1 | 2 | -1;
  username: string;
  userid: string;
}

const props = defineProps<Props>();

// 解构props供模板使用
const { usertype, username, userid } = props;
</script>

<style scoped>
.qa-page {
  height: 100vh;
  overflow: hidden;
}

.error-message {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f5f7fa;
  color: #606266;
}

.error-message h2 {
  margin: 0 0 16px 0;
  font-size: 24px;
  font-weight: 600;
}

.error-message p {
  margin: 0;
  font-size: 16px;
  color: #909399;
}
</style>
