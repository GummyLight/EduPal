<template>
  <div class="login-form-container">
    <div class="login-card">
      <h2 class="login-title">登录</h2>
      <form @submit.prevent="handleSubmit" class="login-form">
        <div class="form-group">
          <label for="type">登录方式：</label>
          <select v-model="form.type" id="type" class="form-select">
            <option :value="0">用户名</option>
            <option :value="1">手机号</option>
          </select>
        </div>
        <div class="form-group">
          <label for="userId">{{ userIdLabel }}</label>
          <input 
            v-model="form.userId" 
            :type="userIdInputType"
            id="userId"
            class="form-input"
            :placeholder="userIdPlaceholder"
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
        <div class="button-group">
          <button type="submit" class="login-button">登录</button>
          <button type="button" @click="handleRegister" class="register-button">注册</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, computed } from 'vue';
import { ElMessage } from 'element-plus';
import ex from '../api/auth';

const form = reactive({
  userId: '',
  password: '',
  type: 0 as number
});

const userIdLabel = computed(() => {
  return form.type === 1 ? '手机号：' : '用户名：';
});

const userIdPlaceholder = computed(() => {
  return form.type === 1 ? '请输入手机号' : '请输入用户名';
});

const userIdInputType = computed(() => {
  return form.type === 1 ? 'tel' : 'text';
});

const handleSubmit = async () => {
  try {
    const response = await ex.login(form.userId, form.password, form.type);
    if (response.data.success) {
      ElMessage.success(response.data.message);
      window.location.href = '/';
    } else {
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

const handleRegister = () => {
  window.location.href = '/register'; // 跳转到注册页面
};
</script>

<style scoped>
.login-form-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-card {
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

.login-title {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 28px;
  font-weight: 600;
}

.login-form {
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

.login-button,
.register-button {
  flex: 1; /* 让两个按钮平分父容器宽度，实现对称 */
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

.login-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
}

.login-button:active {
  transform: translateY(0);
}

.register-button {
  background: linear-gradient(135deg, #1f4ce1 0%, #7942d1 100%); /* 注册按钮使用不同的颜色 */
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(108, 117, 125, 0.3);
}

.register-button:active {
  transform: translateY(0);
}

@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
    margin: 10px;
  }
  
  .login-title {
    font-size: 24px;
  }

  .button-group {
    flex-direction: column; /* 在小屏幕上，可以将按钮堆叠起来 */
    gap: 10px;
  }

  .login-button,
  .register-button {
    /* 在堆叠时，flex:1 不再是为了平分宽度，可以保持或移除 */
    /* 如果希望它们仍然是全宽，flex:1 配合父容器的列方向仍然有效 */
  }
}
</style>