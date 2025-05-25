<!--
 本页面需要参数格式
 {
  "filename": "线性代数",
  "uploader": "张老师",
  "uploadTime": "2025-05",
  "fileType": "pdf"
}


 -->


<template>
  <div class="course-form-page">
    <!-- 顶部导航栏 -->
    <el-header class="top-bar">
      <div class="left">智慧教学系统</div>
      <div class="right">
        <el-select v-model="currentClass" placeholder="选择班级" size="small">
          <el-option label="班级 1" value="class1" />
          <el-option label="班级 2" value="class2" />
        </el-select>
        <span class="welcome">欢迎您，admin</span>
        <el-button type="danger" size="small">退出登录</el-button>
      </div>
    </el-header>

    <el-main>
      <!-- 搜索过滤区 -->
      <el-form :inline="true" class="filter-form" label-width="80px">
        <el-form-item label="文件名称">
          <el-input v-model="filters.name" placeholder="输入文件名称" />
        </el-form-item>
        <el-form-item label="上传人">
          <el-input v-model="filters.uploader" placeholder="输入上传人" />
        </el-form-item>
        <el-form-item label="上传时间">
          <el-select v-model="filters.uploadTime" placeholder="选择时间">
            <el-option label="近一周" value="1w" />
            <el-option label="近一月" value="1m" />
            <el-option label="近三月" value="3m" />
          </el-select>
        </el-form-item>
        <el-form-item label="文件类型">
          <el-select v-model="filters.type" placeholder="选择类型">
            <el-option label="文档" value="doc" />
            <el-option label="视频" value="video" />
            <el-option label="音频" value="audio" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">搜索</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>


      <!-- 资源库展示区 -->
      <h3>资源库</h3>
      <el-table
          :data="filteredData"
          border
          @selection-change="handleSelectionChange"
          style="margin-bottom: 20px;"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="name" label="文件名称" />
        <el-table-column prop="uploader" label="上传人" />
        <el-table-column prop="size" label="大小" />
        <el-table-column prop="uploadTime" label="上传时间" />
        <el-table-column prop="type" label="文件类型" />
      </el-table>

      <!-- 底部操作按钮区 -->
      <div class="footer-actions">
        <div class="left-actions">
          <el-button type="danger" :disabled="!hasSelection">删除</el-button>
          <el-button :disabled="!hasSelection">预览</el-button>
          <el-button type="success" :disabled="!hasSelection">下载</el-button>
        </div>
        <div class="right-actions">
          <el-select v-model="generateType" placeholder="生成文件类型" style="width: 160px;">
            <el-option label="文档" value="doc" />
            <el-option label="视频" value="video" />
          </el-select>
          <el-button type="primary">智能生成</el-button>
          <span class="hint">智能生成后文件将自动保存至资源库中</span>
        </div>
      </div>
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

const currentClass = ref('class1');

const filters = ref({
  name: '',
  uploader: '',
  uploadTime: '',
  type: '',
});

const tableData = ref([
  { name: '线性代数讲义.pdf', uploader: '张老师', size: '1.2MB', uploadTime: '2025-05-20', type: '文档' },
  { name: '概率论视频.mp4', uploader: '李老师', size: '120MB', uploadTime: '2025-05-21', type: '视频' },
  { name: '英语听力.mp3', uploader: '王老师', size: '5MB', uploadTime: '2025-05-22', type: '音频' },
  { name: '高数习题集.doc', uploader: '李老师', size: '2.5MB', uploadTime: '2025-05-15', type: '文档' }
]);

const selectedRows = ref<any[]>([]);
const hasSelection = computed(() => selectedRows.value.length > 0);

const handleSelectionChange = (val: any[]) => {
  selectedRows.value = val;
};

const generateType = ref('');

const search = () => {
  // 这里只是模拟过滤
  console.log('搜索条件：', filters.value);
};

// 简单过滤演示（可以接后端搜索接口）
const filteredData = computed(() => {
  return tableData.value.filter(item => {
    const matchName = filters.value.name === '' || item.name.includes(filters.value.name);
    const matchUploader = filters.value.uploader === '' || item.uploader.includes(filters.value.uploader);
    const matchType = filters.value.type === '' || item.type === filters.value.type;
    return matchName && matchUploader && matchType;
  });
});
</script>

<style scoped>
.course-form-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #409EFF;
  color: white;
  padding: 0 20px;
  height: 60px;
}

.top-bar .left {
  font-size: 20px;
  font-weight: bold;
}

.top-bar .right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.welcome {
  font-size: 14px;
}

.filter-form {
  margin: 20px 0;
}

.footer-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20px;
}

.left-actions el-button + el-button {
  margin-left: 10px;
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.hint {
  color: #999;
  font-size: 12px;
}
</style>
