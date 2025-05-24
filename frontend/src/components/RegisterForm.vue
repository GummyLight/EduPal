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
          <label for="phoneNum">手机号：</label>
          <input 
            v-model="form.phoneNum" 
            type="tel" 
            id="phoneNum"
            class="form-input"
            :class="{ 'error': phoneError }"
            placeholder="请输入手机号"
            @input="handlePhoneInput"
            @blur="validatePhone"
            required 
          />
          <span v-if="phoneError" class="error-message">{{ phoneError }}</span>
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
  phoneNum: '',
  userType: 1 as number
});

const loading = ref(false);

// 错误信息状态
const userIdError = ref('');
const passwordError = ref('');
const confirmPasswordError = ref('');
const phoneError = ref('');

// 表单验证状态
const isFormValid = computed(() => {
  return form.userId && 
         form.password && 
         form.confirmPassword &&
         form.phoneNum &&
         !userIdError.value &&
         !passwordError.value &&
         !confirmPasswordError.value &&
         !phoneError.value;
});

// 用户名验证规则：只允许英文字母和数字
const isValidUsername = (username: string): boolean => {
  const usernameRegex = /^[a-zA-Z0-9]+$/;
  return usernameRegex.test(username);
};

// 手机号验证规则：只允许数字，11位
const isValidPhone = (phone: string): boolean => {
  const phoneRegex = /^[0-9]{11}$/;
  return phoneRegex.test(phone);
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

// 处理手机号输入
const handlePhoneInput = (event: Event) => {
  const target = event.target as HTMLInputElement;
  let value = target.value;
  
  // 只保留数字，最多11位
  value = value.replace(/[^0-9]/g, '').slice(0, 11);
  
  form.phoneNum = value;
  target.value = value;
  
  // 清除之前的错误信息
  phoneError.value = '';
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

// 验证手机号
const validatePhone = () => {
  if (!form.phoneNum) {
    phoneError.value = '';
    return;
  }
  
  if (!isValidPhone(form.phoneNum)) {
    phoneError.value = '请输入正确的11位手机号';
  } else {
    phoneError.value = '';
  }
};

const handleSubmit = async () => {
  // 提交前再次验证所有字段
  validateUserId();
  validatePassword();
  validateConfirmPassword();
  validatePhone();
  
  // 检查是否有任何错误
  if (userIdError.value || passwordError.value || confirmPasswordError.value || phoneError.value) {
    ElMessage.error('请检查输入格式');
    return;
  }

  loading.value = true;
  try {
    const response = await ex.register(form.userId, form.password, form.phoneNum, form.userType);
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
}

.register-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  max-width: 450px;
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

.form-input.error {
  border-color: #ef4444;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

.form-input::placeholder {
  color: #9ca3af;
}

.error-message {
  color: #ef4444;
  font-size: 12px;
  margin-top: 4px;
}

.button-group {
  display: flex;
  gap: 15px;
}

.register-button,
.back-button {
  flex: 1;
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
}

.register-button:active {
  transform: translateY(0);
}

.register-button:disabled {
  background: linear-gradient(135deg, #9ca3af 0%, #6b7280 100%);
  cursor: not-allowed;
  transform: none;
}

.back-button {
  background: linear-gradient(135deg, #1f4ce1 0%, #7942d1 100%);
}

.back-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(108, 117, 125, 0.3);
}

.back-button:active {
  transform: translateY(0);
}

.error {
  border-color: #f44336;
}

.error-message {
  color: #f44336;
  font-size: 12px;
  margin-top: -5px;
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
    flex-direction: column;
    gap: 10px;
  }
}
</style>