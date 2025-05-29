<template>
  <div class="sidebar-wrapper">
    <el-menu
        class="sidebar-menu"
        :default-active="activePath"
        background-color="#2c3e50"
        text-color="#ecf0f1"
        active-text-color="#42b983"
        router
    >
      <el-menu-item index="/home">
        <i class="el-icon-house"></i>
        <span>首页</span>
      </el-menu-item>
      <el-menu-item v-if="usertype === 'student'" index="/home/course">
        <i class="el-icon-reading"></i>
        <span>教学内容</span>
      </el-menu-item>
      <el-menu-item v-if="usertype === 'student'" index="/home/materials">
        <i class="el-icon-folder"></i>
        <span>资料管理</span>
      </el-menu-item>
      <el-menu-item v-if="usertype === 'student'" index="/home/practice">
        <i class="el-icon-edit-outline"></i>
        <span>在线练习</span>
      </el-menu-item>
      <el-menu-item v-if="usertype === 'student'" index="/home/community">
        <i class="el-icon-chat-line-square"></i>
        <span>社区交流</span>
      </el-menu-item>
      <el-menu-item v-if="usertype === 'student'" index="/home/qa">
        <i class="el-icon-message"></i>
        <span>智能答疑</span>
      </el-menu-item>
      <el-menu-item v-if="usertype === 'teacher'" index="/home/course">
        <i class="el-icon-reading"></i>
        <span>教学内容</span>
      </el-menu-item>
      <el-menu-item v-if="usertype === 'teacher'" index="/home/materials">
        <i class="el-icon-folder"></i>
        <span>资料管理</span>
      </el-menu-item>
      <el-menu-item v-if="usertype === 'teacher'" index="/home/practice">
        <i class="el-icon-edit-outline"></i>
        <span>在线练习</span>
      </el-menu-item>
      <el-menu-item v-if="usertype === 'teacher'" index="/home/community">
        <i class="el-icon-chat-line-square"></i>
        <span>社区交流</span>
      </el-menu-item>
      <el-menu-item v-if="usertype === 'teacher'" index="/home/qa">
        <i class="el-icon-document"></i>
        <span>智能批改</span>
      </el-menu-item>
    </el-menu>

    <div class="logout-button-container">
      <el-button type="danger" class="logout-button" @click="logout">退出登录</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router'; // 导入 useRouter
import { defineProps } from 'vue';
import { ElMessage } from 'element-plus'; // 导入 ElMessage 用于提示

// 定义props，接收usertype变量
const props = defineProps({
  usertype: {
    type: String,
    required: true
  }
});

const route = useRoute();
const router = useRouter(); // 获取 router 实例

const activePath = computed(() => route.path);

const logout = () => {
  console.log('退出登录');
  // 实际项目中，这里需要清除用户会话信息（如 token），然后跳转到登录页
  ElMessage.info('您已成功退出登录。');
  router.push('/login'); // 跳转到登录页面
};
</script>

<style scoped>
/* 整个侧边栏的包装器，使用 flex 布局 */
.sidebar-wrapper {
  width: 200px;
  min-height: 100vh; /* 确保它占据至少视口的高度 */
  background-color: #2c3e50;
  display: flex; /* 启用 flex 布局 */
  flex-direction: column; /* 子项垂直排列 */
  justify-content: space-between; /* 菜单顶部，按钮底部 */
  border-right: none; /* 移除右侧边框 */
}

/* 侧边菜单的样式 */
.sidebar-menu {
  flex-grow: 1; /* 让菜单占据所有可用空间 */
  border-right: none; /* 移除菜单自身的右侧边框 */
  /* overflow-y: auto; */ /* 如果菜单项过多，菜单内部可以滚动 */
  /* 由于 .sidebar-wrapper 已经处理了整体高度和 flex，这里可以考虑移除 min-height */
  /* 或者保留，让菜单在内部也具有弹性 */
}

/* 退出登录按钮容器样式 */
.logout-button-container {
  padding: 15px; /* 给按钮一些内边距 */
  text-align: center; /* 按钮居中 */
  border-top: 1px solid #3d5a6b; /* 按钮上方添加分割线 */
}

.logout-button {
  width: 90%; /* 按钮宽度 */
  /* 可以根据需要调整颜色和大小 */
  background-color: #e74c3c; /* 红色按钮 */
  border-color: #e74c3c;
  color: white;
}
</style>