import http from '@/utils/http'

// 获取权限列表（树形）
export const getPermissionList = (params?: any) => {
  return http.get('/sys/permission/list', params)
}

// 新增权限
export const addPermission = (data: any) => {
  return http.post('/sys/permission/add', data)
}

// 修改权限
export const updatePermission = (data: any) => {
  return http.put('/sys/permission/update', data)
}

// 删除权限
export const deletePermission = (id: number) => {
  return http.delete(`/sys/permission/delete/${id}`)
}
