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
      { path: 'practice', component: Practice },
      { path: 'qa', component: QA },
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;