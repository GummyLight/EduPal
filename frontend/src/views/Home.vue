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
import { HomeService, HomeData } from '../api/home';
import { ElMessage, ElSkeleton, ElButton } from 'element-plus'; // 导入 ElButton

// usertype 现在是 0 | 1 | 2 类型，-1 可以作为初始值或加载失败时的默认值
const usertype = ref<0 | 1 | 2 | -1>(-1); // 初始化为 -1，表示未知或未加载
const username = ref('');
const userid = ref('');
const isDataLoaded = ref(false); // 新增一个加载状态

// 页面加载函数
const loadInitialData = async () => {
  // 从本地存储获取 userId 和 userType
  // 假设这些值在用户登录成功后存储在 localStorage 中

  const storedUserId =localStorage.getItem('user_id');
  const storedUserType = localStorage.getItem('user_type'); // 通常也是字符串

  if (!storedUserId || !storedUserType) {
    console.error('Home.vue - 未从本地存储获取到 userId 或 userType，请确保已登录。');
    ElMessage.error('用户信息缺失，请重新登录！');
    usertype.value = -1; // 设置为未知
    isDataLoaded.value = true; // 标记为加载完成，但有错误
    // 考虑跳转到登录页面
    // router.push('/login');
    return;
  }

  // 将 storedUserType 转换为 number 类型，并进行类型断言以符合 0 | 1 | 2
  const parsedUserType = parseInt(storedUserType);
  if (isNaN(parsedUserType) || (parsedUserType !== 0 && parsedUserType !== 1 && parsedUserType !== 2)) {
    console.error('Home.vue - 从本地存储获取到的 userType 无效:', storedUserType);
    ElMessage.error('无效的用户类型信息，请尝试重新登录！');
    usertype.value = -1; // 设置为未知
    isDataLoaded.value = true; // 标记为加载完成，但有错误
    return;
  }

  try {
    // 传递获取到的 userId 和 userType 给 HomeService
    const homeData: HomeData = await HomeService.getHomeData(storedUserId, parsedUserType as (0 | 1 | 2));

    usertype.value = homeData.userType;
    username.value = homeData.username;
    userid.value = homeData.userId;
    isDataLoaded.value = true; // 数据加载完成

    console.log('Home.vue - Fetched usertype:', usertype.value);
    console.log('Home.vue - Fetched username:', username.value);
    console.log('Home.vue - Fetched userid:', userid.value);

  } catch (error) {
    console.error('Home.vue - 获取首页数据失败:', error);
    ElMessage.error('获取首页数据失败，请刷新页面或联系管理员！');
    usertype.value = -1; // 保持为 -1，表示加载失败或类型未知
    username.value = '访客';
    userid.value = 'guest';
    isDataLoaded.value = true; // 即使失败也设置为true，避免无限加载，但页面会显示错误提示
  }
};

onMounted(() => {
  loadInitialData();
});

const reloadPage = () => {
  window.location.reload(); // 简单粗暴地刷新页面
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


.loading-indicator, .error-indicator {
  margin-top: 50px;
  color: #606266;
  font-size: 16px;
}

.error-indicator p {
  margin-bottom: 20px;
  color: #F56C6C; /* 错误提示颜色 */
}
</style>