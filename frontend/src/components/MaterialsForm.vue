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
            <el-input v-model="filters.uploader" placeholder="请输入上传人ID" />
          </el-form-item>
          <el-form-item label="学科分类">
            <el-select v-model="filters.subject" placeholder="选择学科" clearable>
              <el-option label="数学" value="Math" /> <el-option label="物理" value="Physics" />
              <el-option label="英语" value="English" />
              <el-option label="化学" value="Chemistry" />
            </el-select>
          </el-form-item>
          <el-form-item label="资料描述">
            <el-input v-model="filters.description" placeholder="描述关键词" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="search">查询</el-button>
            <el-button @click="resetFilters">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
      <div class="actions" v-if="userType === 2 || userType === 1">
        <el-button type="success" icon="el-icon-upload" @click="handleUpload">上传资料</el-button>
      </div>

      <el-card class="table-card" shadow="never">
        <h3>资料库</h3>
        <el-table :data="displayMaterials" border style="width: 100%" v-loading="loading" row-key="id">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="name" label="资料名称" />
          <el-table-column prop="uploader" label="上传人" />
          <el-table-column prop="subject" label="学科分类">
            <template #default="{ row }">
              {{ formatSubject(row.subject) }}
            </template>
          </el-table-column>
          <el-table-column prop="uploadTime" label="上传时间">
            <template #default="{ row }">
              {{ formatUploadTime(row.uploadTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="description" label="资料描述">
            <template #default="{ row }">
              {{ row.description }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <div class="operation-buttons">
                <el-button type="primary" text size="small" @click="handlePreview(row)">预览</el-button>
                <el-button type="success" text size="small" @click="openDownloadDialog(row)">下载</el-button>
                <el-button v-if="userType === 2" type="danger" text size="small" @click="handleDelete(row)">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!loading && displayMaterials.length === 0" description="暂无资料"></el-empty>
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
import { ref, computed, defineProps, onMounted } from 'vue';
import { ElMessage, ElDialog, ElForm, ElFormItem, ElInput, ElButton, ElMessageBox, ElAlert, ElEmpty } from 'element-plus';
import { useRouter } from 'vue-router';
// 导入 ResourceResponse 接口和所有 API 方法
import {
  ResourceResponse,
  getPreviewFileUrl,
  getDownloadFileUrl,
  deleteFile,
  fetchAllResources,
  searchResourcesByName,
  deleteResourceById
} from '../api/materialsApi';

const router = useRouter();

// Props 定义，与父组件 Home.vue 传递的 props 对应
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

// 计算属性，方便在模板中使用 props
const userType = computed(() => props.usertype);
const username = computed(() => props.username);
const userId = computed(() => props.userid);

const showTopBar = ref(true);
const loading = ref(false); // 控制表格加载状态

// 服务器文件根路径，与后端一致
// 确保这个路径和后端存储文件时的前缀完全一致
const SERVER_FILE_ROOT_PATH = 'resource/';

// 筛选条件模型
const filters = ref({
  name: '',
  uploader: '',
  subject: '',
  description: '', // 资料描述筛选
});

// 学科选项映射
const subjectOptions: { [key: string]: string } = {
  'Math': '数学', // 后端返回 Math, Physics 等，前端显示中文
  'Physics': '物理',
  'English': '英语',
  'Chemistry': '化学',
  // 如果后端可能直接返回中文，为了健壮性也可以加上，但通常建议以后端返回的英文为准
  // '数学': '数学',
  // '物理': '物理',
  // '英语': '英语',
  // '化学': '化学',
};


// 存储从后端获取的原始资料数据 (下划线命名)
const rawMaterials = ref<ResourceResponse[]>([]);

// **重要：displayMaterials 计算属性**
// 将后端返回的下划线命名数据，映射为前端表格期望的字段名和格式，并处理 null 值
const displayMaterials = computed(() => {
  return rawMaterials.value
      .filter((item) => {
        const f = filters.value;
        return (
            // name 可能为 null，需要安全访问
            (!f.name || (item.name && item.name.includes(f.name))) &&
            // subject 保持原样，formatSubject 会处理
            (!f.subject || item.subject === f.subject) &&
            // description 可能为 null，需要安全访问
            (!f.description || (item.description && item.description.includes(f.description))) &&
            // uploader 筛选（teacher_id）
            (!f.uploader || (item.teacher_id && item.teacher_id.includes(f.uploader)))
        );
      })
      .map(item => ({
        // **将后端下划线命名映射到前端需要的字段名，并自动填充 null 数据**
        id: item.resource_id, // 确保 resource_id 是唯一的，且非 null
        name: item.name || '未知名称', // 如果 name 为 null，显示 '未知名称'
        uploader: item.teacher_id || '未知上传人', // 如果 teacher_id 为 null，显示 '未知上传人'
        subject: item.subject || '未知学科', // 如果 subject 为 null，显示 '未知学科'
        description: item.description || '无描述', // 如果 description 为 null，显示 '无描述'
        uploadTime: item.upload_time || '', // 如果 upload_time 为 null，显示空字符串，由 formatUploadTime 处理
        // 关键：保留 resource_content，用于后续提取文件名和扩展名
        resource_content: item.resource_content || '',
      }));
});


// 获取资料列表的异步函数
const getMaterialsList = async () => {
  loading.value = true; // 开始加载
  try {
    const data = await fetchAllResources(); // 调用 API 获取所有资料
    rawMaterials.value = data; // 更新原始数据
    // 只有当有数据时才显示成功，避免空列表也弹成功
    if (data.length > 0) {
      ElMessage.success('资料列表获取成功！');
    } else {
      ElMessage.info('资料库中暂无资料。');
    }
  } catch (error) {
    ElMessage.error('获取资料列表失败。');
    console.error('获取资料列表错误:', error);
  } finally {
    loading.value = false; // 结束加载
  }
};

// 组件挂载时调用获取资料列表
onMounted(() => {
  getMaterialsList();
});

// 格式化学科显示
const formatSubject = (value: string) => {
  return subjectOptions[value] || value; // 如果没有映射，显示原始值
};

// 格式化上传时间显示
const formatUploadTime = (time: string | null) => { // 明确 time 可能为 string 或 null
  if (!time) return ''; // 如果为 null 或空，显示空字符串
  try {
    const date = new Date(time); // 假设后端返回的是 ISO 8601 字符串
    return date.toLocaleString(); // 格式化为本地日期时间字符串
  } catch (e) {
    console.error("日期格式化失败:", e);
    return String(time); // 如果解析失败，直接返回原始字符串
  }
};


// 搜索功能
const search = async () => {
  loading.value = true;
  try {
    // 这里的后端 searchResourcesByName 接口目前只支持按 name 模糊查询
    // 如果需要根据 subject, uploader, description 筛选，后端需要提供相应的接口或扩展现有接口
    const queryName = filters.value.name; // 只用 name 字段进行后端模糊查询
    let filteredData: ResourceResponse[] = [];

    if (queryName) {
      filteredData = await searchResourcesByName(queryName);
    } else {
      // 如果没有输入查询名称，则重新获取所有数据
      filteredData = await fetchAllResources();
    }

    // 前端进行进一步的筛选 (如果后端接口不支持所有筛选条件)
    // 注意：这里的筛选逻辑依然会应用所有 filters，即使后端 searchResourcesByName 只用了 name
    const finalFilteredData = filteredData.filter((item) => {
      const f = filters.value;
      // 保持原始名称筛选逻辑，因为后端 searchResourcesByName 已经处理了 name
      const matchesName = !f.name || (item.name && item.name.includes(f.name));
      const matchesUploader = !f.uploader || (item.teacher_id && item.teacher_id.includes(f.uploader));
      const matchesSubject = !f.subject || item.subject === f.subject;
      const matchesDescription = !f.description || (item.description && item.description.includes(f.description));

      return matchesName && matchesUploader && matchesSubject && matchesDescription;
    });

    rawMaterials.value = finalFilteredData;
    ElMessage.success('查询成功！');

  } catch (error) {
    ElMessage.error('查询资料失败。');
    console.error('查询资料错误:', error);
  } finally {
    loading.value = false;
  }
};

// 重置筛选条件并重新加载所有资料
const resetFilters = async () => {
  filters.value = {
    name: '',
    uploader: '',
    subject: '',
    description: '',
  };
  ElMessage.info('筛选条件已重置，正在重新加载所有资料。');
  await getMaterialsList();
};

const logout = () => {
  console.log('退出登录');
  ElMessage.info('您已退出登录。');
  // 实际项目中这里应该跳转到登录页并清除用户token
  // router.push('/login');
};

// 教师角色才有的上传资料功能
const handleUpload = () => {
  console.log('教师操作: 跳转到上传资料界面');
  router.push('/home/materials/upload');
};


// 辅助函数：从完整路径中提取文件名 (带扩展名)
// function getFileNameFromPath(fullPath: string): string | null {
//   if (!fullPath || typeof fullPath !== 'string') {
//     return null;
//   }
//   const lastSlashIndex = fullPath.lastIndexOf('/');
//   if (lastSlashIndex === -1) {
//     return fullPath; // 没有斜杠，本身就是文件名
//   }
//   return fullPath.substring(lastSlashIndex + 1); // 获取斜杠后的部分
// }

// function getFileExtension(filename: string): string | null {
//   if (!filename || typeof filename !== 'string') {
//     return null; // 如果文件名为空或不是字符串，返回 null
//   }
//   // 找到最后一个点的索引
//   const lastDotIndex = filename.lastIndexOf('.');
//   // 如果没有点或者点是第一个字符（隐藏文件），则认为没有扩展名
//   if (lastDotIndex === -1 || lastDotIndex === 0) {
//     return null;
//   }
//   // 返回点号后的部分（即扩展名，包含点）
//   return filename.substring(lastDotIndex);
// }

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


// 预览功能
// row 参数是 displayMaterials 中映射后的对象
const handlePreview = (row: { id: string; name: string; resource_content: string }) => {
  console.log(`${userType.value === 2 ? '教师操作' : '学生操作'}: 预览资料:`, row.name, row.id, row.resource_content);

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
      path: SERVER_FILE_ROOT_PATH,
    });
    //window.open(previewUrl, '_blank'); // 在新标签页打开预览
    ElMessage.success(`正在打开预览：${row.name}`);
  } catch (error) {
    ElMessage.error('无法生成预览链接。');
    console.error('预览失败:', error);
  }
};

// 下载弹窗相关状态和函数
const downloadDialogVisible = ref(false);
// currentDownloadRow 存储当前要下载的资料行数据，类型为 displayMaterials 中的行类型
const currentDownloadRow = ref<{ id: string; name: string; resource_content: string } | null>(null);

const downloadForm = ref({
  originalName: '', // 显示原始的资料名称
  fileName: '',     // 用户可编辑的下载文件名 (包含扩展名)
  outFile: 'E:/downloadsedge/test/', // 默认下载到服务器上的这个路径（示例），用户可修改
});

// 打开下载弹窗
const openDownloadDialog = (row: { id: string; name: string; resource_content: string }) => {
  if (!row.id || !row.name || !row.resource_content) {
    ElMessage.warning('该资料信息不完整，无法下载。');
    return;
  }
  currentDownloadRow.value = row;

  // 原始文件名显示资料的 name
  downloadForm.value.originalName = row.name;

  // 尝试从 resource_content 中获取文件扩展名，并拼接到 name 上作为默认下载文件名
  downloadForm.value.fileName = row.name;

  // 重置 outFile 为默认值，防止上次修改的影响
  downloadForm.value.outFile = 'E:/downloadsedge/test/'; // 或其他你希望的默认值

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

// 下载功能
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
      path: SERVER_FILE_ROOT_PATH,
      fileName: customFileName,
      outFile: customOutFile,
    });

    // 改成 axios 请求，防止页面跳转
    const res = await axios.get(downloadUrl);

    if (res.data.code === 200) {
      ElMessage.success(`文件 ${customFileName} 下载成功！（已保存到服务器）`);
    } else {
      ElMessage.error(res.data.message || '下载失败');
    }

  } catch (error) {
    ElMessage.error('下载失败，请检查网络或后端服务。');
    console.error('下载失败:', error);
  }
};

// 删除功能
const handleDelete = async (row: { id: string; name: string }) => {
  console.log('教师操作: 删除资料:', row.name, row.id);
  if (!row.id) {
    ElMessage.warning('该资料没有资源ID，无法删除。');
    return;
  }

  try {
    await ElMessageBox.confirm(`确定要删除资料《${row.name}》吗？此操作将永久删除文件和相关记录。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    // 1. 调用后端删除资料记录的接口 (resource/delete/{id})
    const resourceDeleteResponse = await deleteResourceById(row.id); // 传递 resource_id

    if (resourceDeleteResponse.code === 200) {
      ElMessage.success(`资料《${row.name}》记录删除成功！`);

      // 2. 如果资料记录删除成功，再尝试删除文件服务器上的实际文件 (file/delete)
      // deleteFile 接口的 fileId 参数也可能是 resource_id
      const fileDeleteResponse = await deleteFile({
        fileId: row.id+getFileExtension(row.name), // 使用 resource_id 作为 fileId
        path: SERVER_FILE_ROOT_PATH // 文件服务器的根路径
      });

      if (fileDeleteResponse.code === 200) {
        ElMessage.success(`关联文件《${row.name}》删除成功！`);
      } else {
        ElMessage.warning(fileDeleteResponse.message || `关联文件《${row.name}》删除失败，但资料记录已删除。`);
      }
      // 3. 刷新前端列表以反映最新状态
      await getMaterialsList();
    } else {
      ElMessage.error(resourceDeleteResponse.message || '资料记录删除失败！');
    }
  } catch (error: any) {
    if (error !== 'cancel') { // 如果用户点击了取消，不显示错误
      ElMessage.error('删除操作请求失败，请检查网络或后端。');
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
  justify-content: flex-start; /* 左对齐 */
  align-items: center;
  gap: 5px; /* 按钮之间的小间距 */
}

/* Element Plus 按钮图标与文本间距 */
.el-button [class*='el-icon-'] + span {
  margin-left: 5px;
}
</style>