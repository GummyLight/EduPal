<template>
  <el-menu
      class="sidebar-menu"
      :default-active="activePath"
      background-color="#2c3e50"
      text-color="#ecf0f1"
      active-text-color="#42b983"
      router
  >
    <el-menu-item index="/">
      <i class="el-icon-house"></i>
      <span>首页</span>
    </el-menu-item>
    <el-menu-item index="/course">
      <i class="el-icon-reading"></i>
      <span>教学内容</span>
    </el-menu-item>
    <el-menu-item index="/materials">
      <i class="el-icon-folder"></i>
      <span>资料管理</span>
    </el-menu-item>
    <el-menu-item index="/practice">
      <i class="el-icon-edit-outline"></i>
      <span>在线练习</span>
    </el-menu-item>
    <el-menu-item index="/qa">
      <i class="el-icon-message"></i>
      <span>智能答疑</span>
    </el-menu-item>
    <el-menu-item index="/community">
      <i class="el-icon-chat-line-square"></i>
      <span>社区交流</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';
const route = useRoute();
const activePath = route.path;
</script>

<style scoped>
.sidebar-menu {
  width: 200px;
  height: 100vh;
  border-right: none;
}
</style>
