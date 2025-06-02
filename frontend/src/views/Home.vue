<template>
  <div class="home-page">
    <Sidebar :usertype="usertype" />
    <div class="main-content">
      <router-view
          v-if="isDataLoaded && usertype !== -1"
          :username="username"
          :usertype="usertype"
          :userid="userid"
      />
      <div v-else-if="!isDataLoaded" class="loading-indicator">
        <el-skeleton :rows="5" animated />
        <p>正在加载首页数据...</p>
      </div>
      <div v-else class="error-indicator">
        <p>数据加载失败或用户类型未知，请尝试重新登录。</p>
        <el-button @click="reloadPage">刷新页面</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import Sidebar from '../components/Siderbar.vue';
// 导入 HomeService 和相关的 HomeData 接口，以便在需要时使用，尽管此处主要用于 prop 传递
import { HomeService, StudentHomeData, TeacherHomeData } from '../api/home';
import { ElMessage, ElSkeleton, ElButton } from 'element-plus';

const usertype = ref<0 | 1 | 2 | -1>(-1); // 初始化为 -1，表示未知或未加载
const username = ref(''); // 保持为空字符串，从 localStorage 或后端获取真实值
const userid = ref('');
const isDataLoaded = ref(false); // 新增一个加载状态

// 页面加载函数
const loadInitialData = async () => {
  const storedUserId = localStorage.getItem('user_id');
  const storedUserType = localStorage.getItem('user_type');
  // 尝试从 localStorage 获取 username
  const storedUsername = localStorage.getItem('user_name');

  if (!storedUserId || !storedUserType) {
    console.error('Home.vue - 未从本地存储获取到 userId 或 userType，请确保已登录。');
    ElMessage.error('用户信息缺失，请重新登录！');
    usertype.value = -1;
    isDataLoaded.value = true;
    return;
  }

  const parsedUserType = parseInt(storedUserType);
  if (isNaN(parsedUserType) || (parsedUserType !== 0 && parsedUserType !== 1 && parsedUserType !== 2)) {
    console.error('Home.vue - 从本地存储获取到的 userType 无效:', storedUserType);
    ElMessage.error('无效的用户类型信息，请尝试重新登录！');
    usertype.value = -1;
    isDataLoaded.value = true;
    return;
  }

  try {
    userid.value = storedUserId;
    usertype.value = parsedUserType as (0 | 1 | 2);

    // 如果从 localStorage 获取到 username，则设置它
    if (storedUsername) {
      username.value = storedUsername;
    } else {
      // 如果 localStorage 中没有 username，保持为空字符串，HomeForm 会在数据加载后显示真实的用户名
      username.value = '';
      console.warn('Home.vue - localStorage 中未找到 username，HomeForm 将在数据加载后显示。');
    }

    isDataLoaded.value = true; // 只要身份信息加载成功，就标记为true，让router-view渲染HomeForm

  } catch (error) {
    console.error('Home.vue - 处理用户身份信息失败:', error);
    ElMessage.error('加载用户信息失败，请刷新页面！');
    usertype.value = -1;
    username.value = ''; // 失败时也保持为空，避免显示“访客”字样
    userid.value = 'guest';
    isDataLoaded.value = true; // 即使失败也设置为true，避免无限加载
  }
};

onMounted(() => {
  loadInitialData();
});

const reloadPage = () => {
  window.location.reload();
};

// 这些 console.log 会在 onMounted 之前执行，所以它们可能仍然显示初始的空值
// 它们主要用于开发调试，在生产环境中通常会移除
console.log('Home.vue - Initial usertype:', usertype.value);
console.log('Home.vue - Initial username:', username.value);
console.log('Home.vue - Initial userid:', userid.value);
</script>

<style scoped>
.home-page {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background-color: #f5f7fa;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  box-sizing: border-box;
}

.loading-indicator, .error-indicator {
  margin-top: 50px;
  color: #606266;
  font-size: 16px;
}

.error-indicator p {
  margin-bottom: 20px;
  color: #F56C6C;
}
</style>