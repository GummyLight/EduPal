<template>
  <el-card shadow="hover">
    <h2>资料管理</h2>
    <el-form :model="form" label-width="120px" @submit.prevent>
      <el-form-item label="资料名称">
        <el-input v-model="form.title" placeholder="请输入资料名称" />
      </el-form-item>
      <el-form-item label="上传文件">
        <el-upload
            action=""
            :auto-upload="false"
            :file-list="fileList"
            :on-change="handleChange"
            :before-upload="beforeUpload"
        >
          <el-button>选取文件</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">提交</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';

const form = reactive({
  title: '',
});

const fileList = ref([]);

function handleChange(file) {
  fileList.value = file.raw ? [file.raw] : [];
}

function beforeUpload() {
  return false; // 阻止自动上传
}

function submitForm() {
  console.log('提交资料信息:', form, fileList.value);
  ElMessage.success('资料提交成功！');
}

function resetForm() {
  form.title = '';
  fileList.value = [];
}
</script>
