<template>
  <el-card shadow="hover">
    <h2>在线练习</h2>
    <el-form :model="form" label-width="120px" @submit.prevent>
      <el-form-item label="练习名称">
        <el-input v-model="form.title" placeholder="请输入练习名称" />
      </el-form-item>
      <el-form-item label="题目数量">
        <el-input-number v-model="form.questionCount" :min="1" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">开始练习</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { ElMessage } from 'element-plus';

const form = reactive({
  title: '',
  questionCount: 10,
});

function submitForm() {
  console.log('提交练习信息:', form);
  ElMessage.success('练习开始！');
}

function resetForm() {
  form.title = '';
  form.questionCount = 10;
}
</script>
