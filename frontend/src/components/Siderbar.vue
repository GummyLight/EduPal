<!-- Sidebar.vue -->
<template>
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
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { defineProps } from 'vue';

// 定义props，接收usertype变量
const props = defineProps({
  usertype: {
    type: String,
    required: true
  }
});

const route = useRoute();
const activePath = computed(() => route.path);
</script>

<style scoped>
.sidebar-menu {
  width: 200px;
  min-height: 100vh;
  border-right: none;
  
}
</style>
