<!-- frontend/src/components/CourseForm.vue -->
<template>
  <el-form :model="form" label-width="100px" @submit.prevent>
    <el-form-item label="课程名称">
      <el-input v-model="form.title" placeholder="请输入课程名称" />
    </el-form-item>
    <el-form-item label="讲师">
      <el-input v-model="form.teacher" placeholder="请输入讲师名称" />
    </el-form-item>
    <el-form-item label="描述">
      <el-input
          type="textarea"
          v-model="form.description"
          placeholder="请输入课程描述"
          rows="4"
      />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submitForm">提交</el-button>
      <el-button @click="resetForm">重置</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { ElMessage } from 'element-plus';

const form = reactive({
  title: '',
  teacher: '',
  description: ''
});

function submitForm() {
  console.log('提交数据：', form);
  ElMessage.success('课程信息提交成功！');
}

function resetForm() {
  form.title = '';
  form.teacher = '';
  form.description = '';
}
</script>
