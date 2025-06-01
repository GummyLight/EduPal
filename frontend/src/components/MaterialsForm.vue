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
                <el-button type="success" text size="small" @click="openDownloadDialog(row)">下载</el-button>
              </div>
              <div v-else-if="userType === 2" class="operation-buttons">
                <el-button type="primary" text size="small" @click="handlePreview(row)">预览</el-button>
                <el-button type="success" text size="small" @click="openDownloadDialog(row)">下载</el-button>
                <el-button type="danger" text size="small" @click="handleDelete(row)">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-main>

    <el-dialog
        v-model="downloadDialogVisible"
        title="下载设置"
        width="30%"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <el-form :model="downloadForm" label-width="120px">
        <el-form-item label="原始文件名">
          <el-input v-model="downloadForm.originalName" disabled />
        </el-form-item>
        <el-form-item label="下载文件名">
          <el-input v-model="downloadForm.fileName" placeholder="请输入下载文件名" />
        </el-form-item>
        <el-form-item label="保存到文件夹">
          <el-input v-model="downloadForm.outFile" placeholder="请输入服务器上的保存文件夹路径" />
          <el-alert type="warning" show-icon :closable="false" style="margin-top: 10px;">
            <template #title>注意：此处路径指服务器上的临时下载文件夹。</template>
          </el-alert>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="downloadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmDownload">确定下载</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, defineProps } from 'vue';
import { ElMessage, ElDialog, ElForm, ElFormItem, ElInput, ElButton, ElMessageBox, ElAlert } from 'element-plus'; // 导入所需组件
import { useRouter } from 'vue-router';
import { getPreviewFileUrl, getDownloadFileUrl, deleteFile } from '../api/materialsApi';

const router = useRouter();
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

const userType = computed(() => props.usertype);
const username = computed(() => props.username);
const userId = computed(() => props.userid);

const showTopBar = ref(true);

// 假设后端的文件存储根路径在前端也是已知的常量
const SERVER_FILE_ROOT_PATH = 'resource/'; // <-- 你的材料可能在 resource/ 目录下，如果不是，请修改

const filters = ref({
  name: '',
  uploader: '',
  subject: '',
  format: '',
  description: '',
});

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

// 资料数据，确保包含 fileId 和 name
const materials = ref([
  {
    id: 'mat001',
    name: '高数讲义.doc',
    uploader: '张老师',
    subject: 'math',
    size: '2MB',
    rating: 4.5,
    uploadTime: '2025-05-20',
    format: 'PDF',
    description: '讲义',
    fileId: 'r2025002', // 确保这个fileId是正确的
  },
  {
    id: 'mat002',
    name: '英语听力.mp3',
    uploader: '李老师',
    subject: 'english',
    size: '3MB',
    rating: 4.8,
    uploadTime: '2025-05-21',
    format: 'audio',
    description: '练习',
    fileId: 'audio2025002',
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
    fileId: 'doc2025003',
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
    fileId: 'zip2025004',
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
    fileId: 'pdf2025005',
  },
]);

const filteredData = computed(() => {
  return materials.value.filter((item) => {
    const f = filters.value;
    return (
        (!f.name || item.name.includes(f.name)) &&
        (!f.uploader || item.uploader.includes(f.uploader)) &&
        (!f.subject || item.subject === f.subject) &&
        (!f.format || item.format === f.format) &&
        (!f.description || item.description.includes(f.description))
    );
  });
});

const formatSubject = (value: string) => {
  return subjectOptions[value] || value;
};

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
  router.push('/home/materials/upload');
};

const handleBatchDownload = () => {
  console.log('教师操作: 批量下载');
  ElMessage.info('模拟批量下载功能。');
};

// --- 文件扩展名提取函数 ---
function getFileExtension(fileName: string): string {
  if (!fileName || typeof fileName !== 'string') {
    return '';
  }
  const lastDotIndex = fileName.lastIndexOf('.');
  if (lastDotIndex === -1 || lastDotIndex === 0) {
    return '';
  }
  return fileName.substring(lastDotIndex);
}

// --- 预览功能 ---
const handlePreview = (row: any) => {
  console.log(`${userType.value === 2 ? '教师操作' : '学生操作'}: 预览资料:`, row.name, row.fileId);

  if (!row.fileId || !row.name) {
    ElMessage.warning('该资料没有文件ID或文件名，无法预览。');
    return;
  }

  try {
    // 预览接口需要 fileId + 扩展名 作为其文件标识符
    const previewUrl = getPreviewFileUrl({
      fileId: row.fileId + getFileExtension(row.name), // 给fileId加上name的后缀
      path: SERVER_FILE_ROOT_PATH, // 传入服务器文件根路径
    });
    window.open(previewUrl, '_blank');
    ElMessage.success(`正在打开预览：${row.name}`);
  } catch (error) {
    ElMessage.error('无法生成预览链接。');
    console.error('预览失败:', error);
  }
};

// --- 下载弹窗相关状态和函数 ---
const downloadDialogVisible = ref(false); // 控制弹窗显示
const currentDownloadRow = ref<any>(null); // 存储当前点击下载的行数据

const downloadForm = ref({
  originalName: '', // 原始文件名，只读
  fileName: '', // 用户输入的下载文件名
  outFile: 'E:/downloadsedge/test/', // 用户输入的服务器 outFile 路径，提供默认值
});

// 打开下载弹窗
const openDownloadDialog = (row: any) => {
  if (!row.fileId || !row.name) {
    ElMessage.warning('该资料没有文件ID或文件名，无法下载。');
    return;
  }
  currentDownloadRow.value = row;
  downloadForm.value.originalName = row.name;
  downloadForm.value.fileName = row.name; // 默认下载文件名与原始文件名相同
  // downloadForm.value.outFile 默认值已经设置，如果需要根据用户习惯或配置修改，可以在这里进行。
  downloadDialogVisible.value = true;
};

// 确认下载
const confirmDownload = () => {
  if (!downloadForm.value.fileName) {
    ElMessage.warning('下载文件名不能为空。');
    return;
  }
  if (!downloadForm.value.outFile) {
    ElMessage.warning('服务器保存文件夹路径不能为空。');
    return;
  }

  // 关闭弹窗
  downloadDialogVisible.value = false;

  // 调用实际的下载逻辑
  handleDownload(currentDownloadRow.value, downloadForm.value.fileName, downloadForm.value.outFile);
};


// --- 下载功能 (现在接受自定义文件名和路径) ---
const handleDownload = (row: any, customFileName: string, customOutFile: string) => {
  console.log(
      `${userType.value === 2 ? '教师操作' : '学生操作'}: 下载资料:`,
      row.name,
      row.fileId,
      '自定义文件名:',
      customFileName,
      '自定义服务器outFile:',
      customOutFile
  );

  try {
    const downloadUrl = getDownloadFileUrl({
      fileName: customFileName, // 使用用户自定义的下载文件名
      path: SERVER_FILE_ROOT_PATH, // 服务器上的文件根路径
      fileId: row.fileId,
      outFile: customOutFile, // 使用用户自定义的服务器 outFile 路径
    });

    const link = document.createElement('a');
    link.href = downloadUrl;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    ElMessage.success(`正在下载：${customFileName}`);

  } catch (error) {
    ElMessage.error('无法生成下载链接。');
    console.error('下载失败:', error);
  }
};

// --- 删除功能 ---
const handleDelete = async (row: any) => {
  console.log('教师操作: 删除资料:', row.name);
  if (!row.fileId) {
    ElMessage.warning('该资料没有文件ID，无法删除。');
    return;
  }

  try {
    await ElMessageBox.confirm(`确定要删除资料《${row.name}》吗？此操作将永久删除文件。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    // 调用封装的删除 API
    const response = await deleteFile({
      fileId: row.fileId,
      path: SERVER_FILE_ROOT_PATH // 传入服务器文件根路径
    });

    if (response.code === 200) {
      ElMessage.success(`资料《${row.name}》及文件删除成功！`);
      // 从前端列表中移除
      const index = materials.value.findIndex(item => item.id === row.id);
      if (index !== -1) {
        materials.value.splice(index, 1);
      }
    } else {
      ElMessage.error(response.message || '删除失败！');
    }
  } catch (error: any) {
    if (error !== 'cancel') { // 区分用户点击取消和真正的错误
      ElMessage.error('删除请求失败，请检查网络或后端。');
      console.error('删除失败:', error);
    } else {
      ElMessage.info('已取消删除操作。');
    }
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
  background-color: #409eff; /* Element Plus Primary Color */
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
.el-button [class*='el-icon-'] + span {
  margin-left: 5px;
}
</style>