import http from '@/utils/http'

// 获取角色列表
export const getRoleList = (params: any) => {
  return http.get('/sys/role/list', params)
}

// 新增角色
export const addRole = (data: any) => {
  return http.post('/sys/role/add', data)
}

// 修改角色
export const updateRole = (data: any) => {
  return http.put('/sys/role/update', data)
}

// 删除角色
export const deleteRole = (id: number) => {
  return http.delete(`/sys/role/delete/${id}`)
}

// 获取角色拥有的权限ID列表
export const getRolePermissions = (roleId: number) => {
  return http.get(`/sys/role/permissions/${roleId}`)
}

// 分配权限
export const assignPermissions = (data: { roleId: number; permissionIds: number[] }) => {
  return http.post('/sys/role/assign-permissions', data)
}
