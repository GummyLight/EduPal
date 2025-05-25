<template>
  <div class="practice-form">
    <!-- 顶部导航栏 -->
    <el-header class="navbar">
      <div class="title">智慧教学系统</div>
      <div class="user-info">
        <span>您好，admin</span>
        <el-button type="danger" @click="logout">退出登录</el-button>
      </div>
    </el-header>

    <el-main>
      <!-- 搜索过滤区 -->
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
              <el-option label="物理" value="physics" />
              <el-option label="化学" value="chemistry" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 操作按钮区 -->
      <div class="actions">
        <el-button type="success" icon="el-icon-plus" @click="handleAdd">添加</el-button>
        <el-button type="warning" icon="el-icon-download" @click="handleExport">导出</el-button>
      </div>

      <!-- 表格展示区 -->
      <el-card class="table-card" shadow="never">
        <h3>练习列表</h3>
        <el-table :data="filteredData" border style="width: 100%">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="name" label="练习名称" />
          <el-table-column prop="code" label="练习编号" />
          <el-table-column prop="type" label="类型" />
          <el-table-column prop="difficulty" label="难度" />
          <el-table-column prop="knowledge" label="知识点" />
          <el-table-column prop="answered" label="已答题数" />
          <el-table-column prop="unanswered" label="未答题数" />
          <el-table-column prop="publishTime" label="发布时间" />
          <el-table-column prop="updateTime" label="修改时间" />
          <el-table-column prop="publisher" label="发布人" />
          <el-table-column label="操作" width="140">
            <template #default="scope">
              <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button type="text" size="small" danger @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

const filters = ref({
  keyword: '',
  chapter: '',
  difficulty: '',
  subject: '',
});

const tableData = ref([
  {
    name: '函数与图像基础练习',
    code: 'EX20250501',
    type: '选择题',
    difficulty: '中等',
    knowledge: '函数图像',
    answered: 20,
    unanswered: 5,
    publishTime: '2025-05-20',
    updateTime: '2025-05-22',
    publisher: '张老师',
    chapter: '1',
    subject: 'math',
  },
  {
    name: '牛顿运动定律训练',
    code: 'EX20250502',
    type: '填空题',
    difficulty: '困难',
    knowledge: '牛顿力学',
    answered: 10,
    unanswered: 10,
    publishTime: '2025-05-15',
    updateTime: '2025-05-21',
    publisher: '李老师',
    chapter: '2',
    subject: 'physics',
  },
]);

const filteredData = computed(() => {
  return tableData.value.filter(item => {
    const keywordMatch = filters.value.keyword === '' || item.name.includes(filters.value.keyword);
    const chapterMatch = !filters.value.chapter || item.chapter === filters.value.chapter;
    const difficultyMatch = !filters.value.difficulty || item.difficulty === filters.value.difficulty;
    const subjectMatch = !filters.value.subject || item.subject === filters.value.subject;
    return keywordMatch && chapterMatch && difficultyMatch && subjectMatch;
  });
});

function handleSearch() {
  // 这里搜索是响应式computed自动过滤，无需额外处理
  console.log('搜索条件:', filters.value);
}

function handleAdd() {
  console.log('添加练习操作');
}

function handleExport() {
  console.log('导出练习操作');
}

function handleEdit(row: any) {
  console.log('编辑练习:', row);
}

function handleDelete(row: any) {
  console.log('删除练习:', row);
}

function logout() {
  console.log('退出登录');
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
  border-bottom: 1px solid #ddd;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}
.title {
  font-weight: bold;
  font-size: 18px;
}
el-main {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
.filter-card {
  margin-bottom: 20px;
}
.actions {
  margin: 10px 0 20px;
  display: flex;
  gap: 10px;
}
.table-card h3 {
  margin-bottom: 10px;
}
</style>
