<template>
  <el-card shadow="hover">
    <h2>智能答疑</h2>
    <el-input
        type="textarea"
        v-model="question"
        placeholder="请输入你的问题"
        rows="4"
    />
    <el-button type="primary" @click="askQuestion" style="margin-top: 10px;">
      提问
    </el-button>
    <div v-if="answer" class="answer-box">
      <h3>回答：</h3>
      <p>{{ answer }}</p>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

const question = ref('');
const answer = ref('');

function askQuestion() {
  if (!question.value.trim()) {
    ElMessage.warning('请输入问题');
    return;
  }
  // 模拟智能答疑返回结果
  answer.value = `这是针对“${question.value}”的智能回答（示例）。`;
  question.value = '';
}
</script>

<style scoped>
.answer-box {
  margin-top: 20px;
  padding: 10px;
  background: #f0f9ff;
  border: 1px solid #91d5ff;
  border-radius: 4px;
}
</style>
