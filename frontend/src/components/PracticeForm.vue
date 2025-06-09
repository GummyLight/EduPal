<template>
  <div class="practice-form">
    <el-header class="navbar">
      <div class="title">智慧教学系统</div>
      <div class="user-info">
        <span>您好，{{ username }} {{ userType === 2 ? '老师' : '同学' }} </span>
<!--        <el-button type="danger" @click="logout">退出登录</el-button>-->
      </div>
    </el-header>

    <el-main>
      <el-card class="filter-card" shadow="always">
        <el-form :inline="true" :model="filters" class="filter-form">
          <el-form-item label="关键词">
            <el-input v-model="filters.keyword" placeholder="请输入关键词" />
          </el-form-item>
          <el-form-item label="难易级别">
            <el-select v-model="filters.difficulty" placeholder="请选择难度" clearable>
              <el-option label="简单" value="简单" />
              <el-option label="中等" value="中等" />
              <el-option label="困难" value="困难" />
            </el-select>
          </el-form-item>
          <el-form-item label="所属学科">
            <el-select v-model="filters.subject" placeholder="请选择学科" clearable>
              <el-option label="数学" value="数学" />
              <el-option label="物理" value="物理" />
              <el-option label="化学" value="化学" />
              <el-option label="语文" value="语文" />
              <el-option label="英语" value="英语" />
              <el-option label="生物" value="生物" />
              <el-option label="历史" value="历史" />
              <el-option label="地理" value="地理" />
              <el-option label="政治" value="政治" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="userType === 2" label="选择班级">
            <el-select v-model="filters.classId" placeholder="请选择班级" clearable @change="handleSelectClass">
              <el-option
                  v-for="classItem in classList"
                  :key="classItem.classId"
                  :label="classItem.className"
                  :value="classItem.classId"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              {{ userType === 2 ? '搜索试卷' : '搜索任务' }}
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <div class="actions">
        <template v-if="userType === 2">
          <el-button type="success" :icon="Plus" @click="handleAdd">添加练习</el-button>
        </template>
        <template v-if="userType === 1">
          <!-- 学生端作业进度条 - 全屏适配版 -->
          <div class="homework-progress-fullwidth">
            <span class="progress-label">📊 作业完成进度</span>
            <div class="progress-bar-container">
              <el-progress 
                :percentage="homeworkProgress.percentage" 
                :stroke-width="12"
                :color="getProgressColor(homeworkProgress.percentage)"
                :show-text="false"
                class="progress-bar-fullwidth"
              />
            </div>
            <span class="progress-text">{{ homeworkProgress.completed }}/{{ homeworkProgress.total }} ({{ homeworkProgress.percentage }}%)</span>
            <div class="progress-details">
              <el-tag type="success" size="small" effect="plain">已批改: {{ homeworkProgress.graded }}</el-tag>
              <el-tag type="info" size="small" effect="plain">已提交: {{ homeworkProgress.submitted }}</el-tag>
              <el-tag type="warning" size="small" effect="plain">未提交: {{ homeworkProgress.unsubmitted }}</el-tag>
            </div>
          </div>
        </template>
      </div>

      <el-card class="table-card" shadow="never">
        <h3>练习列表</h3>
        <el-table :data="filteredData" border style="width: 100%">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="内容" label="练习主题" />
          <el-table-column prop="习题号" label="编号" />
          <el-table-column prop="类型" label="题目类型" />
          <el-table-column prop="难度" label="难度等级" />
          <el-table-column prop="知识点" label="关联知识点" />
          <el-table-column v-if="userType === 2" prop="已提交人数" label="已提交人数" />
          <el-table-column v-if="userType === 2" prop="未提交人数" label="未提交人数" />
          <el-table-column v-if="userType === 2" prop="已批改人数" label="已批改人数" />
          <el-table-column v-if="userType === 2" prop="发布时间" label="发布日期" />
          <el-table-column v-if="userType === 2" prop="截止时间" label="截止日期" />
          <el-table-column v-if="userType === 2" prop="发布人" label="发布教师" />
          <el-table-column v-if="userType === 1" prop="发布时间" label="发布日期" />
          <el-table-column v-if="userType === 1" prop="截止时间" label="截止日期" />
          <el-table-column v-if="userType === 1" prop="我的状态" label="我的状态" />
          <el-table-column v-if="userType === 1" prop="我的分数" label="我的分数">
            <template #default="scope">
              {{ scope.row.我的分数 ?? '--' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" v-if="userType === 2 || userType === 1">
            <template #default="scope">
              <div v-if="userType === 2" class="operation-buttons-horizontal">
                <el-button link size="small" @click="handleViewSubmissions(scope.row)">查看提交</el-button>
                <el-button link size="small" @click="handleEdit(scope.row)">编辑</el-button>
                <el-button link size="small" danger @click="handleDelete(scope.row)">删除</el-button>
              </div>
              <div v-if="userType === 1" class="operation-buttons-horizontal">
                <el-button link size="small" @click="handleGoToPracticeDetail(scope.row)">
                  {{ scope.row.我的状态 === '未提交' ? '开始练习' : '查看详情' }}
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-main>

    <!-- 添加练习对话框 -->
    <el-dialog v-model="showAddDialog" title="添加练习" width="600px">
      <el-form :model="addQuizForm" label-width="100px">
        <el-form-item label="练习标题" required>
          <el-input v-model="addQuizForm.title" placeholder="请输入练习标题" />
        </el-form-item>
        
        <el-form-item label="所属学科" required>
          <el-select v-model="addQuizForm.subject" placeholder="请选择学科" style="width: 100%">
            <el-option label="数学" value="数学" />
            <el-option label="物理" value="物理" />
            <el-option label="化学" value="化学" />
            <el-option label="语文" value="语文" />
            <el-option label="英语" value="英语" />
            <el-option label="生物" value="生物" />
            <el-option label="历史" value="历史" />
            <el-option label="地理" value="地理" />
            <el-option label="政治" value="政治" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="题目类型" required>
          <el-select v-model="addQuizForm.contentType" placeholder="请选择题目类型" style="width: 100%">
            <el-option label="选择题" value="选择题" />
            <el-option label="填空题" value="填空题" />
            <el-option label="判断题" value="判断题" />
            <el-option label="简答题" value="简答题" />
            <el-option label="计算题" value="计算题" />
            <el-option label="综合题" value="综合题" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="难度等级" required>
          <el-select v-model="addQuizForm.difficulty" placeholder="请选择难度" style="width: 100%">
            <el-option label="简单" value="简单" />
            <el-option label="中等" value="中等" />
            <el-option label="困难" value="困难" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="知识点">
          <el-input v-model="addQuizForm.knowledgePoints" 
                    placeholder="请输入知识点，多个知识点用逗号分隔" />
        </el-form-item>
        
        <el-form-item label="练习描述">
          <el-input v-model="addQuizForm.description" 
                    type="textarea" 
                    rows="3"
                    placeholder="请输入练习描述" />
        </el-form-item>
        
        <el-form-item label="截止时间" required>
          <el-date-picker
            v-model="addQuizForm.deadline"
            type="datetime"
            placeholder="请选择截止时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="班级1">
          <el-input v-model="addQuizForm.class1" placeholder="请输入班级1" />
        </el-form-item>
        
        <el-form-item label="班级2">
          <el-input v-model="addQuizForm.class2" placeholder="请输入班级2" />
        </el-form-item>
        
        <el-form-item label="练习文件">
          <el-upload
            class="upload-demo"
            :before-upload="beforeUpload"
            :on-change="handleFileChange"
            :file-list="addFileList"
            :auto-upload="false"
            accept=".pdf,.doc,.docx,.ppt,.pptx,.txt"
            :limit="1"
            :on-exceed="handleExceed"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持 PDF、Word、PPT、TXT 格式文件，最多上传1个文件
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmAddQuiz">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑练习对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑练习" width="600px">
      <el-form :model="editQuizForm" label-width="100px">
        <el-form-item label="练习标题" required>
          <el-input v-model="editQuizForm.title" placeholder="请输入练习标题" />
        </el-form-item>
        
        <el-form-item label="所属学科" required>
          <el-select v-model="editQuizForm.subject" placeholder="请选择学科" style="width: 100%">
            <el-option label="数学" value="数学" />
            <el-option label="物理" value="物理" />
            <el-option label="化学" value="化学" />
            <el-option label="语文" value="语文" />
            <el-option label="英语" value="英语" />
            <el-option label="生物" value="生物" />
            <el-option label="历史" value="历史" />
            <el-option label="地理" value="地理" />
            <el-option label="政治" value="政治" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="题目类型" required>
          <el-select v-model="editQuizForm.contentType" placeholder="请选择题目类型" style="width: 100%">
            <el-option label="选择题" value="选择题" />
            <el-option label="填空题" value="填空题" />
            <el-option label="判断题" value="判断题" />
            <el-option label="简答题" value="简答题" />
            <el-option label="计算题" value="计算题" />
            <el-option label="综合题" value="综合题" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="难度等级" required>
          <el-select v-model="editQuizForm.difficulty" placeholder="请选择难度" style="width: 100%">
            <el-option label="简单" value="简单" />
            <el-option label="中等" value="中等" />
            <el-option label="困难" value="困难" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="知识点">
          <el-input v-model="editQuizForm.knowledgePoints" 
                    placeholder="请输入知识点，多个知识点用逗号分隔" />
        </el-form-item>
        
        <el-form-item label="练习描述">
          <el-input v-model="editQuizForm.description" 
                    type="textarea" 
                    rows="3"
                    placeholder="请输入练习描述" />
        </el-form-item>
        
        <el-form-item label="截止时间" required>
          <el-date-picker
            v-model="editQuizForm.deadline"
            type="datetime"
            placeholder="请选择截止时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="班级1">
          <el-input v-model="editQuizForm.class1" placeholder="请输入班级1" />
        </el-form-item>
        
        <el-form-item label="班级2">
          <el-input v-model="editQuizForm.class2" placeholder="请输入班级2" />
        </el-form-item>
        
        <el-form-item label="练习文件">
          <el-upload
            class="upload-demo"
            :before-upload="beforeUpload"
            :on-change="handleEditFileChange"
            :file-list="editFileList"
            :auto-upload="false"
            accept=".pdf,.doc,.docx,.ppt,.pptx,.txt"
            :limit="1"
            :on-exceed="handleExceed"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持 PDF、Word、PPT、TXT 格式文件，最多上传1个文件。选择新文件将替换原文件。
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmEditQuiz">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, TrendCharts } from '@element-plus/icons-vue';
import axios from 'axios';
import { createQuiz, modifyQuiz, type CreateQuizRequest, type ModifyQuizRequest } from '../api/quiz';
import { uploadFile } from '../api/materialUploadApi';

// API 基地址（根据实际环境配置）
const API_BASE = 'http://localhost:8080';

// 定义类型接口
interface ClassItem {
  classId: string;
  className: string;
}

interface TableDataItem {
  习题号: string | number;
  内容: string;
  科目: string;
  类型: string;
  难度: string;
  知识点: string;
  发布时间: string;
  截止时间: string;
  发布人: string;
  我的状态: string;
  我的分数: number | null;
  习题文件路径: string;
  chapter: string;
  已提交人数: number;
  未提交人数: number;
  已批改人数: number;
  classId: string;
}

// 定义 props
const props = defineProps({
  usertype: {
    type: Number,
    required: true,
  },
  username: {
    type: String,
    required: true,
  },
  userid: {
    type: String,
    required: true,
  },
});

const router = useRouter();
const userType = computed(() => props.usertype);
const username = computed(() => props.username);
const userId = computed(() => props.userid);

// 筛选条件
const filters = ref({
  keyword: '',
  chapter: '',
  difficulty: '',
  subject: '',
  classId: '',
});

// 班级列表（教师用）
const classList = ref<ClassItem[]>([]);

// 表格数据
const tableData = ref<TableDataItem[]>([]);

// 作业进度数据（学生端使用）
const homeworkProgress = ref({
  total: 0,
  completed: 0,
  graded: 0,
  submitted: 0,
  unsubmitted: 0,
  percentage: 0
});

// 状态映射函数（学生端使用）
const mapQuizStatus = (status: number | null) => {
  if (status === null) return '';
  switch (status) {
    case 0:
      return '未提交';
    case 1:
      return '已提交';
    case 2:
      return '已批改';
    default:
      return '未知';
  }
};

// 获取学生测验数据
const fetchStudentQuizzes = async (userId: string) => {
  try {
    const response = await axios.get(`${API_BASE}/quiz/getStudentQuiz`, {
      params: { userId },
    });
    if (response.data.status === 'success') {
      tableData.value = response.data.quizDetails.map((quiz: any) => ({
        习题号: quiz.quizId,
        内容: quiz.title,
        科目: quiz.subject,
        类型: quiz.contentType,
        难度: quiz.difficulty,
        知识点: quiz.knowledgePoints,
        发布时间: quiz.createTime,
        截止时间: quiz.deadline,
        发布人: quiz.teacherName,
        我的状态: mapQuizStatus(quiz.quizStatus),
        我的分数: quiz.score === -1 ? null : quiz.score,
        习题文件路径: '', // 后端未提供，设为空
        chapter: '', // 后端未提供，设为空
        已提交人数: 0, // 学生端无需显示
        未提交人数: 0,
        已批改人数: 0,
        classId: quiz.classId || '', // 假设后端可能返回
      }));
      
      // 加载作业进度数据
      await loadHomeworkProgress(userId);
      
      ElMessage.success('测验列表加载成功');
    } else {
      ElMessage.error('获取测验列表失败');
    }
  } catch (error) {
    ElMessage.error('请求失败，请稍后重试');
    console.error(error);
  }
};

// 加载作业进度数据（学生端）
const loadHomeworkProgress = async (userId: string) => {
  try {
    const response = await axios.get(`${API_BASE}/quiz/getStudentQuiz`, {
      params: { userId },
    });
    
    if (response.data.status === 'success') {
      const quizDetails = response.data.quizDetails;
      const total = quizDetails.length;
      let graded = 0;
      let submitted = 0;
      let unsubmitted = 0;
      
      quizDetails.forEach((quiz: any) => {
        switch (quiz.quizStatus) {
          case 2: // 已批改
            graded++;
            break;
          case 1: // 已提交但未批改
            submitted++;
            break;
          case 0: // 未提交
          case null:
          default:
            unsubmitted++;
            break;
        }
      });
      
      const completed = graded + submitted;
      const percentage = total > 0 ? Math.round((completed / total) * 100) : 0;
      
      homeworkProgress.value = {
        total,
        completed,
        graded,
        submitted,
        unsubmitted,
        percentage
      };
      
      console.log('作业进度数据更新:', homeworkProgress.value);
    }
  } catch (error) {
    console.error('加载作业进度失败:', error);
    // 设置默认值，避免界面显示异常
    homeworkProgress.value = {
      total: 0,
      completed: 0,
      graded: 0,
      submitted: 0,
      unsubmitted: 0,
      percentage: 0
    };
  }
};

// 获取进度条颜色
const getProgressColor = (percentage: number) => {
  return '#67c23a'; // 统一使用绿色
};

// 获取教师测验数据
const fetchTeacherQuizzes = async (userId: string) => {
  try {
    const response = await axios.get(`${API_BASE}/quiz/getTeacherQuiz`, {
      params: { userId },
    });
    if (response.data.status === 'success') {
      tableData.value = response.data.quizzes.map((quiz: any) => ({
        习题号: quiz.quizId,
        内容: quiz.title,
        subjects: quiz.subject,
        类型: quiz.contentType,
        难度: quiz.difficulty,
        知识点: quiz.knowledgePoints,
        发布时间: quiz.createTime,
        截止时间: quiz.deadline,
        发布人: quiz.teacherName,
        已提交人数: quiz.submitNum,
        未提交人数: quiz.unSubmitNum,
        已批改人数: quiz.gradedNum,
        我的状态: '', // 教师端无需显示
        我的分数: null, // 教师端无需显示
        classId: quiz.classId || '', // 后端没有返回
      }));
      ElMessage.success('测验列表加载成功');
    } else {
      ElMessage.error('获取测验列表失败');
    }
  } catch (error) {
    ElMessage.error('请求失败，请稍后重试');
    console.error(error);
  }
};

// 获取教师班级列表
const fetchTeacherClasses = async (userId: string) => {
  try {
    const response = await axios.get(`${API_BASE}/quiz/getTeacherClass`, {
      params: { userId },
    });
    if (response.data.code === 200) {
      classList.value = response.data.data.map((cls: any) => ({
        classId: cls.classId,
        className: cls.className,
      }));
      ElMessage.success('班级列表加载成功');
    } else {
      ElMessage.error('获取班级列表失败');
    }
  } catch (error) {
    ElMessage.error('请求班级列表失败，请稍后重试');
    console.error(error);
  }
};

// 筛选数据
const filteredData = computed(() => {
  return tableData.value.filter(item => {
    const keywordMatch =
        filters.value.keyword === '' ||
        item.内容.includes(filters.value.keyword) ||
        item.习题号.toString().includes(filters.value.keyword);
    const chapterMatch = !filters.value.chapter; // 后端未提供章节，暂时跳过
    const difficultyMatch = !filters.value.difficulty || item.难度 === filters.value.difficulty;
    const subjectMatch = !filters.value.subject || item.科目 === filters.value.subject;
    const classMatch = !filters.value.classId || item.classId === filters.value.classId;
    return keywordMatch && chapterMatch && difficultyMatch && subjectMatch && classMatch;
  });
});

// 搜索
const handleSearch = () => {
  console.log('搜索条件:', filters.value, '用户类型:', userType.value);
  ElMessage.success('正在搜索...');
};

// 添加练习表单数据
const addQuizForm = ref({
  title: '',
  subject: '',
  contentType: '',
  difficulty: '',
  knowledgePoints: '',
  description: '',
  deadline: '',
  class1: '',
  class2: ''
});

// 添加练习对话框可见性
const showAddDialog = ref(false);

// 添加练习
const handleAdd = () => {
  console.log('教师操作: 添加练习');
  // 重置表单
  resetAddForm();
  showAddDialog.value = true;
};

// 重置添加表单
const resetAddForm = () => {
  addQuizForm.value = {
    title: '',
    subject: '',
    contentType: '',
    difficulty: '',
    knowledgePoints: '',
    description: '',
    deadline: '',
    class1: '',
    class2: ''
  };
  addFileList.value = [];
  currentAddFile.value = null;
};

// 重置编辑表单
const resetEditForm = () => {
  editFileList.value = [];
  currentEditFile.value = null;
};

// 确认添加练习
const confirmAddQuiz = async () => {
  try {
    // 验证必填字段
    if (!addQuizForm.value.title.trim()) {
      ElMessage.error('请输入练习标题');
      return;
    }
    if (!addQuizForm.value.subject) {
      ElMessage.error('请选择所属学科');
      return;
    }
    if (!addQuizForm.value.contentType) {
      ElMessage.error('请选择题目类型');
      return;
    }
    if (!addQuizForm.value.difficulty) {
      ElMessage.error('请选择难度等级');
      return;
    }
    if (!addQuizForm.value.deadline) {
      ElMessage.error('请选择截止时间');
      return;
    }

    // 验证截止时间不能早于当前时间
    const deadlineTime = new Date(addQuizForm.value.deadline).getTime();
    const currentTime = new Date().getTime();
    if (deadlineTime <= currentTime) {
      ElMessage.error('截止时间必须晚于当前时间');
      return;
    }

    // 显示上传进度提示
    const loadingMessage = ElMessage({
      message: '正在创建练习，请稍候...',
      type: 'info',
      duration: 0,
      showClose: false,
    });

    let fileUrl = '';
    
    // 如果有文件，先上传文件
    if (currentAddFile.value) {
      try {
        ElMessage.info('正在上传练习文件...');
        fileUrl = await uploadFileToServer(currentAddFile.value);
        console.log('文件上传成功，文件路径:', fileUrl);
      } catch (error) {
        loadingMessage.close();
        ElMessage.error('文件上传失败，请重试');
        console.error('文件上传错误:', error);
        return;
      }
    }

    // 构造创建quiz的请求数据
    const createQuizData: CreateQuizRequest = {
      title: addQuizForm.value.title.trim(),
      subject: addQuizForm.value.subject,
      contentType: addQuizForm.value.contentType,
      difficulty: addQuizForm.value.difficulty,
      knowledgePoints: addQuizForm.value.knowledgePoints.trim() || '',
      description: addQuizForm.value.description.trim() || `练习：${addQuizForm.value.title}`,
      teacherId: userId.value,
      teacherName: username.value,
      createTime: new Date().toISOString().split('T')[0], // 只发送日期部分
      deadline: addQuizForm.value.deadline.split(' ')[0], // 只发送日期部分
      class1: addQuizForm.value.class1.trim() || '',
      class2: addQuizForm.value.class2.trim() || ''
    };

    console.log('创建练习请求数据:', createQuizData);

    // 调用创建quiz API
    const response = await createQuiz(createQuizData);
    
    loadingMessage.close();
    
    if (response.code === 200) {
      ElMessage.success('练习创建成功！');
      showAddDialog.value = false;
      
      // 重置表单和文件
      resetAddForm();
      
      // 重新加载数据
      if (userType.value === 2) {
        await fetchTeacherQuizzes(userId.value);
      }
    } else {
      ElMessage.error('创建失败：' + (response.message || '未知错误'));
    }
  } catch (error: any) {
    ElMessage.error('创建失败：' + (error.message || '请稍后重试'));
    console.error('创建练习失败:', error);
  }
};

// 编辑练习表单数据
const editQuizForm = ref({
  title: '',
  subject: '',
  contentType: '',
  difficulty: '',
  knowledgePoints: '',
  description: '',
  deadline: '',
  class1: '',
  class2: ''
});

// 编辑练习对话框可见性
const showEditDialog = ref(false);

// 当前编辑的练习ID
const currentEditQuizId = ref<number>(0);

// 编辑练习
const handleEdit = (row: TableDataItem) => {
  console.log('教师操作: 编辑练习内容:', row);
  
  // 导航到练习编辑页面，通过查询参数传递数据
  router.push({
    name: 'PracticeEdit',
    params: { exerciseId: row.习题号.toString() },
    query: {
      usertype: userType.value.toString(),
      username: username.value,
      userid: userId.value,
      title: row.内容,
      subject: row.科目,
      contentType: row.类型,
      difficulty: row.难度,
      knowledgePoints: row.知识点,
      deadline: row.截止时间,
      description: `练习：${row.内容}`,
      class1: '', // 这些信息在当前数据结构中没有，保持空
      class2: ''
    }
  });
};

// 确认编辑练习
const confirmEditQuiz = async () => {
  try {
    // 验证必填字段
    if (!editQuizForm.value.title.trim()) {
      ElMessage.error('请输入练习标题');
      return;
    }
    if (!editQuizForm.value.subject) {
      ElMessage.error('请选择所属学科');
      return;
    }
    if (!editQuizForm.value.contentType) {
      ElMessage.error('请选择题目类型');
      return;
    }
    if (!editQuizForm.value.difficulty) {
      ElMessage.error('请选择难度等级');
      return;
    }
    if (!editQuizForm.value.deadline) {
      ElMessage.error('请选择截止时间');
      return;
    }

    // 验证截止时间不能早于当前时间
    const deadlineTime = new Date(editQuizForm.value.deadline).getTime();
    const currentTime = new Date().getTime();
    if (deadlineTime <= currentTime) {
      ElMessage.error('截止时间必须晚于当前时间');
      return;
    }

    // 显示更新进度提示
    const loadingMessage = ElMessage({
      message: '正在更新练习，请稍候...',
      type: 'info',
      duration: 0,
      showClose: false,
    });

    let fileUrl = '';
    
    // 如果有新文件，先上传文件
    if (currentEditFile.value) {
      try {
        ElMessage.info('正在上传新的练习文件...');
        fileUrl = await uploadFileToServer(currentEditFile.value);
        console.log('新文件上传成功，文件路径:', fileUrl);
      } catch (error) {
        loadingMessage.close();
        ElMessage.error('文件上传失败，请重试');
        console.error('文件上传错误:', error);
        return;
      }
    }

    // 构造修改quiz的请求数据
    const modifyQuizData: ModifyQuizRequest = {
      quizId: currentEditQuizId.value,
      title: editQuizForm.value.title.trim(),
      subject: editQuizForm.value.subject,
      contentType: editQuizForm.value.contentType,
      difficulty: editQuizForm.value.difficulty,
      knowledgePoints: editQuizForm.value.knowledgePoints.trim() || '',
      description: editQuizForm.value.description.trim() || `练习：${editQuizForm.value.title}`,
      teacherId: userId.value,
      teacherName: username.value,
      createTime: new Date().toISOString().split('T')[0], // 只发送日期部分
      deadline: editQuizForm.value.deadline.split(' ')[0], // 只发送日期部分
      class1: editQuizForm.value.class1.trim() || '',
      class2: editQuizForm.value.class2.trim() || ''
    };

    console.log('编辑练习请求数据:', modifyQuizData);

    // 调用修改quiz API
    const response = await modifyQuiz(modifyQuizData);
    
    loadingMessage.close();
    
    if (response.code === 200) {
      ElMessage.success('练习修改成功！');
      showEditDialog.value = false;
      
      // 重置文件列表
      resetEditForm();
      
      // 重新加载数据
      if (userType.value === 2) {
        await fetchTeacherQuizzes(userId.value);
      }
    } else {
      ElMessage.error('修改失败：' + (response.message || '未知错误'));
    }
  } catch (error: any) {
    ElMessage.error('修改失败：' + (error.message || '请稍后重试'));
    console.error('编辑练习失败:', error);
  }
};

// 删除练习
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定删除练习《${row.内容}》吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });
    const response = await axios.post(`${API_BASE}/quiz/deleteQuiz`, null, {
      params: { quizId: row.习题号 },
    });
    if (response.data.code === 200) {
      tableData.value = tableData.value.filter(item => item.习题号 !== row.习题号);
      ElMessage.success('练习删除成功！');
    } else {
      ElMessage.error('删除失败：' + response.data.message);
    }
  } catch (error) {
    if (error === 'cancel') {
      ElMessage.info('已取消删除。');
    } else {
      ElMessage.error('删除失败，请稍后重试');
      console.error(error);
    }
  }
};

// 退出登录
const logout = () => {
  console.log('退出登录');
  router.push('/login');
  ElMessage.info('您已退出登录。');
};



// 选择班级
const handleSelectClass = () => {
  console.log('教师操作: 选择了班级', filters.value.classId);
  handleSearch();
};

// 查看学生提交
const handleViewSubmissions = (row: any) => {
  console.log('教师操作: 查看学生提交情况，练习ID:', row.习题号);
  router.push({
    name: 'TeacherHomeworkReview',
    params: { exerciseId: row.习题号, exerciseTitle: row.内容 },
  });
};

// 进入练习详情
const handleGoToPracticeDetail = (row: any) => {
  console.log('学生操作: 进入练习详情，练习ID:', row.习题号, '行数据:', row);
  router.push({
    name: 'PracticeDetail',
    params: { exerciseId: row.习题号 },
    state: { exercise: { ...row } }, // 深拷贝确保数据完整传递
  });
};

// 文件上传相关变量
const addFileList = ref<any[]>([]);
const editFileList = ref<any[]>([]);
const currentAddFile = ref<File | null>(null);
const currentEditFile = ref<File | null>(null);

// 文件上传前的验证
const beforeUpload = (file: File) => {
  console.log('验证上传文件:', file.name, file.type, file.size);
  
  // 检查文件类型
  const validTypes = [
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'application/vnd.ms-powerpoint',
    'application/vnd.openxmlformats-officedocument.presentationml.presentation',
    'text/plain'
  ];
  
  const isValidType = validTypes.includes(file.type);
  if (!isValidType) {
    ElMessage.error(`不支持的文件格式: ${file.type}。只支持 PDF、Word、PPT、TXT 格式！`);
    return false;
  }
  
  // 检查文件大小 (限制为50MB)
  const maxSize = 50 * 1024 * 1024; // 50MB
  if (file.size > maxSize) {
    ElMessage.error(`文件大小超出限制！文件大小: ${Math.round(file.size / 1024 / 1024)}MB，最大允许: 50MB`);
    return false;
  }
  
  // 检查文件名长度
  if (file.name.length > 100) {
    ElMessage.error('文件名过长，请重命名后再上传（最长100个字符）');
    return false;
  }
  
  console.log('文件验证通过:', file.name);
  return false; // 阻止自动上传，我们手动控制上传
};

// 添加练习时文件变化处理
const handleFileChange = (file: any, fileList: any[]) => {
  console.log('添加练习 - 文件变化:', file, fileList.length);
  if (fileList.length > 0) {
    currentAddFile.value = file.raw;
    addFileList.value = [file];
    ElMessage.success(`已选择文件: ${file.name}`);
  } else {
    currentAddFile.value = null;
    addFileList.value = [];
  }
};

// 编辑练习时文件变化处理
const handleEditFileChange = (file: any, fileList: any[]) => {
  console.log('编辑练习 - 文件变化:', file, fileList.length);
  if (fileList.length > 0) {
    currentEditFile.value = file.raw;
    editFileList.value = [file];
    ElMessage.success(`已选择新文件: ${file.name}，将替换原文件`);
  } else {
    currentEditFile.value = null;
    editFileList.value = [];
  }
};

// 文件超出限制处理
const handleExceed = () => {
  ElMessage.warning('每个练习最多只能上传1个文件！如需上传新文件，请先移除当前文件。');
};

// 生成唯一文件ID - 使用简单的时间戳
const generateFileId = () => {
  return Date.now().toString();
};

// 上传文件到服务器
const uploadFileToServer = async (file: File): Promise<string> => {
  try {
    const fileId = generateFileId();
    const toPath = 'quiz/'; 
    const path = '/tmp'; // 使用有权限的临时目录
    
    console.log('准备上传练习文件:', {
      fileName: file.name,
      fileSize: Math.round(file.size / 1024) + ' KB',
      fileType: file.type,
      fileId,
      toPath,
      path
    });
    
    const uploadResponse = await uploadFile(file, fileId, toPath, path);
    console.log('练习文件上传响应:', uploadResponse);
    
    if (uploadResponse.code === 200) {
      const fileExtension = file.name.substring(file.name.lastIndexOf('.'));
      const fullPath = `${toPath}${fileId}${fileExtension}`;
      console.log('练习文件上传成功，服务器路径:', fullPath);
      return fullPath;
    } else {
      throw new Error(uploadResponse.message || '文件上传失败');
    }
  } catch (error: any) {
    console.error('练习文件上传失败详情:', error);
    
    // 根据错误类型提供更具体的错误信息
    let errorMessage = '文件上传失败';
    if (error.message) {
      errorMessage = error.message;
    } else if (error.response?.status === 500) {
      errorMessage = '服务器内部错误，请检查文件格式或稍后重试';
    } else if (error.response?.status === 413) {
      errorMessage = '文件太大，请选择较小的文件';
    } else if (!navigator.onLine) {
      errorMessage = '网络连接失败，请检查网络后重试';
    }
    
    throw new Error(errorMessage);
  }
};

// 组件挂载时加载数据
onMounted(() => {
  if (userType.value === 1) {
    fetchStudentQuizzes(userId.value);
  } else if (userType.value === 2) {
    fetchTeacherQuizzes(userId.value);
    fetchTeacherClasses(userId.value);
  }
});
</script>

<style scoped>
.practice-form {
  height: 100vh;
  display: flex;
  flex-direction: column;
}
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: #409EFF;
  color: white;
  border-bottom: 1px solid #ddd;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}
.user-info span {
  color: white;
}
.title {
  font-weight: bold;
  font-size: 18px;
  color: white;
}
.el-main {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
  background-color: #f4f6f8;
}
.filter-card {
  margin-bottom: 15px;
  background-color: #fff;
}
.filter-form .el-form-item {
  margin-bottom: 10px;
}

/* 作业进度条样式 */
.progress-card {
  margin-bottom: 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.progress-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: white;
}

.progress-summary {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.1);
  padding: 4px 12px;
  border-radius: 15px;
  backdrop-filter: blur(10px);
}

.progress-content {
  background: rgba(255, 255, 255, 0.1);
  padding: 20px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.progress-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.progress-text {
  font-size: 16px;
  font-weight: 500;
  color: white;
}

.progress-percentage {
  font-size: 24px;
  font-weight: bold;
  color: #FFD700;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.homework-progress-bar {
  margin-bottom: 15px;
}

.progress-detail {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.progress-detail .el-tag {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  backdrop-filter: blur(5px);
}

.progress-detail .el-tag.el-tag--success {
  background: rgba(103, 194, 58, 0.3);
  border-color: rgba(103, 194, 58, 0.5);
}

.progress-detail .el-tag.el-tag--info {
  background: rgba(144, 147, 153, 0.3);
  border-color: rgba(144, 147, 153, 0.5);
}

.progress-detail .el-tag.el-tag--warning {
  background: rgba(230, 162, 60, 0.3);
  border-color: rgba(230, 162, 60, 0.5);
}

.actions {
  margin: 10px 0 15px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

/* 学生端操作区域 - 全屏适配版进度条 */
.homework-progress-fullwidth {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border: 1px solid #dee2e6;
  border-radius: 8px;
  font-size: 14px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 95vw;
  flex-wrap: wrap;
}

.progress-label {
  color: #495057;
  font-weight: 600;
  white-space: nowrap;
  min-width: 120px;
  font-size: 15px;
}

.progress-bar-container {
  flex: 1;
  min-width: 200px;
  max-width: 60%;
}

.progress-bar-fullwidth {
  width: 100%;
}

.progress-text {
  color: #409EFF;
  font-weight: 700;
  white-space: nowrap;
  min-width: 120px;
  font-size: 15px;
}

.progress-details {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.progress-details .el-tag {
  font-size: 12px;
  padding: 3px 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .homework-progress-fullwidth {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    max-width: 100%;
    padding: 16px;
  }
  
  .progress-bar-container {
    width: 100%;
    max-width: 100%;
  }
  
  .progress-details {
    width: 100%;
    justify-content: flex-start;
  }
}
.table-card {
  background-color: #fff;
}
.table-card h3 {
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: 600;
}
.el-button [class*="el-icon-"] + span {
  margin-left: 5px;
}
.operation-buttons-horizontal {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-start;
}
</style>