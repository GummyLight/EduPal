<template>
  <div class="practice-page">
    <router-view
        :usertype="props.usertype"
        :username="props.username"
        :userid="props.userid"
    >
    </router-view>
  </div>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'; // 确保导入 defineProps

// Practice.vue 需要定义 props 来接收 Home.vue 传递过来的值
const props = defineProps({
  usertype: {
    type: String as () => 'teacher' | 'student',
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

// 您可以在这里添加 console.log 来验证 Practice.vue 是否收到了 props
console.log('Practice.vue received usertype (from props):', props.usertype);
console.log('Practice.vue received username (from props):', props.username);
console.log('Practice.vue received userid (from props):', props.userid);

</script>

<style scoped>
.practice-page {
  height: 100%;
}
</style>