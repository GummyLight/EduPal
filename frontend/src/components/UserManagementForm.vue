<template>
  <div class="user-management-container">
    <el-header class="navbar">
      <div class="title">用户管理系统</div>
      <div class="user-info">
        <span>您好，{{ username }} 管理员</span>
      </div>
    </el-header>

    <el-main class="main-content">
      <!-- 顶部操作栏 -->
      <el-card class="action-card" shadow="never">
        <div class="action-buttons">
          <el-button type="primary" icon="Plus" @click="openRegisterDialog">
            新增用户
          </el-button>
          <el-button type="success" icon="Refresh" @click="refreshUserData">
            刷新数据
          </el-button>
        </div>
      </el-card>

      <!-- 用户数据展示 -->
      <el-row :gutter="20" class="data-overview">
        <el-col :span="8">
          <el-card shadow="hover" class="overview-card">
            <el-statistic title="总用户数" :value="userStats.totalUsers">
              <template #suffix>
                <el-icon><User /></el-icon>
              </template>
            </el-statistic>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="overview-card">
            <el-statistic title="学生数量" :value="userStats.totalStudents">
              <template #suffix>
                <el-icon><Reading /></el-icon>
              </template>
            </el-statistic>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="overview-card">
            <el-statistic title="教师数量" :value="userStats.totalTeachers">
              <template #suffix>
                <el-icon><UserFilled /></el-icon>
              </template>
            </el-statistic>
          </el-card>
        </el-col>
      </el-row>

      <!-- 用户列表 -->
      <el-card class="table-card" shadow="never">
        <template #header>
          <div class="table-header">
            <h3>用户列表</h3>
            <div class="filter-options">
              <el-select v-model="filterUserType" placeholder="用户类型" clearable>
                <el-option label="管理员" :value="0" />
                <el-option label="学生" :value="1" />
                <el-option label="教师" :value="2" />
              </el-select>
              <el-input v-model="searchKeyword" placeholder="搜索用户名或ID" clearable style="width: 200px;" />
            </div>
          </div>
        </template>

        <el-table :data="filteredUsers" border style="width: 100%" v-loading="loading">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="userId" label="用户ID" />
          <el-table-column prop="userName" label="用户名" />
          <el-table-column prop="userEmail" label="邮箱" />
          <el-table-column prop="userType" label="用户类型" width="100">
            <template #default="scope">
              <el-tag :type="getUserTypeTagType(scope.row.userType)">
                {{ getUserTypeName(scope.row.userType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="loginTime" label="最后登录" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.loginTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button link type="primary" size="small" @click="openEditDialog(scope.row)">
                编辑
              </el-button>
              <el-button link type="warning" size="small" @click="openManageDialog(scope.row)" v-if="scope.row.userType !== 0">
                管理
              </el-button>
              <el-button link type="danger" size="small" @click="deleteUser(scope.row)" v-if="scope.row.userType !== 0">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-main>

    <!-- 新增用户对话框 -->
    <el-dialog v-model="registerDialogVisible" title="新增用户" width="600px" @close="resetRegisterForm">
      <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" label-width="100px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="registerForm.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="registerForm.userName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="registerForm.userType" placeholder="请选择用户类型" style="width: 100%;">
            <el-option label="学生" :value="1" />
            <el-option label="教师" :value="2" />
          </el-select>
        </el-form-item>
        
        <!-- 学生特有字段 -->
        <template v-if="registerForm.userType === 1">
          <el-form-item label="班级" prop="studentClass">
            <el-input v-model="registerForm.studentClass" placeholder="请输入班级" />
          </el-form-item>
          <el-form-item label="性别" prop="studentGender">
            <el-select v-model="registerForm.studentGender" placeholder="请选择性别" style="width: 100%;">
              <el-option label="男" :value="0" />
              <el-option label="女" :value="1" />
            </el-select>
          </el-form-item>
        </template>
        
        <!-- 教师特有字段 -->
        <template v-if="registerForm.userType === 2">
          <el-form-item label="教学科目" prop="teachingSubject">
            <el-input v-model="registerForm.teachingSubject" placeholder="请输入教学科目" />
          </el-form-item>
          <el-form-item label="授课班级" prop="teacherClass">
            <el-select v-model="registerForm.teacherClass" multiple placeholder="请选择授课班级" style="width: 100%;">
              <el-option label="2501" value="2501" />
              <el-option label="2502" value="2502" />
              <el-option label="2503" value="2503" />
              <el-option label="2504" value="2504" />
            </el-select>
          </el-form-item>
        </template>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="registerDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRegister" :loading="submitting">
            {{ submitting ? '创建中...' : '创建用户' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑用户对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑用户信息" width="500px" @close="resetEditForm">
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <el-form-item label="用户ID">
          <el-input v-model="editForm.userId" disabled />
        </el-form-item>
        <el-form-item label="用户名" prop="newUserName">
          <el-input v-model="editForm.newUserName" placeholder="请输入新用户名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="newEmail">
          <el-input v-model="editForm.newEmail" placeholder="请输入新邮箱" />
        </el-form-item>
        <el-form-item label="密码" prop="newPassword">
          <el-input v-model="editForm.newPassword" type="password" placeholder="请输入新密码" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit" :loading="submitting">
            {{ submitting ? '更新中...' : '更新' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 用户管理对话框 (学生/教师特有信息管理) -->
    <el-dialog v-model="manageDialogVisible" :title="`管理${getUserTypeName(currentManageUser?.userType || 0)}信息`" width="500px" @close="resetManageForm">
      <template v-if="currentManageUser?.userType === 1">
        <!-- 学生管理 -->
        <el-form :model="manageForm" label-width="120px">
          <el-form-item label="学生ID">
            <el-input v-model="currentManageUser.userId" disabled />
          </el-form-item>
          <el-form-item label="当前班级">
            <el-input v-model="currentStudentInfo.studentClass" disabled />
          </el-form-item>
          <el-form-item label="新班级">
            <el-input v-model="manageForm.newClassId" placeholder="请输入新班级" />
          </el-form-item>
        </el-form>
      </template>
      
      <template v-if="currentManageUser?.userType === 2">
        <!-- 教师管理 -->
        <el-form :model="manageForm" label-width="120px">
          <el-form-item label="教师ID">
            <el-input v-model="currentManageUser.userId" disabled />
          </el-form-item>
          <el-form-item label="当前科目">
            <el-input v-model="currentTeacherInfo.teachingSubject" disabled />
          </el-form-item>
          <el-form-item label="新科目">
            <el-input v-model="manageForm.newSubject" placeholder="请输入新教学科目" />
          </el-form-item>
          <el-form-item label="当前班级">
            <el-tag v-for="cls in currentTeacherInfo.teacherClass" :key="cls" style="margin-right: 8px;">
              {{ cls }}
            </el-tag>
          </el-form-item>
          <el-form-item label="新班级">
            <el-select v-model="manageForm.newClass" multiple placeholder="请选择新授课班级" style="width: 100%;">
              <el-option label="2501" value="2501" />
              <el-option label="2502" value="2502" />
              <el-option label="2503" value="2503" />
              <el-option label="2504" value="2504" />
            </el-select>
          </el-form-item>
        </el-form>
      </template>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="manageDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitManage" :loading="submitting">
            {{ submitting ? '更新中...' : '更新' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { User, Reading, UserFilled, Plus, Refresh } from '@element-plus/icons-vue';
import { 
  register, 
  deleteAccount, 
  updateAccount, 
  updateStudentClass, 
  updateTeacherClass, 
  updateTeacherSubject, 
  listUsers, 
  listStudents, 
  listTeachers
} from '../api/admin';

// 类型定义 - 匹配backend响应格式
interface UserDetail {
  userId: string;
  userEmail: string;
  userType: number;
  userName: string;
  createTime: Date;
  loginTime: Date;
}

interface StudentDetail {
  studentId: string;
  studentName: string;
  studentGender: number;
  studentClass: string;
}

interface TeacherDetail {
  teacherId: string;
  teacherName: string;
  teachingSubject: string;
  teacherClass: string[];
}

// Props定义
interface Props {
  usertype: number;
  username: string;
  userid: string;
}

const props = defineProps<Props>();

// 响应式数据
const loading = ref(false);
const submitting = ref(false);
const filterUserType = ref<number>();
const searchKeyword = ref('');

// 用户数据
const allUsers = ref<UserDetail[]>([]);
const allStudents = ref<StudentDetail[]>([]);
const allTeachers = ref<TeacherDetail[]>([]);

// 统计数据
const userStats = reactive({
  totalUsers: 0,
  totalStudents: 0,
  totalTeachers: 0
});

// 对话框状态
const registerDialogVisible = ref(false);
const editDialogVisible = ref(false);
const manageDialogVisible = ref(false);

// 表单引用
const registerFormRef = ref();
const editFormRef = ref();

// 当前操作的用户
const currentManageUser = ref<UserDetail>();
const currentStudentInfo = ref<StudentDetail>({} as StudentDetail);
const currentTeacherInfo = ref<TeacherDetail>({} as TeacherDetail);

// 注册表单
const registerForm = reactive({
  userId: '',
  userName: '',
  email: '',
  password: '',
  userType: 1,
  studentClass: '',
  studentGender: 0,
  teachingSubject: '',
  teacherClass: [] as string[]
});

// 编辑表单
const editForm = reactive({
  userId: '',
  newUserName: '',
  newEmail: '',
  newPassword: ''
});

// 管理表单
const manageForm = reactive({
  newClassId: '',
  newSubject: '',
  newClass: [] as string[]
});

// 表单验证规则
const registerRules = {
  userId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }],
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }]
};

const editRules = {
  newUserName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  newEmail: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  newPassword: [{ required: true, message: '请输入密码', trigger: 'blur' }]
};

// 计算属性
const filteredUsers = computed(() => {
  let filtered = allUsers.value;
  
  // 按用户类型过滤
  if (filterUserType.value !== undefined) {
    filtered = filtered.filter(user => user.userType === filterUserType.value);
  }
  
  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    filtered = filtered.filter(user => 
      user.userName.toLowerCase().includes(keyword) || 
      user.userId.toLowerCase().includes(keyword)
    );
  }
  
  return filtered;
});

// 工具函数
const getUserTypeName = (type: number) => {
  switch (type) {
    case 0: return '管理员';
    case 1: return '学生';
    case 2: return '教师';
    default: return '未知';
  }
};

const getUserTypeTagType = (type: number) => {
  switch (type) {
    case 0: return 'danger';
    case 1: return 'success';
    case 2: return 'warning';
    default: return 'info';
  }
};

const formatDate = (date: Date | string) => {
  if (!date) return '-';
  const d = new Date(date);
  return d.toLocaleString('zh-CN');
};

// 数据加载方法
const loadAllData = async () => {
  loading.value = true;
  try {
    const [usersRes, studentsRes, teachersRes] = await Promise.all([
      listUsers(),
      listStudents(),
      listTeachers()
    ]);
    
    if (usersRes.status === 'success') {
      allUsers.value = usersRes.users;
      userStats.totalUsers = usersRes.userNum;
    }
    
    if (studentsRes.status === 'success') {
      allStudents.value = studentsRes.students;
      userStats.totalStudents = studentsRes.studentNum;
    }
    
    if (teachersRes.status === 'success') {
      allTeachers.value = teachersRes.teachers;
      userStats.totalTeachers = teachersRes.teacherNum;
    }
    
    ElMessage.success('数据加载成功');
  } catch (error) {
    console.error('加载数据失败:', error);
    ElMessage.error('数据加载失败');
  } finally {
    loading.value = false;
  }
};

const refreshUserData = () => {
  loadAllData();
};

// 对话框操作
const openRegisterDialog = () => {
  registerDialogVisible.value = true;
};

const openEditDialog = (user: UserDetail) => {
  editForm.userId = user.userId;
  editForm.newUserName = user.userName;
  editForm.newEmail = user.userEmail;
  editForm.newPassword = '';
  editDialogVisible.value = true;
};

const openManageDialog = async (user: UserDetail) => {
  currentManageUser.value = user;
  
  if (user.userType === 1) {
    // 查找学生详细信息
    const student = allStudents.value.find((s: StudentDetail) => s.studentId === user.userId);
    if (student) {
      currentStudentInfo.value = student;
    }
  } else if (user.userType === 2) {
    // 查找教师详细信息
    const teacher = allTeachers.value.find((t: TeacherDetail) => t.teacherId === user.userId);
    if (teacher) {
      currentTeacherInfo.value = teacher;
    }
  }
  
  manageDialogVisible.value = true;
};

// 表单提交
const submitRegister = async () => {
  try {
    await registerFormRef.value.validate();
    submitting.value = true;
    
    const additionalData: any = {};
    if (registerForm.userType === 1) {
      additionalData.studentClass = registerForm.studentClass;
      additionalData.studentGender = registerForm.studentGender;
    } else if (registerForm.userType === 2) {
      additionalData.teachingSubject = registerForm.teachingSubject;
      additionalData.teacherClass = registerForm.teacherClass;
    }
    
    const result = await register(
      registerForm.userId,
      registerForm.userName,
      registerForm.password,
      registerForm.email,
      registerForm.userType,
      additionalData
    );
    
    if (result.code === 200) {
      ElMessage.success('用户创建成功');
      registerDialogVisible.value = false;
      resetRegisterForm();
      await loadAllData();
    } else {
      ElMessage.error(result.message || '用户创建失败');
    }
  } catch (error) {
    console.error('创建用户失败:', error);
    ElMessage.error('创建用户失败');
  } finally {
    submitting.value = false;
  }
};

const submitEdit = async () => {
  try {
    await editFormRef.value.validate();
    submitting.value = true;
    
    const result = await updateAccount(
      editForm.userId,
      editForm.newUserName,
      editForm.newEmail,
      editForm.newPassword
    );
    
    if (result.code === 200) {
      ElMessage.success('用户信息更新成功');
      editDialogVisible.value = false;
      resetEditForm();
      await loadAllData();
    } else {
      ElMessage.error(result.message || '更新失败');
    }
  } catch (error) {
    console.error('更新用户失败:', error);
    ElMessage.error('更新用户失败');
  } finally {
    submitting.value = false;
  }
};

const submitManage = async () => {
  if (!currentManageUser.value) return;
  
  try {
    submitting.value = true;
    
    if (currentManageUser.value.userType === 1 && manageForm.newClassId) {
      // 更新学生班级
      const result = await updateStudentClass(currentManageUser.value.userId, manageForm.newClassId);
      if (result.code === 200) {
        ElMessage.success('学生班级更新成功');
      } else {
        ElMessage.error(result.message || '更新失败');
        return;
      }
    } else if (currentManageUser.value.userType === 2) {
      // 更新教师信息
      if (manageForm.newSubject) {
        const result = await updateTeacherSubject(currentManageUser.value.userId, manageForm.newSubject);
        if (result.code !== 200) {
          ElMessage.error(result.message || '科目更新失败');
          return;
        }
      }
      
      if (manageForm.newClass.length > 0) {
        const result = await updateTeacherClass(currentManageUser.value.userId, manageForm.newClass);
        if (result.code !== 200) {
          ElMessage.error(result.message || '班级更新失败');
          return;
        }
      }
      
      ElMessage.success('教师信息更新成功');
    }
    
    manageDialogVisible.value = false;
    resetManageForm();
    await loadAllData();
  } catch (error) {
    console.error('管理操作失败:', error);
    ElMessage.error('操作失败');
  } finally {
    submitting.value = false;
  }
};

const deleteUser = async (user: UserDetail) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${user.userName}" 吗？此操作不可撤销。`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    
    const result = await deleteAccount(0, user.userId); // 管理员权限删除
    
    if (result.code === 200) {
      ElMessage.success('用户删除成功');
      await loadAllData();
    } else {
      ElMessage.error(result.message || '删除失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error);
      ElMessage.error('删除用户失败');
    }
  }
};

// 重置表单
const resetRegisterForm = () => {
  Object.assign(registerForm, {
    userId: '',
    userName: '',
    email: '',
    password: '',
    userType: 1,
    studentClass: '',
    studentGender: 0,
    teachingSubject: '',
    teacherClass: []
  });
  registerFormRef.value?.resetFields();
};

const resetEditForm = () => {
  Object.assign(editForm, {
    userId: '',
    newUserName: '',
    newEmail: '',
    newPassword: ''
  });
  editFormRef.value?.resetFields();
};

const resetManageForm = () => {
  Object.assign(manageForm, {
    newClassId: '',
    newSubject: '',
    newClass: []
  });
  currentManageUser.value = undefined;
  currentStudentInfo.value = {} as StudentDetail;
  currentTeacherInfo.value = {} as TeacherDetail;
};

// 生命周期
onMounted(() => {
  if (props.usertype === 0) {
    loadAllData();
  }
});
</script>

<style scoped>
.user-management-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.navbar {
  background: white;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 60px;
}

.title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.user-info {
  color: #606266;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.action-card {
  margin-bottom: 20px;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.data-overview {
  margin-bottom: 20px;
}

.overview-card {
  text-align: center;
}

.table-card {
  margin-bottom: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-header h3 {
  margin: 0;
  color: #303133;
}

.filter-options {
  display: flex;
  gap: 12px;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .data-overview {
    flex-direction: column;
  }
  
  .table-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .filter-options {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
