<template>
  <div class="community-view-wrapper">
    <CommunityForm
        :usertype="props.usertype" :username="props.username"
        :userid="props.userid"
    />
  </div>
</template>

<script setup lang="ts">
import CommunityForm from '../components/CommunityForm.vue';
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

// 您可以在这里再次添加 console.log 来验证 Community.vue 是否收到了 props
// 这次应该会显示正确的值
console.log('Community.vue received usertype (from props):', props.usertype);
console.log('Community.vue received username (from props):', props.username);
console.log('Community.vue received userid (from props):', props.userid);

</script>

<style scoped>
.community-view-wrapper {
  background-color: #f4f6f8;
  min-height: 100%;
  padding: 15px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}
</style>