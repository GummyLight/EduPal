import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import Register from '../views/Register.vue';
import Home from '../views/Home.vue'; // 包含 Sidebar
import HomeForm from '../components/HomeForm.vue'; // 首页主内容
import Course from '../views/Course.vue';
import Community from "../views/Community.vue";
import Materials from "../views/Materials.vue";
import Practice from "../views/Practice.vue";
import QA  from "../views/QA.vue";
import PracticeDetail from '../components/PracticeDetail.vue';
import TeacherHomeworkReview from '../components/TeacherHomeworkReview.vue';
import PracticeEdit from '../views/PracticeEdit.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  {
    path: '/home',
    component: Home,
    children: [
      { path: '', component: HomeForm }, // 默认子页面 /home
      { path: 'course', component: Course }, // 对应 /home/course
      { path: 'community', component: Community },
      { path:'materials', component: Materials },
      {
        path: 'practice', // 父路由 /home/practice
        component: Practice, // Practice.vue 应该包含 <router-view />
        children: [
          {
            path: '',
            name: 'PracticeList', // 给列表页一个名字
            component: () => import('../components/PracticeForm.vue')
          },
          {
            path: ':exerciseId', // 学生练习详情页路由: /home/practice/:exerciseId
            name: 'PracticeDetail',
            component: PracticeDetail,
            props: true,
          },
          {
            path: 'review/:exerciseId/:exerciseTitle', // 教师作业批改页路由: /home/practice/review/:exerciseId/:exerciseTitle
            name: 'TeacherHomeworkReview',
            component: TeacherHomeworkReview,
            props: true,
          },
          {
            path: 'edit/:exerciseId?', // <-- 新增路由，? 表示 exerciseId 可选，用于新增和编辑
            name: 'PracticeEdit',
            component: PracticeEdit,
            props: true, // 允许通过 props 接收路由参数
          }
        ]
      },
      { path: 'qa', component: QA },
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;