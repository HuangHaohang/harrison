import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'
import i18n from '@/i18n'

// Create a singleton message handler
const showMessage = (() => {
  let messageInstance: any = null;
  return (options: any) => {
    if (messageInstance) {
      messageInstance.close();
    }
    messageInstance = ElMessage(options);
  };
})();

// 创建 axios 实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api', // 从环境变量获取，默认为 /api
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      // 如果有 token，添加到 headers 中
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error: any) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const { code, msg } = response.data
    // 假设后端返回结构为 { code: 200, data: ..., msg: ... }
    // 如果没有 code 字段，可能直接返回了数据，视具体后端规范而定
    // 这里简单判断，如果 code 存在且不是 200，视为错误
    if (code && code !== 200) {
      showMessage({
        message: msg || i18n.global.t('http.systemError'),
        type: 'error',
        grouping: true
      })
      
      // 401 未授权，清除 token 并跳转登录页
      if (code === 401) {
        const authStore = useAuthStore()
        authStore.logout()
        router.push('/login')
      }
      return Promise.reject(new Error(msg || 'Error'))
    }
    return response.data
  },
  (error: any) => {
    console.error('err' + error)
    let message = error.message || i18n.global.t('http.requestFailed')
    if (error.response?.status === 401) {
      message = i18n.global.t('http.loginExpired')
      const authStore = useAuthStore()
      authStore.logout()
      router.push('/login')
    } else if (error.response?.status === 403) {
      message = i18n.global.t('http.noPermission')
    } else if (error.response?.status === 404) {
      message = i18n.global.t('http.notFound')
    } else if (error.response?.status === 500) {
      message = i18n.global.t('http.serverError')
    }
    showMessage({
      message: message,
      type: 'error',
      grouping: true
    })
    return Promise.reject(error)
  }
)

// 封装常用方法
const http = {
  get<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.get(url, { params, ...config })
  },

  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.post(url, data, config)
  },

  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.put(url, data, config)
  },

  delete<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.delete(url, { params, ...config })
  }
}

export default http
