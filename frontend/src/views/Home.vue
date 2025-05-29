<template>
  <div class="home-page">
    <Sidebar :usertype="usertype" />
    <div class="main-content">
      <router-view :username="username" :usertype="usertype" :userid="userid" /> </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import Sidebar from '../components/Siderbar.vue';

// 定义 usertype, username, userid 变量
const usertype = ref(''); // 初始值可以为空字符串，但要确保后续会被正确赋值
const username = ref('');
const userid = ref(''); // 新增 userid 的 ref

console.log('Home.vue - Initial usertype:', usertype.value); // 打印初始值
console.log('Home.vue - Initial username:', username.value); // 打印初始值
console.log('Home.vue - Initial userid:', userid.value); // 打印初始值


// 模拟登录成功后的处理
const handleLoginSuccess = (response: any) => {
  usertype.value = response.userType;
  username.value = response.userName;
  userid.value = response.userId; // 确保 userid 被赋值

  console.log('Home.vue - After handleLoginSuccess - usertype:', usertype.value); // 打印赋值后的值
  console.log('Home.vue - After handleLoginSuccess - username:', username.value); // 打印赋值后的值
  console.log('Home.vue - After handleLoginSuccess - userid:', userid.value); // 打印赋值后的值
};

const loginResponse = {
  message: '登录成功',
  success: true,
  userId: '114514',
  userName: '孙梓康',
  userType: 'student', // teacher or student
};

// 调用登录成功的处理函数
handleLoginSuccess(loginResponse);

// 在组件渲染前，再次确认最终要传递给子组件的值
console.log('Home.vue - Final usertype for children:', usertype.value);
console.log('Home.vue - Final username for children:', username.value);
console.log('Home.vue - Final userid for children:', userid.value);
</script>


<style scoped>
.home-page {
  display: flex;
  height: 100vh; /* 使用 height: 100vh 确保占据视口全高 */
  /* min-height: 100vh; 也可以，但 height: 100vh 更直接 */
  overflow: hidden; /* **关键：防止 home-page 自身滚动** */
  background-color: #f5f7fa;
}

/* 侧边栏的样式由 Sidebar.vue 控制，但要确保它在 home-page 的 flex 布局下高度正确 */
/* Sidebar.vue 中的 .sidebar-menu 已经设置了 min-height: 100vh，这很好 */

.main-content {
  flex: 1; /* 占据剩余空间 */
  padding: 20px;
  overflow-y: auto; /* **关键：只允许垂直方向滚动** */
  /* overflow: auto; 也可以，但明确 overflow-y 更好 */
  box-sizing: border-box; /* 确保 padding 不会导致溢出 */
  /* background-color: #f5f7fa; */ /* 可以在这里设置背景色，或者让子组件自己控制 */
}
</style>