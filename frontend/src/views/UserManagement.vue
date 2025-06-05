<template>
  <div class="user-management-page">
    <UserManagementForm 
      :usertype="usertype"
      :username="username" 
      :userid="userid"
    />
  </div>
</template>

<script setup lang="ts">
import UserManagementForm from '@/components/UserManagementForm.vue';

// 接收从父组件传递的props
interface Props {
  usertype: 0 | 1 | 2 | -1;
  username: string;
  userid: string;
}

const props = defineProps<Props>();

// 解构props供模板使用
const { usertype, username, userid } = props;
</script>

<style scoped>
.user-management-page {
  height: 100vh;
  overflow: hidden;
  background-color: #f5f7fa;
}
</style>