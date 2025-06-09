<template>
  <div class="admin-community-container">
    <div class="header">
      <h2>浏览社区</h2>
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
        <el-col :span="8">
          <el-input
            v-model="searchAuthor"
            placeholder="按作者搜索"
            clearable
            @clear="handleAuthorSearch"
            @keyup.enter="handleAuthorSearch"
          >
            <template #append>
              <el-button @click="handleAuthorSearch" :icon="Search">搜索作者</el-button>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-button @click="refreshData" :icon="Refresh">刷新</el-button>
        </el-col>
        <el-col :span="4">
          <el-button @click="clearSearch" :icon="Close">清空搜索</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 统计信息 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-item">
              <span class="stats-label">总帖子数</span>
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
              <span class="stats-label">有附件帖子</span>
              <span class="stats-value">{{ attachmentCount }}</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-item">
              <span class="stats-label">总回复数</span>
              <span class="stats-value">{{ totalRepliesCount }}</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 帖子列表表格 -->
    <div class="table-section">
      <el-table
        :data="paginatedData"
        v-loading="loading"
        style="width: 100%"
        stripe
        border
        height="500"
      >
        <el-table-column prop="id" label="帖子ID" width="120" />
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <span :title="row.title">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="authorName" label="作者" width="120">
          <template #default="{ row }">
            <span>{{ row.authorName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="authorId" label="作者ID" width="120" />
        <el-table-column label="附件" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.attachedFileUrl" type="success" size="small">
              <el-icon><Paperclip /></el-icon>
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="回复数" width="100">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.replies?.length || 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180">
          <template #default="{ row }">
            <span>{{ formatTime(row.publishTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容预览" min-width="200">
          <template #default="{ row }">
            <el-tooltip :content="row.content" placement="top">
              <span class="content-preview">
                {{ truncateText(row.content, 100) }}
              </span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="viewPost(row)"
              :icon="View"
            >
              查看
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeletePost(row)"
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

    <!-- 查看帖子详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      :title="`查看帖子: ${selectedPost?.title}`"
      width="70%"
      top="5vh"
    >
      <div v-if="selectedPost" class="post-dialog">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="帖子ID">
            {{ selectedPost.id }}
          </el-descriptions-item>
          <el-descriptions-item label="标题">
            {{ selectedPost.title }}
          </el-descriptions-item>
          <el-descriptions-item label="作者">
            {{ selectedPost.authorName }}
          </el-descriptions-item>
          <el-descriptions-item label="作者ID">
            {{ selectedPost.authorId }}
          </el-descriptions-item>
          <el-descriptions-item label="发布时间">
            {{ formatTime(selectedPost.publishTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="回复数量">
            {{ selectedPost.replies?.length || 0 }}
          </el-descriptions-item>
        </el-descriptions>
        
        <el-divider content-position="left">帖子内容</el-divider>
        <div class="content-display">
          <pre v-if="selectedPost.content">{{ selectedPost.content }}</pre>
          <span v-else class="no-content">无内容</span>
        </div>

        <el-divider content-position="left" v-if="selectedPost.attachedFileUrl">附件</el-divider>
        <div v-if="selectedPost.attachedFileUrl" class="attachment-display">
          <el-link :href="selectedPost.attachedFileUrl" target="_blank" type="primary">
            <el-icon><Document /></el-icon>
            附件文件
          </el-link>
        </div>

        <el-divider content-position="left" v-if="selectedPost.replies && selectedPost.replies.length > 0">
          回复列表 ({{ selectedPost.replies.length }})
        </el-divider>
        <div v-if="selectedPost.replies && selectedPost.replies.length > 0" class="replies-section">
          <div v-for="reply in selectedPost.replies" :key="reply.id" class="reply-item">
            <div class="reply-header">
              <span class="reply-author">{{ reply.authorName }}</span>
              <span class="reply-time">{{ formatTime(reply.publishTime) }}</span>
              <el-button
                type="danger"
                size="small"
                text
                @click="handleDeleteReply(reply, selectedPost)"
                :icon="Delete"
              >
                删除回复
              </el-button>
            </div>
            <div class="reply-content">{{ reply.content }}</div>
            <div v-if="reply.attachedFileUrl" class="reply-attachment">
              <el-link :href="reply.attachedFileUrl" target="_blank" type="primary" size="small">
                <el-icon><Document /></el-icon>
                回复附件
              </el-link>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View, Delete, Document, Paperclip, Close } from '@element-plus/icons-vue'
import { CommunityService, type Post, type Reply } from '../api/community'

// 响应式数据
const loading = ref(false)
const allData = ref<Post[]>([])
const filteredData = ref<Post[]>([])
const searchTitle = ref('')
const searchAuthor = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const viewDialogVisible = ref(false)
const selectedPost = ref<Post | null>(null)

// 计算属性
const totalCount = computed(() => allData.value.length)
const attachmentCount = computed(() => allData.value.filter((item: Post) => item.attachedFileUrl).length)
const totalRepliesCount = computed(() => 
  allData.value.reduce((total: number, post: Post) => total + (post.replies?.length || 0), 0)
)

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
    const data = await CommunityService.getPosts()
    allData.value = data
    filteredData.value = data
  } catch (error) {
    ElMessage.error('加载社区数据失败')
    console.error('Error loading community posts:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  applyFilters()
}

const handleAuthorSearch = () => {
  applyFilters()
}

const applyFilters = () => {
  let data = allData.value
  
  if (searchTitle.value.trim()) {
    data = data.filter((item: Post) => 
      item.title.toLowerCase().includes(searchTitle.value.toLowerCase()) ||
      item.content.toLowerCase().includes(searchTitle.value.toLowerCase())
    )
  }
  
  if (searchAuthor.value.trim()) {
    data = data.filter((item: Post) => 
      item.authorName.toLowerCase().includes(searchAuthor.value.toLowerCase()) ||
      item.authorId.includes(searchAuthor.value)
    )
  }
  
  filteredData.value = data
  currentPage.value = 1
}

const clearSearch = () => {
  searchTitle.value = ''
  searchAuthor.value = ''
  filteredData.value = allData.value
  currentPage.value = 1
}

const refreshData = () => {
  clearSearch()
  loadData()
}

const viewPost = async (post: Post) => {
  try {
    // 获取完整的帖子详情（包括回复）
    const fullPost = await CommunityService.getPostDetail(post.id)
    selectedPost.value = fullPost
    viewDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取帖子详情失败')
    console.error('Error getting post detail:', error)
  }
}

const handleDeletePost = async (post: Post) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除帖子 "${post.title}" 吗？此操作不可恢复，同时会删除该帖子的所有回复。`,
      '确认删除',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    loading.value = true
    await CommunityService.deletePost(post.id)
    ElMessage.success('删除成功')
    loadData() // 重新加载数据
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除操作失败')
      console.error('Error deleting post:', error)
    }
  } finally {
    loading.value = false
  }
}

const handleDeleteReply = async (reply: Reply, post: Post) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除 ${reply.authorName} 的回复吗？此操作不可恢复。`,
      '确认删除回复',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    // 注意：这里需要根据实际的API接口来删除回复
    // 假设有删除回复的API接口
    ElMessage.info('删除回复功能需要后端提供相应的API接口')
    
    // 如果有删除回复的API，可以这样调用：
    // await CommunityService.deleteReply(post.id, reply.id)
    // ElMessage.success('删除回复成功')
    // 重新获取帖子详情
    // const updatedPost = await CommunityService.getPostDetail(post.id)
    // selectedPost.value = updatedPost
    // loadData() // 重新加载列表数据
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除回复操作失败')
      console.error('Error deleting reply:', error)
    }
  }
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
}

const formatTime = (timeStr: string): string => {
  if (!timeStr) return '未知'
  return new Date(timeStr).toLocaleString('zh-CN')
}

const truncateText = (text: string, maxLength: number): string => {
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}
</script>

<style scoped>
.admin-community-container {
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

.content-preview {
  cursor: help;
}

.no-content {
  color: #909399;
  font-style: italic;
}

.post-dialog {
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

.attachment-display {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  text-align: center;
}

.replies-section {
  max-height: 300px;
  overflow-y: auto;
}

.reply-item {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 10px;
  background-color: #fff;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  padding-bottom: 5px;
  border-bottom: 1px solid #ebeef5;
}

.reply-author {
  font-weight: bold;
  color: #409eff;
}

.reply-time {
  font-size: 12px;
  color: #909399;
}

.reply-content {
  margin-bottom: 8px;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.reply-attachment {
  text-align: right;
}
</style>
