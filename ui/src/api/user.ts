import http from '@/utils/http'

// 获取用户列表
export const getUserList = (params: any) => {
  return http.get('/sys/user/list', params)
}

// 新增用户
export const addUser = (data: any) => {
  return http.post('/sys/user/add', data)
}

// 修改用户
export const updateUser = (data: any) => {
  return http.put('/sys/user/update', data)
}

// 删除用户
export const deleteUser = (id: number) => {
  return http.delete(`/sys/user/delete/${id}`)
}

// 修改用户状态
export const updateUserStatus = (id: number, status: number) => {
  return http.put(`/sys/user/status/${id}`, { status })
}

// 重置密码
export const resetPassword = (id: number) => {
  return http.put(`/sys/user/reset-password/${id}`)
}

// 分配角色
export const assignUserRole = (data: { userId: number; roleId: number }) => {
  return http.post('/sys/user/assign-role', data)
}
