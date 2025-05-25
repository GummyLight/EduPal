<!-- Home.vue -->
<template>
  <div class="home-page">
    <Sidebar />
    <div class="main-content">
      <router-view /> <!-- 切换显示子功能页面 -->
    </div>
  </div>
</template>

<script setup lang="ts">
import Sidebar from '../components/Siderbar.vue';
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
