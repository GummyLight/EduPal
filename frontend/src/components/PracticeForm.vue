<template>
  <div class="practice-form">
    <el-header class="navbar">
      <div class="title">智慧教学系统</div>
      <div class="user-info">
        <span>您好，{{ username }} {{ userType === 2 ? '老师' : '同学' }} </span>
        <el-button type="danger" @click="logout">退出登录</el-button>
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
              <el-option label="简单" value="easy" />
              <el-option label="中等" value="medium" />
              <el-option label="困难" value="hard" />
            </el-select>
          </el-form-item>
          <el-form-item label="所属学科">
            <el-select v-model="filters.subject" placeholder="请选择学科" clearable>
              <el-option label="数学" value="math" />
              <el-option label="物理" value="physics" />
              <el-option label="化学" value="chemistry" />
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
          <el-button type="success" icon="el-icon-plus" @click="handleAdd">添加练习</el-button>
          <el-button type="primary" icon="el-icon-pie-chart" @click="handleViewAnalytics">查看学情分析</el-button>
        </template>
        <template v-if="userType === 1">
          <el-button type="primary" icon="el-icon-data-analysis" @click="handleViewProgress">查看学习进度</el-button>
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
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';

// API 基地址（根据实际环境配置）
const API_BASE = 'http://localhost:8080';

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
const classList = ref([]);

// 表格数据
const tableData = ref([]);

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
      ElMessage.success('测验列表加载成功');
    } else {
      ElMessage.error('获取测验列表失败');
    }
  } catch (error) {
    ElMessage.error('请求失败，请稍后重试');
    console.error(error);
  }
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

// 添加练习
const handleAdd = () => {
  console.log('教师操作: 添加练习');
  router.push({ name: 'PracticeEdit' });
};

// 编辑练习
const handleEdit = (row: any) => {
  console.log('教师操作: 编辑练习内容:', row);
  router.push({
    name: 'PracticeEdit',
    params: { exerciseId: row.习题号 },
    state: { exercise: { ...row } }, // 通过 state 传递完整 row 数据
  });
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

// 查看学情分析
const handleViewAnalytics = () => {
  console.log('教师操作: 查看学情分析');
  ElMessage.info('正在查看学情分析...');
};

// 选择班级
const handleSelectClass = () => {
  console.log('教师操作: 选择了班级', filters.value.classId);
  handleSearch();
};

// 查看学习进度
const handleViewProgress = () => {
  console.log('学生操作: 查看学习进度');
  ElMessage.info('正在查看学习进度...');
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
.actions {
  margin: 10px 0 15px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
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