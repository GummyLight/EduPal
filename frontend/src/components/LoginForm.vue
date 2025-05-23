<template>
  <div>
    <h2>登录</h2>
    <form @submit.prevent="handleSubmit">
      <div>
        <label for="userId">用户名：</label>
        <input v-model="form.userId" type="text" required />
      </div>
      <div>
        <label for="password">密码：</label>
        <input v-model="form.password" type="password" required />
      </div>
      <div>
        <label for="type">登录方式：</label>
        <select v-model="form.type">
          <option value="0">用户名</option>
          <option value="1">手机号</option>
        </select>
      </div>
      <button type="submit">登录</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { ElMessage } from 'element-plus';
import  ex  from '../api/auth';

const form = reactive({
  userId: '',
  password: '',
  type: 0 as number
});

const handleSubmit = async () => {
  try {
    const response = await ex.login(form.userId, form.password, form.type);
    if (response.data.success) {
      // 登录成功后的操作，例如跳转到主页
      ElMessage.success(response.data.message);
      window.location.href = '/';
    } else {
      // 登录失败后的操作，例如显示错误消息
      ElMessage.error(response.data.message);
    }
  } catch (error: any) {
    console.error('Login failed:', error);
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.message || '登录失败，请检查网络连接或联系管理员');
    } else {
      ElMessage.error('登录失败，请检查网络连接或联系管理员');
    }
  }
};
</script>