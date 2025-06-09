<template>
  <div class="teacher-qa-container">
    <!-- 主内容区域 -->
    <div class="qa-main" :class="{ 'qa-expanded': sidebarCollapsed }">
      <!-- 头部 -->
      <div class="qa-header">
        <div class="header-title">
          <el-icon><UserFilled /></el-icon>
          <h2>教师智能答疑中心</h2>
        </div>
        <div class="header-actions">
          <div v-if="isSyncing" class="sync-indicator">
            <el-icon class="sync-icon"><Loading /></el-icon>
            <span class="sync-text">同步中</span>
          </div>
          <el-button 
            type="primary" 
            :icon="Refresh" 
            @click="refreshQuestions"
            :loading="isLoading"
          >
            刷新问题
          </el-button>
        </div>
      </div>

      <!-- 问题列表区域 -->
      <div class="questions-list" ref="questionsContainer">
        <!-- 空状态 -->
        <div v-if="questions.length === 0 && !isLoading" class="empty-state">
          <div class="empty-icon">
            <el-icon><DocumentRemove /></el-icon>
          </div>
          <div class="empty-content">
            <h3>暂无学生提问</h3>
            <p>当学生将问题转交给您时，问题会显示在这里</p>
            <el-button type="primary" @click="refreshQuestions">
              <el-icon><Refresh /></el-icon>
              刷新问题列表
            </el-button>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="isLoading" class="loading-state">
          <el-icon class="loading-icon"><Loading /></el-icon>
          <p>正在加载学生问题...</p>
        </div>

        <!-- 问题卡片 -->
        <div v-for="question in questions" :key="question.studentClass" class="question-card">
          <!-- 学生信息头部 -->
          <div class="question-header">
            <div class="student-info">
              <div class="student-avatar">
                <img :src="userIconDefault" :alt="question.questionId" class="avatar-image" />
              </div>
              <div class="student-details">
                <h4>{{ question.questionId }}</h4>
                <div class="student-meta">
                  <span class="student-id">学号: {{ question.studentName }}</span>
                  <span class="student-class">班级: {{ question.studentId }}</span>
                </div>
              </div>
            </div>
            <div class="question-meta">
              <el-tag type="info" size="small">{{ formatTime(new Date(question.transferTime)) }}</el-tag>
            </div>
          </div>

          <!-- 问题内容 -->
          <div class="question-content">
            <div class="question-text" v-html="formatQuestionContent(question.questionContent)"></div>
          </div>

          <!-- 教师答案区域 -->
          <div class="answers-section">
            <!-- 已有答案 -->
            <div v-if="question.teacherAnswers.length > 0" class="existing-answers">
              <h5>历史回答:</h5>
              <div 
                v-for="(answer, index) in question.teacherAnswers" 
                :key="index" 
                class="answer-item"
                :class="getAnswerClass(answer.answerType)"
              >
                <div class="answer-header">
                  <div class="answer-type-badge" :class="getAnswerBadgeClass(answer.answerType)">
                    <el-icon v-if="answer.answerType === 0"><Monitor /></el-icon>
                    <el-icon v-else><UserFilled /></el-icon>
                    <span>{{ getAnswerTypeName(answer.answerType) }}</span>
                  </div>
                  <div class="answer-time">{{ formatTime(new Date(answer.answerTime)) }}</div>
                </div>
                <div class="answer-content" v-html="formatAnswerContent(answer.answerContent)"></div>
              </div>
            </div>

            <!-- 回答输入区域 -->
            <div class="answer-input-section">
              <h5 v-if="question.teacherAnswers.length === 0">回答学生问题:</h5>
              <h5 v-else>补充回答:</h5>
              
              <el-input
                v-model="answerInputs[question.studentClass]"
                type="textarea"
                :rows="4"
                placeholder="请输入您的回答..."
                class="answer-textarea"
                @keydown="handleAnswerKeydown($event, question.studentClass)"
              />
              
              <div class="answer-actions">
                <div class="input-tools">
                  <span class="input-hint">Ctrl+Enter 换行，Enter 发送回答</span>
                </div>
                <el-button
                  type="primary"
                  @click="submitAnswer(question.studentClass)"
                  :loading="submittingAnswers[question.studentClass]"
                  :disabled="!answerInputs[question.studentClass]?.trim()"
                >
                  <el-icon><Promotion /></el-icon>
                  发送回答
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 侧边栏 - 统计信息（右侧） -->
    <div class="sidebar" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <!-- 折叠状态的快速切换按钮 -->
      <div v-if="sidebarCollapsed" class="sidebar-collapsed-trigger" @click="toggleSidebar">
        <el-icon><DataAnalysis /></el-icon>
        <span class="trigger-tooltip">统计</span>
      </div>
      
      <!-- 展开状态的完整内容 -->
      <div v-else class="sidebar-content">
        <div class="sidebar-header">
          <h3>答疑统计</h3>
          <div class="header-actions">
            <el-button 
              text 
              :icon="Operation" 
              @click="toggleSidebar"
              class="collapse-btn"
            >
            </el-button>
          </div>
        </div>
        
        <div class="stats-content">
          <!-- 基本统计 -->
          <div class="stats-card">
            <h4>今日统计</h4>
            <div class="stats-item">
              <span class="stats-label">待回答问题:</span>
              <span class="stats-value primary">{{ unansweredCount }}</span>
            </div>
            <div class="stats-item">
              <span class="stats-label">已回答问题:</span>
              <span class="stats-value success">{{ answeredCount }}</span>
            </div>
            <div class="stats-item">
              <span class="stats-label">总问题数:</span>
              <span class="stats-value">{{ questions.length }}</span>
            </div>
          </div>

          <!-- 学科分布 -->
          <div class="stats-card">
            <h4>学科分布</h4>
            <div v-for="(count, subject) in subjectStats" :key="subject" class="stats-item">
              <span class="stats-label">{{ getSubjectName(subject) }}:</span>
              <span class="stats-value">{{ count }}</span>
            </div>
          </div>

          <!-- 快捷操作 -->
          <div class="quick-actions">
            <el-button 
              type="primary" 
              :icon="Refresh" 
              @click="refreshQuestions"
              :loading="isLoading"
              style="width: 100%;"
            >
              刷新问题
            </el-button>
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
  UserFilled, Loading, Refresh, DocumentRemove, 
  Promotion, DataAnalysis, Operation, Monitor
} from '@element-plus/icons-vue';
// 导入教师相关API
import { TeacherViewQuestions, TeacherAnswer, type QA, type AnswerDetail } from '../api/teacher';
// 导入用户默认头像
import userIconDefault from '../assets/userIconDefault.jpg';
// 导入KaTeX用于LaTeX渲染
import * as katex from 'katex';
import 'katex/dist/katex.min.css';

// Props定义
interface Props {
  usertype: number;
  username: string;
  userid: string;
}

const props = defineProps<Props>();

// 响应式数据
const questions = ref<QA[]>([]);
const isLoading = ref(false);
const isSyncing = ref(false);
const sidebarCollapsed = ref(false);
const questionsContainer = ref<HTMLElement>();

// 回答相关状态
const answerInputs = ref<Record<string, string>>({});
const submittingAnswers = ref<Record<string, boolean>>({});

// 教师信息
const currentTeacher = ref({
  teacherId: props.userid,
  teacherName: props.username
});

// 计算属性
const unansweredCount = computed(() => {
  return questions.value.filter((q: QA) => q.teacherAnswers.length === 0).length;
});

const answeredCount = computed(() => {
  return questions.value.filter((q: QA) => q.teacherAnswers.length > 0).length;
});

const subjectStats = computed(() => {
  const stats: Record<string, number> = {};
  questions.value.forEach((q: QA) => {
    // 这里需要根据问题内容或其他字段推断学科，暂时使用默认值
    const subject = 'other'; // 可以根据实际情况修改
    stats[subject] = (stats[subject] || 0) + 1;
  });
  return stats;
});

// 方法
const refreshQuestions = async () => {
  if (isLoading.value) return;
  
  isLoading.value = true;
  try {
    const response = await TeacherViewQuestions({
      teacherId: currentTeacher.value.teacherId
    });
    
    if (response.status === 'success') {
      questions.value = response.questionSet.sort((a, b) => 
        new Date(b.transferTime).getTime() - new Date(a.transferTime).getTime()
      );
      
      // 初始化回答输入框
      questions.value.forEach((q: QA) => {
        if (!answerInputs.value[q.studentClass]) {
          answerInputs.value[q.studentClass] = '';
        }
      });
      
      ElMessage.success(`成功加载 ${questions.value.length} 个学生问题`);
    } else {
      ElMessage.error(response.message || '加载问题失败');
    }
  } catch (error: any) {
    console.error('加载问题失败:', error);
    ElMessage.error(error.message || '加载问题失败');
  } finally {
    isLoading.value = false;
  }
};

const submitAnswer = async (questionId: string) => {
  const answerContent = answerInputs.value[questionId]?.trim();
  if (!answerContent) {
    ElMessage.warning('请输入回答内容');
    return;
  }

  submittingAnswers.value[questionId] = true;
  
  try {
    const response = await TeacherAnswer({
      teacherId: currentTeacher.value.teacherId,
      questionId: questionId,
      answerContent: answerContent
    });

    if (response.code === 200) {
      ElMessage.success('回答提交成功');
      
      // 清空输入框
      answerInputs.value[questionId] = '';
      
      // 刷新问题列表以获取最新的回答
      await refreshQuestions();
    } else {
      ElMessage.error(response.message || '回答提交失败');
    }
  } catch (error: any) {
    console.error('提交回答失败:', error);
    ElMessage.error(error.message || '回答提交失败');
  } finally {
    submittingAnswers.value[questionId] = false;
  }
};

const formatQuestionContent = (content: string) => {
  return formatMathContent(content);
};

const formatAnswerContent = (content: string) => {
  return formatMathContent(content);
};

const formatMathContent = (content: string) => {
  try {
    // 处理LaTeX数学公式
    let formatted = content
      // 处理块级公式 \[...\] 和 $$...$$
      .replace(/\\\[([\s\S]*?)\\\]/g, (match, formula) => {
        try {
          const html = katex.renderToString(formula.trim(), {
            displayMode: true,
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
    console.error('格式化内容时出错:', error);
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

const getSubjectName = (subject: string): string => {
  const subjectNames: Record<string, string> = {
    // 英文到中文的映射（向后兼容）
    'math': '数学',
    'physics': '物理', 
    'chemistry': '化学',
    'chem': '化学',
    'english': '英语',
    'chinese': '语文',
    'biology': '生物',
    'history': '历史',
    'geography': '地理',
    'politics': '政治',
    'programming': '编程',
    'prog': '编程',
    'other': '其他',
    // 中文到中文的映射（新标准）
    '数学': '数学',
    '物理': '物理',
    '英语': '英语',
    '化学': '化学',
    '语文': '语文',
    '生物': '生物',
    '历史': '历史',
    '地理': '地理',
    '政治': '政治',
    '编程': '编程',
    '其他': '其他'
  };
  return subjectNames[subject] || subject;
};

const getAnswerTypeName = (answerType: number): string => {
  return answerType === 0 ? 'AI回答' : '教师回答';
};

const getAnswerClass = (answerType: number): string => {
  return answerType === 0 ? 'ai-answer' : 'teacher-answer';
};

const getAnswerBadgeClass = (answerType: number): string => {
  return answerType === 0 ? 'ai-badge' : 'teacher-badge';
};

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value;
};

const handleAnswerKeydown = (event: KeyboardEvent, questionId: string) => {
  if (event.key === 'Enter') {
    if (event.ctrlKey) {
      // Ctrl+Enter 换行，让默认行为发生
      return;
    } else {
      // Enter 提交回答
      event.preventDefault();
      submitAnswer(questionId);
    }
  }
};

const scrollToBottom = () => {
  if (questionsContainer.value) {
    questionsContainer.value.scrollTop = questionsContainer.value.scrollHeight;
  }
};

// 生命周期
onMounted(() => {
  refreshQuestions();
});
</script>

<style scoped>
.teacher-qa-container {
  display: flex;
  height: 100vh;
  background: #f5f7fa;
  overflow: hidden;
}

/* 主内容区域 */
.qa-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
  transition: margin-right 0.3s ease;
  height: 100vh;
  overflow: hidden;
}

.qa-expanded {
  margin-right: 0;
}

.qa-header {
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
  color: #67c23a;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 同步状态指示器 */
.sync-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 2px 6px;
  background: #e6f7ff;
  border: 1px solid #91d5ff;
  border-radius: 4px;
  font-size: 12px;
  color: #1890ff;
}

.sync-icon {
  animation: rotate 1s linear infinite;
  font-size: 12px;
}

.sync-text {
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 问题列表区域 */
.questions-list {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background: #fafbfc;
  min-height: 0;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  color: #c0c4cc;
  margin-bottom: 16px;
}

.empty-content h3 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 20px;
}

.empty-content p {
  margin: 0 0 24px 0;
  color: #606266;
  font-size: 14px;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  text-align: center;
}

.loading-icon {
  font-size: 32px;
  color: #409eff;
  margin-bottom: 12px;
  animation: rotate 1s linear infinite;
}

.loading-state p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

/* 问题卡片 */
.question-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 16px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.question-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

/* 学生信息头部 */
.question-header {
  padding: 16px 20px;
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.student-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.student-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #67c23a;
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.3);
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.student-details h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.student-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #909399;
}

.question-meta {
  display: flex;
  align-items: center;
}

/* 问题内容 */
.question-content {
  padding: 20px;
}

.question-text {
  line-height: 1.6;
  color: #303133;
  font-size: 14px;
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

/* 答案区域 */
.answers-section {
  padding: 0 20px 20px 20px;
}

.existing-answers {
  margin-bottom: 16px;
}

.existing-answers h5 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #67c23a;
  display: flex;
  align-items: center;
  gap: 8px;
}

.existing-answers h5::before {
  content: '';
  width: 4px;
  height: 16px;
  background: #67c23a;
  border-radius: 2px;
}

.answer-item {
  background: #f0f9ff;
  border: 1px solid #bae7ff;
  border-radius: 8px;
  padding: 12px 16px;
  margin-bottom: 8px;
  border-left: 4px solid #67c23a;
  transition: all 0.2s ease;
}

.answer-item.ai-answer {
  background: #fef7e0;
  border: 1px solid #ffd666;
  border-left: 4px solid #faad14;
}

.answer-item.teacher-answer {
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  border-left: 4px solid #52c41a;
}

.answer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.answer-type-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.answer-type-badge.ai-badge {
  background: #fff7e6;
  color: #fa8c16;
  border: 1px solid #ffd591;
}

.answer-type-badge.teacher-badge {
  background: #f6ffed;
  color: #389e0d;
  border: 1px solid #95de64;
}

.answer-type-badge .el-icon {
  font-size: 14px;
}

.answer-content {
  line-height: 1.6;
  color: #303133;
  font-size: 14px;
  margin-bottom: 8px;
}

.answer-time {
  font-size: 12px;
  color: #67c23a;
  text-align: right;
}

/* 回答输入区域 */
.answer-input-section h5 {
  margin: 16px 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 8px;
}

.answer-input-section h5::before {
  content: '';
  width: 4px;
  height: 16px;
  background: #409eff;
  border-radius: 2px;
}

.answer-textarea {
  margin-bottom: 12px;
}

.answer-textarea :deep(.el-textarea__inner) {
  border-radius: 8px;
  resize: none;
  transition: all 0.2s ease;
}

.answer-textarea :deep(.el-textarea__inner):focus {
  border-color: #67c23a;
  box-shadow: 0 0 0 2px rgba(103, 194, 58, 0.1);
}

.answer-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.input-tools {
  display: flex;
  align-items: center;
}

.input-hint {
  font-size: 12px;
  color: #909399;
}

/* 侧边栏样式 */
.sidebar {
  width: 280px;
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
  color: #67c23a;
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

.collapse-btn {
  padding: 4px;
  min-height: auto;
  color: #606266;
}

.collapse-btn:hover {
  color: #67c23a;
}

/* 统计内容 */
.stats-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

.stats-card {
  background: #f8f9fa;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.stats-card h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 8px;
}

.stats-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.stats-item:last-child {
  margin-bottom: 0;
}

.stats-label {
  font-size: 13px;
  color: #606266;
}

.stats-value {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.stats-value.primary {
  color: #409eff;
}

.stats-value.success {
  color: #67c23a;
}

.quick-actions {
  margin-top: 16px;
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

/* 数学公式错误样式 */
.math-error {
  color: #f56c6c !important;
  background: rgba(245, 108, 108, 0.1) !important;
  border-color: rgba(245, 108, 108, 0.3) !important;
  font-family: 'Consolas', 'Monaco', 'Fira Code', monospace;
  font-size: 0.9em;
}

/* 代码样式 */
code {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
  padding: 3px 8px;
  border-radius: 6px;
  font-family: 'Consolas', 'Monaco', 'Fira Code', monospace;
  font-size: 0.9em;
  font-weight: 500;
}

/* 文本格式样式 */
strong {
  font-weight: 600;
  color: inherit;
}

em {
  font-style: italic;
  color: inherit;
}

/* 段落样式 */
p {
  margin: 0 0 8px 0;
  line-height: 1.6;
}

p:last-child {
  margin-bottom: 0;
}

/* 滚动条样式 */
.questions-list::-webkit-scrollbar,
.stats-content::-webkit-scrollbar {
  width: 4px;
}

.questions-list::-webkit-scrollbar-track,
.stats-content::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.questions-list::-webkit-scrollbar-thumb,
.stats-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}

.questions-list::-webkit-scrollbar-thumb:hover,
.stats-content::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .teacher-qa-container {
    height: 100vh;
    height: 100dvh;
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
  
  .qa-main {
    width: 100%;
    height: 100vh;
    height: 100dvh;
  }
  
  .question-card {
    margin-bottom: 12px;
    border-radius: 8px;
  }
  
  .question-header {
    padding: 12px 16px;
  }
  
  .student-avatar {
    width: 40px;
    height: 40px;
  }
  
  .question-content {
    padding: 16px;
  }
  
  .answers-section {
    padding: 0 16px 16px 16px;
  }
}

@media (max-width: 480px) {
  .qa-header {
    padding: 12px 16px;
  }
  
  .header-title h2 {
    font-size: 16px;
  }
  
  .questions-list {
    padding: 16px 12px;
  }
  
  .question-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .student-meta {
    flex-direction: column;
    gap: 4px;
  }
}
</style>
