<template>
  <div class="forgot-password-form-container">
    <div class="forgot-password-card">
      <h2 class="forgot-password-title">忘记密码</h2>
      <form @submit.prevent="handleResetPassword" class="forgot-password-form">
        <!-- 邮箱输入 -->
        <div class="form-group">
          <label for="email">邮箱地址：</label>
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

        <!-- 邮箱验证码 -->
        <div class="form-group">
          <label for="emailCode">邮箱验证码：</label>
          <div class="email-code-container">
            <input 
              v-model="form.emailCode" 
              type="text" 
              id="emailCode"
              class="form-input"
              :class="{ 'error': emailCodeError }"
              placeholder="请输入邮箱验证码"
              maxlength="6"
              @blur="validateEmailCode"
              required 
            />
            <button 
              type="button" 
              @click="handleSendCode" 
              class="send-code-button"
              :disabled="!form.email || !!emailError || countdown > 0"
            >
              {{ countdown > 0 ? `${countdown}s后重发` : (form.emailCode ? '重新发送' : '发送验证码') }}
            </button>
          </div>
          <span v-if="emailCodeError" class="error-message">{{ emailCodeError }}</span>
        </div>

        <!-- 新密码 -->
        <div class="form-group">
          <label for="newPassword">新密码：</label>
          <input 
            v-model="form.newPassword" 
            type="password" 
            id="newPassword"
            class="form-input"
            :class="{ 'error': passwordError }"
            placeholder="请输入新密码"
            @blur="validatePassword"
            required 
          />
          <span v-if="passwordError" class="error-message">{{ passwordError }}</span>
        </div>

        <!-- 确认密码 -->
        <div class="form-group">
          <label for="confirmPassword">确认密码：</label>
          <input 
            v-model="form.confirmPassword" 
            type="password" 
            id="confirmPassword"
            class="form-input"
            :class="{ 'error': confirmPasswordError }"
            placeholder="请再次输入新密码"
            @blur="validateConfirmPassword"
            required 
          />
          <span v-if="confirmPasswordError" class="error-message">{{ confirmPasswordError }}</span>
        </div>

        <!-- 按钮组 -->
        <div class="button-group">
          <button type="submit" class="reset-button" :disabled="!isFormValid">重置密码</button>
          <button type="button" @click="handleBackToLogin" class="back-button">返回登录</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, computed, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { sendEmailCode, forgetPassword } from '../api/auth';

// 表单数据
const form = reactive({
  email: '',
  emailCode: '',
  newPassword: '',
  confirmPassword: ''
});

// 错误信息
const emailError = ref('');
const passwordError = ref('');
const confirmPasswordError = ref('');
const emailCodeError = ref('');

// 倒计时
const countdown = ref(0);
let countdownTimer: NodeJS.Timeout | null = null;

// 表单验证
const isFormValid = computed(() => {
  return form.email && 
         form.emailCode && 
         form.newPassword && 
         form.confirmPassword &&
         !emailError.value &&
         !passwordError.value &&
         !confirmPasswordError.value &&
         !emailCodeError.value;
});

// 验证规则
const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

const isValidPassword = (password: string): boolean => {
  return password.length >= 6 && password.length <= 20;
};

const isValidEmailCode = (code: string): boolean => {
  // 验证码通常是4-6位数字或字母
  const codeRegex = /^[A-Za-z0-9]{4,6}$/;
  return codeRegex.test(code);
};

// 处理邮箱输入
const handleEmailInput = (event: Event) => {
  const target = event.target as HTMLInputElement;
  form.email = target.value;
  emailError.value = '';
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

// 验证密码
const validatePassword = () => {
  if (!form.newPassword) {
    passwordError.value = '';
    return;
  }
  
  if (!isValidPassword(form.newPassword)) {
    passwordError.value = '密码长度应为6-20个字符';
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
  
  if (form.newPassword !== form.confirmPassword) {
    confirmPasswordError.value = '两次输入的密码不一致';
  } else {
    confirmPasswordError.value = '';
  }
};

// 验证邮箱验证码
const validateEmailCode = () => {
  if (!form.emailCode) {
    emailCodeError.value = '';
    return;
  }
  
  if (!isValidEmailCode(form.emailCode)) {
    emailCodeError.value = '验证码格式不正确，应为4-6位数字或字母';
  } else {
    emailCodeError.value = '';
  }
};

// 发送验证码
const handleSendCode = async () => {
  validateEmail();
  
  if (emailError.value) {
    ElMessage.error('请检查邮箱格式');
    return;
  }
  
  try {
    ElMessage.info('正在发送验证码...');
    const response = await sendEmailCode(form.email);
    
    if (response.code === 200) {
      ElMessage.success(response.message || '验证码已发送到您的邮箱，请查收');
      startCountdown();
    } else {
      ElMessage.error(response.message || '发送验证码失败，请重试');
    }
    
  } catch (error: any) {
    console.error('Send code failed:', error);
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.message || '发送验证码失败，请检查网络连接');
    } else {
      ElMessage.error('发送验证码失败，请检查网络连接或联系管理员');
    }
  }
};

// 重置密码
const handleResetPassword = async () => {
  // 验证所有字段
  validateEmail();
  validateEmailCode();
  validatePassword();
  validateConfirmPassword();
  
  if (emailError.value || emailCodeError.value || passwordError.value || confirmPasswordError.value) {
    ElMessage.error('请检查表单输入');
    return;
  }
  
  if (!form.emailCode.trim()) {
    ElMessage.error('请输入邮箱验证码');
    return;
  }
  
  try {
    ElMessage.info('正在重置密码...');
    const response = await forgetPassword(form.email, form.emailCode, form.newPassword);
    
    if (response.code === 200) {
      ElMessage.success(response.message || '密码重置成功，请使用新密码登录');
      // 延迟跳转，让用户看到成功消息
      setTimeout(() => {
        window.location.href = '/login';
      }, 1500);
    } else {
      ElMessage.error(response.message || '密码重置失败，请检查验证码是否正确');
    }
    
  } catch (error: any) {
    console.error('Reset password failed:', error);
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.message || '密码重置失败，请检查网络连接');
    } else {
      ElMessage.error('密码重置失败，请检查网络连接或联系管理员');
    }
  }
};

// 重新发送验证码（重用发送验证码逻辑）
const handleResendCode = async () => {
  await handleSendCode();
};

// 开始倒计时
const startCountdown = () => {
  countdown.value = 60;
  countdownTimer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      if (countdownTimer) {
        clearInterval(countdownTimer);
        countdownTimer = null;
      }
    }
  }, 1000);
};

// 返回登录
const handleBackToLogin = () => {
  window.location.href = '/login';
};
</script>

<style scoped>
.forgot-password-form-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.forgot-password-card {
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

.forgot-password-title {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 28px;
  font-weight: 600;
}

.forgot-password-form {
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

.verify-code-container,
.email-code-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.resend-button {
  padding: 8px 16px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.resend-button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.resend-button:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.button-group {
  display: flex;
  gap: 15px;
}

.send-code-button,
.reset-button,
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

.send-code-button,
.reset-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.send-code-button:hover:not(:disabled),
.reset-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
}

.send-code-button:active:not(:disabled),
.reset-button:active:not(:disabled) {
  transform: translateY(0);
}

.send-code-button:disabled,
.reset-button:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.back-button {
  background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%);
}

.back-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(107, 114, 128, 0.3);
}

.back-button:active {
  transform: translateY(0);
}

@media (max-width: 480px) {
  .forgot-password-card {
    padding: 30px 20px;
    margin: 10px;
  }
  
  .forgot-password-title {
    font-size: 24px;
  }

  .button-group {
    flex-direction: column;
    gap: 10px;
  }

  .email-code-container {
    flex-direction: column;
    align-items: stretch;
  }

  .resend-button {
    align-self: center;
    width: fit-content;
  }
}
</style>
