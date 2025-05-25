<!--
 {
  "name": "英语听力",
  "uploader": "李老师",
  "subject": "英语",
  "format": "音频",
  "description": "练习"
}

 -->



<template>
  <div class="materials-page">
    <!-- 顶部导航栏 -->
    <el-header class="top-bar">
      <div class="left">智慧教学系统</div>
      <div class="right">
        <span class="welcome">您好，admin</span>
        <el-button type="danger" size="small">退出登录</el-button>
      </div>
    </el-header>



    <el-main>
      <!-- 搜索过滤区 -->
      <el-form :inline="true" class="filter-form">
        <el-form-item label="资料名称">
          <el-input v-model="filters.name" placeholder="请输入资料名称" />
        </el-form-item>
        <el-form-item label="上传人">
          <el-input v-model="filters.uploader" placeholder="请输入上传人" />
        </el-form-item>
        <el-form-item label="学科分类">
          <el-select v-model="filters.subject" placeholder="选择学科">
            <el-option label="数学" value="math" />
            <el-option label="物理" value="physics" />
            <el-option label="英语" value="english" />
          </el-select>
        </el-form-item>
        <el-form-item label="格式类型">
          <el-select v-model="filters.format" placeholder="选择格式">
            <el-option label="PDF" value="pdf" />
            <el-option label="视频" value="video" />
            <el-option label="音频" value="audio" />
          </el-select>
        </el-form-item>
        <el-form-item label="资源描述">
          <el-select v-model="filters.description" placeholder="描述关键词">
            <el-option label="讲义" value="lecture" />
            <el-option label="习题" value="exercise" />
            <el-option label="试卷" value="exam" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 资料库展示区 -->
      <h3>资料库</h3>
      <el-table :data="filteredData" border>
        <el-table-column prop="name" label="资料名称" />
        <el-table-column prop="uploader" label="上传人" />
        <el-table-column prop="subject" label="学科分类" />
        <el-table-column prop="size" label="大小" />
        <el-table-column prop="rating" label="评分" />
        <el-table-column prop="uploadTime" label="上传时间" />
        <el-table-column prop="format" label="格式类型" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button type="primary" text size="small">预览</el-button>
            <el-button type="success" text size="small">下载</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

const filters = ref({
  name: '',
  uploader: '',
  subject: '',
  format: '',
  description: '',
});

const materials = ref([
  {
    name: '高数讲义.pdf',
    uploader: '张老师',
    subject: '数学',
    size: '2MB',
    rating: 4.5,
    uploadTime: '2025-05-20',
    format: 'PDF',
    description: '讲义',
  },
  {
    name: '英语听力.mp3',
    uploader: '李老师',
    subject: '英语',
    size: '3MB',
    rating: 4.8,
    uploadTime: '2025-05-21',
    format: '音频',
    description: '练习',
  },
]);

const filteredData = computed(() => {
  return materials.value.filter(item => {
    const f = filters.value;
    return (!f.name || item.name.includes(f.name)) &&
        (!f.uploader || item.uploader.includes(f.uploader)) &&
        (!f.subject || item.subject === f.subject) &&
        (!f.format || item.format === f.format) &&
        (!f.description || item.description.includes(f.description));
  });
});

const search = () => {
  console.log('查询条件：', filters.value);
};

const resetFilters = () => {
  filters.value = {
    name: '',
    uploader: '',
    subject: '',
    format: '',
    description: '',
  };
};
</script>

<style scoped>
.materials-page {
  background-color: #f5f7fa;
  min-height: 100vh;
  padding: 0;
}

.top-bar {
  display: flex;
  justify-content: space-between; /* 关键修改点 */
  align-items: center;
  background-color: #409EFF;
  padding: 10px 20px;
  color: white;
}


.top-bar .left {
  font-size: 20px;
  font-weight: bold;
  align-self: flex-start;
}

.top-bar .right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.top-bar .welcome {
  margin-right: 15px;
}

.filter-form {
  margin: 20px 0;
}
</style>
