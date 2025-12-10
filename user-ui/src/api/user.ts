import http from '@/utils/http'

export const register = (data: any) => {
  return http.post('/auth/register', data)
}

export const sendCode = (data: { mobile: string }) => {
  return http.post('/auth/send-code', data)
}

export const resetPasswordByMobile = (data: any) => {
  return http.post('/auth/reset-password', data)
}
