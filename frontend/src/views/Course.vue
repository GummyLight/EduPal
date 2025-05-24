<!-- frontend/src/views/Course.vue -->
<template>
  <div class="course-page">
    <h1>教学内容管理</h1>
    <course-form />
  </div>
</template>

<script setup lang="ts">
import CourseForm from '@/components/CourseForm.vue';
</script>

<style scoped>
.course-page {
  padding: 24px;
}
</style>
