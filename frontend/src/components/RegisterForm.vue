<template>
  <div>
    <h2>注册</h2>
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
        <label for="phoneNum">手机号：</label>
        <input v-model="form.phoneNum" type="text" />
      </div>
      <div>
        <label for="userType">用户类型：</label>
        <select v-model="form.userType">
          <option value="1">学生</option>
          <option value="2">教师</option>
        </select>
      </div>
      <button type="submit">注册</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import ex from '../api/auth';

const form = reactive({
  userId: '',
  password: '',
  phoneNum: '',
  userType: 1 as number
});

const handleSubmit = async () => {
  await ex.register(form.userId, form.password, form.phoneNum, form.userType);
};
</script>