<template>
  <div class="home-page">
    <Sidebar />
    <div class="main-content">
      <HomeForm />
    </div>
  </div>
</template>

<script setup lang="ts">
import Sidebar from '../components/Siderbar.vue';
import HomeForm from '../components/HomeForm.vue';
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
