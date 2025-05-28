<template>
  <div class="materials-view">
    <router-view />
  </div>
</template>

<script setup lang="ts">
// 这里不再直接导入 MaterialsForm，因为它会由 <router-view /> 渲染
// import MaterialsForm from '../components/MaterialsForm.vue';
</script>

<style scoped>
.materials-view {
  padding: 0;
  height: 100%; /* 确保这个容器能撑开高度 */
  display: flex; /* 让内部 router-view 渲染的内容能更好地布局 */
  flex-direction: column; /* 如果子组件是 column 布局的话 */
}
</style>