<!-- Course.vue -->
<template>
  <div class="course-wrapper">
    <CourseForm />
  </div>
</template>

<script setup lang="ts">
import CourseForm from '../components/CourseForm.vue';
</script>

<style scoped>
.course-wrapper {
  background-color: #f0f2f5;
  min-height: 100vh;
  padding: 0;
}
</style>
