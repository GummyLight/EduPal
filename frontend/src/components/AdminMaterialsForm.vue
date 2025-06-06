<template>
  <div class="admin-materials-container">
    <div class="header">
      <h2>资料管理</h2>
      <el-divider />
    </div>

    <!-- 搜索栏 -->
    <div class="search-section">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
            v-model="searchName"
            placeholder="按资料名称搜索"
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
              <span class="stats-label">总资料数</span>
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
      </el-row>
    </div>

    <!-- 资料列表表格 -->
    <div class="table-section">
      <el-table
        :data="paginatedData"
        v-loading="loading"
        style="width: 100%"
        stripe
        border
        height="500"
      >
        <el-table-column prop="resource_id" label="资料ID" width="120" />
        <el-table-column prop="name" label="资料名称" min-width="200">
          <template #default="{ row }">
            <span :title="row.name">{{ row.name || '未命名' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="subject" label="学科" width="100" />
        <el-table-column prop="teacher_id" label="教师ID" width="120" />
        <el-table-column prop="class_id" label="班级ID" width="120">
          <template #default="{ row }">
            <span>{{ row.class_id || '无' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="upload_time" label="上传时间" width="180">
          <template #default="{ row }">
            <span>{{ formatTime(row.upload_time) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200">
          <template #default="{ row }">
            <el-tooltip
              v-if="row.description"
              :content="row.description"
              placement="top"
            >
              <span class="description-text">
                {{ truncateText(row.description, 50) }}
              </span>
            </el-tooltip>
            <span v-else class="no-description">无描述</span>
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

    <!-- 查看资料内容对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      :title="`查看资料: ${selectedResource?.name || '未命名'}`"
      width="70%"
      top="5vh"
    >
      <div v-if="selectedResource" class="content-dialog">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="资料ID">
            {{ selectedResource.resource_id }}
          </el-descriptions-item>
          <el-descriptions-item label="资料名称">
            {{ selectedResource.name || '未命名' }}
          </el-descriptions-item>
          <el-descriptions-item label="学科">
            {{ selectedResource.subject }}
          </el-descriptions-item>
          <el-descriptions-item label="教师ID">
            {{ selectedResource.teacher_id }}
          </el-descriptions-item>
          <el-descriptions-item label="班级ID">
            {{ selectedResource.class_id || '无' }}
          </el-descriptions-item>
          <el-descriptions-item label="上传时间">
            {{ formatTime(selectedResource.upload_time) }}
          </el-descriptions-item>
        </el-descriptions>
        
        <el-divider content-position="left">资料内容</el-divider>
        <div class="content-display">
          <pre v-if="selectedResource.resource_content">{{ selectedResource.resource_content }}</pre>
          <span v-else class="no-content">无内容</span>
        </div>
        
        <el-divider content-position="left">描述信息</el-divider>
        <div class="description-display">
          <p v-if="selectedResource.description">{{ selectedResource.description }}</p>
          <span v-else class="no-description">无描述</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View, Delete } from '@element-plus/icons-vue'
import { 
  fetchAllResources, 
  searchResourcesByName, 
  deleteResourceById,
  type ResourceResponse 
} from '../api/materialsApi'

// 响应式数据
const loading = ref(false)
const allData = ref<ResourceResponse[]>([])
const filteredData = ref<ResourceResponse[]>([])
const searchName = ref('')
const selectedSubject = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const viewDialogVisible = ref(false)
const selectedResource = ref<ResourceResponse | null>(null)

// 计算属性
const totalCount = computed(() => allData.value.length)
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
    const data = await fetchAllResources()
    allData.value = data
    filteredData.value = data
  } catch (error) {
    ElMessage.error('加载资料数据失败')
    console.error('Error loading materials:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  if (!searchName.value.trim()) {
    applyFilters()
    return
  }
  
  loading.value = true
  try {
    const data = await searchResourcesByName(searchName.value.trim())
    filteredData.value = data
    currentPage.value = 1
  } catch (error) {
    ElMessage.error('搜索失败')
    console.error('Error searching materials:', error)
  } finally {
    loading.value = false
  }
}

const handleSubjectFilter = () => {
  applyFilters()
}

const applyFilters = () => {
  let data = allData.value
  
  if (selectedSubject.value) {
    data = data.filter(item => item.subject === selectedSubject.value)
  }
  
  if (searchName.value.trim()) {
    data = data.filter(item => 
      item.name?.toLowerCase().includes(searchName.value.toLowerCase()) ||
      item.description?.toLowerCase().includes(searchName.value.toLowerCase())
    )
  }
  
  filteredData.value = data
  currentPage.value = 1
}

const refreshData = () => {
  searchName.value = ''
  selectedSubject.value = ''
  loadData()
}

const viewContent = (resource: ResourceResponse) => {
  selectedResource.value = resource
  viewDialogVisible.value = true
}

const handleDelete = async (resource: ResourceResponse) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除资料 "${resource.name || '未命名'}" 吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    loading.value = true
    const response = await deleteResourceById(resource.resource_id)
    
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadData() // 重新加载数据
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除操作失败')
      console.error('Error deleting resource:', error)
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

const formatTime = (timeStr: string | null): string => {
  if (!timeStr) return '未知'
  return new Date(timeStr).toLocaleString('zh-CN')
}

const truncateText = (text: string, maxLength: number): string => {
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}
</script>

<style scoped>
.admin-materials-container {
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

.description-text {
  cursor: help;
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
</style>