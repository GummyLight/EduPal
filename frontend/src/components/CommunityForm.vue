<template>
  <el-card shadow="hover">
    <h2>社区交流</h2>
    <el-input
        v-model="newPost"
        placeholder="说点什么吧..."
        type="textarea"
        rows="3"
    />
    <el-button type="primary" @click="postMessage" style="margin-top: 10px;">
      发布
    </el-button>

    <div class="posts" style="margin-top: 20px;">
      <el-card
          v-for="(post, index) in posts"
          :key="index"
          shadow="always"
          style="margin-bottom: 10px;"
      >
        <p>{{ post }}</p>
      </el-card>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

const newPost = ref('');
const posts = ref<string[]>([]);

function postMessage() {
  if (!newPost.value.trim()) {
    ElMessage.warning('请输入内容');
    return;
  }
  posts.value.unshift(newPost.value);
  newPost.value = '';
  ElMessage.success('发布成功！');
}
</script>
