import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import Register from '../views/Register.vue';
import Home from '../views/Home.vue';

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/home', component: Home },
  {path:'/course',component:()=>import('../views/Course.vue')}
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;