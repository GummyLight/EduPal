<template>
  <div class="login-form-container">
    <!-- 神秘触发区域 - 点击左上角隐藏区域 -->
    <div 
      class="secret-trigger" 
      @click="handleSecretTrigger"
      title="神秘区域"
    ></div>
    
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
          <div class="password-input-container">
            <input 
              v-model="form.password" 
              :type="showPassword ? 'text' : 'password'" 
              id="password"
              class="form-input password-input"
              placeholder="请输入密码"
              required 
            />
            <span 
              @click="togglePasswordVisibility" 
              class="password-toggle-icon"
              :title="showPassword ? '隐藏密码' : '显示密码'"
            >
              <!-- 显示密码图标 (眼睛睁开) -->
              <svg v-if="!showPassword" width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              
              <!-- 隐藏密码图标 (眼睛带斜线) -->
              <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="m9.88 9.88a3 3 0 1 0 4.24 4.24" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 11 8 11 8a13.16 13.16 0 0 1-1 1.73" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M6.61 6.61A13.526 13.526 0 0 0 1 12s4 8 11 8a9.74 9.74 0 0 0 5-1.73" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <line x1="2" y1="2" x2="22" y2="22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </span>
          </div>
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
    
    <!-- 神秘窗口模态框 -->
    <div v-if="showSecretModal" class="secret-modal-overlay" @click="closeSecretModal">
      <div class="secret-modal" @click.stop>
        <div class="secret-header">
          <h3>🎮 神秘彩蛋发现！</h3>
          <button @click="closeSecretModal" class="close-btn">×</button>
        </div>
        <div class="secret-content">
          <p class="secret-instruction">
            输入神秘代码解锁隐藏功能：
          </p>
          <div class="code-display">
            <span class="code-hint"></span>
          </div>
          <div class="input-sequence">
            <span 
              v-for="(input, index) in inputSequence" 
              :key="index"
              class="input-char"
              :class="{ 'correct': input.correct, 'incorrect': input.incorrect }"
            >
              {{ input.char }}
            </span>
          </div>
          <p class="secret-status" :class="sequenceStatus">
            {{ statusMessage }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, computed, ref, watch, onMounted, onUnmounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import ex from '../api/auth';
import SIdentify from './identify/identify.vue'; // 引入图片验证码组件

const router = useRouter();

const form = reactive({
  userId: '',
  password: '',
  verifyCode: '', // 添加验证码字段
  type: 0 as number
});

// 错误信息
const userIdError = ref('');
const verifyCodeError = ref('');

// 密码显示状态
const showPassword = ref(false);

// Doro 模式音频播放器
const doroAudioPlayer = ref<HTMLAudioElement | null>(null);

// 神秘彩蛋相关状态
const showSecretModal = ref(false);
const inputSequence = ref<Array<{char: string, correct: boolean, incorrect: boolean}>>([]);
const sequenceStatus = ref('');
const statusMessage = ref('按键盘输入神秘代码...');

// 多种神秘代码序列
const secretCodes = {
  konami: {
    sequence: ['ArrowUp', 'ArrowUp', 'ArrowDown', 'ArrowDown', 'ArrowLeft', 'ArrowRight', 'ArrowLeft', 'ArrowRight', 'KeyB', 'KeyA', 'KeyB', 'KeyA'],
    display: ['↑', '↑', '↓', '↓', '←', '→', '←', '→', 'B', 'A', 'B', 'A'],
    action: () => {
      router.push('/register');
      ElMessage.success('欢迎来到注册页面！这是一个隐藏彩蛋！');
    },
    message: '🎉 恭喜！神秘代码正确！正在传送到注册页面...'
  },
  doro: {
    sequence: ['KeyD', 'KeyO', 'KeyR', 'KeyO'],
    display: ['D', 'O', 'R', 'O'],
    action: () => {
      // 初始化并播放 BGM
      if (!doroAudioPlayer.value) {
        doroAudioPlayer.value = new Audio('/src/assets/DoroBgm.mp3');
        doroAudioPlayer.value.loop = true;
      }
      doroAudioPlayer.value.play().catch(error => {
        console.error("Error playing Doro BGM:", error);
        ElMessage.error('无法播放BGM，请检查控制台。');
      });
      ElMessage.success('🌸 Doroの小曲已启动！');
    },
    message: '🌸 神秘的 Doro 之声...' // 更新消息
  },
  contributors: {
    sequence: ['KeyC', 'KeyO', 'KeyN', 'KeyT', 'KeyR', 'KeyI', 'KeyB', 'KeyU', 'KeyT', 'KeyO', 'KeyR', 'KeyS'],
    display: ['C', 'O', 'N', 'T', 'R', 'I', 'B', 'U', 'T', 'O', 'R', 'S'],
    action: () => {
      router.push('/credits-game');
      ElMessage.success('🎮 进入贡献者弹幕游戏！准备好躲避攻击了吗？');
    },
    message: '🎮 感谢所有贡献者！正在启动弹幕游戏...'
  }
};

let currentSequenceIndex = 0;
const possibleCodes = ref<string[]>(Object.keys(secretCodes));

// 计算属性
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

// 切换密码显示状态
const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value;
};

// 监听登录方式切换，清除输入和错误信息
const handleTypeChange = () => {
  form.userId = '';
  userIdError.value = '';
  form.verifyCode = '';
  verifyCodeError.value = '';
  refreshCode(); // 切换登录方式时也刷新验证码
};

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
      
      localStorage.setItem('user_id', form.userId);
      localStorage.setItem('user_type', response.data?.userType?.toString() || '1');
      localStorage.setItem('user_name', response.data?.userName?.toString() || 'name');
      
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

// 神秘窗口彩蛋功能
const handleSecretTrigger = () => {
  showSecretModal.value = true;
  inputSequence.value = [];
  currentSequenceIndex = 0;
  possibleCodes.value = Object.keys(secretCodes); // Reset to all codes
  sequenceStatus.value = '';
  statusMessage.value = '按键盘输入神秘代码...';
};

const closeSecretModal = () => {
  showSecretModal.value = false;
  // Reset state when closing
  inputSequence.value = [];
  currentSequenceIndex = 0;
  possibleCodes.value = Object.keys(secretCodes);
  sequenceStatus.value = '';
  statusMessage.value = '按键盘输入神秘代码...'; // Reset message
};

// 处理键盘输入
const handleKeyDown = (event: KeyboardEvent) => {
  if (!showSecretModal.value) return;

  event.preventDefault();
  const keyPressed = event.code;

  const nextPossibleCodes: string[] = [];
  let matchedAnyCodeThisTurn = false;

  for (const codeName of possibleCodes.value) {
    const codeDetail = secretCodes[codeName as keyof typeof secretCodes];
    // Check if the current key pressed matches the expected key in the sequence for this code
    if (currentSequenceIndex < codeDetail.sequence.length && codeDetail.sequence[currentSequenceIndex] === keyPressed) {
      nextPossibleCodes.push(codeName);
      matchedAnyCodeThisTurn = true;
    }
  }

  if (matchedAnyCodeThisTurn) {
    // A key was pressed that matches the next expected key for one or more possible codes
    // Use the display character from the first matching code for the visual feedback
    const displayCharSource = secretCodes[nextPossibleCodes[0] as keyof typeof secretCodes];
    inputSequence.value.push({
      char: displayCharSource.display[currentSequenceIndex], // Or a generic '●' if preferred
      correct: true,
      incorrect: false,
    });
    
    currentSequenceIndex++;
    possibleCodes.value = nextPossibleCodes; // Update the list of still-possible codes

    // Check if any of the currently possible codes are now complete
    let completedCodeName: string | null = null;
    for (const codeName of possibleCodes.value) {
      const codeDetail = secretCodes[codeName as keyof typeof secretCodes];
      if (currentSequenceIndex === codeDetail.sequence.length) {
        completedCodeName = codeName;
        break; // Found a completed code
      }
    }

    if (completedCodeName) {
      const completedCodeDetail = secretCodes[completedCodeName as keyof typeof secretCodes];
      sequenceStatus.value = 'success';
      statusMessage.value = completedCodeDetail.message;
      setTimeout(() => {
        completedCodeDetail.action();
        closeSecretModal(); // This will also reset state for the next opening
      }, 1500);
    } else {
      statusMessage.value = '很好！继续输入...';
    }

  } else {
    // Key pressed does not match the next key for ANY of the currently possible codes
    const wrongCharDisplay = getDisplayChar(keyPressed); // Get a display for the wrong key
    inputSequence.value.push({ char: wrongCharDisplay, correct: false, incorrect: true });
    sequenceStatus.value = 'error';
    statusMessage.value = '❌ 输入错误！序列已重置。';

    // Reset the sequence matching process
    setTimeout(() => {
      inputSequence.value = [];
      currentSequenceIndex = 0;
      possibleCodes.value = Object.keys(secretCodes); // Reset to all codes
      sequenceStatus.value = '';
      statusMessage.value = '按键盘输入神秘代码...';
    }, 1000);
  }
};

// 将键盘代码转换为显示字符
const getDisplayChar = (code: string): string => {
  const codeMap: {[key: string]: string} = {
    'ArrowUp': '↑',
    'ArrowDown': '↓',
    'ArrowLeft': '←',
    'ArrowRight': '→',
    'KeyB': 'B',
    'KeyA': 'A',
    'KeyD': 'D',
    'KeyO': 'O',
    'KeyR': 'R',
    'KeyC': 'C',
    'KeyN': 'N',
    'KeyT': 'T',
    'KeyI': 'I',
    'KeyU': 'U',
    'KeyS': 'S',
  };
  // Fallback for other letter keys, e.g. KeyC -> C
  if (code.startsWith('Key') && code.length === 4 && /^[A-Z]$/.test(code.charAt(3))) {
      if (!codeMap[code]) return code.charAt(3);
  }
  return codeMap[code] || code.replace(/^Key/, ''); // General fallback
};

// 监听键盘事件
onMounted(() => {
  document.addEventListener('keydown', handleKeyDown);
  // 预加载音频元素，但不播放
  if (!doroAudioPlayer.value) {
    doroAudioPlayer.value = new Audio('/src/assets/DoroBgm.mp3');
    doroAudioPlayer.value.loop = true;
  }
});

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeyDown);
  // 清理音频播放器
  if (doroAudioPlayer.value) {
    doroAudioPlayer.value.pause();
    doroAudioPlayer.value.src = ''; // 尝试释放资源
    doroAudioPlayer.value = null;
  }
});
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

/* 密码输入框容器样式 */
.password-input-container {
  position: relative;
  width: 100%;
}

.password-input {
  width: 100%;
  padding-right: 45px; /* 为眼睛图标留出空间 */
  box-sizing: border-box; /* 确保padding不会增加总宽度 */
}

.password-toggle-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  color: #6b7280;
  transition: all 0.3s ease;
  user-select: none;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 4px;
  opacity: 0.7;
  z-index: 10; /* 确保图标在输入框之上 */
}

.password-toggle-icon:hover {
  color: #4f46e5;
  background-color: rgba(79, 70, 229, 0.08);
  opacity: 1;
  transform: translateY(-50%) scale(1.05);
}

.password-toggle-icon svg {
  transition: transform 0.2s ease;
}

/* 神秘触发区域样式 */
.secret-trigger {
  position: fixed;
  top: 0;
  left: 0;
  width: 50px;
  height: 50px;
  background: transparent;
  cursor: pointer;
  z-index: 9999;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.secret-trigger:hover {
  background: linear-gradient(135deg, rgba(79, 70, 229, 0.1) 0%, rgba(147, 51, 234, 0.1) 100%);
  border-radius: 0 0 25px 0;
  backdrop-filter: blur(5px);
}

.secret-icon {
  font-size: 16px;
  opacity: 0.3;
  transition: all 0.3s ease;
  animation: float 3s ease-in-out infinite;
}

.secret-trigger:hover .secret-icon {
  opacity: 0.8;
  transform: scale(1.2);
  filter: drop-shadow(0 0 8px rgba(79, 70, 229, 0.5));
}

/* 神秘窗口模态框样式 */
.secret-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10000;
  animation: fadeIn 0.3s ease;
}

.secret-modal {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 30px;
  max-width: 500px;
  width: 90%;
  color: white;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideIn 0.3s ease;
}

.secret-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.secret-header h3 {
  margin: 0;
  font-size: 24px;
  background: linear-gradient(45deg, #fff, #ffd700);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.close-btn {
  background: none;
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  padding: 5px;
  border-radius: 50%;
  width: 35px;
  height: 35px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: rotate(90deg);
}

.secret-content {
  text-align: center;
}

.secret-instruction {
  font-size: 16px;
  margin-bottom: 15px;
  opacity: 0.9;
}

.code-display {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  padding: 15px;
  margin: 15px 0;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.code-hint {
  font-family: 'Courier New', monospace;
  font-size: 20px;
  font-weight: bold;
  letter-spacing: 4px;
  color: #ffd700;
  text-shadow: 0 0 10px rgba(255, 215, 0, 0.5);
}

.input-sequence {
  min-height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin: 20px 0;
  flex-wrap: wrap;
}

.input-char {
  display: inline-block;
  width: 30px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  border-radius: 50%;
  font-weight: bold;
  font-size: 14px;
  transition: all 0.3s ease;
  animation: bounceIn 0.5s ease;
}

.input-char.correct {
  background: rgba(34, 197, 94, 0.8);
  color: white;
  box-shadow: 0 0 15px rgba(34, 197, 94, 0.5);
}

.input-char.incorrect {
  background: rgba(239, 68, 68, 0.8);
  color: white;
  box-shadow: 0 0 15px rgba(239, 68, 68, 0.5);
  animation: shake 0.5s ease;
}

.secret-status {
  font-size: 14px;
  margin-top: 15px;
  min-height: 20px;
  font-weight: 500;
}

.secret-status.success {
  color: #4ade80;
  animation: pulse 1s infinite;
}

.secret-status.error {
  color: #f87171;
}

/* 动画效果 */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideIn {
  from { 
    opacity: 0;
    transform: translateY(-50px) scale(0.9);
  }
  to { 
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes bounceIn {
  0% { 
    opacity: 0;
    transform: scale(0.3);
  }
  50% { 
    opacity: 1;
    transform: scale(1.05);
  }
  70% { 
    transform: scale(0.9);
  }
  100% { 
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  10%, 30%, 50%, 70%, 90% { transform: translateX(-10px); }
  20%, 40%, 60%, 80% { transform: translateX(10px); }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
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

  .secret-modal {
    width: 95%;
    padding: 15px;
  }
}
</style>