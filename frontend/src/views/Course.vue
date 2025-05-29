<!-- Course.vue -->
<template>
  <div class="course-wrapper">
    <CourseForm
        :usertype="props.usertype" :username="props.username"
        :userid="props.userid"
    />
  </div>
</template>

<script setup lang="ts">
import CourseForm from '../components/CourseForm.vue';
import CommunityForm from "@/components/CommunityForm.vue";

import { defineProps } from 'vue'; // 确保导入 defineProps


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



// 您可以在这里再次添加 console.log 来验证 Community.vue 是否收到了 props
// 这次应该会显示正确的值
console.log('Course.vue received usertype (from props):', props.usertype);
console.log('Course.vue received username (from props):', props.username);
console.log('Course.vue received userid (from props):', props.userid);

</script>

<style scoped>
.course-wrapper {
  background-color: #f0f2f5;
  min-height: 100vh;
  padding: 0;
}
</style>
