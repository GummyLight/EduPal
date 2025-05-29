<template>
  <div class="login-form-container">
    <div class="login-card">
      <h2 class="login-title">登录</h2>
      <form @submit.prevent="handleSubmit" class="login-form">
        <div class="form-group">
          <label for="type">登录方式：</label>
          <select v-model="form.type" id="type" class="form-select">
            <option :value="0">账号</option>
            <option :value="1">邮箱</option>
          </select>
        </div>
        <div class="form-group">
          <label for="userId">{{ userIdLabel }}</label>
          <input 
            v-model="form.userId" 
            :type="userIdInputType"
            id="userId"
            class="form-input"
            :class="{ 'error': userIdError }"
            :placeholder="userIdPlaceholder"
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
            placeholder="请输入密码"
            required 
          />
        </div>
        <div class="form-group">
          <label for="verifyCode">验证码：</label>
          <div class="verify-code-container">
            <input 
              v-model="form.verifyCode" 
              type="text" 
              id="verifyCode"
              class="form-input"
              :class="{ 'error': verifyCodeError }"
              placeholder="请输入验证码"
              maxlength="4"
              @input="validateVerifyCode"
              @blur="validateVerifyCode"
              required 
            />
            <span @click="refreshCode" style="cursor: pointer; margin-left: 10px;">
              <s-identify :identifyCode="identifyCode" ></s-identify>
            </span>
          </div>
          <span v-if="verifyCodeError" class="error-message">{{ verifyCodeError }}</span>
        </div>
        <div class="button-group">
          <button type="submit" class="login-button" :disabled="!isFormValid">登录</button>
          <button type="button" @click="handleForgotPassword" class="register-button">忘记密码</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, computed, ref } from 'vue';
import { ElMessage } from 'element-plus';
import ex from '../api/auth';
import SIdentify from './identify/identify.vue'; // 引入图片验证码组件

const form = reactive({
  userId: '',
  password: '',
  verifyCode: '', // 添加验证码字段
  type: 0 as number
});

// 错误信息
const userIdError = ref('');
const verifyCodeError = ref('');

const userIdLabel = computed(() => {
  return form.type === 1 ? '邮箱：' : '账号：';
});

const userIdPlaceholder = computed(() => {
  return form.type === 1 ? '请输入邮箱地址' : '请输入账号';
});

const userIdInputType = computed(() => {
  return form.type === 1 ? 'email' : 'text';
});

// 表单验证状态
const isFormValid = computed(() => {
  return form.userId && 
         form.password && 
         form.verifyCode && 
         !userIdError.value &&
         !verifyCodeError.value;
});

// 账号验证规则：只允许英文字母和数字
const isValidUsername = (username: string): boolean => {
  const usernameRegex = /^[a-zA-Z0-9]+$/;
  return usernameRegex.test(username);
};

// 邮箱验证规则
const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

// 处理用户输入
const handleUserIdInput = (event: Event) => {
  const target = event.target as HTMLInputElement;
  let value = target.value;
  
  if (form.type === 0) {
    // 账号模式：只保留英文字母和数字
    value = value.replace(/[^a-zA-Z0-9]/g, '');
  } else {
    // 邮箱模式：不做特殊处理，保留原始输入
    value = target.value;
  }
  
  form.userId = value;
  target.value = value;
  
  // 清除之前的错误信息
  userIdError.value = '';
};

// 验证用户输入
const validateUserId = () => {
  if (!form.userId) {
    userIdError.value = '';
    return;
  }
  
  if (form.type === 0) {
    // 验证账号
    if (!isValidUsername(form.userId)) {
      userIdError.value = '账号只能包含英文字母和数字';
    } else if (form.userId.length < 3) {
      userIdError.value = '账号至少3个字符';
    } else if (form.userId.length > 20) {
      userIdError.value = '账号不能超过20个字符';
    } else {
      userIdError.value = '';
    }
  } else {
    // 验证邮箱
    if (!isValidEmail(form.userId)) {
      userIdError.value = '请输入正确的邮箱格式';
    } else {
      userIdError.value = '';
    }
  }
};

// 验证验证码
const validateVerifyCode = () => {
  if (!form.verifyCode) {
    verifyCodeError.value = '';
    return;
  }
  
  if (form.verifyCode.length !== 4) {
    verifyCodeError.value = '验证码为4位字符';
  } else if (form.verifyCode.toUpperCase() !== identifyCode.value.toUpperCase()) {
    verifyCodeError.value = '验证码输入错误';
  } else {
    verifyCodeError.value = '';
  }
};

// 监听登录方式切换，清除输入和错误信息
const handleTypeChange = () => {
  form.userId = '';
  userIdError.value = '';
  form.verifyCode = '';
  verifyCodeError.value = '';
  refreshCode(); // 切换登录方式时也刷新验证码
};

// 添加监听器来处理类型切换
import { watch } from 'vue';
watch(() => form.type, () => {
  handleTypeChange();
});

const handleSubmit = async () => {
  // 提交前再次验证
  validateUserId();
  
  if (userIdError.value) {
    ElMessage.error('请检查输入格式');
    return;
  }
  
  // 验证码检查
  if (form.verifyCode.toUpperCase() !== identifyCode.value.toUpperCase()) {
    ElMessage.error('验证码输入错误，请重新输入');
    refreshCode(); // 验证码错误时刷新验证码
    form.verifyCode = ''; // 清空验证码输入
    return;
  }
  
  try {
    const response = await ex.login(form.userId, form.password, form.type);
    if (response.code==200) {
      ElMessage.success(response.message);
      window.location.href = '/home';
    } else {
      ElMessage.error(response.message);
      refreshCode(); // 登录失败时刷新验证码
      form.verifyCode = ''; // 清空验证码输入
    }
  } catch (error: any) {
    console.error('Login failed:', error);
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.message || '登录失败，请检查网络连接或联系管理员');
    } else {
      ElMessage.error('登录失败，请检查网络连接或联系管理员');
    }
    refreshCode(); // 登录失败时刷新验证码
    form.verifyCode = ''; // 清空验证码输入
  }
};

const handleForgotPassword = () => {
  window.location.href = '/forgot-password'; // 跳转到忘记密码页面
};

// 验证码相关逻辑
const identifyCode = ref(''); // 定义验证码
const identifyCodes = '3456789ABCDEFGHjKMNPQRSTUVWXY'; // 验证码规则

const refreshCode = () => {
  identifyCode.value = '';
  makeCode(identifyCodes, 4);
};

const makeCode = (o: string, l: number) => {
  for (let i = 0; i < l; i++) {
    identifyCode.value += o[Math.floor(Math.random() * o.length)];
  }
};

// 初始化验证码
refreshCode();
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

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
}

.login-button:active:not(:disabled) {
  transform: translateY(0);
}

.login-button:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
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

.verify-code-container {
  display: flex;
  align-items: center;
  gap: 10px; /* 输入框和验证码图片之间的间距 */
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
    width: 100%;
  }
}
</style>