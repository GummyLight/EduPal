<template>
  <div class="practice-edit">
    <el-header class="navbar">
      <div class="title">{{ isEdit ? '编辑练习' : '创建练习' }}</div>
      <div class="actions">
        <el-button @click="goBack">返回</el-button>
      </div>
    </el-header>

    <el-main class="edit-content">
      <el-card shadow="always">
        <el-form
          ref="quizFormRef"
          :model="quizForm"
          :rules="formRules"
          label-width="120px"
          class="quiz-form"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="练习标题" prop="title">
                <el-input
                  v-model="quizForm.title"
                  placeholder="请输入练习标题"
                  maxlength="100"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所属学科" prop="subject">
                <el-select v-model="quizForm.subject" placeholder="请选择学科">
                  <el-option label="数学" value="数学" />
                  <el-option label="物理" value="物理" />
                  <el-option label="英语" value="英语" />
                  <el-option label="化学" value="化学" />
                  <el-option label="语文" value="语文" />
                  <el-option label="生物" value="生物" />
                  <el-option label="历史" value="历史" />
                  <el-option label="地理" value="地理" />
                  <el-option label="政治" value="政治" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="内容类型" prop="contentType">
                <el-select v-model="quizForm.contentType" placeholder="请选择内容类型">
                  <el-option label="选择题" value="choice" />
                  <el-option label="填空题" value="blank" />
                  <el-option label="简答题" value="essay" />
                  <el-option label="综合题" value="comprehensive" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="难度等级" prop="difficulty">
                <el-select v-model="quizForm.difficulty" placeholder="请选择难度">
                  <el-option label="简单" value="简单" />
                  <el-option label="中等" value="中等" />
                  <el-option label="困难" value="困难" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="截止时间" prop="deadline">
                <el-date-picker
                  v-model="quizForm.deadline"
                  type="datetime"
                  placeholder="请选择截止时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="班级选择" prop="classSelection">
                <el-select
                  v-model="classSelection"
                  multiple
                  placeholder="请选择班级"
                  style="width: 100%"
                >
                  <el-option
                    v-for="classItem in classList"
                    :key="classItem.classId"
                    :label="classItem.className"
                    :value="classItem.classId"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="知识点" prop="knowledgePoints">
            <el-select
              v-model="quizForm.knowledgePoints"
              multiple
              filterable
              allow-create
              placeholder="请输入或选择知识点"
              style="width: 100%"
            >
              <el-option
                v-for="point in knowledgePointOptions"
                :key="point"
                :label="point"
                :value="point"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="练习描述" prop="description">
            <el-input
              v-model="quizForm.description"
              type="textarea"
              :rows="4"
              placeholder="请输入练习描述"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">
              {{ isEdit ? '更新练习' : '创建练习' }}
            </el-button>
            <el-button @click="handleReset">重置</el-button>
            <el-button @click="goBack">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus';
import { createQuiz, modifyQuiz, getMaxQuizId, getTeacherQuiz, CreateQuizRequest, ModifyQuizRequest } from '../api/quiz';
import axios from 'axios';

const router = useRouter();
const route = useRoute();

// API 基地址
const API_BASE = 'http://localhost:8080';

// 定义 props（如果通过 props 传递）
const props = defineProps({
  usertype: {
    type: Number,
    default: 2,
  },
  username: {
    type: String,
    default: '',
  },
  userid: {
    type: String,
    default: '',
  },
});

// 从路由查询参数获取用户信息
const userInfo = computed(() => ({
  usertype: Number(route.query.usertype) || props.usertype,
  username: route.query.username as string || props.username,
  userid: route.query.userid as string || props.userid,
}));

// 表单引用
const quizFormRef = ref<FormInstance>();

// 是否编辑模式
const isEdit = computed(() => !!route.params.exerciseId);

// 提交状态
const submitting = ref(false);

// 班级列表
const classList = ref<Array<{ classId: string; className: string }>>([]);

// 班级选择
const classSelection = ref<string[]>([]);

// 知识点选项
const knowledgePointOptions = ref<string[]>([
  '函数与方程',
  '数列',
  '不等式',
  '几何图形',
  '概率统计',
  '力学基础',
  '电磁学',
  '光学',
  '原子物理',
  '化学反应',
  '有机化学',
  '无机化学',
  '物理化学'
]);

// 表单数据
const quizForm = reactive({
  title: '',
  subject: '',
  contentType: '',
  difficulty: '',
  knowledgePoints: [] as string[],
  description: '',
  deadline: '',
});

// 表单验证规则
const formRules: FormRules = {
  title: [
    { required: true, message: '请输入练习标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度应在 2-100 个字符', trigger: 'blur' }
  ],
  subject: [
    { required: true, message: '请选择学科', trigger: 'change' }
  ],
  contentType: [
    { required: true, message: '请选择内容类型', trigger: 'change' }
  ],
  difficulty: [
    { required: true, message: '请选择难度等级', trigger: 'change' }
  ],
  deadline: [
    { required: true, message: '请选择截止时间', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入练习描述', trigger: 'blur' },
    { min: 10, max: 500, message: '描述长度应在 10-500 个字符', trigger: 'blur' }
  ]
};

// 获取班级列表
const fetchTeacherClasses = async () => {
  try {
    const response = await axios.get(`${API_BASE}/quiz/getTeacherClass`, {
      params: { userId: userInfo.value.userid },
    });
    if (response.data.code === 200) {
      classList.value = response.data.data.map((cls: any) => ({
        classId: cls.classId,
        className: cls.className,
      }));
    } else {
      ElMessage.error('获取班级列表失败');
    }
  } catch (error) {
    ElMessage.error('请求班级列表失败');
    console.error(error);
  }
};

// 加载编辑数据
const loadEditData = async () => {
  if (isEdit.value && route.params.exerciseId) {
    try {
      const quizId = parseInt(route.params.exerciseId as string);
      
      // 先尝试从URL查询参数获取数据
      if (route.query.title) {
        Object.assign(quizForm, {
          title: route.query.title as string || '',
          subject: route.query.subject as string || '',
          contentType: route.query.contentType as string || '',
          difficulty: route.query.difficulty as string || '',
          knowledgePoints: route.query.knowledgePoints ? (route.query.knowledgePoints as string).split(',') : [],
          description: route.query.description as string || '',
          deadline: route.query.deadline as string || '',
        });
        
        // 设置班级选择
        if (route.query.class1 || route.query.class2) {
          const classes = [];
          if (route.query.class1) classes.push(route.query.class1 as string);
          if (route.query.class2) classes.push(route.query.class2 as string);
          classSelection.value = classes;
        }
      } else {
        // 如果URL参数不存在，从后端获取quiz详情
        const response = await getTeacherQuiz(userInfo.value.userid);
        if (response.status === 'success' && response.quizzes) {
          const targetQuiz = response.quizzes.find((quiz: any) => quiz.quizId === quizId);
          if (targetQuiz) {
            Object.assign(quizForm, {
              title: targetQuiz.title || '',
              subject: targetQuiz.subject || '',
              contentType: targetQuiz.contentType || '',
              difficulty: targetQuiz.difficulty || '',
              knowledgePoints: targetQuiz.knowledgePoints ? targetQuiz.knowledgePoints.split(',') : [],
              description: targetQuiz.description || '',
              deadline: targetQuiz.deadline ? new Date(targetQuiz.deadline).toISOString().slice(0, 16) : '',
            });
            
            // 设置班级选择
            const classes = [];
            if (targetQuiz.class1) classes.push(targetQuiz.class1);
            if (targetQuiz.class2) classes.push(targetQuiz.class2);
            classSelection.value = classes;
          } else {
            throw new Error('未找到对应的quiz');
          }
        } else {
          throw new Error('获取quiz列表失败');
        }
      }
      
      console.log('编辑模式，练习ID:', route.params.exerciseId);
      console.log('加载的练习数据:', quizForm);
    } catch (error) {
      console.error('加载编辑数据失败:', error);
      ElMessage.error('加载练习数据失败');
    }
  }
};

// 提交表单
const handleSubmit = async () => {
  if (!quizFormRef.value) return;

  try {
    await quizFormRef.value.validate();
  } catch (error) {
    ElMessage.error('请检查表单信息');
    return;
  }

  if (classSelection.value.length === 0) {
    ElMessage.error('请至少选择一个班级');
    return;
  }

  submitting.value = true;

  try {
    const currentTime = new Date().toISOString().slice(0, 19).replace('T', ' ');
    
    const baseData = {
      title: quizForm.title,
      subject: quizForm.subject,
      contentType: quizForm.contentType,
      difficulty: quizForm.difficulty,
      knowledgePoints: Array.isArray(quizForm.knowledgePoints) ? quizForm.knowledgePoints.join(',') : quizForm.knowledgePoints,
      description: quizForm.description,
      teacherId: userInfo.value.userid,
      teacherName: userInfo.value.username,
      createTime: currentTime,
      deadline: quizForm.deadline,
      class1: classSelection.value[0] || '',
      class2: classSelection.value[1] || '',
    };

    let response;
    if (isEdit.value) {
      const modifyData: ModifyQuizRequest = {
        ...baseData,
        quizId: Number(route.params.exerciseId),
      };
      response = await modifyQuiz(modifyData);
    } else {
      const createData: CreateQuizRequest = baseData;
      response = await createQuiz(createData);
    }

    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '练习更新成功！' : '练习创建成功！');
      // 返回到练习列表页面，并传递成功状态以便刷新
      router.push({
        name: 'PracticeList',
        query: {
          usertype: userInfo.value.usertype.toString(),
          username: userInfo.value.username,
          userid: userInfo.value.userid,
          refresh: 'true' // 标记需要刷新
        }
      });
    } else {
      ElMessage.error(response.message || '操作失败');
    }
  } catch (error) {
    ElMessage.error('操作失败，请稍后重试');
    console.error(error);
  } finally {
    submitting.value = false;
  }
};

// 重置表单
const handleReset = () => {
  if (!quizFormRef.value) return;
  quizFormRef.value.resetFields();
  classSelection.value = [];
};

// 返回
const goBack = () => {
  router.back();
};

// 组件挂载
onMounted(async () => {
  await fetchTeacherClasses();
  await loadEditData();
});
</script>

<style scoped>
.practice-edit {
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

.edit-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f4f6f8;
}

.quiz-form {
  background-color: white;
  padding: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-button {
  margin-right: 10px;
}
</style>
