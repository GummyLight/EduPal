<template>
  <div class="materials-content-wrapper">
    <el-header class="navbar" v-if="showTopBar">
      <div class="title">智慧教学系统</div>
      <div class="user-info">
        <span>您好，{{ username }} {{ userType === 2 ? '教师' : '同学' }}</span>
        <el-button type="danger" @click="logout">退出登录</el-button>
      </div>
    </el-header>

    <el-main>
      <el-card class="filter-card" shadow="always">
        <el-form :inline="true" :model="filters" class="filter-form">
          <el-form-item label="资料名称">
            <el-input v-model="filters.name" placeholder="请输入资料名称" />
          </el-form-item>
          <el-form-item label="上传人">
            <el-input v-model="filters.uploader" placeholder="请输入上传人" />
          </el-form-item>
          <el-form-item label="学科分类">
            <el-select v-model="filters.subject" placeholder="选择学科" clearable>
              <el-option label="数学" value="math" />
              <el-option label="物理" value="physics" />
              <el-option label="英语" value="english" />
              <el-option label="化学" value="chemistry" />
            </el-select>
          </el-form-item>
          <el-form-item label="格式类型">
            <el-select v-model="filters.format" placeholder="选择格式" clearable>
              <el-option label="PDF" value="PDF" />
              <el-option label="视频" value="video" /> <el-option label="音频" value="audio" /> <el-option label="DOCX" value="DOCX" />
              <el-option label="ZIP" value="ZIP" />
            </el-select>
          </el-form-item>
          <el-form-item label="资源描述">
            <el-select v-model="filters.description" placeholder="描述关键词" clearable>
              <el-option label="讲义" value="讲义" />
              <el-option label="习题" value="习题" />
              <el-option label="试卷" value="试卷" />
              <el-option label="答案" value="答案" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="search">查询</el-button>
            <el-button @click="resetFilters">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
<!--      //由于本处要测试上传功能，故先对 userType 做改变（老师2，学生1）-->
      <div class="actions" v-if="userType === 1">
        <el-button type="success" icon="el-icon-upload" @click="handleUpload">上传资料</el-button>
      </div>

      <el-card class="table-card" shadow="never">
        <h3>资料库</h3>
        <el-table :data="filteredData" border style="width: 100%">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="name" label="资料名称" />
          <el-table-column prop="uploader" label="上传人" />
          <el-table-column prop="subject" label="学科分类">
            <template #default="{ row }">
              {{ formatSubject(row.subject) }}
            </template>
          </el-table-column>
          <el-table-column prop="size" label="大小" />
          <el-table-column prop="rating" label="评分" />
          <el-table-column prop="uploadTime" label="上传时间" />
          <el-table-column prop="format" label="格式类型">
            <template #default="{ row }">
              {{ formatFormat(row.format) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <div v-if="userType === 1" class="operation-buttons">
                <el-button type="primary" text size="small" @click="handlePreview(row)">预览</el-button>
                <el-button type="success" text size="small" @click="handleDownload(row)">下载</el-button>
              </div>
              <div v-else-if="userType === 2" class="operation-buttons">
                <el-button type="primary" text size="small" @click="handlePreview(row)">预览</el-button>
                <el-button type="success" text size="small" @click="handleDownload(row)">下载</el-button>
                <el-button type="danger" text size="small" @click="handleDelete(row)">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed,defineProps } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router'; // 引入 useRouter

const router = useRouter(); // 初始化 router 实例
const props = defineProps({
  usertype: {
    type: Number, // 明确类型
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



const showTopBar = ref(true); // 确保顶部导航栏显示



const filters = ref({
  name: '',
  uploader: '',
  subject: '', // 存储英文值
  format: '',  // 存储英文值
  description: '',
});

// 定义学科和格式的映射，用于表格显示和过滤
const subjectOptions = {
  'math': '数学',
  'physics': '物理',
  'english': '英语',
  'chemistry': '化学',
};

const formatOptions = {
  'PDF': 'PDF',
  'video': '视频',
  'audio': '音频',
  'DOCX': 'DOCX',
  'ZIP': 'ZIP',
};

const materials = ref([
  {
    id: 'mat001',
    name: '高数讲义.pdf',
    uploader: '张老师',
    subject: 'math',
    size: '2MB',
    rating: 4.5,
    uploadTime: '2025-05-20',
    format: 'PDF',
    description: '讲义',
    filePath: '/mock-materials/gaoshu_jiangyi.pdf'
  },
  {
    id: 'mat002',
    name: '英语听力.mp3',
    uploader: '李老师',
    subject: 'english',
    size: '3MB',
    rating: 4.8,
    uploadTime: '2025-05-21',
    format: 'audio', // <-- 修改为 'audio' 匹配 value
    description: '练习',
    filePath: '/mock-materials/yingyu_tingli.mp3'
  },
  {
    id: 'mat003',
    name: '物理习题集.docx',
    uploader: '王老师',
    subject: 'physics',
    size: '5MB',
    rating: 4.2,
    uploadTime: '2025-05-18',
    format: 'DOCX',
    description: '习题',
    filePath: '/mock-materials/wuli_xitiji.docx'
  },
  {
    id: 'mat004',
    name: '化学期末试卷.zip',
    uploader: '赵老师',
    subject: 'chemistry',
    size: '1.5MB',
    rating: 4.0,
    uploadTime: '2025-05-22',
    format: 'ZIP',
    description: '试卷',
    filePath: '/mock-materials/huaxue_shijuan.zip'
  },
  {
    id: 'mat005',
    name: '数学答案解析.pdf',
    uploader: '张老师',
    subject: 'math',
    size: '1MB',
    rating: 4.7,
    uploadTime: '2025-05-23',
    format: 'PDF',
    description: '答案',
    filePath: '/mock-materials/shuxue_daan.pdf'
  },
]);

const filteredData = computed(() => {
  return materials.value.filter(item => {
    const f = filters.value;
    return (!f.name || item.name.includes(f.name)) &&
        (!f.uploader || item.uploader.includes(f.uploader)) &&
        // 学科筛选：直接比较英文值
        (!f.subject || item.subject === f.subject) &&
        // 格式筛选：直接比较英文值
        (!f.format || item.format === f.format) &&
        (!f.description || item.description.includes(f.description));
  });
});

// 用于表格中显示中文学科名称
const formatSubject = (value: string) => {
  return subjectOptions[value] || value;
};

// 用于表格中显示中文格式类型
const formatFormat = (value: string) => {
  return formatOptions[value] || value;
};


const search = () => {
  console.log('查询条件：', filters.value);
  ElMessage.success('正在查询资料...');
};

const resetFilters = () => {
  filters.value = {
    name: '',
    uploader: '',
    subject: '',
    format: '',
    description: '',
  };
  ElMessage.info('筛选条件已重置。');
};

const logout = () => {
  console.log('退出登录');
  ElMessage.info('您已退出登录。');
};

const handleUpload = () => {
  console.log('教师操作: 跳转到上传资料界面');
  // 方式一：使用完整的绝对路径
  router.push('/home/materials/upload');
  // 方式二：使用路由名称 (推荐，因为即使路径改变，名称不变)
  // router.push({ name: 'MaterialUpload' });
};

const handleBatchDownload = () => {
  console.log('教师操作: 批量下载');
  ElMessage.info('模拟批量下载功能。');
};

const handlePreview = (row: any) => {
  console.log(`${userType.value === 2 ? '教师操作' : '学生操作'}: 预览资料:`, row.name, row.filePath);
  if (row.filePath) {
    window.open(row.filePath, '_blank');
  } else {
    ElMessage.warning('该资料没有可预览的文件路径。');
  }
};

const handleDownload = (row: any) => {
  console.log(`${userType.value === 2 ? '教师操作' : '学生操作'}: 下载资料:`, row.name, row.filePath);
  if (row.filePath) {
    const link = document.createElement('a');
    link.href = row.filePath;
    link.download = row.name;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    ElMessage.success(`正在下载：${row.name}`);
  } else {
    ElMessage.warning('该资料没有文件可供下载。');
  }
};

const handleDelete = (row: any) => {
  console.log('教师操作: 删除资料:', row.name);
  const index = materials.value.findIndex(item => item.id === row.id);
  if (index !== -1) {
    materials.value.splice(index, 1);
    ElMessage.success(`资料《${row.name}》删除成功！`);
  } else {
    ElMessage.error('删除失败：未找到该资料。');
  }
};
</script>

<style scoped>
/* MaterialsForm 自身的容器，用于布局内部内容 */
.materials-content-wrapper {
  height: 100%; /* 确保占据整个视口高度，因为它包含了顶栏和主内容 */
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 */
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

/* 主要内容区 */
.el-main {
  flex: 1; /* 占据剩余空间 */
  padding: 15px; /* 内边距 */
  overflow-y: auto; /* 内容溢出时可滚动 */
  background-color: #f4f6f8; /* 浅灰色背景 */
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 15px; /* 卡片下边距 */
  background-color: #fff; /* 白色背景 */
  border-radius: 4px; /* 轻微圆角 */
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05); /* 轻微阴影 */
}
.filter-form .el-form-item {
  margin-bottom: 10px; /* 表单项间距 */
}

/* 操作按钮区 */
.actions {
  margin: 10px 0 15px; /* 上下边距 */
  display: flex;
  gap: 10px; /* 按钮间距 */
  flex-wrap: wrap; /* 按钮过多时换行 */
}

/* 表格卡片 */
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
  /* flex-wrap: wrap; /* 如果按钮多可以考虑换行 */
  justify-content: flex-start; /* 左对齐 */
  align-items: center;
  gap: 5px; /* 按钮之间的小间距 */
}

/* Element Plus 按钮图标与文本间距 */
.el-button [class*="el-icon-"] + span {
  margin-left: 5px;
}
</style>