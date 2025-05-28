<template>
  <div class="practice-edit-page">
    <el-header class="navbar">
      <div class="title">{{ isNew ? '添加练习' : '编辑练习' }} - {{ practiceForm.内容 }}</div>
      <el-button type="info" @click="router.back()">返回练习列表</el-button>
    </el-header>

    <el-main>
      <el-card class="edit-card">
        <el-form :model="practiceForm" label-width="120px" class="edit-form">
          <el-form-item label="练习主题">
            <el-input v-model="practiceForm.内容" placeholder="请输入练习主题" />
          </el-form-item>
          <el-form-item label="习题编号">
            <el-input v-model="practiceForm.习题号" :disabled="!isNew" placeholder="请输入习题编号" />
          </el-form-item>
          <el-form-item label="所属科目">
            <el-select v-model="practiceForm.科目" placeholder="请选择科目">
              <el-option label="数学" value="math" />
              <el-option label="物理" value="physics" />
              <el-option label="化学" value="chemistry" />
            </el-select>
          </el-form-item>
          <el-form-item label="题目类型">
            <el-input v-model="practiceForm.类型" placeholder="例如：选择题、填空题" />
          </el-form-item>
          <el-form-item label="难度等级">
            <el-select v-model="practiceForm.难度" placeholder="请选择难度">
              <el-option label="简单" value="easy" />
              <el-option label="中等" value="medium" />
              <el-option label="困难" value="hard" />
            </el-select>
          </el-form-item>
          <el-form-item label="关联知识点">
            <el-input v-model="practiceForm.知识点" placeholder="请输入关联知识点" />
          </el-form-item>
          <el-form-item label="发布日期">
            <el-date-picker
                v-model="practiceForm.发布时间"
                type="datetime"
                placeholder="选择发布日期时间"
                value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
          <el-form-item label="截止日期">
            <el-date-picker
                v-model="practiceForm.截止时间"
                type="datetime"
                placeholder="选择截止日期时间"
                value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>

          <el-form-item label="习题文件">
            <el-input v-model="practiceForm.习题文件路径" placeholder="请输入文件路径或点击上传" />
            <el-button type="primary" size="small" style="margin-left: 10px;">上传文件</el-button>
            <div v-if="practiceForm.习题文件路径" style="margin-top: 5px;">
              当前文件: <a :href="practiceForm.习题文件路径" target="_blank">{{ practiceForm.习题文件路径.split('/').pop() }}</a>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSubmit">保存练习</el-button>
            <el-button @click="router.back()">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const route = useRoute();
const router = useRouter();

const exerciseId = route.params.exerciseId as string | undefined; // 如果是添加，则 exerciseId 为 undefined
const isNew = computed(() => !exerciseId); // 判断是新增还是编辑

const practiceForm = ref<any>({
  习题号: '',
  科目: '',
  内容: '',
  发布时间: '',
  截止时间: '',
  发布人: '当前教师', // 模拟当前教师
  习题文件路径: '',
  类型: '',
  难度: '',
  知识点: '',
  已提交人数: 0,
  未提交人数: 0,
  已批改人数: 0,
  我的状态: '未提交',
  我的分数: null,
});

// 模拟的练习数据源 (与 PracticeForm.vue 中的 tableData 保持一致)
const mockPracticeData = ref([
  {
    习题号: 'EX20250501',
    科目: 'math',
    内容: '函数与图像基础练习',
    发布时间: '2025-05-20 10:00:00',
    截止时间: '2025-06-01 23:59:59',
    发布人: '张老师',
    习题文件路径: '/mock-files/exercise_EX20250501.pdf',
    类型: '选择题',
    难度: '中等',
    知识点: '函数图像',
    已提交人数: 15,
    未提交人数: 10,
    已批改人数: 10,
    我的状态: '未提交',
    我的分数: null,
  },
  {
    习题号: 'EX20250502',
    科目: 'physics',
    内容: '牛顿运动定律训练',
    发布时间: '2025-05-15 09:30:00',
    截止时间: '2025-05-30 23:59:59',
    发布人: '李老师',
    习题文件路径: '/mock-files/exercise_EX20250502.docx',
    类型: '填空题',
    难度: '困难',
    知识点: '牛顿力学',
    已提交人数: 20,
    未提交人数: 5,
    已批改人数: 15,
    我的状态: '已提交',
    我的分数: null,
  },
  {
    习题号: 'EX20250503',
    科目: 'chemistry',
    内容: '元素周期表复习',
    发布时间: '2025-05-25 14:00:00',
    截止时间: '2025-06-05 23:59:59',
    发布人: '王老师',
    习题文件路径: '/mock-files/exercise_EX20250503.zip',
    类型: '判断题',
    难度: '简单',
    知识点: '元素性质',
    已提交人数: 25,
    未提交人数: 0,
    已批改人数: 25,
    我的状态: '已批改',
    我的分数: 95,
  },
]);

onMounted(() => {
  if (!isNew.value && exerciseId) {
    loadPracticeDetail(exerciseId);
  } else {
    // 如果是新增，初始化一个空白表单
    practiceForm.value.习题号 = 'EX' + Date.now().toString().slice(-8); // 简单生成一个模拟ID
    practiceForm.value.发布人 = '当前教师'; // 默认发布人
    practiceForm.value.发布时间 = new Date().toISOString().slice(0, 19).replace('T', ' '); // 当前时间
  }
});

async function loadPracticeDetail(id: string) {
  console.log('[Mock] Loading practice detail for ID:', id);
  // 模拟异步获取数据
  setTimeout(() => {
    const found = mockPracticeData.value.find(item => item.习题号 === id);
    if (found) {
      // 深度拷贝以避免直接修改原始模拟数据
      practiceForm.value = JSON.parse(JSON.stringify(found));
      ElMessage.success('练习详情加载成功！');
    } else {
      ElMessage.error('模拟数据：未找到该练习！');
      router.back(); // 如果没找到，返回列表
    }
  }, 500);
}

async function handleSubmit() {
  console.log('[Mock] Submitting practice form:', practiceForm.value);

  // 简单的前端验证
  if (!practiceForm.value.内容 || !practiceForm.value.习题号 || !practiceForm.value.科目) {
    ElMessage.error('请填写完整练习主题、习题编号和科目！');
    return;
  }

  // 模拟异步提交数据
  setTimeout(() => {
    if (isNew.value) {
      // 模拟添加新练习
      mockPracticeData.value.push(JSON.parse(JSON.stringify(practiceForm.value)));
      ElMessage.success('练习添加成功！');
    } else {
      // 模拟更新现有练习
      const index = mockPracticeData.value.findIndex(item => item.习题号 === exerciseId);
      if (index !== -1) {
        // 确保不会覆盖只存在于列表页的统计数据
        const originalStats = {
          已提交人数: mockPracticeData.value[index].已提交人数,
          未提交人数: mockPracticeData.value[index].未提交人数,
          已批改人数: mockPracticeData.value[index].已批改人数,
          我的状态: mockPracticeData.value[index].我的状态,
          我的分数: mockPracticeData.value[index].我的分数,
        };
        practiceForm.value = { ...practiceForm.value, ...originalStats }; // 合并回统计数据
        mockPracticeData.value[index] = JSON.parse(JSON.stringify(practiceForm.value));
        ElMessage.success('练习更新成功！');
      } else {
        ElMessage.error('模拟更新失败：未找到该练习。');
      }
    }
    router.back(); // 保存后返回列表页
  }, 800);
}
</script>

<style scoped>
.practice-edit-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
}
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: #409EFF;
  color: white;
  border-bottom: 1px solid #ddd;
}
.title {
  font-weight: bold;
  font-size: 18px;
  color: white;
}
.el-main {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
  background-color: #f4f6f8;
}
.edit-card {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  background-color: #fff;
}
.edit-form .el-form-item {
  margin-bottom: 20px;
}
</style>