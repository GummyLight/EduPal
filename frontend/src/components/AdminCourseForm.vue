<template>
  <div class="admin-course-container">
    <div class="header">
      <h2>教学内容管理</h2>
      <el-divider />
    </div>

    <!-- 搜索栏 -->
    <div class="search-section">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
            v-model="searchTitle"
            placeholder="按标题搜索"
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
          <el-select
            v-model="selectedStatus"
            placeholder="选择状态"
            clearable
            @change="handleStatusFilter"
          >
            <el-option label="全部" value="" />
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已归档" :value="2" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button @click="refreshData" :icon="Refresh">刷新</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 统计信息 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-item">
              <span class="stats-label">总内容数</span>
              <span class="stats-value">{{ totalCount }}</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-item">
              <span class="stats-label">当前显示</span>
              <span class="stats-value">{{ filteredData.length }}</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-item">
              <span class="stats-label">已发布</span>
              <span class="stats-value">{{ publishedCount }}</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-item">
              <span class="stats-label">草稿</span>
              <span class="stats-value">{{ draftCount }}</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 教学内容列表表格 -->
    <div class="table-section">
      <el-table
        :data="paginatedData"
        v-loading="loading"
        style="width: 100%"
        stripe
        border
        height="500"
      >
        <el-table-column prop="id" label="内容ID" width="120" />
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <span :title="row.title">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="subject" label="学科" width="100" />
        <el-table-column prop="teacher_id" label="教师ID" width="120" />
        <el-table-column prop="class_id" label="班级ID" width="120">
          <template #default="{ row }">
            <span>{{ row.class_id || '无' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row.status)"
              size="small"
            >
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="create_time" label="创建时间" width="180">
          <template #default="{ row }">
            <span>{{ formatTime(row.create_time) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="update_time" label="更新时间" width="180">
          <template #default="{ row }">
            <span>{{ formatTime(row.update_time) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="viewContent(row)"
              :icon="View"
            >
              查看
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(row)"
              :icon="Delete"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="filteredData.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 查看教学内容对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      :title="`查看教学内容: ${selectedContent?.title}`"
      width="70%"
      top="5vh"
    >
      <div v-if="selectedContent" class="content-dialog">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="内容ID">
            {{ selectedContent.id }}
          </el-descriptions-item>
          <el-descriptions-item label="标题">
            {{ selectedContent.title }}
          </el-descriptions-item>
          <el-descriptions-item label="学科">
            {{ selectedContent.subject }}
          </el-descriptions-item>
          <el-descriptions-item label="教师ID">
            {{ selectedContent.teacher_id }}
          </el-descriptions-item>
          <el-descriptions-item label="班级ID">
            {{ selectedContent.class_id || '无' }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedContent.status)">
              {{ getStatusText(selectedContent.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatTime(selectedContent.create_time) }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间">
            {{ formatTime(selectedContent.update_time) }}
          </el-descriptions-item>
        </el-descriptions>
        
        <el-divider content-position="left">教学内容</el-divider>
        <div class="content-display">
          <pre v-if="selectedContent.content">{{ selectedContent.content }}</pre>
          <span v-else class="no-content">无内容</span>
        </div>
        
        <el-divider content-position="left">描述信息</el-divider>
        <div class="description-display">
          <p v-if="selectedContent.description">{{ selectedContent.description }}</p>
          <span v-else class="no-description">无描述</span>
        </div>

        <el-divider content-position="left" v-if="selectedContent.file_path">附件信息</el-divider>
        <div v-if="selectedContent.file_path" class="file-display">
          <el-link :href="selectedContent.file_path" target="_blank" type="primary">
            <el-icon><Document /></el-icon>
            查看附件
          </el-link>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View, Delete, Document } from '@element-plus/icons-vue'
import { 
  fetchAllTeachingContent, 
  searchTeachingContentByTitle, 
  deleteTeachingContentById,
  type TeachingContentResponse 
} from '../api/teachingContent'

// 响应式数据
const loading = ref(false)
const allData = ref<TeachingContentResponse[]>([])
const filteredData = ref<TeachingContentResponse[]>([])
const searchTitle = ref('')
const selectedSubject = ref('')
const selectedStatus = ref<number | ''>('')
const currentPage = ref(1)
const pageSize = ref(20)
const viewDialogVisible = ref(false)
const selectedContent = ref<TeachingContentResponse | null>(null)

// 计算属性
const totalCount = computed(() => allData.value.length)
const publishedCount = computed(() => allData.value.filter(item => item.status === 1).length)
const draftCount = computed(() => allData.value.filter(item => item.status === 0).length)

const subjects = computed(() => {
  const subjectSet = new Set(allData.value.map(item => item.subject))
  return Array.from(subjectSet).filter(Boolean)
})

const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredData.value.slice(start, end)
})

// 生命周期
onMounted(() => {
  loadData()
})

// 方法
const loadData = async () => {
  loading.value = true
  try {
    const data = await fetchAllTeachingContent()
    allData.value = data
    filteredData.value = data
  } catch (error) {
    ElMessage.error('加载教学内容数据失败')
    console.error('Error loading teaching content:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  if (!searchTitle.value.trim()) {
    applyFilters()
    return
  }
  
  loading.value = true
  try {
    const data = await searchTeachingContentByTitle(searchTitle.value.trim())
    filteredData.value = data
    currentPage.value = 1
  } catch (error) {
    ElMessage.error('搜索失败')
    console.error('Error searching teaching content:', error)
  } finally {
    loading.value = false
  }
}

const handleSubjectFilter = () => {
  applyFilters()
}

const handleStatusFilter = () => {
  applyFilters()
}

const applyFilters = () => {
  let data = allData.value
  
  if (selectedSubject.value) {
    data = data.filter(item => item.subject === selectedSubject.value)
  }
  
  if (selectedStatus.value !== '') {
    data = data.filter(item => item.status === selectedStatus.value)
  }
  
  if (searchTitle.value.trim()) {
    data = data.filter(item => 
      item.title.toLowerCase().includes(searchTitle.value.toLowerCase()) ||
      item.content?.toLowerCase().includes(searchTitle.value.toLowerCase()) ||
      item.description?.toLowerCase().includes(searchTitle.value.toLowerCase())
    )
  }
  
  filteredData.value = data
  currentPage.value = 1
}

const refreshData = () => {
  searchTitle.value = ''
  selectedSubject.value = ''
  selectedStatus.value = ''
  loadData()
}

const viewContent = (content: TeachingContentResponse) => {
  selectedContent.value = content
  viewDialogVisible.value = true
}

const handleDelete = async (content: TeachingContentResponse) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除教学内容 "${content.title}" 吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    loading.value = true
    const response = await deleteTeachingContentById(content.id)
    
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadData() // 重新加载数据
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除操作失败')
      console.error('Error deleting teaching content:', error)
    }
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
}

const formatTime = (timeStr: string | null | undefined): string => {
  if (!timeStr) return '未知'
  return new Date(timeStr).toLocaleString('zh-CN')
}

const getStatusText = (status: number | undefined): string => {
  switch (status) {
    case 0: return '草稿'
    case 1: return '已发布'
    case 2: return '已归档'
    default: return '未知'
  }
}

const getStatusType = (status: number | undefined): string => {
  switch (status) {
    case 0: return 'info'
    case 1: return 'success'
    case 2: return 'warning'
    default: return ''
  }
}
</script>

<style scoped>
.admin-course-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.header h2 {
  margin: 0;
  color: #303133;
  font-weight: 600;
}

.search-section,
.stats-section,
.table-section {
  margin-bottom: 20px;
}

.stats-card {
  text-align: center;
}

.stats-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stats-label {
  font-size: 14px;
  color: #606266;
}

.stats-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.no-description,
.no-content {
  color: #909399;
  font-style: italic;
}

.content-dialog {
  max-height: 60vh;
  overflow-y: auto;
}

.content-display {
  max-height: 200px;
  overflow-y: auto;
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.description-display {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  min-height: 50px;
}

.file-display {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  text-align: center;
}
</style>