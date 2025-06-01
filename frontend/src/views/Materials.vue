<template>
  <div class="materials-view">
    <router-view
        :usertype="props.usertype" :username="props.username"
        :userid="props.userid"
    />
  </div>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'; // 确保导入 defineProps

// Community.vue 需要定义 props 来接收 Home.vue 传递过来的值
const props = defineProps({
  usertype: {
    type: Number,
    required: true,
  },
  username: {
    type: String,
    required: true,
  },
  userid: {
    type: String,
    required: true,
  }
});

// 您可以在这里再次添加 console.log 来验证 .vue 是否收到了 props
// 这次应该会显示正确的值
console.log('Materials.vue received usertype (from props):', props.usertype);
console.log('Materials.vue received username (from props):', props.username);
console.log('Materials.vue received userid (from props):', props.userid);
</script>

<style scoped>
.materials-view {
  padding: 0;
  height: 100%; /* 确保这个容器能撑开高度 */
  display: flex; /* 让内部 router-view 渲染的内容能更好地布局 */
  flex-direction: column; /* 如果子组件是 column 布局的话 */
}
</style>