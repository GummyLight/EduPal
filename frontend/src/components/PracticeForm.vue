<template>
  <div class="practice-form">
    <el-header class="navbar">
      <div class="title">智慧教学系统</div>
      <div class="user-info">
        <span>您好，{{ userType === 'teacher' ? '老师' : '同学' }} ({{ userType }})</span>
        <el-button type="danger" @click="logout">退出登录</el-button>
      </div>
    </el-header>

    <el-main>
      <el-card class="filter-card" shadow="always">
        <el-form :inline="true" :model="filters" class="filter-form">
          <el-form-item label="关键词">
            <el-input v-model="filters.keyword" placeholder="请输入关键词" />
          </el-form-item>
          <el-form-item label="章节">
            <el-select v-model="filters.chapter" placeholder="请选择章节" clearable>
              <el-option label="第一章" value="1" />
              <el-option label="第二章" value="2" />
              <el-option label="第三章" value="3" />
            </el-select>
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
              <el-option label="物理" CHEMISTRY>化 </el-option>
            </el-select>
          </el-form-item>
          <el-form-item v-if="userType === 'teacher'" label="选择班级">
            <el-select v-model="filters.classId" placeholder="请选择班级" clearable @change="handleSelectClass">
              <el-option label="高一(1)班" value="class1" />
              <el-option label="高一(2)班" value="class2" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              {{ userType === 'teacher' ? '搜索试卷' : '搜索任务' }}
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <div class="actions">
        <template v-if="userType === 'teacher'">
          <el-button type="success" icon="el-icon-plus" @click="handleAdd">添加练习</el-button>
          <el-button type="primary" icon="el-icon-pie-chart" @click="handleViewAnalytics">查看学情分析</el-button>
        </template>

        <template v-if="userType === 'student'">
          <el-button type="primary" icon="el-icon-data-analysis" @click="handleViewProgress">查看学习进度</el-button>
        </template>

        <el-button type="warning" icon="el-icon-download" @click="handleExport">
          {{ userType === 'teacher' ? '导出练习完成情况' : '导出我的练习情况' }}
        </el-button>
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

          <el-table-column v-if="userType === 'teacher'" prop="已提交人数" label="已提交人数" />
          <el-table-column v-if="userType === 'teacher'" prop="未提交人数" label="未提交人数" />
          <el-table-column v-if="userType === 'teacher'" prop="已批改人数" label="已批改人数" />
          <el-table-column v-if="userType === 'teacher'" prop="发布时间" label="发布日期" />
          <el-table-column v-if="userType === 'teacher'" prop="截止时间" label="截止日期" />
          <el-table-column v-if="userType === 'teacher'" prop="发布人" label="发布教师" />

          <el-table-column v-if="userType === 'student'" prop="发布时间" label="发布日期" />
          <el-table-column v-if="userType === 'student'" prop="截止时间" label="截止日期" />
          <el-table-column v-if="userType === 'student'" prop="我的状态" label="我的状态" />
          <el-table-column v-if="userType === 'student'" prop="我的分数" label="我的分数" />

          <el-table-column label="操作" width="150" v-if="userType === 'teacher' || userType === 'student'">
            <template #default="scope">
              <div v-if="userType === 'teacher'">
                <el-button type="text" size="small" @click="handleViewSubmissions(scope.row)">查看提交</el-button>
                <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
                <el-button type="text" size="small" danger @click="handleDelete(scope.row)">删除</el-button>
              </div>
              <div v-if="userType === 'student'">
                <el-button type="text" size="small" @click="handleGoToPracticeDetail(scope.row)">
                  {{ scope.row.我的状态 === '已提交' ? '查看详情' : '开始练习' }}
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
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// FIX: Changed default userType to 'student' to easily test the student view
//const userType = ref<'teacher' | 'student'>('teacher');
const userType = ref<'teacher' | 'student'>('student');

const filters = ref({
  keyword: '',
  chapter: '',
  difficulty: '',
  subject: '',
  classId: '',
});

const tableData = ref([
  {
    习题号: 'EX20250501',
    科目: 'math',
    内容: '函数与图像基础练习',
    发布时间: '2025-05-20 10:00:00',
    截止时间: '2025-06-01 23:59:59',
    发布人: '张老师',
    习题文件路径: '/files/exercises/EX20250501.pdf',
    类型: '选择题',
    难度: '中等',
    知识点: '函数图像',
    已提交人数: 15,
    未提交人数: 10,
    已批改人数: 10,
    我的状态: '未提交',
    我的分数: null,
  },
  {
    习题号: 'EX20250502',
    科目: 'physics',
    内容: '牛顿运动定律训练',
    发布时间: '2025-05-15 09:30:00',
    截止时间: '2025-05-30 23:59:59',
    发布人: '李老师',
    习题文件路径: '/files/exercises/EX20250502.docx',
    类型: '填空题',
    难度: '困难',
    知识点: '牛顿力学',
    已提交人数: 20,
    未提交人数: 5,
    已批改人数: 15,
    我的状态: '已提交',
    我的分数: null,
  },
  {
    习题号: 'EX20250503',
    科目: 'chemistry',
    内容: '元素周期表复习',
    发布时间: '2025-05-25 14:00:00',
    截止时间: '2025-06-05 23:59:59',
    发布人: '王老师',
    习题文件路径: '/files/exercises/EX20250503.zip',
    类型: '判断题',
    难度: '简单',
    知识点: '元素性质',
    已提交人数: 25,
    未提交人数: 0,
    已批改人数: 25,
    我的状态: '已批改',
    我的分数: 95,
  },
]);

const filteredData = computed(() => {
  return tableData.value.filter(item => {
    const keywordMatch = filters.value.keyword === '' ||
        item.内容.includes(filters.value.keyword) ||
        item.习题号.includes(filters.value.keyword);
    const chapterMatch = !filters.value.chapter || item.chapter === filters.value.chapter;
    const difficultyMatch = !filters.value.difficulty || item.难度 === filters.value.difficulty;
    const subjectMatch = !filters.value.subject || item.科目 === filters.value.subject;
    return keywordMatch && chapterMatch && difficultyMatch && subjectMatch;
  });
});

function handleSearch() {
  console.log('搜索条件:', filters.value, '用户类型:', userType.value);
}

function handleAdd() {
  console.log('教师操作: 添加练习');
  router.push({ name: 'PracticeEdit' });
}

function handleExport() {
  if (userType.value === 'teacher') {
    console.log('教师操作: 导出练习完成情况', filteredData.value);
  } else {
    console.log('学生操作: 导出我的练习情况', filteredData.value);
  }
}

function handleEdit(row: any) {
  console.log('教师操作: 编辑练习内容:', row);
  router.push({ name: 'PracticeEdit', params: { exerciseId: row.习题号 } });
}

function handleDelete(row: any) {
  console.log('教师操作: 删除练习:', row);
}

function logout() {
  console.log('退出登录');
  router.push('/login');
}

function handleViewAnalytics() {
  console.log('教师操作: 查看学情分析');
}

function handleSelectClass() {
  console.log('教师操作: 选择了班级', filters.value.classId);
  handleSearch();
}

function handleViewProgress() {
  console.log('学生操作: 查看学习进度');
}

function handleViewSubmissions(row: any) {
  console.log('教师操作: 查看学生提交情况，练习ID:', row.习题号);
  router.push({
    name: 'TeacherHomeworkReview',
    params: { exerciseId: row.习题号, exerciseTitle: row.内容 }
  });
}

function handleGoToPracticeDetail(row: any) {
  console.log('学生操作: 进入练习详情，练习ID:', row.习题号);
  router.push({
    name: 'PracticeDetail',
    params: { exerciseId: row.习题号 }
  });
}
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
</style>