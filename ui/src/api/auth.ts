import http from '@/utils/http'

export const login = (data: any) => {
  return http.post('/auth/login', data)
}

export const getUserInfo = () => {
  return http.get('/auth/info')
}
