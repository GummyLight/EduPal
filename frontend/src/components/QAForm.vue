<template>
  <div class="qa-container">
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
            <img :src="aiIconDefault" alt="AI头像" class="avatar-image" />
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
              <img :src="userIconDefault" alt="用户头像" class="avatar-image" />
            </div>
          </div>

          <!-- AI消息 -->
          <div v-else class="message ai-message">
            <div class="ai-avatar">
              <img :src="aiIconDefault" alt="AI头像" class="avatar-image" />
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
            <img :src="aiIconDefault" alt="AI头像" class="avatar-image" />
          </div>
          <div class="message-content">
            <div class="loading-container">
              <div class="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
              <div class="loading-info">
                <div class="loading-text">{{ loadingText }}</div>
                <div class="loading-progress">
                  <el-progress :percentage="loadingProgress" :show-text="false" />
                </div>
                <el-button 
                  text 
                  size="small" 
                  @click="cancelRequest"
                  class="cancel-btn"
                >
                  <el-icon><Close /></el-icon>
                  取消请求
                </el-button>
              </div>
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
            placeholder="输入你的问题，按 Enter 发送，Ctrl+Enter 换行...&#10;"
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

    <!-- 侧边栏 - 历史对话（右侧） -->
    <div class="sidebar" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <!-- 折叠状态的快速切换按钮 -->
      <div v-if="sidebarCollapsed" class="sidebar-collapsed-trigger" @click="toggleSidebar">
        <el-icon><Operation /></el-icon>
        <span class="trigger-tooltip">历史</span>
      </div>
      
      <!-- 展开状态的完整内容 -->
      <div v-else class="sidebar-content">
        <div class="sidebar-header">
          <h3>对话历史</h3>
          <div class="header-actions">
            <el-button 
              text 
              @click="refreshBackendHistory" 
              :loading="isRefreshing"
              class="refresh-btn"
              title="同步云端历史"
            >
              <el-icon><Refresh /></el-icon>
            </el-button>
            <el-button 
              text 
              @click="toggleSidebar" 
              class="collapse-btn"
              title="收起侧边栏"
            >
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>
        
        <div class="conversation-list">
          <div class="new-chat-btn">
            <el-button type="primary" @click="startNewConversation" style="width: 100%;">
              <el-icon></el-icon>
              提问
            </el-button>
          </div>
          
          <div class="conversation-history">
            <div 
              v-for="conv in conversations" 
              :key="conv.id"
              class="conversation-item"
              :class="{ 'active': currentConversationId === conv.id }"
            >
              <div class="conversation-content" @click="selectConversation(conv.id)">
                <div class="conversation-header">
                  <div class="conversation-title">{{ conv.title }}</div>
                  <div class="conversation-badges">
                    <el-tag v-if="conv.isFromBackend" size="small" type="success">云端</el-tag>
                    <el-tag v-if="conv.subject" size="small" type="info">{{ getSubjectName(conv.subject) }}</el-tag>
                  </div>
                </div>
                <div class="conversation-time">{{ formatTime(conv.lastTime) }}</div>
              </div>
              <div class="conversation-actions">
                <el-button 
                  v-if="conv.isFromBackend"
                  text 
                  size="small"
                  @click.stop="handleDeleteConversation(conv)"
                  class="delete-btn"
                  title="删除历史记录"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  Plus, UserFilled, Delete, DocumentCopy, Star, 
  Paperclip, Promotion, ArrowLeft, ArrowRight, Service, Operation, Close, Refresh
} from '@element-plus/icons-vue';
// 导入真实的AI API和类型
import { askAI, getConversationHistory, deleteConversation, type HistoryResponse, type QA, type AnswerDetail } from '../api/ai';
// 导入axios用于取消令牌
import axios, { CancelTokenSource } from 'axios';
// 导入用户默认头像
import userIconDefault from '../assets/userIconDefault.jpg';
// 导入AI默认头像
import aiIconDefault from '../assets/aiIconDefault.jpg';
// 导入KaTeX用于LaTeX渲染
import * as katex from 'katex';
import 'katex/dist/katex.min.css';

// 接口定义
interface Message {
  id: string;
  type: 'user' | 'ai';
  content: string;
  timestamp: Date;
  questionId?: string;
  answerId?: string;
  subject?: string;
}

interface Conversation {
  id: string;
  title: string;
  lastTime: Date;
  messages: Message[];
  subject?: string;
  isFromBackend?: boolean; // 标记是否来自后端
}

// 响应式数据
const currentQuestion = ref('');
const currentSubject = ref('math');
const isLoading = ref(false);
const isRefreshing = ref(false);
const isSyncing = ref(false); // 同步状态
const loadingProgress = ref(0);
const loadingText = ref('AI正在思考中...');
const sidebarCollapsed = ref(false);
const messagesContainer = ref<HTMLElement>();

// 请求取消相关
const currentRequestCancel = ref<CancelTokenSource | null>(null);

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
  '什么是递归算法？',
  '请解释积分 ∫x²dx 的计算过程'
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

// 从后端加载历史对话并整合到本地对话列表
const loadAndMergeBackendHistory = async () => {
  try {
    const historyResponse = await getConversationHistory(currentUser.value.studentId);
    
    if (historyResponse.status === 'success' && historyResponse.questionSet.length > 0) {
      // 将后端历史对话转换为本地对话格式
      const backendConversations: Conversation[] = historyResponse.questionSet.map((qa: QA) => {
        const conversation: Conversation = {
          id: `backend-${qa.questionId}`, // 使用问题ID作为唯一标识符
          title: qa.questionContent.length > 30 
            ? qa.questionContent.substring(0, 30) + '...' 
            : qa.questionContent,
          lastTime: new Date(qa.questionTime),
          subject: qa.questionSubject,
          isFromBackend: true,
          messages: []
        };
        
        // 添加用户问题
        const userMessage: Message = {
          id: generateId(),
          type: 'user',
          content: qa.questionContent,
          timestamp: new Date(qa.questionTime),
          subject: qa.questionSubject,
          questionId: qa.questionId // 添加问题ID
        };
        conversation.messages.push(userMessage);
        
        // 添加所有答案
        qa.answers.forEach((answer: AnswerDetail) => {
          const aiMessage: Message = {
            id: generateId(),
            type: 'ai',
            content: answer.answerContent,
            timestamp: new Date(answer.answerTime),
            subject: qa.questionSubject,
            questionId: qa.questionId, // 添加问题ID
            answerId: answer.answerId  // 添加回答ID
          };
          conversation.messages.push(aiMessage);
        });
        
        return conversation;
      });
      
      // 智能去重：移除本地对话中已在云端的对话
      const localConversations = conversations.value.filter(conv => {
        if (conv.isFromBackend) return false; // 移除之前的后端对话
        
        // 检查本地对话是否已存在于云端
        const hasMatchInBackend = backendConversations.some(backendConv => {
          const localFirstMessage = conv.messages.find(m => m.type === 'user');
          const backendFirstMessage = backendConv.messages.find(m => m.type === 'user');
          
          // 如果问题内容相同，认为是重复对话
          return localFirstMessage && backendFirstMessage && 
                 localFirstMessage.content.trim() === backendFirstMessage.content.trim();
        });
        
        return !hasMatchInBackend; // 只保留云端没有的本地对话
      });
      
      // 合并去重后的对话并按时间倒序排列（最新的在上面）
      const allConversations = [...localConversations, ...backendConversations];
      conversations.value = allConversations.sort((a, b) => 
        new Date(b.lastTime).getTime() - new Date(a.lastTime).getTime()
      );
      
      console.log(`成功整合 ${backendConversations.length} 条后端历史对话，去重后保留 ${localConversations.length} 条本地对话，总共 ${conversations.value.length} 条对话`);
    }
  } catch (error: any) {
    console.error('加载后端历史对话失败:', error);
    // 不影响用户使用，静默失败
  }
};

// 获取学科中文名称
const getSubjectName = (subject: string): string => {
  const subjectNames: Record<string, string> = {
    'math': '数学',
    'physics': '物理', 
    'chemistry': '化学',
    'programming': '编程',
    'other': '其他'
  };
  return subjectNames[subject] || subject;
};

// 手动刷新后端历史对话
const refreshBackendHistory = async () => {
  if (isRefreshing.value) return;
  
  isRefreshing.value = true;
  try {
    await loadAndMergeBackendHistory();
    ElMessage.success('历史对话已同步');
  } catch (error) {
    ElMessage.error('同步历史对话失败');
  } finally {
    isRefreshing.value = false;
  }
};

// 实时同步功能：在成功发送消息后同步到云端
const syncAfterMessage = async (questionContent: string, answerContent: string, questionId?: string, answerId?: string) => {
  if (isSyncing.value) return;
  
  isSyncing.value = true;
  try {
    // 给后端更多时间保存数据
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    // 保存当前对话ID，用于后续切换
    const currentLocalConvId = currentConversationId.value;
    
    // 重新加载后端历史
    await loadAndMergeBackendHistory();
    
    // 智能切换：如果当前本地对话已被同步到云端，切换到云端版本
    if (questionId) {
      const matchingBackendConv = conversations.value.find(conv => 
        conv.isFromBackend && conv.id === `backend-${questionId}`
      );
      
      if (matchingBackendConv) {
        // 找到匹配的云端对话，切换到云端版本
        currentConversationId.value = matchingBackendConv.id;
        console.log('已切换到云端同步版本');
      }
    }
    
    console.log('实时同步完成');
    
  } catch (error) {
    console.warn('实时同步失败，但不影响用户使用:', error);
    // 不显示错误消息，避免干扰用户体验
  } finally {
    isSyncing.value = false;
  }
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

// 删除历史对话
const handleDeleteConversation = async (conv: Conversation) => {
  console.log('开始删除对话:', conv);
  
  if (!conv.isFromBackend) {
    ElMessage.warning('只能删除云端历史记录');
    return;
  }

  // 从conversation ID中提取questionId (格式: "backend-{questionId}")
  const questionId = conv.id.replace('backend-', '');
  console.log('提取的questionId:', questionId);
  console.log('当前用户ID:', currentUser.value.studentId);
  
  try {
    await ElMessageBox.confirm(
      `确定要删除这条历史对话吗？\n"${conv.title}"`,
      '删除确认',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: false
      }
    );

    console.log('用户确认删除，开始调用API');
    // 调用删除API
    const result = await deleteConversation(currentUser.value.studentId, questionId);
    console.log('删除API返回结果:', result);
    
    // 从本地对话列表中移除
    const index = conversations.value.findIndex(c => c.id === conv.id);
    if (index !== -1) {
      conversations.value.splice(index, 1);
    }
    
    // 如果删除的是当前选中的对话，切换到其他对话
    if (currentConversationId.value === conv.id) {
      if (conversations.value.length > 0) {
        currentConversationId.value = conversations.value[0].id;
      } else {
        startNewConversation();
      }
    }
    
    ElMessage.success('历史对话删除成功');
    
  } catch (error: any) {
    if (error === 'cancel') {
      // 用户取消删除，不做任何操作
      return;
    }
    console.error('删除历史对话失败:', error);
    ElMessage.error(error.message || '删除历史对话失败');
  }
};

const askQuickQuestion = (question: string) => {
  currentQuestion.value = question;
  sendMessage();
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
  loadingText.value = 'AI正在思考中...';
  loadingProgress.value = 0;

  // 创建取消令牌
  currentRequestCancel.value = axios.CancelToken.source();

  // 模拟加载进度
  const progressInterval = setInterval(() => {
    if (loadingProgress.value < 90) {
      loadingProgress.value += Math.random() * 10;
      if (loadingProgress.value > 30 && loadingProgress.value < 60) {
        loadingText.value = 'AI正在分析问题...';
      } else if (loadingProgress.value >= 60) {
        loadingText.value = 'AI正在生成回答...';
      }
    }
  }, 800);

  // 滚动到底部
  await nextTick();
  scrollToBottom();

  try {
    // 调用真实的AI API，传入取消令牌
    const aiResponse = await askAI(
      currentUser.value.studentId,
      questionContent,
      currentSubject.value,
      60000, // 60秒超时
      currentRequestCancel.value // 传入取消令牌源
    );

    // 清除进度模拟
    clearInterval(progressInterval);
    loadingProgress.value = 100;

    // 添加AI回答消息
    const aiMessage: Message = {
      id: generateId(),
      type: 'ai',
      content: aiResponse.answerContent,
      timestamp: new Date(),
      questionId: aiResponse.questionId,
      answerId: aiResponse.answerId,
      subject: currentSubject.value
    };

    conv.messages.push(aiMessage);
    
    ElMessage.success('AI回答已生成');
    
    // 实时同步到云端（异步执行，不阻塞用户操作）
    syncAfterMessage(
      questionContent, 
      aiResponse.answerContent,
      aiResponse.questionId,
      aiResponse.answerId
    ).catch(error => {
      console.warn('同步失败但不影响使用:', error);
    });
    
  } catch (error: any) {
    console.error('AI回答失败:', error);
    clearInterval(progressInterval);
    
    // 检查是否为取消请求
    if (axios.isCancel(error)) {
      // 取消请求不显示错误，只重置状态
      return;
    }
    
    let errorMessage = '抱歉，我现在无法回答你的问题，请稍后再试。';
    
    if (error.message.includes('超时')) {
      errorMessage = '请求超时，AI响应时间较长，请稍后重试。你也可以尝试简化问题或分段提问。';
    } else if (error.message.includes('网络')) {
      errorMessage = '网络连接错误，请检查网络连接后重试。';
    }
    
    ElMessage.error(error.message || 'AI服务暂时不可用');
    
    // 添加错误消息到对话中
    const errorMsg: Message = {
      id: generateId(),
      type: 'ai',
      content: errorMessage,
      timestamp: new Date()
    };
    conv.messages.push(errorMsg);
  } finally {
    isLoading.value = false;
    loadingProgress.value = 0;
    currentRequestCancel.value = null;
    await nextTick();
    scrollToBottom();
  }
};

const formatAIResponse = (content: string) => {
  try {
    // 处理LaTeX数学公式
    let formatted = content
      // 处理块级公式 \[...\] 和 $$...$$
      .replace(/\\\[([\s\S]*?)\\\]/g, (match, formula) => {
        try {
          const html = katex.renderToString(formula.trim(), {
            displayMode: true,
            throwOnError: false,
            errorColor: '#ff0000'
          });
          return `<div class="math-block">${html}</div>`;
        } catch (error) {
          console.warn('KaTeX块级公式渲染错误:', error);
          return `<div class="math-block math-error">$$${formula}$$</div>`;
        }
      })
      .replace(/\$\$([\s\S]*?)\$\$/g, (match, formula) => {
        try {
          const html = katex.renderToString(formula.trim(), {
            displayMode: true,
            throwOnError: false,
            errorColor: '#ff0000'
          });
          return `<div class="math-block">${html}</div>`;
        } catch (error) {
          console.warn('KaTeX块级公式渲染错误:', error);
          return `<div class="math-block math-error">$$${formula}$$</div>`;
        }
      })
      // 处理行内公式 \(...\) 和 $...$
      .replace(/\\\((.*?)\\\)/g, (match, formula) => {
        try {
          const html = katex.renderToString(formula.trim(), {
            displayMode: false,
            throwOnError: false,
            errorColor: '#ff0000'
          });
          return `<span class="math-inline">${html}</span>`;
        } catch (error) {
          console.warn('KaTeX行内公式渲染错误:', error);
          return `<span class="math-inline math-error">$${formula}$</span>`;
        }
      })
      .replace(/\$([^$\n]+)\$/g, (match, formula) => {
        try {
          const html = katex.renderToString(formula.trim(), {
            displayMode: false,
            throwOnError: false,
            errorColor: '#ff0000'
          });
          return `<span class="math-inline">${html}</span>`;
        } catch (error) {
          console.warn('KaTeX行内公式渲染错误:', error);
          return `<span class="math-inline math-error">$${formula}$</span>`;
        }
      })
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
  } catch (error) {
    console.error('格式化AI回答时出错:', error);
    // 如果出现错误，返回原始内容
    return content.replace(/\n\n/g, '</p><p>').replace(/\n/g, '<br>');
  }
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

const cancelRequest = () => {
  if (currentRequestCancel.value) {
    currentRequestCancel.value.cancel('用户取消了请求');
    currentRequestCancel.value = null;
  }
  isLoading.value = false;
  loadingProgress.value = 0;
  ElMessage.info('已取消AI请求');
};

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
};

const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Enter') {
    if (event.ctrlKey) {
      // Ctrl+Enter 换行，让默认行为发生
      return;
    } else {
      // Enter 发送消息
      event.preventDefault();
      sendMessage();
    }
  }
};

// 生命周期
onMounted(() => {
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
      
      // 如果有保存的对话，选择第一个作为当前对话
      if (conversations.value.length > 0) {
        currentConversationId.value = conversations.value[0].id;
      } else {
        // 如果没有保存的对话，创建默认对话
        startNewConversation();
      }
    } catch (error) {
      console.error('恢复对话历史失败:', error);
      // 恢复失败时也要创建默认对话
      startNewConversation();
    }
  } else {
    // 没有保存的对话时创建默认对话
    startNewConversation();
  }
  
  // 异步加载并整合后端历史对话
  loadAndMergeBackendHistory();
});

// 保存对话历史到localStorage（只保存本地对话，不保存后端对话）
const saveConversations = () => {
  try {
    const localConversations = conversations.value.filter(conv => !conv.isFromBackend);
    localStorage.setItem('qa-conversations', JSON.stringify(localConversations));
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
  overflow: hidden;
}

/* 主聊天区域 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
  transition: margin-right 0.3s ease;
  height: 100vh;
  overflow: hidden;
}

.chat-expanded {
  margin-right: 0;
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

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sidebar-toggle-btn {
  color: #606266;
}

.sidebar-toggle-btn:hover {
  color: #409eff;
}

/* 侧边栏样式 - 右侧 */
.sidebar {
  width: 320px;
  background: white;
  border-left: 1px solid #e4e7ed;
  transition: all 0.3s ease;
  overflow: hidden;
  position: relative;
  order: 2;
  height: 100vh;
}

.sidebar-collapsed {
  width: 60px;
}

.sidebar-collapsed-trigger {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 60px;
  height: 80px;
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 8px 0 0 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.1);
}

.sidebar-collapsed-trigger:hover {
  background: #f5f7fa;
  box-shadow: -4px 0 12px rgba(0, 0, 0, 0.15);
}

.sidebar-collapsed-trigger .el-icon {
  font-size: 20px;
  color: #606266;
  margin-bottom: 4px;
}

.trigger-tooltip {
  font-size: 12px;
  color: #909399;
  writing-mode: vertical-rl;
  text-orientation: mixed;
}

.sidebar-collapsed-trigger:hover .trigger-tooltip {
  color: #409eff;
}

.sidebar-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fafbfc;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.refresh-btn,
.collapse-btn {
  padding: 4px;
  min-height: auto;
  color: #606266;
}

.refresh-btn:hover,
.collapse-btn:hover {
  color: #409eff;
}

.conversation-list {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.new-chat-btn {
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
  background: white;
}

.conversation-history {
  flex: 1;
  padding: 8px;
  overflow-y: auto;
  background: white;
}

.conversation-item {
  padding: 12px;
  margin-bottom: 4px;
  border-radius: 8px;
  transition: all 0.2s ease;
  border: 1px solid transparent;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
}

.conversation-item:hover {
  background: #f5f7fa;
  border-color: #e4e7ed;
}

.conversation-item.active {
  background: #e6f4ff;
  border-color: #91caff;
}

.conversation-content {
  flex: 1;
  cursor: pointer;
  min-width: 0; /* 允许flex子项收缩 */
}

.conversation-actions {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.conversation-item:hover .conversation-actions {
  opacity: 1;
}

.delete-btn {
  color: #f56c6c;
  padding: 4px;
  min-height: auto;
  font-size: 14px;
}

.delete-btn:hover {
  color: #e55353;
  background-color: rgba(245, 108, 108, 0.1);
}

/* 侧边栏会话项样式 */
.conversation-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 4px;
  gap: 8px;
}

.conversation-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  min-width: 0;
}

.conversation-badges {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
}

.conversation-time {
  font-size: 11px;
  color: #909399;
  font-weight: 400;
  opacity: 0.8;
  transition: all 0.2s ease;
  background: rgba(144, 147, 153, 0.08);
  padding: 2px 6px;
  border-radius: 10px;
  border: 1px solid rgba(144, 147, 153, 0.15);
  display: inline-block;
  line-height: 1.2;
}

.conversation-item:hover .conversation-time {
  opacity: 1;
  background: rgba(64, 158, 255, 0.1);
  border-color: rgba(64, 158, 255, 0.2);
  color: #409eff;
}

.conversation-item.active .conversation-time {
  background: rgba(64, 158, 255, 0.15);
  border-color: rgba(64, 158, 255, 0.3);
  color: #409eff;
  opacity: 1;
}

/* 消息区域 */
.chat-messages {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background: #fafbfc;
  min-height: 0; /* 允许flex子项收缩 */
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
  background: linear-gradient(135deg, #409eff, #67c23a);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
  overflow: hidden; /* 确保图片不会溢出圆形边界 */
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
  border-radius: 16px;
}

.quick-question-tag:hover {
  background: #409eff;
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.message-item {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-start;
}

.message {
  display: flex;
  gap: 12px;
  max-width: calc(100% - 80px); /* 留出头像和间距的空间 */
  min-width: 120px; /* 最小宽度，防止过短的消息 */
  width: fit-content; /* 根据内容自适应宽度 */
}

.user-message {
  flex-direction: row-reverse;
  margin-left: auto;
  justify-content: flex-end;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #67c23a, #85ce61);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.3);
  overflow: hidden; /* 确保图片不会溢出圆形边界 */
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 保持图片比例并填充整个容器 */
  border-radius: 50%;
}

.message-content {
  flex: 1;
  min-width: 0; /* 允许flex子项收缩 */
  max-width: 100%;
}

.user-message .message-content {
  text-align: right;
}

.message-text {
  display: inline-block;
  background: white;
  padding: 12px 16px;
  border-radius: 18px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  line-height: 1.6;
  word-wrap: break-word;
  word-break: break-word;
  position: relative;
  transition: all 0.2s ease;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.message-text:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-1px);
}

/* AI消息气泡样式 */
.ai-message .message-text {
  background: linear-gradient(135deg, #ffffff, #f8fafb);
  border-left: 3px solid #409eff;
  border-radius: 18px 18px 18px 4px;
}

.ai-message .message-text::before {
  content: '';
  position: absolute;
  left: -8px;
  bottom: 8px;
  width: 0;
  height: 0;
  border: 8px solid transparent;
  border-right-color: #409eff;
  border-left: none;
  margin-left: -3px;
}

/* 用户消息气泡样式 */
.user-message .message-text {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: white;
  border-radius: 18px 18px 4px 18px;
  border: none;
}

.user-message .message-text::before {
  content: '';
  position: absolute;
  right: -8px;
  bottom: 8px;
  width: 0;
  height: 0;
  border: 8px solid transparent;
  border-left-color: #409eff;
  border-right: none;
  margin-right: -3px;
}

.message-time {
  font-size: 11px;
  color: #909399;
  margin-top: 6px;
  opacity: 0.7;
  transition: all 0.2s ease;
  background: rgba(144, 147, 153, 0.06);
  padding: 2px 8px;
  border-radius: 12px;
  border: 1px solid rgba(144, 147, 153, 0.12);
  display: inline-block;
  line-height: 1.3;
  font-weight: 400;
  letter-spacing: 0.2px;
}

.user-message .message-time {
  background: rgba(64, 158, 255, 0.08);
  border-color: rgba(64, 158, 255, 0.15);
  color: #409eff;
}

.ai-message .message-time {
  background: rgba(103, 194, 58, 0.08);
  border-color: rgba(103, 194, 58, 0.15);
  color: #67c23a;
}

.message:hover .message-time {
  opacity: 1;
  transform: translateY(-1px);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
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
  opacity: 0;
  transition: opacity 0.2s ease;
}

.message-item:hover .action-buttons {
  opacity: 1;
}

/* 打字指示器 */
.loading-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: fit-content;
  min-width: 200px;
  max-width: 400px;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #ffffff, #f8fafb);
  border-radius: 18px 18px 18px 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border-left: 3px solid #409eff;
  position: relative;
  width: fit-content;
}

.typing-indicator::before {
  content: '';
  position: absolute;
  left: -8px;
  bottom: 8px;
  width: 0;
  height: 0;
  border: 8px solid transparent;
  border-right-color: #409eff;
  border-left: none;
  margin-left: -3px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #409eff;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) { animation-delay: -0.32s; }
.typing-indicator span:nth-child(2) { animation-delay: -0.16s; }

.loading-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 0 16px;
}

.loading-text {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.loading-progress {
  width: 100%;
  min-width: 200px;
}

.cancel-btn {
  color: #f56c6c;
  align-self: flex-start;
  transition: all 0.2s ease;
  border-radius: 16px;
}

.cancel-btn:hover {
  background-color: rgba(245, 108, 108, 0.1);
  transform: translateY(-1px);
}

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0.6);
    opacity: 0.4;
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
  flex-shrink: 0; /* 防止输入区域被压缩 */
  max-height: 200px; /* 限制最大高度 */
}

.input-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.question-input {
  resize: none;
  border-radius: 12px;
  transition: all 0.2s ease;
}

.question-input:focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
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
  border-radius: 20px;
  font-weight: 500;
  transition: all 0.2s ease;
  background: linear-gradient(135deg, #409eff, #66b1ff);
  border: none;
}

.send-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.send-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* 消息项动画 */
.message-item {
  animation: slideInUp 0.3s ease-out;
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 聊天气泡的细节优化 */
.message-text {
  position: relative;
  overflow: hidden;
}

.message-text::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255,255,255,0.1), rgba(255,255,255,0));
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.message-text:hover::after {
  opacity: 1;
}

/* 改进的滚动行为 */
.chat-messages {
  scroll-behavior: smooth;
}

/* 选择文本时的样式 */
.message-text::selection {
  background: rgba(64, 158, 255, 0.2);
}

.user-message .message-text::selection {
  background: rgba(255, 255, 255, 0.3);
}

/* 数学公式样式 */
.math-inline {
  display: inline-block;
  margin: 0 2px;
  vertical-align: middle;
  background: rgba(233, 30, 99, 0.05);
  padding: 2px 4px;
  border-radius: 4px;
  border: 1px solid rgba(233, 30, 99, 0.1);
  transition: all 0.2s ease;
}

.math-inline:hover {
  background: rgba(233, 30, 99, 0.1);
  border-color: rgba(233, 30, 99, 0.2);
}

.math-block {
  margin: 16px 0;
  padding: 16px;
  background: rgba(233, 30, 99, 0.03);
  border: 1px solid rgba(233, 30, 99, 0.1);
  border-left: 4px solid #e91e63;
  border-radius: 8px;
  text-align: center;
  overflow-x: auto;
  transition: all 0.2s ease;
}

.math-block:hover {
  background: rgba(233, 30, 99, 0.05);
  border-color: rgba(233, 30, 99, 0.15);
  box-shadow: 0 2px 8px rgba(233, 30, 99, 0.1);
}

/* KaTeX 样式覆盖 */
.math-inline .katex,
.math-block .katex {
  font-size: inherit !important;
}

.math-inline .katex {
  font-size: 1em !important;
}

.math-block .katex {
  font-size: 1.2em !important;
}

/* 数学公式错误样式 */
.math-error {
  color: #f56c6c !important;
  background: rgba(245, 108, 108, 0.1) !important;
  border-color: rgba(245, 108, 108, 0.3) !important;
  font-family: 'Consolas', 'Monaco', 'Fira Code', monospace;
  font-size: 0.9em;
}

/* 确保KaTeX字体正确显示 */
.katex .katex-mathml {
  display: none;
}

/* 用户消息中的数学公式样式调整 */
.user-message .math-inline {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.2);
  color: inherit;
}

.user-message .math-inline:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.3);
}

.user-message .math-block {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
  border-left-color: rgba(255, 255, 255, 0.4);
  color: inherit;
}

.user-message .math-block:hover {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.3);
}

/* 响应式数学公式 */
@media (max-width: 768px) {
  .math-block {
    margin: 12px 0;
    padding: 12px;
    font-size: 0.9em;
  }
  
  .math-inline {
    font-size: 0.9em;
  }
  
  .math-block .katex {
    font-size: 1em !important;
  }
}

@media (max-width: 480px) {
  .math-block {
    margin: 8px 0;
    padding: 8px;
    font-size: 0.85em;
  }
  
  .math-inline {
    font-size: 0.85em;
    margin: 0 1px;
    padding: 1px 2px;
  }
  
  .math-block .katex {
    font-size: 0.9em !important;
  }
}

.message-text code {
  background: rgba(64, 158, 255, 0.1);
  color: #409eff;
  padding: 3px 8px;
  border-radius: 6px;
  font-family: 'Consolas', 'Monaco', 'Fira Code', monospace;
  font-size: 0.9em;
  font-weight: 500;
}

.user-message .message-text code {
  background: rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.95);
}

/* 长文本样式优化 */
.message-text p {
  margin: 0 0 8px 0;
  line-height: 1.6;
}

.message-text p:last-child {
  margin-bottom: 0;
}

.message-text strong {
  font-weight: 600;
  color: inherit;
}

.message-text em {
  font-style: italic;
  color: inherit;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .qa-container {
    height: 100vh;
    height: 100dvh; /* 动态视口高度，在移动设备上更准确 */
  }
  
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    height: 100vh;
    height: 100dvh;
    z-index: 1000;
    transform: translateX(-100%);
  }
  
  .sidebar:not(.sidebar-collapsed) {
    transform: translateX(0);
  }
  
  .chat-main {
    width: 100%;
    height: 100vh;
    height: 100dvh;
  }
  
  /* 移动端时间显示优化 */
  .conversation-time {
    font-size: 10px;
    padding: 1px 5px;
  }
  
  .conversation-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .conversation-badges {
    order: -1;
    margin-bottom: 2px;
  }
  
  .message-time {
    font-size: 10px;
    padding: 1px 6px;
    margin-top: 4px;
  }
  
  .message {
    max-width: calc(100% - 60px); /* 移动端留更少空间 */
    min-width: 80px; /* 移动端最小宽度更小 */
  }
  
  .message-text {
    padding: 10px 14px;
    font-size: 14px;
    border-radius: 16px;
  }
  
  .ai-message .message-text {
    border-radius: 16px 16px 16px 4px;
  }
  
  .user-message .message-text {
    border-radius: 16px 16px 4px 16px;
  }
  
  .welcome-message {
    padding: 16px;
    flex-direction: column;
    text-align: center;
  }
  
  .quick-questions {
    flex-direction: column;
    align-items: center;
  }
  
  .chat-input {
    padding: 12px 16px;
  }
  
  .loading-container {
    min-width: 150px;
  }
  
  .loading-progress {
    min-width: 150px;
  }
}

@media (max-width: 480px) {
  .message-text {
    padding: 8px 12px;
    font-size: 13px;
  }
  
  .ai-avatar, .user-avatar {
    width: 32px;
    height: 32px;
    font-size: 14px;
  }
  
  .ai-avatar .avatar-image,
  .user-avatar .avatar-image {
    width: 100%;
    height: 100%;
  }
  
  .message {
    gap: 8px;
  }
  
  .chat-messages {
    padding: 16px 12px;
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
