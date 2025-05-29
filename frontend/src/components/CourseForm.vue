<template>
  <el-container class="course-page-container">
    <el-header class="navbar">
      <div class="title">智慧教学系统</div>
      <div class="user-info">
        <el-select v-model="currentClass" placeholder="选择班级" size="small" class="class-select">
          <el-option label="班级 1" value="class1" />
          <el-option label="班级 2" value="class2" />
        </el-select>
        <span>您好，{{ username }} {{ userType === 'teacher' ? '教师' : '同学' }}</span> <el-button type="danger" size="small" @click="logout">退出登录</el-button>
      </div>
    </el-header>

    <el-main class="main-content">
      <el-card class="filter-card" shadow="always">
        <el-form :inline="true" :model="filters" class="filter-form">
          <el-form-item label="文件名称">
            <el-input v-model="filters.name" placeholder="输入文件名称" />
          </el-form-item>
          <el-form-item label="上传人">
            <el-input v-model="filters.uploader" placeholder="输入上传人" />
          </el-form-item>
          <el-form-item label="上传时间">
            <el-select v-model="filters.uploadTime" placeholder="选择时间" clearable>
              <el-option label="近一周" value="1w" />
              <el-option label="近一月" value="1m" />
              <el-option label="近三月" value="3m" />
            </el-select>
          </el-form-item>
          <el-form-item label="文件类型">
            <el-select v-model="filters.type" placeholder="选择类型" clearable>
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
      </el-card>

      <el-card class="table-card" shadow="never">
        <h3>资源库</h3>
        <el-table
            :data="filteredData"
            border
            @selection-change="handleSelectionChange"
            style="width: 100%; margin-bottom: 20px;"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="name" label="文件名称" />
          <el-table-column prop="uploader" label="上传人" />
          <el-table-column prop="size" label="大小" />
          <el-table-column prop="uploadTime" label="上传时间" />
          <el-table-column prop="type" label="文件类型">
            <template #default="{ row }">
              {{ formatFileType(row.type) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <div class="operation-buttons">
                <el-button type="primary" text size="small" @click="handlePreview(row)">预览</el-button>
                <el-button type="success" text size="small" @click="handleDownload(row)">下载</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="footer-actions">
          <div class="left-actions operation-buttons">
            <el-button type="danger" :disabled="!hasSelection" @click="handleDeleteSelected">删除</el-button>
            <el-button :disabled="!hasSelection" @click="handlePreviewSelected">预览</el-button>
            <el-button type="success" :disabled="!hasSelection" @click="handleDownloadSelected">下载</el-button>
          </div>
          <div class="right-actions" v-if="userType === 'teacher'">
            <el-select v-model="generateType" placeholder="生成文件类型" style="width: 160px;" clearable>
              <el-option label="文档" value="doc" />
              <el-option label="视频" value="video" />
            </el-select>
            <el-button type="primary" @click="handleSmartGenerate">智能生成</el-button>
            <span class="hint">智能生成后文件将自动保存至资源库中</span>
          </div>
        </div>
      </el-card>
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed ,defineProps} from 'vue';
import { ElMessage } from 'element-plus';


//接收props
const props = defineProps({
  usertype: {
    type: String as () => 'teacher' | 'student', // 明确类型
    required: true,
  },
  username: { // 确保也接收 username，因为您在模板中也使用了它
    type: String,
    required: true,
  },
  userid: {
    type: String,
    required: true,
  }
});

const userType = computed(() => props.usertype);

const username = computed(() => props.username);
const userId = computed(() => props.userid);


const currentClass = ref('class1'); // 模拟班级选择

const filters = ref({
  name: '',
  uploader: '',
  uploadTime: '',
  type: '',
});

// 模拟数据：将 type 字段改为英文，以便与 filter value 一致
const tableData = ref([
  { id: 'c001', name: '线性代数讲义.pdf', uploader: '张老师', size: '1.2MB', uploadTime: '2025-05-20', type: 'doc', filePath: '/mock-materials/xianxingdaishu_jiangyi.pdf' },
  { id: 'c002', name: '概率论视频.mp4', uploader: '李老师', size: '120MB', uploadTime: '2025-05-21', type: 'video', filePath: '/mock-materials/gailv_shipin.mp4' },
  { id: 'c003', name: '英语听力.mp3', uploader: '王老师', size: '5MB', uploadTime: '2025-05-22', type: 'audio', filePath: '/mock-materials/yingyu_tingli.mp3' },
  { id: 'c004', name: '高数习题集.doc', uploader: '李老师', size: '2.5MB', uploadTime: '2025-05-15', type: 'doc', filePath: '/mock-materials/gaoshu_xitiji.doc' }
]);

// 文件类型映射（用于表格显示中文）
const fileTypeMap: { [key: string]: string } = {
  'doc': '文档',
  'video': '视频',
  'audio': '音频',
};
const formatFileType = (value: string) => {
  return fileTypeMap[value] || value;
};


const selectedRows = ref<any[]>([]);
const hasSelection = computed(() => selectedRows.value.length > 0);

const handleSelectionChange = (val: any[]) => {
  selectedRows.value = val;
};

const generateType = ref('');

const search = () => {
  ElMessage.success('正在查询资源...');
  console.log('搜索条件：', filters.value);
  // 实际：发起API请求到后端，根据 filters 获取数据
};

const resetFilters = () => {
  filters.value = {
    name: '',
    uploader: '',
    uploadTime: '',
    type: '',
  };
  ElMessage.info('筛选条件已重置。');
};

const logout = () => {
  console.log('退出登录');
  ElMessage.info('您已退出登录。');
  // 实际：执行退出登录操作，如清除 token，跳转登录页
};


const handlePreview = (row: any) => {
  console.log('预览资源:', row.name, row.filePath);
  if (row.filePath) {
    window.open(row.filePath, '_blank');
  } else {
    ElMessage.warning('该资源没有可预览的文件路径。');
  }
};

const handleDownload = (row: any) => {
  console.log('下载资源:', row.name, row.filePath);
  if (row.filePath) {
    const link = document.createElement('a');
    link.href = row.filePath;
    link.download = row.name;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    ElMessage.success(`正在下载：${row.name}`);
  } else {
    ElMessage.warning('该资源没有文件可供下载。');
  }
};

const handleDeleteSelected = () => {
  if (!hasSelection.value) {
    ElMessage.warning('请选择要删除的资源。');
    return;
  }
  const selectedNames = selectedRows.value.map(row => row.name).join('，');
  ElMessage.success(`模拟删除选中的资源：${selectedNames}`);
  console.log('删除选中的资源:', selectedRows.value);
  tableData.value = tableData.value.filter(item => !selectedRows.value.some(sel => sel.id === item.id));
  selectedRows.value = []; // 清空选中
};

const handlePreviewSelected = () => {
  if (!hasSelection.value) {
    ElMessage.warning('请选择要预览的资源。');
    return;
  }
  const firstSelected = selectedRows.value[0];
  if (selectedRows.value.length > 1) {
    ElMessage.info('只支持预览第一个选中的文件。');
  }
  handlePreview(firstSelected);
};

const handleDownloadSelected = () => {
  if (!hasSelection.value) {
    ElMessage.warning('请选择要下载的资源。');
    return;
  }
  ElMessage.success('模拟批量下载选中的资源。');
  console.log('批量下载选中的资源:', selectedRows.value);
};

const handleSmartGenerate = () => {
  if (!generateType.value) {
    ElMessage.warning('请选择要生成的类型。');
    return;
  }
  ElMessage.success(`模拟智能生成${generateType.value}文件。`);
  console.log('智能生成类型:', generateType.value);
};

// 过滤逻辑修正：确保 type 比较的是英文值
const filteredData = computed(() => {
  return tableData.value.filter(item => {
    const matchName = filters.value.name === '' || item.name.includes(filters.value.name);
    const matchUploader = filters.value.uploader === '' || item.uploader.includes(filters.value.uploader);
    const matchType = filters.value.type === '' || item.type === filters.value.type; // 现在 item.type 也是英文了

    // 假设 uploadTime 筛选是基于简单字符串匹配
    const matchUploadTime = filters.value.uploadTime === '' || item.uploadTime.includes(filters.value.uploadTime.replace('w', '').replace('m', '')); // 简单示例

    return matchName && matchUploader && matchType && matchUploadTime;
  });
});
</script>

<style scoped>
/* 样式与之前提供的保持一致，仅在底部操作区添加 v-if 即可 */
.course-page-container {
  height: 100vh; /* 确保整个页面容器占据视口高度 */
  display: flex;
  flex-direction: column;
  background-color: #f4f6f8; /* 整体背景色 */
}

/* 顶部导航栏 - 与 MaterialsForm.vue 的 navbar 样式统一 */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: #409EFF; /* Element Plus Primary Color */
  color: white;
  border-bottom: 1px solid #ddd;
  flex-shrink: 0; /* 防止导航栏被压缩 */
}
.navbar .title {
  font-weight: bold;
  font-size: 18px;
  color: white;
}
.navbar .user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}
.navbar .user-info span {
  color: white;
}
.navbar .class-select {
  width: 120px; /* 调整班级选择框宽度 */
}


/* 主要内容区 */
.main-content {
  flex: 1; /* 占据剩余空间 */
  padding: 15px; /* 内边距 */
  overflow-y: auto; /* 内容溢出时可滚动 */
  /* 背景色由 .course-page-container 提供，这里不需要重复设置 */
}

/* 筛选卡片 - 与 MaterialsForm.vue 的 filter-card 样式统一 */
.filter-card {
  margin-bottom: 15px; /* 卡片下边距 */
  background-color: #fff; /* 白色背景 */
  border-radius: 4px; /* 轻微圆角 */
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05); /* 轻微阴影 */
}
.filter-form .el-form-item {
  margin-bottom: 10px; /* 表单项间距 */
}

/* 表格卡片 - 与 MaterialsForm.vue 的 table-card 样式统一 */
.table-card {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}
.table-card h3 {
  margin-bottom: 15px; /* 标题下边距 */
  font-size: 16px;
  font-weight: 600;
  color: #303133; /* 标题颜色 */
}

/* 表格操作列按钮布局 */
.operation-buttons {
  display: flex;
  justify-content: flex-start; /* 左对齐 */
  align-items: center;
  gap: 5px; /* 按钮之间的小间距 */
}

/* 底部操作按钮区 */
.footer-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0 5px; /* 调整内边距，使其不贴着底部 */
  margin-top: 10px; /* 与表格保持距离 */
  flex-wrap: wrap; /* 按钮过多时换行 */
  gap: 10px; /* 行间距 */
}

.left-actions {
  /* 继承 operation-buttons 的样式 */
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap; /* 按钮过多时换行 */
}

.hint {
  color: #909399; /* 更柔和的颜色 */
  font-size: 12px;
  margin-left: 5px; /* 文本与按钮间距 */
}

/* Element Plus 按钮图标与文本间距 */
.el-button [class*="el-icon-"] + span {
  margin-left: 5px;
}
</style>