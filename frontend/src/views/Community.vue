<template>
  <div class="community-view-wrapper">
    <CommunityForm />
  </div>
</template>

<script setup lang="ts">
import CommunityForm from '../components/CommunityForm.vue';
</script>

<style scoped>
.community-view-wrapper {
  background-color: #f4f6f8; /* 与主内容区背景色一致 */
  min-height: 100%; /* 确保占据父容器（如 Home.vue 的 el-main）的全部高度 */
  padding: 15px; /* 提供统一的内边距，使内容不贴边 */
  box-sizing: border-box; /* 内边距包含在宽度内 */
  display: flex; /* 确保 CommunityForm 能够良好布局 */
  flex-direction: column;
}
</style>