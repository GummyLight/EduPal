<!-- frontend/src/views/Course.vue -->
<template>
  <div class="course-page">
    <h1>教学内容管理</h1>
    <CourseForm />  <!-- ✅ 首字母大写，PascalCase -->
  </div>
</template>

<script setup lang="ts">
import CourseForm from '@/components/CourseForm.vue';
</script>
