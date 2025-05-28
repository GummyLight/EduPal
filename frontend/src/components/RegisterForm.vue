<template>
  <div class="register-form-container">
    <div class="register-card">
      <h2 class="register-title">注册</h2>
      <form @submit.prevent="handleSubmit" class="register-form">
        <div class="form-group">
          <label for="userType">用户类型：</label>
          <select v-model="form.userType" id="userType" class="form-select">
            <option :value="1">学生</option>
            <option :value="2">教师</option>
          </select>
        </div>
        <div class="form-group">
          <label for="userId">用户名：</label>
          <input 
            v-model="form.userId" 
            type="text" 
            id="userId"
            class="form-input"
            :class="{ 'error': userIdError }"
            placeholder="请输入用户名"
            @input="handleUserIdInput"
            @blur="validateUserId"
            required 
          />
          <span v-if="userIdError" class="error-message">{{ userIdError }}</span>
        </div>
        <div class="form-group">
          <label for="password">密码：</label>
          <input 
            v-model="form.password" 
            type="password" 
            id="password"
            class="form-input"
            :class="{ 'error': passwordError }"
            placeholder="请输入密码"
            @blur="validatePassword"
            required 
          />
          <span v-if="passwordError" class="error-message">{{ passwordError }}</span>
        </div>
        <div class="form-group">
          <label for="confirmPassword">确认密码：</label>
          <input 
            v-model="form.confirmPassword" 
            type="password" 
            id="confirmPassword"
            class="form-input"
            :class="{ 'error': confirmPasswordError }"
            placeholder="请再次输入密码"
            @blur="validateConfirmPassword"
            required 
          />
          <span v-if="confirmPasswordError" class="error-message">{{ confirmPasswordError }}</span>
        </div>
        <div class="form-group">
          <label for="email">邮箱：</label>
          <input 
            v-model="form.email" 
            type="email" 
            id="email"
            class="form-input"
            :class="{ 'error': emailError }"
            placeholder="请输入邮箱地址"
            @input="handleEmailInput"
            @blur="validateEmail"
            required 
          />
          <span v-if="emailError" class="error-message">{{ emailError }}</span>
        </div>
        <div class="button-group">
          <button type="submit" class="register-button" :disabled="loading || !isFormValid">
            {{ loading ? '注册中...' : '注册' }}
          </button>
          <button type="button" @click="handleBack" class="back-button">返回</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import ex from '../api/auth';

const form = reactive({
  userId: '',
  password: '',
  confirmPassword: '',
  email: '',
  userType: 1 as number
});

const loading = ref(false);

// 错误信息状态
const userIdError = ref('');
const passwordError = ref('');
const confirmPasswordError = ref('');
const emailError = ref('');

// 表单验证状态
const isFormValid = computed(() => {
  return form.userId && 
         form.password && 
         form.confirmPassword &&
         form.email &&
         !userIdError.value &&
         !passwordError.value &&
         !confirmPasswordError.value &&
         !emailError.value;
});

// 用户名验证规则：只允许英文字母和数字
const isValidUsername = (username: string): boolean => {
  const usernameRegex = /^[a-zA-Z0-9]+$/;
  return usernameRegex.test(username);
};

// 邮箱验证规则
const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

// 处理用户名输入
const handleUserIdInput = (event: Event) => {
  const target = event.target as HTMLInputElement;
  let value = target.value;
  
  // 只保留英文字母和数字
  value = value.replace(/[^a-zA-Z0-9]/g, '');
  
  form.userId = value;
  target.value = value;
  
  // 清除之前的错误信息
  userIdError.value = '';
};

// 处理邮箱输入
const handleEmailInput = (event: Event) => {
  const target = event.target as HTMLInputElement;
  form.email = target.value;
  
  // 清除之前的错误信息
  emailError.value = '';
};

// 验证用户名
const validateUserId = () => {
  if (!form.userId) {
    userIdError.value = '';
    return;
  }
  
  if (!isValidUsername(form.userId)) {
    userIdError.value = '用户名只能包含英文字母和数字';
  } else if (form.userId.length < 3) {
    userIdError.value = '用户名至少3个字符';
  } else if (form.userId.length > 20) {
    userIdError.value = '用户名不能超过20个字符';
  } else {
    userIdError.value = '';
  }
};

// 验证密码
const validatePassword = () => {
  if (!form.password) {
    passwordError.value = '';
    return;
  }
  
  if (form.password.length < 6) {
    passwordError.value = '密码长度不能少于6位';
  } else {
    passwordError.value = '';
  }
  
  // 如果确认密码已填写，重新验证确认密码
  if (form.confirmPassword) {
    validateConfirmPassword();
  }
};

// 验证确认密码
const validateConfirmPassword = () => {
  if (!form.confirmPassword) {
    confirmPasswordError.value = '';
    return;
  }
  
  if (form.password !== form.confirmPassword) {
    confirmPasswordError.value = '两次输入的密码不匹配';
  } else {
    confirmPasswordError.value = '';
  }
};

// 验证邮箱
const validateEmail = () => {
  if (!form.email) {
    emailError.value = '';
    return;
  }
  
  if (!isValidEmail(form.email)) {
    emailError.value = '请输入正确的邮箱格式';
  } else {
    emailError.value = '';
  }
};

const handleSubmit = async () => {
  // 提交前再次验证所有字段
  validateUserId();
  validatePassword();
  validateConfirmPassword();
  validateEmail();
  
  // 检查是否有任何错误
  if (userIdError.value || passwordError.value || confirmPasswordError.value || emailError.value) {
    ElMessage.error('请检查输入格式');
    return;
  }

  loading.value = true;
  try {
    const response = await ex.register(form.userId, form.password, form.email, form.userType);
    if (response.data.success) {
      ElMessage.success(response.data.message);
      setTimeout(() => {
        window.location.href = '/login';
      }, 2000);
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
  } finally {
    loading.value = false;
  }
};

const handleBack = () => {
  window.location.href = '/login';
};
</script>

<style scoped>
.register-form-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
}

.register-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  border-radius: 24px;
  padding: 48px 40px;
  box-shadow: 
    0 25px 50px rgba(0, 0, 0, 0.12),
    0 12px 24px rgba(0, 0, 0, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.3);
  max-width: 480px;
  width: 100%;
  margin: 20px;
  transition: all 0.3s ease;
}

.register-card:hover {
  transform: translateY(-2px);
  box-shadow: 
    0 30px 60px rgba(0, 0, 0, 0.15),
    0 15px 30px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.register-title {
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 35px;
  font-size: 32px;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  position: relative;
}

.form-group label {
  font-weight: 600;
  color: #374151;
  font-size: 15px;
  margin-left: 4px;
  transition: color 0.3s ease;
}

.form-input,
.form-select {
  padding: 16px 20px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.9);
  color: #374151;
  font-weight: 500;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 
    0 0 0 4px rgba(102, 126, 234, 0.1),
    0 4px 12px rgba(102, 126, 234, 0.15);
  transform: translateY(-1px);
}

.form-input.error {
  border-color: #ef4444;
  box-shadow: 
    0 0 0 4px rgba(239, 68, 68, 0.1),
    0 4px 12px rgba(239, 68, 68, 0.15);
}

.form-input::placeholder {
  color: #9ca3af;
  font-weight: 400;
}

.form-select {
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%236b7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='m6 8 4 4 4-4'/%3e%3c/svg%3e");
  background-position: right 12px center;
  background-repeat: no-repeat;
  background-size: 16px;
  padding-right: 40px;
}

.error-message {
  color: #ef4444;
  font-size: 13px;
  font-weight: 500;
  margin-top: 6px;
  margin-left: 4px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.error-message::before {
  content: '⚠';
  font-size: 12px;
}

.button-group {
  display: flex;
  gap: 16px;
  margin-top: 12px;
}

.register-button,
.back-button {
  flex: 1;
  color: white;
  border: none;
  padding: 16px 24px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.register-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.register-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.register-button:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.register-button:hover:not(:disabled)::before {
  left: 100%;
}

.register-button:active:not(:disabled) {
  transform: translateY(-1px);
}

.register-button:disabled {
  background: linear-gradient(135deg, #9ca3af 0%, #6b7280 100%);
  cursor: not-allowed;
  transform: none;
  box-shadow: 0 2px 8px rgba(156, 163, 175, 0.2);
}

.back-button {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  box-shadow: 0 4px 15px rgba(99, 102, 241, 0.3);
}

.back-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.back-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(99, 102, 241, 0.4);
}

.back-button:hover::before {
  left: 100%;
}

.back-button:active {
  transform: translateY(-1px);
}

.error {
  border-color: #ef4444;
}

@media (max-width: 480px) {
  .register-card {
    padding: 32px 24px;
    margin: 10px;
    border-radius: 20px;
  }
  
  .register-title {
    font-size: 28px;
    margin-bottom: 28px;
  }

  .button-group {
    flex-direction: column;
    gap: 12px;
  }
  
  .form-input,
  .form-select {
    padding: 14px 16px;
    font-size: 16px;
  }
}
</style>