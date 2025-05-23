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
            type="tel" 
            id="phoneNum"
            class="form-input"
            placeholder="请输入手机号"
          />
        </div>
        <div class="form-group">
          <label for="userType">用户类型：</label>
          <select v-model="form.userType" id="userType" class="form-select">
            <option :value="1">学生</option>
            <option :value="2">教师</option>
          </select>
        </div>
        <div class="button-group">
          <button type="submit" class="register-button">注册</button>
          <button type="button" @click="handleBack" class="back-button">返回</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { ElMessage } from 'element-plus';
import ex from '../api/auth';

const form = reactive({
  userId: '',
  password: '',
  phoneNum: '',
  userType: 1 as number
});

const handleSubmit = async () => {
  try {
    const response = await ex.register(form.userId, form.password, form.phoneNum, form.userType);
    if (response.data.success) {
      ElMessage.success(response.data.message);
      window.location.href = '/login'; // 注册成功后跳转到登录页面
    } else {
      ElMessage.error(response.data.message);
    }
  } catch (error: any) {
    console.error('Register failed:', error);
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.message || '注册失败，请检查网络连接或联系管理员');
    } else {
      ElMessage.error('注册失败，请检查网络连接或联系管理员');
    }
  }
};

const handleBack = () => {
  window.history.back(); // 返回到上一个页面
};
</script>

<style scoped>
.register-form-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.register-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  max-width: 400px;
  width: 100%;
  margin: 20px;
}

.register-title {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 28px;
  font-weight: 600;
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

.form-input::placeholder {
  color: #9ca3af;
}

.button-group {
  display: flex;
  gap: 15px; /* 按钮之间的间距 */
}

.register-button,
.back-button {
  flex: 1; /* 让按钮平分父容器宽度 */
  color: white;
  border: none;
  padding: 14px 20px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
}

.register-button {
  background: linear-gradient(135deg, #1f4ce1 0%, #7942d1 100%);
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(108, 117, 125, 0.3);
}

.register-button:active {
  transform: translateY(0);
}

.back-button {
  background: linear-gradient(135deg, #ff7e5f 0%, #feb47b 100%);
}

.back-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(255, 126, 95, 0.3);
}

.back-button:active {
  transform: translateY(0);
}

@media (max-width: 480px) {
  .register-card {
    padding: 30px 20px;
    margin: 10px;
  }
  
  .register-title {
    font-size: 24px;
  }

  .button-group {
    flex-direction: column; /* 在小屏幕上，可以将按钮堆叠起来 */
    gap: 10px;
  }
}
</style>