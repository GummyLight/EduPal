<template>
  <div class="register-form-container">
    <div class="register-card">
      <h2 class="register-title">注册</h2>
      <form @submit.prevent="handleSubmit" class="register-form">
        <div class="form-group">
          <label for="userId">用户名：</label>
          <input
              v-model="form.userId"
              type="text"
              id="userId"
              class="form-input"
              placeholder="请输入用户名"
              required
          />
        </div>
        <div class="form-group">
          <label for="password">密码：</label>
          <input
              v-model="form.password"
              type="password"
              id="password"
              class="form-input"
              placeholder="请输入密码"
              required
          />
        </div>
        <div class="form-group">
          <label for="phoneNum">手机号：</label>
          <input
              v-model="form.phoneNum"
              type="text"
              id="phoneNum"
              class="form-input"
              placeholder="请输入手机号"
          />
        </div>
        <div class="form-group">
          <label for="userType">用户类型：</label>
          <select
              v-model="form.userType"
              id="userType"
              class="form-select"
          >
            <option value="1">学生</option>
            <option value="2">教师</option>
          </select>
        </div>
        <div class="button-group">
          <button type="submit" class="register-button">注册</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import ex from '../api/auth';

const router = useRouter();

const form = reactive({
  userId: '',
  password: '',
  phoneNum: '',
  userType: 1 as number,
});

const handleSubmit = async () => {
  try {
    const response = await ex.register(form.userId, form.password, form.phoneNum, form.userType);
    if (response.data.success) {
      ElMessage.success('注册成功！请登录');
      router.push('/login');
    } else {
      ElMessage.error(response.data.message || '注册失败');
    }
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message || '注册失败，请稍后再试');
  }
};
</script>

<style scoped>
.register-form-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;
}

.register-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 100%;
}

.register-title {
  text-align: center;
  color: #333;
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 30px;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 500;
  color: #555;
  font-size: 14px;
}

.form-input,
.form-select {
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 10px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.9);
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #4f46e5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
}

.button-group {
  display: flex;
  justify-content: center;
}

.register-button {
  background: linear-gradient(135deg, #1f4ce1 0%, #7942d1 100%);
  color: white;
  border: none;
  padding: 14px 24px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(108, 117, 125, 0.3);
}

.register-button:active {
  transform: translateY(0);
}

@media (max-width: 480px) {
  .register-card {
    padding: 30px 20px;
  }

  .register-title {
    font-size: 24px;
  }

  .register-button {
    width: 100%;
  }
}
</style>
