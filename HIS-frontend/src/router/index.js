import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/home'
    },
    {
      path: '/login',
      component: () => import('@/views/login')
    },
    {
      path: '/register',
      component: () => import('@/views/login')
    },
    {
      path: '/home',
      component: () => import('@/views/home'),
      meta: { requiresAuth: true },
      children: [
        // 访问 /home 时默认展示工作台
        {
          path: '',
          redirect: '/dashboard'
        },
        {
          path: '/dashboard',
          component: () => import('@/views/dashboard')
        },
        {
          path: '/dept',
          component: () => import('@/views/system/dept')
        },
        {
          path: '/user',
          component: () => import('@/views/system/user')
        },
        {
          path: '/registration',
          component: () => import('@/views/registration')
        },
        {
          path: '/registration/add',
          component: () => import('@/views/registration/add')
        }
      ]
    }
  ]
})

router.beforeEach(to => {
  const logged = localStorage.getItem('his_user')
  if ((to.path === '/login' || to.path === '/register') && logged) {
    return '/dashboard'
  }
  if (to.matched.some(record => record.meta.requiresAuth) && !logged) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
})

export default router
