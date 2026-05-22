import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import Login from '@/views/Login.vue'
import MainLayout from '@/views/MainLayout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/main',
    name: 'MainLayout',
    component: MainLayout
  },
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (!userStore.isLoggedIn && to.path !== '/login') {
    next('/login')
  } else if (userStore.isLoggedIn && to.path === '/login') {
    next('/main')
  } else {
    next()
  }
})

export default router
