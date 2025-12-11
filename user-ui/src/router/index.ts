import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/Home/index.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/common/Login.vue')
    }
  ]
})

const whiteList = ['/login', '/']

// Permission guard
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  const token = localStorage.getItem('token')

  if (token) {
    if (to.path === '/login') {
      next('/index')
    } else {
      // If token exists but menus are empty (reload or first login)
      if (authStore.menus.length === 0) {
        try {
          const success = await authStore.fetchUserInfo()
          if (success) {
            next({ ...to, replace: true })
          } else {
            await authStore.logout()
            next('/login')
          }
        } catch (error) {
          await authStore.logout()
          next('/login')
        }
      } else {
        next()
      }
    }
  } else {
    // No token
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
