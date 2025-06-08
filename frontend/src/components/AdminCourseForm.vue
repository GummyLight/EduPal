<template>
  <div class="admin-course-container">
    <div class="header">
      <h2>教学内容管理</h2>
      <el-divider />
    </div>

    <!-- 搜索和操作栏 -->
    <div class="search-section">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
            v-model="searchName"
            placeholder="按名称搜索"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button @click="handleSearch" :icon="Search">搜索</el-button>
            </template>
          </el-input>
        </el-col>
        <el-col :span="6">
          <el-select
            v-model="selectedSubject"
            placeholder="选择学科"
            clearable
            @change="handleSubjectFilter"
          >
            <el-option label="全部" value="" />
            <el-option
              v-for="subject in subjects"
              :key="subject"
              :label="subject"
              :value="subject"
            />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="handleAdd" :icon="Plus">
            添加教学内容
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 教学内容列表 -->
    <div class="content-section">
      <el-table
        :data="displayedContent"
        style="width: 100%"
        v-loading="loading"
        element-loading-text="加载中..."
      >
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="subject" label="学科" width="100" />
        <el-table-column prop="teacher_id" label="教师ID" width="120" />
        <el-table-column prop="class_id" label="班级ID" width="120" />
        <el-table-column prop="upload_time" label="上传时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.upload_time) }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 添加对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="添加教学内容"
      width="60%"
      :before-close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-form-item label="教学内容ID" prop="teaching_content_id">
          <el-input
            v-model="formData.teaching_content_id"
            placeholder="请输入教学内容ID"
          />
        </el-form-item>
        
        <el-form-item label="名称" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="请输入教学内容名称"
          />
        </el-form-item>
        
        <el-form-item label="学科" prop="subject">
          <el-select
            v-model="formData.subject"
            placeholder="请选择学科"
            style="width: 100%"
          >
            <el-option
              v-for="subject in subjects"
              :key="subject"
              :label="subject"
              :value="subject"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="教师ID" prop="teacher_id">
          <el-input
            v-model="formData.teacher_id"
            placeholder="请输入教师ID"
          />
        </el-form-item>
        
        <el-form-item label="班级ID" prop="class_id">
          <el-input
            v-model="formData.class_id"
            placeholder="请输入班级ID（可选）"
          />
        </el-form-item>
        
        <el-form-item label="教学内容" prop="teaching_content_content">
          <el-input
            v-model="formData.teaching_content_content"
            type="textarea"
            :rows="6"
            placeholder="请输入教学内容"
          />
        </el-form-item>
        
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述（可选）"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            创建
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="查看教学内容"
      width="60%"
    >
      <div v-if="viewData">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="教学内容ID">
            {{ viewData.teaching_content_id }}
          </el-descriptions-item>
          <el-descriptions-item label="名称">
            {{ viewData.name }}
          </el-descriptions-item>
          <el-descriptions-item label="学科">
            {{ viewData.subject }}
          </el-descriptions-item>
          <el-descriptions-item label="教师ID">
            {{ viewData.teacher_id }}
          </el-descriptions-item>
          <el-descriptions-item label="班级ID">
            {{ viewData.class_id || '无' }}
          </el-descriptions-item>
          <el-descriptions-item label="上传时间">
            {{ formatDate(viewData.upload_time) }}
          </el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">
            {{ viewData.description || '无' }}
          </el-descriptions-item>
        </el-descriptions>
        
        <el-divider content-position="left">教学内容</el-divider>
        <div class="content-preview">
          <pre>{{ viewData.teaching_content_content }}</pre>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import {
  fetchAllTeachingContent,
  searchTeachingContentByName,
  createTeachingContent,
  deleteTeachingContent,
  type TeachingContentResponse,
  type TeachingContentRequest
} from '../api/teachingContent'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const searchName = ref('')
const selectedSubject = ref('')
const teachingContentList = ref<TeachingContentResponse[]>([])
const filteredList = ref<TeachingContentResponse[]>([])

// 对话框相关
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)

// 表单相关
const formRef = ref<FormInstance>()
const formData = reactive<TeachingContentRequest>({
  teaching_content_id: '',
  name: '',
  subject: '',
  teacher_id: '',
  teaching_content_content: '',
  class_id: '',
  description: '',
  upload_time: null
})

const viewData = ref<TeachingContentResponse | null>(null)

// 学科选项
const subjects = ref([
  '语文', '数学', '英语', '物理', '化学', '生物', '历史', '地理', '政治', '音乐', '美术', '体育', '信息技术'
])

// 表单验证规则
const formRules: FormRules = {
  teaching_content_id: [
    { required: true, message: '请输入教学内容ID', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入名称', trigger: 'blur' },
    { min: 1, max: 100, message: '名称长度在1到100个字符之间', trigger: 'blur' }
  ],
  subject: [
    { required: true, message: '请选择学科', trigger: 'change' }
  ],
  teacher_id: [
    { required: true, message: '请输入教师ID', trigger: 'blur' }
  ],
  teaching_content_content: [
    { required: true, message: '请输入教学内容', trigger: 'blur' }
  ]
}

// 计算属性
const displayedContent = computed(() => {
  let result = filteredList.value
  
  if (selectedSubject.value) {
    result = result.filter(item => item.subject === selectedSubject.value)
  }
  
  return result
})

// 方法
const loadTeachingContent = async () => {
  loading.value = true
  try {
    console.log('开始加载教学内容列表...');
    const data = await fetchAllTeachingContent()
    console.log('加载到的数据:', data);
    teachingContentList.value = data
    filteredList.value = data
    
    if (data.length === 0) {
      console.log('暂无教学内容数据');
    } else {
      console.log(`成功加载 ${data.length} 条教学内容`);
    }
  } catch (error) {
    console.error('加载教学内容失败:', error)
    ElMessage.error('加载教学内容失败');
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  if (!searchName.value.trim()) {
    filteredList.value = teachingContentList.value
    return
  }
  
  loading.value = true
  try {
    const data = await searchTeachingContentByName(searchName.value.trim())
    filteredList.value = data
  } catch (error) {
    console.error('搜索失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSubjectFilter = () => {
  // 计算属性会自动处理过滤
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleView = (row: TeachingContentResponse) => {
  viewData.value = row
  viewDialogVisible.value = true
}

const handleDelete = async (row: TeachingContentResponse) => {
  try {
    // 验证行数据和ID
    if (!row || !row.teaching_content_id) {
      ElMessage.error('无效的教学内容数据，无法删除');
      console.error('删除失败：无效的行数据', row);
      return;
    }
    
    await ElMessageBox.confirm(
      `确定要删除教学内容"${row.name}"吗？\nID: ${row.teaching_content_id}`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    console.log('准备删除教学内容:', row);
    console.log('删除ID:', row.teaching_content_id);
    loading.value = true;
    
    const success = await deleteTeachingContent(row.teaching_content_id)
    if (success) {
      await loadTeachingContent()
      ElMessage.success('删除成功');
    } else {
      ElMessage.error('删除失败，请重试');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除操作失败:', error)
      ElMessage.error('删除操作失败');
    }
  } finally {
    loading.value = false;
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitting.value = true
    
    const success = await createTeachingContent(formData)
    
    if (success) {
      dialogVisible.value = false
      await loadTeachingContent()
    }
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitting.value = false
  }
}

const handleDialogClose = () => {
  dialogVisible.value = false
  resetForm()
}

const resetForm = () => {
  Object.assign(formData, {
    teaching_content_id: '',
    name: '',
    subject: '',
    teacher_id: '',
    teaching_content_content: '',
    class_id: '',
    description: '',
    upload_time: null
  })
  
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const formatDate = (dateString: string) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadTeachingContent()
})
</script>

<style scoped>
.admin-course-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.header {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
}

.search-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.content-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.content-preview {
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  max-height: 300px;
  overflow-y: auto;
}

.content-preview pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.5;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.el-table {
  font-size: 14px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-textarea {
  font-family: inherit;
}
</style>