<template>
  <el-container class="course-page-container">
    <el-header class="navbar">
      <div class="title">智慧教学系统</div>
      <div class="user-info">
        <el-select v-model="currentClass" placeholder="选择班级" size="small" class="class-select">
          <el-option v-for="option in classOptions" :key="option.value" :label="option.label" :value="option.value" />
        </el-select>
        <span>您好，{{ username }} {{ userType === 2 ? '教师' : '同学' }}</span>
<!--        <el-button type="danger" size="small" @click="logout">退出登录</el-button>-->
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
          <el-table-column prop="uploadTime" label="上传时间" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <div class="operation-buttons">
                <el-button type="primary" text size="small" @click="handlePreview(row)">预览</el-button>
                <el-button type="success" text size="small" @click="openDownloadDialog(row)">下载</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="footer-actions">
          <div class="left-actions operation-buttons">
            <el-button type="danger" :disabled="!hasSelection" @click="handleDeleteSelected">删除</el-button>
            <el-button type="success" @click="handleUploadSelected">上传</el-button>
          </div>
          <div class="right-actions" v-if="userType === 2">
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

    <!-- 下载弹窗 -->
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
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, defineProps } from 'vue';
import { ElMessage ,ElMessageBox} from 'element-plus';
import { useRouter } from 'vue-router';

import {
  ResourceResponse,
  fetchAllResources,
  getPreviewFileUrl,
  getDownloadFileUrl,
  deleteResourceById
} from '../api/materialsApi';

const props = defineProps({
  usertype: Number,
  username: String,
  userid: String,
});

const userType = computed(() => props.usertype);
const username = computed(() => props.username);
const userId = computed(() => props.userid);

const router = useRouter();

const filters = ref({
  name: '',
  uploader: '',
  uploadTime: '',
  type: '',
});

const rawMaterials = ref<ResourceResponse[]>([]);
const tableData = ref<any[]>([]);

const classOptions = ref([]); // 用于存储班级选项
const currentClass = ref(''); // 当前选择的班级

const refreshResources = async () => {
  try {
    const res = await fetchAllResources();
    rawMaterials.value = res || [];


    // 提取所有唯一的 class_id
    const uniqueClassIds = new Set(rawMaterials.value.map(item => item.class_id));
    classOptions.value = Array.from(uniqueClassIds).map(classId => ({
      label: `班级 ${classId}`, // 假设班级名称为 "班级 + class_id"
      value: classId
    }));

    // 默认选择第一个班级
    // if (classOptions.value.length > 0) {
    //   currentClass.value = classOptions.value[0].value;
    // }


    tableData.value = res.map(item => {
      const filePath = item.resource_content || '';
      const fileExt = filePath.split('.').pop()?.toLowerCase();
      let type = '';
      if (fileExt?.match(/(doc|docx|pdf|txt)/)) type = 'doc';
      else if (fileExt?.match(/(mp4|avi|mov)/)) type = 'video';
      else if (fileExt?.match(/(mp3|wav|m4a)/)) type = 'audio';

      return {
        id: item.resource_id,
        name: item.name || '未知名称',
        uploader: item.teacher_id || '未知上传人',
        uploadTime: item.upload_time || '',
        type,
        filePath,
        classId: item.class_id // 添加这一行
      };
    });
  } catch (err) {
    ElMessage.error('获取资源失败');
    console.error(err);
  }
};
// 初始化获取所有资源
onMounted(() => {
  refreshResources();
});

const fileTypeMap = {
  doc: '文档',
  video: '视频',
  audio: '音频',
};
const formatFileType = (value: string) => fileTypeMap[value] || value;

const selectedRows = ref<any[]>([]);
const hasSelection = computed(() => selectedRows.value.length > 0);
const handleSelectionChange = (val: any[]) => selectedRows.value = val;

const generateType = ref('');
const resetFilters = () => {
  filters.value = { name: '', uploader: '', uploadTime: '', type: '' };
};

const search = () => {
  ElMessage.success('筛选已应用');
};

const filteredData = computed(() => {
  return tableData.value.filter(item => {
    const f = filters.value;
    const matchName = !f.name || item.name.includes(f.name);
    const matchUploader = !f.uploader || item.uploader.includes(f.uploader);
    const matchType = !f.type || item.type === f.type;

    const now = new Date();
    let matchUploadTime = true;
    if (f.uploadTime && item.uploadTime) {
      const uploadDate = new Date(item.uploadTime);
      if (f.uploadTime === '1w') {
        const oneWeekAgo = new Date(now);
        oneWeekAgo.setDate(now.getDate() - 7);
        matchUploadTime = uploadDate >= oneWeekAgo;
      } else if (f.uploadTime === '1m') {
        const oneMonthAgo = new Date(now);
        oneMonthAgo.setMonth(now.getMonth() - 1);
        matchUploadTime = uploadDate >= oneMonthAgo;
      } else if (f.uploadTime === '3m') {
        const threeMonthsAgo = new Date(now);
        threeMonthsAgo.setMonth(now.getMonth() - 3);
        matchUploadTime = uploadDate >= threeMonthsAgo;
      }
    }

    // 筛选班级
    const matchClass = !currentClass.value || item.classId === currentClass.value;

    return matchName && matchUploader && matchType && matchUploadTime && matchClass;
  });
});

// 辅助函数：从文件名中提取扩展名 (包含点)
function getFileExtension(filename: string): string {
  if (!filename || typeof filename !== 'string') {
    return '';
  }
  const lastDotIndex = filename.lastIndexOf('.');
  if (lastDotIndex === -1 || lastDotIndex === 0) {
    return ''; // 没有扩展名或以点开头的文件
  }
  return filename.substring(lastDotIndex); // 包含点，如 ".pdf"
}


const handlePreview = (row: { id: string; name: string }) => {
  console.log(`${userType.value === 2 ? '教师操作' : '学生操作'}: 预览资料:`, row.name, row.id);

  // 确保 resource_id 和 resource_content 有值
  if (!row.id ) {
    ElMessage.warning('该资料没有有效的资源ID或文件内容路径，无法预览。');
    return;
  }

  try {
    // 后端预览接口的 fileId 需要的是完整文件名，例如 "r2025001.pdf"
    const previewFileId = getFileExtension(row.name);

    if (!previewFileId) {
      ElMessage.warning('无法从文件内容路径中解析出文件名进行预览。');
      return;
    }

    const previewUrl = getPreviewFileUrl({
      fileId: row.id+previewFileId, // fileId 需要是完整文件名 (含扩展名)
      path: 'resource/',
    });
    //window.open(previewUrl, '_blank'); // 在新标签页打开预览
    ElMessage.success(`正在打开预览：${row.name}`);
  } catch (error) {
    ElMessage.error('无法生成预览链接。');
    console.error('预览失败:', error);
  }
};
// const handlePreview = (row: any) => {
//   if (row.filePath) window.open(row.filePath, '_blank');
//   else ElMessage.warning('该资源没有预览地址');
// };



const downloadDialogVisible = ref(false); // 控制下载弹窗的显示
const currentDownloadRow = ref<any>(null); // 当前要下载的资源行数据
const downloadForm = ref({
  originalName: '', // 原始文件名
  fileName: '', // 用户自定义的下载文件名
  outFile: 'E:/downloadsedge/test/', // 默认下载路径（服务器上的路径）
});

// 打开下载弹窗
const openDownloadDialog = (row: any) => {
  currentDownloadRow.value = row;
  downloadForm.value.originalName = row.name;
  downloadForm.value.fileName = row.name;
  downloadForm.value.outFile = 'E:/downloadsedge/test/'; // 默认下载路径
  downloadDialogVisible.value = true;
};

// 确认下载
const confirmDownload = async () => {
  if (!downloadForm.value.fileName) {
    ElMessage.warning('下载文件名不能为空');
    return;
  }
  if (!downloadForm.value.outFile) {
    ElMessage.warning('下载路径不能为空');
    return;
  }

  downloadDialogVisible.value = false;
  if (currentDownloadRow.value) {
    // 调用 handleDownload 传递必要的参数
    handleDownload(
        currentDownloadRow.value.id, // 后端 download 接口的 fileId 应该是 resource_id (不带后缀)
        downloadForm.value.fileName, // 用户自定义的下载文件名 (含后缀)
        downloadForm.value.outFile    // 用户自定义的服务器保存路径
    );
  } else {
    ElMessage.error('无法获取下载资料信息。');
  }
};

import axios from 'axios';

const handleDownload = async (resourceId: string, customFileName: string, customOutFile: string) => {
  console.log(
      `${userType.value === 2 ? '教师操作' : '学生操作'}: 下载资料:`,
      'resourceId (不带后缀):', resourceId,
      '自定义文件名 (带后缀):', customFileName,
      '自定义服务器outFile:', customOutFile
  );

  try {
    const downloadUrl = getDownloadFileUrl({
      fileId: resourceId,
      path: 'resource/',
      fileName: customFileName,
      outFile: customOutFile,
    });

    // 改成 axios 请求，防止页面跳转
    const res = await axios.get(downloadUrl);

    if (res.data.code === 200) {
      ElMessage.success(`文件 ${customFileName} 下载成功！`);
    } else {
      ElMessage.error(res.data.message || '下载失败');
    }

  } catch (error) {
    ElMessage.error('下载失败，请检查网络或后端服务。');
    console.error('下载失败:', error);
  }
};



const handleDeleteSelected = async () => {
  if (!hasSelection.value) return ElMessage.warning('请选择要删除的资源');

  const selectedIds = selectedRows.value.map(row => row.id); // 提取选中资源的 ID
  const selectedNames = selectedRows.value.map(row => row.name); // 提取选中资源的名称

  try {
    // 显示确认对话框
    await ElMessageBox.confirm(
        `确定要删除以下 ${selectedIds.length} 个资源吗？这将永久删除文件和相关记录。${selectedNames.join('、')}`,
        '批量删除确认',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning',
          dangerouslyUseHTMLString: true
        }
    );

    // 批量删除逻辑
    const successList = []; // 成功删除的记录
    const failList = [];    // 删除失败的记录

    // 遍历所有选中的资源
    for (const row of selectedRows.value) {
      if (!row.id) {
        failList.push(`ID为空：${row.name}`);
        continue;
      }

      try {
        // 1. 删除资源记录
        const resourceDeleteResponse = await deleteResourceById(row.id);

        if (resourceDeleteResponse.code === 200) {
          successList.push(row.name);

          // 2. 删除文件 (如果需要)
          if (getFileExtension(row.name)) {
            const fileDeleteResponse = await deleteFile({
              fileId: row.id + getFileExtension(row.name),
              path: SERVER_FILE_ROOT_PATH // 服务器文件路径
            });

            if (fileDeleteResponse.code !== 200) {
              failList.push(`文件删除失败：${row.name} (${fileDeleteResponse.message || '未知错误'})`);
            }
          }
        } else {
          failList.push(`记录删除失败：${row.name} (${resourceDeleteResponse.message || '未知错误'})`);
        }
      } catch (error) {
        failList.push(`删除出错：${row.name} (${error.message || '未知错误'})`);
        console.error(`删除 ${row.name} 时出错:`, error);
      }
    }

    // 显示操作结果
    if (successList.length > 0) {
      ElMessage.success(`成功删除：${successList.join('、')}`);
    }
    if (failList.length > 0) {
      ElMessage.error(`以下资源删除失败：${failList.join('、')}`);
    }
    // 刷新页面
    await refreshResources();


  } catch (error: any) {
    if (error !== 'cancel') { // 如果用户点击了取消，不显示错误
      ElMessage.error('批量删除操作请求失败，请检查网络或后端。');
      console.error('批量删除失败:', error);
    } else {
      ElMessage.info('已取消批量删除操作。');
    }
  }
};



const handleUploadSelected = () => {
  console.log('教师操作: 跳转到上传资料界面');
  router.push('/home/materials/upload');
};

const handleSmartGenerate = () => {
  if (!generateType.value) {
    ElMessage.warning('请选择生成类型');
    return;
  }
  ElMessage.success(`已模拟生成 ${generateType.value} 文件`);
};
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