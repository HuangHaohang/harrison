import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo as getUserInfoApi } from '@/api/auth'
import type { RouteRecordRaw } from 'vue-router'

// 导入所有视图组件
const modules = import.meta.glob(['/src/views/**/*.vue', '/src/components/**/*.vue'])

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<any>(null)
  const permissions = ref<string[]>([])
  const menus = ref<any[]>([])

  const isAuthenticated = computed(() => !!token.value)

  const login = async (credentials: any) => {
    try {
      const res = await loginApi(credentials)
      
      if (res.code === 200) {

        token.value = res.data.token
        
        localStorage.setItem('token', token.value!)
        await fetchUserInfo()
        return true
      }
      return false
    } catch (error) {
      console.error(error)
      return false
    }
  }

  const fetchUserInfo = async () => {
    if (!token.value) return false
    try {
      const res = await getUserInfoApi()
      console.log('res', res);
            
      if (res.code === 200) {
        user.value = {
          username: res.data.username,
          avatar: res.data.avatar
        }
        permissions.value = res.data.permissions || []
        menus.value = res.data.menus || []

        // 动态添加路由
        const dynamicRoutes = generateRoutes(menus.value)
        
        // 动态导入 router 以避免循环依赖
        const { default: router } = await import('@/router')

        // 定义根路由 /index
        const indexRoute: RouteRecordRaw = {
          path: '/index',
          name: 'Index',
          component: () => import('@/views/dashboard/index.vue'),
          meta: { requiresAuth: true },
          redirect: dynamicRoutes.length > 0 ? '/index/' + dynamicRoutes[0]!.path.replace(/^\/+/, '') : undefined,
          children: [
            ...dynamicRoutes,
            {
              path: '404',
              name: 'Dashboard404',
              component: () => import('@/views/common/404.vue'),
              meta: { title: '404', requiresAuth: true }
            }
          ]
        }

        // 添加到 router
        router.addRoute(indexRoute)

        // 最后添加全局 404 重定向到仪表盘内部
        router.addRoute({
          path: '/:pathMatch(.*)*',
          redirect: '/index/404'
        })

        return true
      }
      return false
    } catch (error) {
      console.error(error)
      return false
    }
  }

  const logout = async () => {
    token.value = null
    user.value = null
    permissions.value = []
    menus.value = []
    localStorage.removeItem('token')
    const { default: router } = await import('@/router')
    router.push('/login')
  }

  // 递归生成扁平化的路由数组
  const generateRoutes = (menuList: any[]): RouteRecordRaw[] => {
    let routes: RouteRecordRaw[] = []

    menuList.forEach(menu => {
      if (menu.children && menu.children.length > 0) {
        routes = routes.concat(generateRoutes(menu.children))
        return
      }

      if (menu.component) {
        const route: any = {
          path: menu.path.replace(/^\/+/, ''), // Ensure relative path for children
          name: menu.code || menu.simplifiedName,
          meta: { 
            title: menu.simplifiedName,
            icon: menu.icon,
            requiresAuth: true
          }
        }

        let componentPath = menu.component.replace(/^@/, '/src')
        if (modules[componentPath]) {
          route.component = modules[componentPath]
        } else {
          console.warn(`Component not found: ${componentPath}`)
          route.component = () => import('@/views/common/404.vue')
        }
        
        routes.push(route)
      }
    })
    console.log('routes', routes);
    
    return routes
  }

  return {
    token,
    user,
    permissions,
    menus,
    isAuthenticated,
    login,
    fetchUserInfo,
    logout
  }
})
