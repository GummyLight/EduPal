<template>
  <div class="qa-container">
    <!-- 侧边栏 - 历史对话 -->
    <div class="sidebar" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <div class="sidebar-header">
        <h3 v-if="!sidebarCollapsed">对话历史</h3>
        <el-button 
          text 
          @click="toggleSidebar" 
          class="collapse-btn"
          :icon="sidebarCollapsed ? 'ArrowRight' : 'ArrowLeft'"
        />
      </div>
      
      <div class="conversation-list" v-if="!sidebarCollapsed">
        <div class="new-chat-btn">
          <el-button type="primary" @click="startNewConversation" style="width: 100%;">
            <el-icon><Plus /></el-icon>
            新对话
          </el-button>
        </div>
        
        <div class="conversation-history">
          <div 
            v-for="conv in conversations" 
            :key="conv.id"
            class="conversation-item"
            :class="{ 'active': currentConversationId === conv.id }"
            @click="selectConversation(conv.id)"
          >
            <div class="conversation-title">{{ conv.title }}</div>
            <div class="conversation-time">{{ formatTime(conv.lastTime) }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主聊天区域 -->
    <div class="chat-main" :class="{ 'chat-expanded': sidebarCollapsed }">
      <!-- 头部 -->
      <div class="chat-header">
        <div class="header-title">
          <el-icon><Service /></el-icon>
          <h2>EduPal AI 智能答疑</h2>
        </div>
        <div class="header-actions">
          <el-button text @click="clearCurrentConversation">
            <el-icon><Delete /></el-icon>
            清空对话
          </el-button>
        </div>
      </div>

      <!-- 聊天消息区域 -->
      <div class="chat-messages" ref="messagesContainer">
        <!-- 欢迎消息 -->
        <div v-if="currentMessages.length === 0" class="welcome-message">
          <div class="ai-avatar">
            <el-icon><Service /></el-icon>
          </div>
          <div class="welcome-content">
            <h3>欢迎使用 EduPal AI 智能答疑！</h3>
            <p>我是你的学习助手，可以帮助你解答各种学科问题</p>
            <div class="quick-questions">
              <el-tag 
                v-for="q in quickQuestions" 
                :key="q"
                @click="askQuickQuestion(q)"
                class="quick-question-tag"
              >
                {{ q }}
              </el-tag>
            </div>
          </div>
        </div>

        <!-- 对话消息 -->
        <div v-for="message in currentMessages" :key="message.id" class="message-item">
          <!-- 用户消息 -->
          <div v-if="message.type === 'user'" class="message user-message">
            <div class="message-content">
              <div class="message-text">{{ message.content }}</div>
              <div class="message-time">{{ formatTime(message.timestamp) }}</div>
            </div>
            <div class="user-avatar">
              <el-icon><UserFilled /></el-icon>
            </div>
          </div>

          <!-- AI消息 -->
          <div v-else class="message ai-message">
            <div class="ai-avatar">
              <el-icon><Service /></el-icon>
            </div>
            <div class="message-content">
              <div class="message-text" v-html="formatAIResponse(message.content)"></div>
              <div class="message-actions">
                <div class="message-time">{{ formatTime(message.timestamp) }}</div>
                <div class="action-buttons">
                  <el-button text size="small" @click="copyMessage(message.content)">
                    <el-icon><DocumentCopy /></el-icon>
                    复制
                  </el-button>
                  <el-button text size="small" @click="likeMessage(message.id)">
                    <el-icon><Star /></el-icon>
                    赞
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="isLoading" class="message ai-message">
          <div class="ai-avatar">
            <el-icon><Service /></el-icon>
          </div>
          <div class="message-content">
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input">
        <div class="input-container">
          <el-input
            v-model="currentQuestion"
            type="textarea"
            placeholder="输入你的问题，按 Ctrl+Enter 发送..."
            :rows="3"
            resize="none"
            @keydown="handleKeydown"
            class="question-input"
          />
          <div class="input-actions">
            <div class="input-tools">
              <el-button text size="small">
                <el-icon><Paperclip /></el-icon>
              </el-button>
              <el-select v-model="currentSubject" size="small" style="width: 100px;">
                <el-option label="数学" value="math" />
                <el-option label="物理" value="physics" />
                <el-option label="化学" value="chemistry" />
                <el-option label="编程" value="programming" />
                <el-option label="其他" value="other" />
              </el-select>
            </div>
            <el-button 
              type="primary" 
              @click="sendMessage" 
              :disabled="!currentQuestion.trim() || isLoading"
              class="send-btn"
            >
              <el-icon><Promotion /></el-icon>
              发送
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { 
  Plus, UserFilled, Delete, DocumentCopy, Star, 
  Paperclip, Promotion, ArrowLeft, ArrowRight, Service
} from '@element-plus/icons-vue';

// 接口定义
interface Message {
  id: string;
  type: 'user' | 'ai';
  content: string;
  timestamp: Date;
  questionId?: string;
  answerId?: string;
}

interface Conversation {
  id: string;
  title: string;
  lastTime: Date;
  messages: Message[];
}

// 响应式数据
const currentQuestion = ref('');
const currentSubject = ref('math');
const isLoading = ref(false);
const sidebarCollapsed = ref(false);
const messagesContainer = ref<HTMLElement>();

// 对话管理
const conversations = ref<Conversation[]>([]);
const currentConversationId = ref<string>('');

// 用户信息（从localStorage获取）
const currentUser = ref({
  studentId: localStorage.getItem('userId') || 's20250112', // 默认值，实际应该从用户状态获取
  userName: localStorage.getItem('userName') || '用户'
});

// 快速问题
const quickQuestions = [
  '什么是余弦定理？',
  '如何解一元二次方程？',
  '牛顿第二定律是什么？',
  '什么是递归算法？'
];

// 计算属性
const currentMessages = computed(() => {
  const conv = conversations.value.find(c => c.id === currentConversationId.value);
  return conv ? conv.messages : [];
});

// 方法
const generateId = () => Math.random().toString(36).substr(2, 9);

const startNewConversation = () => {
  const newConv: Conversation = {
    id: generateId(),
    title: '新对话',
    lastTime: new Date(),
    messages: []
  };
  conversations.value.unshift(newConv);
  currentConversationId.value = newConv.id;
};

const selectConversation = (id: string) => {
  currentConversationId.value = id;
};

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value;
};

const clearCurrentConversation = () => {
  const conv = conversations.value.find(c => c.id === currentConversationId.value);
  if (conv) {
    conv.messages = [];
    ElMessage.success('对话已清空');
  }
};

const askQuickQuestion = (question: string) => {
  currentQuestion.value = question;
  sendMessage();
};

// 临时AI API模拟函数
const askAI = async (studentId: string, questionContent: string, questionSubject: string) => {
  // 模拟API延迟
  await new Promise(resolve => setTimeout(resolve, 1500));

  let response = '';
  
  // 根据问题内容和学科生成不同的回答
  if (questionContent.includes('余弦定理')) {
    response = `余弦定理是三角形中的重要定理，表述为：

对于任意三角形ABC，设其三边长分别为a、b、c，对应的对角分别为A、B、C，则有：

**c² = a² + b² - 2ab·cos(C)**

其中：
- c是角C的对边  
- a和b是角C的两条邻边
- cos(C)是角C的余弦值

**应用场景：**
1. 已知两边及其夹角，求第三边
2. 已知三边，求任意一个角
3. 验证三角形是否成立

**实例：**
如果三角形的两边分别为 a=3, b=4，夹角C=60°，则第三边：
c² = 3² + 4² - 2×3×4×cos(60°) = 9 + 16 - 24×0.5 = 13
所以 c = √13 ≈ 3.6

这个定理是解三角形的重要工具！`;
  } else if (questionContent.includes('二次方程')) {
    response = `一元二次方程的标准形式为：**ax² + bx + c = 0** (a ≠ 0)

**求解方法：**

1. **求根公式：** 
   $$x = \\frac{-b ± \\sqrt{b² - 4ac}}{2a}$$

2. **判别式 Δ = b² - 4ac：**
   - Δ > 0：两个不等实根
   - Δ = 0：一个重根  
   - Δ < 0：无实根

3. **其他方法：**
   - 因式分解法
   - 配方法
   - 图像法

**例题：** 解方程 x² - 5x + 6 = 0
方法1（因式分解）：(x-2)(x-3) = 0，所以 x₁=2, x₂=3
方法2（求根公式）：x = (5 ± √(25-24))/2 = (5 ± 1)/2，得 x₁=3, x₂=2

需要我举更多具体例子吗？`;
  } else if (questionContent.includes('牛顿') && questionContent.includes('定律')) {
    response = `牛顿第二定律是经典力学的核心定律，表述为：

**物体的加速度与所受的合外力成正比，与物体质量成反比**

**数学表达式：**
$$\\vec{F} = m\\vec{a}$$

其中：
- F⃗ 是物体所受的合外力（单位：牛顿 N）
- m 是物体的质量（单位：千克 kg）  
- a⃗ 是物体的加速度（单位：m/s²）

**重要特点：**
1. **矢量性**：力和加速度都是矢量，方向相同
2. **瞬时性**：描述的是某一瞬间的关系
3. **因果性**：力是产生加速度的原因

**应用实例：**
一个质量为2kg的物体受到10N的水平力作用：
a = F/m = 10N / 2kg = 5 m/s²

**与其他定律的关系：**
- 牛顿第一定律：当F=0时，a=0（惯性定律）
- 牛顿第三定律：作用力与反作用力

这个定律是解决动力学问题的基础！`;
  } else if (questionContent.includes('递归')) {
    response = `递归算法是一种重要的编程思想，指函数调用自身来解决问题。

**递归的核心要素：**

1. **递归基础（Base Case）**：终止条件，防止无限递归
2. **递归关系（Recursive Case）**：问题如何分解为更小的子问题

**经典例子 - 阶乘：**
\`\`\`javascript
function factorial(n) {
    // 递归基础
    if (n <= 1) return 1;
    
    // 递归关系
    return n * factorial(n - 1);
}
\`\`\`

**递归的优缺点：**

**优点：**
- 代码简洁易懂
- 适合解决具有递归结构的问题（如树、图遍历）

**缺点：**
- 可能导致栈溢出
- 重复计算，效率较低

**优化方法：**
1. **记忆化递归**：缓存已计算的结果
2. **动态规划**：自底向上计算
3. **尾递归优化**：将递归转为迭代

**应用场景：**
- 斐波那契数列
- 二叉树遍历
- 快速排序、归并排序
- 图的深度优先搜索

需要看具体的递归算法实现吗？`;
  } else {
    // 根据学科提供针对性回答
    const subjectResponses = {
      'math': '这是一个数学问题。建议先明确问题的已知条件和求解目标，然后选择合适的数学方法。',
      'physics': '这是一个物理问题。建议先分析物理过程，建立物理模型，然后应用相关物理定律。',
      'chemistry': '这是一个化学问题。建议先分析化学反应或化学性质，考虑相关的化学原理。',
      'programming': '这是一个编程问题。建议先分析问题需求，设计算法思路，然后编写代码实现。'
    };
    
    response = `我理解你的问题是关于"${questionContent}"。

${subjectResponses[questionSubject as keyof typeof subjectResponses] || '这是一个很好的问题！'}

虽然我还在不断学习中，但我建议你可以：

1. **明确问题核心**：找出问题的关键点和难点
2. **查阅资料**：搜索相关的教材、文档或在线资源  
3. **分步骤解决**：将复杂问题分解为简单的子问题
4. **实践验证**：通过练习或实验来验证理解

如果你能提供更多具体信息或背景，我会尽力给出更准确和详细的回答。你也可以尝试换个角度重新描述问题。`;
  }

  return {
    answerId: Math.random().toString(36).substr(2, 9),
    questionId: Math.random().toString(36).substr(2, 9),
    answerContent: response
  };
};

const sendMessage = async () => {
  if (!currentQuestion.value.trim() || isLoading.value) return;

  // 确保有当前对话
  if (!currentConversationId.value) {
    startNewConversation();
  }

  const conv = conversations.value.find(c => c.id === currentConversationId.value);
  if (!conv) return;

  // 添加用户消息
  const userMessage: Message = {
    id: generateId(),
    type: 'user',
    content: currentQuestion.value,
    timestamp: new Date()
  };

  conv.messages.push(userMessage);
  
  // 更新对话标题（使用问题的前20个字符）
  if (conv.messages.length === 1) {
    conv.title = currentQuestion.value.substring(0, 20) + (currentQuestion.value.length > 20 ? '...' : '');
  }
  
  conv.lastTime = new Date();

  const questionContent = currentQuestion.value;
  currentQuestion.value = '';
  isLoading.value = true;

  // 滚动到底部
  await nextTick();
  scrollToBottom();

  try {
    // 调用真实的AI API
    const aiResponse = await askAI(
      currentUser.value.studentId,
      questionContent,
      currentSubject.value
    );

    // 添加AI回答消息
    const aiMessage: Message = {
      id: generateId(),
      type: 'ai',
      content: aiResponse.answerContent,
      timestamp: new Date(),
      questionId: aiResponse.questionId,
      answerId: aiResponse.answerId
    };

    conv.messages.push(aiMessage);
    
    ElMessage.success('AI回答已生成');
    
  } catch (error: any) {
    console.error('AI回答失败:', error);
    ElMessage.error(error.message || 'AI服务暂时不可用');
    
    // 添加错误消息
    const errorMessage: Message = {
      id: generateId(),
      type: 'ai',
      content: '抱歉，我现在无法回答你的问题，请稍后再试。可能的原因：网络连接问题或AI服务暂时不可用。',
      timestamp: new Date()
    };
    conv.messages.push(errorMessage);
  } finally {
    isLoading.value = false;
    await nextTick();
    scrollToBottom();
  }
};

const formatAIResponse = (content: string) => {
  // 处理LaTeX数学公式
  let formatted = content
    // 处理行内公式 \(...\) 和 $...$
    .replace(/\\\((.*?)\\\)/g, '<span class="math-inline">$1</span>')
    .replace(/\$([^$]+)\$/g, '<span class="math-inline">$1</span>')
    // 处理块级公式 \[...\] 和 $$...$$
    .replace(/\\\[(.*?)\\\]/g, '<div class="math-block">$1</div>')
    .replace(/\$\$(.*?)\$\$/g, '<div class="math-block">$1</div>')
    // 处理markdown格式
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.*?)\*/g, '<em>$1</em>')
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    // 处理换行
    .replace(/\n\n/g, '</p><p>')
    .replace(/\n/g, '<br>');

  // 包装在段落中
  if (!formatted.includes('<p>')) {
    formatted = '<p>' + formatted + '</p>';
  }

  return formatted;
};

const formatTime = (date: Date) => {
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  
  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`;
  
  return date.toLocaleDateString();
};

const copyMessage = async (content: string) => {
  try {
    // 移除HTML标签
    const textContent = content.replace(/<[^>]*>/g, '');
    await navigator.clipboard.writeText(textContent);
    ElMessage.success('已复制到剪贴板');
  } catch (error) {
    ElMessage.error('复制失败');
  }
};

const likeMessage = (messageId: string) => {
  ElMessage.success('感谢你的反馈！');
};

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
};

const handleKeydown = (event: KeyboardEvent) => {
  if (event.ctrlKey && event.key === 'Enter') {
    event.preventDefault();
    sendMessage();
  }
};

// 生命周期
onMounted(() => {
  // 创建默认对话
  startNewConversation();
  
  // 尝试从localStorage恢复对话历史
  const savedConversations = localStorage.getItem('qa-conversations');
  if (savedConversations) {
    try {
      const parsed = JSON.parse(savedConversations);
      conversations.value = parsed.map((conv: any) => ({
        ...conv,
        lastTime: new Date(conv.lastTime),
        messages: conv.messages.map((msg: any) => ({
          ...msg,
          timestamp: new Date(msg.timestamp)
        }))
      }));
    } catch (error) {
      console.error('恢复对话历史失败:', error);
    }
  }
});

// 保存对话历史到localStorage
const saveConversations = () => {
  try {
    localStorage.setItem('qa-conversations', JSON.stringify(conversations.value));
  } catch (error) {
    console.error('保存对话历史失败:', error);
  }
};

// 监听对话变化，自动保存
import { watch } from 'vue';
watch(conversations, saveConversations, { deep: true });
</script>

<style scoped>
.qa-container {
  display: flex;
  height: 100vh;
  background: #f5f7fa;
}

/* 侧边栏样式 */
.sidebar {
  width: 280px;
  background: white;
  border-right: 1px solid #e4e7ed;
  transition: all 0.3s ease;
  overflow: hidden;
}

.sidebar-collapsed {
  width: 60px;
}

.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.collapse-btn {
  padding: 4px;
  min-height: auto;
}

.new-chat-btn {
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.conversation-history {
  padding: 8px;
  max-height: calc(100vh - 140px);
  overflow-y: auto;
}

.conversation-item {
  padding: 12px;
  margin-bottom: 4px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.conversation-item:hover {
  background: #f5f7fa;
}

.conversation-item.active {
  background: #e6f4ff;
  border: 1px solid #91caff;
}

.conversation-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.conversation-time {
  font-size: 12px;
  color: #909399;
}

/* 主聊天区域 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
}

.chat-expanded {
  margin-left: 0;
}

.chat-header {
  padding: 16px 24px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-title h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-title .el-icon {
  font-size: 24px;
  color: #409eff;
}

/* 消息区域 */
.chat-messages {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background: #fafbfc;
}

.welcome-message {
  display: flex;
  gap: 16px;
  padding: 32px;
  text-align: center;
  justify-content: center;
}

.ai-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.welcome-content h3 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 20px;
}

.welcome-content p {
  margin: 0 0 24px 0;
  color: #606266;
  font-size: 14px;
}

.quick-questions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
}

.quick-question-tag {
  cursor: pointer;
  transition: all 0.2s ease;
}

.quick-question-tag:hover {
  background: #409eff;
  color: white;
}

.message-item {
  margin-bottom: 24px;
}

.message {
  display: flex;
  gap: 12px;
  max-width: 80%;
}

.user-message {
  flex-direction: row-reverse;
  margin-left: auto;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #67c23a;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.message-content {
  flex: 1;
}

.user-message .message-content {
  text-align: right;
}

.message-text {
  background: white;
  padding: 12px 16px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  line-height: 1.5;
  word-wrap: break-word;
}

.user-message .message-text {
  background: #409eff;
  color: white;
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.message-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

/* 打字指示器 */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #c0c4cc;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) { animation-delay: -0.32s; }
.typing-indicator span:nth-child(2) { animation-delay: -0.16s; }

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 输入区域 */
.chat-input {
  padding: 16px 24px;
  border-top: 1px solid #e4e7ed;
  background: white;
}

.input-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.question-input {
  resize: none;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.input-tools {
  display: flex;
  align-items: center;
  gap: 12px;
}

.send-btn {
  padding: 8px 24px;
}

/* 数学公式样式 */
.math-inline {
  font-family: 'KaTeX_Main', 'Times New Roman', serif;
  font-style: italic;
  color: #e91e63;
  background: rgba(233, 30, 99, 0.1);
  padding: 2px 4px;
  border-radius: 3px;
}

.math-block {
  font-family: 'KaTeX_Main', 'Times New Roman', serif;
  text-align: center;
  margin: 12px 0;
  padding: 8px;
  background: rgba(233, 30, 99, 0.05);
  border-left: 3px solid #e91e63;
  border-radius: 4px;
}

.message-text code {
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 0.9em;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    height: 100vh;
    z-index: 1000;
    transform: translateX(-100%);
  }
  
  .sidebar:not(.sidebar-collapsed) {
    transform: translateX(0);
  }
  
  .chat-main {
    width: 100%;
  }
  
  .message {
    max-width: 95%;
  }
  
  .welcome-message {
    padding: 16px;
  }
  
  .quick-questions {
    flex-direction: column;
  }
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar,
.conversation-history::-webkit-scrollbar {
  width: 4px;
}

.chat-messages::-webkit-scrollbar-track,
.conversation-history::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.chat-messages::-webkit-scrollbar-thumb,
.conversation-history::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}

.chat-messages::-webkit-scrollbar-thumb:hover,
.conversation-history::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}
</style>
