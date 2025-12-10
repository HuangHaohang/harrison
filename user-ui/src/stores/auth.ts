import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo as getUserInfoApi } from '@/api/auth'
import router from '@/router'

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
        // user-ui 可能不需要动态路由逻辑，或者逻辑不同，暂时简化保留用户信息获取
        
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
    router.push('/login')
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
