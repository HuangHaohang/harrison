import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo as getUserInfoApi } from '@/api/auth'
import router from '@/router'
import { type RouteRecordRaw } from 'vue-router'

// 导入所有视图组件
// 扩大范围，包含 views 和 components 目录下的组件
const modules = import.meta.glob(['/src/views/**/*.vue', '/src/components/**/*.vue'])
const Layout = () => import('@/layout/HomeView.vue')

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
        // 后端现在返回的是扁平结构 { username: "...", avatar: "...", menus: [], permissions: [] }
        // 并不是嵌套在 user 对象里
        user.value = {
          username: res.data.username,
          avatar: res.data.avatar
        }
        permissions.value = res.data.permissions || []
        menus.value = res.data.menus || []
        
        // 动态添加路由
        const routes = generateRoutes(menus.value)
        
        // 将所有动态路由添加到 HomeView 的 children 中
         routes.forEach(route => {
           router.addRoute('HomeView', route)
         })
         
         // 最后添加 404 路由，防止刷新页面时先匹配到 404
         router.addRoute({
           path: '/:pathMatch(.*)*',
           name: 'NotFound',
           component: () => import('@/views/404.vue'),
           meta: { requiresAuth: false }
         })

         // 添加一个重定向，如果访问 / 但没有子路由匹配，默认跳转到第一个动态路由
        if (routes.length > 0) {
           router.addRoute({
             path: '/:pathMatch(.*)*',
             redirect: routes[0].path
           })
        }
        
        return true
      }
      return false
    } catch (error) {
      console.error(error)
      return false
    }
  }

  const logout = () => {
    token.value = null
    user.value = null
    permissions.value = []
    menus.value = []
    localStorage.removeItem('token')
    // 重置路由，这里简单处理，刷新页面即可重置
    router.push('/login')
    // 实际上应该重置路由，或者由 window.location.reload() 强制刷新
  }

  // 递归生成扁平化的路由数组（只包含叶子节点）
  const generateRoutes = (menuList: any[]): RouteRecordRaw[] => {
    let routes: RouteRecordRaw[] = []

    menuList.forEach(menu => {
      // 如果有子节点，递归处理子节点，当前节点不生成路由（因为它只是菜单目录）
      if (menu.children && menu.children.length > 0) {
        routes = routes.concat(generateRoutes(menu.children))
        return
      }

      // 处理叶子节点
      if (menu.component) {
         const route: any = {
          // 移除开头的 /，使其成为 HomeView 的相对子路由
          // 例如 /system/user -> system/user
          // 这样访问 /system/user 就会匹配到 HomeView -> UserComponent
          path: menu.path.startsWith('/') ? menu.path.substring(1) : menu.path,
          name: menu.code || menu.name, 
          meta: { 
            title: menu.name,
            icon: menu.icon,
            requiresAuth: true
          }
        }

        // 动态匹配组件路径
        // 后端返回格式如: "@/views/system/user/index.vue"
        // 将 @ 替换为 /src 以匹配 import.meta.glob 的 key
        const componentPath = menu.component.replace(/^@/, '/src')
        
        if (modules[componentPath]) {
          route.component = modules[componentPath]
        } else {
          console.warn(`Component not found: ${componentPath}, raw: ${menu.component}`)
          // 如果找不到组件，可以给一个默认的 404 或者空组件，防止路由崩溃
          route.component = () => import('@/views/404.vue')
        }
        
        routes.push(route)
      }
    })

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
