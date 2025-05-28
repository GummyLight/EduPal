<!-- Home.vue -->
<template>
  <div class="home-page">
    <Sidebar :usertype="usertype" />
    <div class="main-content">
      <router-view :username="username" /> <!-- 传递 username 给 HomeForm.vue -->
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import Sidebar from '../components/Siderbar.vue';

// 定义 usertype 变量
const usertype = ref(''); // 默认设置为教师

// 定义 username 变量
const username = ref('');

// 模拟登录成功后的处理
const handleLoginSuccess = (response: any) => {
  usertype.value = response.userType;
  username.value = response.userName;

};

const loginResponse = {
  message: '登录成功',
  success: true,
  userId: '123456',
  userName: '张三同学',
  userType: 'teacher',
};

// 调用登录成功的处理函数
handleLoginSuccess(loginResponse);
</script>

<style scoped>
.home-page {
  display: flex;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow: auto;
}
</style>
