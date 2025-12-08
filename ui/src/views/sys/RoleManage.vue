<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <!-- 搜索栏 -->
        <el-card class="search-card">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item :label="$t('role.roleName')">
              <el-input v-model="searchForm.roleName" :placeholder="$t('role.roleNamePlaceholder')" clearable @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item :label="$t('role.roleCode')">
              <el-input v-model="searchForm.roleCode" :placeholder="$t('role.roleCodePlaceholder')" clearable @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon>{{ $t('common.search') }}
              </el-button>
              <el-button @click="handleReset">
                <el-icon><Refresh /></el-icon>{{ $t('common.reset') }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <el-col :span="24" class="mt-20">
        <!-- 表格栏 -->
        <el-card class="table-card">
          <div class="table-toolbar">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>{{ $t('role.addRole') }}
            </el-button>
          </div>

          <el-table v-loading="loading" :data="tableData" border style="width: 100%">
            <el-table-column prop="roleName" :label="$t('role.roleName')" min-width="150" show-overflow-tooltip />
            <el-table-column prop="roleCode" :label="$t('role.roleCode')" min-width="150" show-overflow-tooltip />
            <el-table-column prop="description" :label="$t('role.description')" min-width="200" show-overflow-tooltip />
            <el-table-column prop="createTime" :label="$t('user.createTime')" width="180" align="center" />
            <el-table-column :label="$t('common.operation')" width="250" fixed="right" align="center">
              <template #default="scope">
                <el-button link type="primary" @click="handleEdit(scope.row)">
                  <el-icon><Edit /></el-icon>{{ $t('common.edit') }}
                </el-button>
                <el-button link type="primary" @click="handleAssignPermission(scope.row)">
                  <el-icon><Setting /></el-icon>{{ $t('role.assignPermission') }}
                </el-button>
                <el-button link type="danger" @click="handleDelete(scope.row)">
                  <el-icon><Delete /></el-icon>{{ $t('common.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="pagination.currentPage"
              v-model:page-size="pagination.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="pagination.total"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? $t('role.addRole') : $t('role.editRole')"
      width="500px"
      @close="resetForm"
    >
      <el-row>
        <el-col :span="24">
          <el-form ref="roleFormRef" :model="roleForm" :rules="rules" label-width="100px">
            <el-form-item :label="$t('role.roleName')" prop="roleName">
              <el-input v-model="roleForm.roleName" :placeholder="$t('role.roleNamePlaceholder')" />
            </el-form-item>
            <el-form-item :label="$t('role.roleCode')" prop="roleCode">
              <el-input v-model="roleForm.roleCode" :placeholder="$t('role.roleCodePlaceholder')" />
            </el-form-item>
            <el-form-item :label="$t('role.description')" prop="description">
              <el-input
                v-model="roleForm.description"
                type="textarea"
                :placeholder="$t('role.descriptionPlaceholder')"
                :rows="3"
              />
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">
            {{ $t('common.confirm') }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 分配权限对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      :title="$t('role.assignPermission')"
      width="600px"
    >
      <el-row>
        <el-col :span="24">
          <div class="permission-tree-container">
            <el-tree
              ref="permissionTreeRef"
              :data="permissionData"
              show-checkbox
              node-key="id"
              :props="defaultProps"
              default-expand-all
              :check-strictly="checkStrictly"
            />
          </div>
        </el-col>
      </el-row>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="submitPermission" :loading="permissionLoading">
            {{ $t('common.confirm') }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox, ElTree } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Setting } from '@element-plus/icons-vue'
import { getRoleList, addRole, updateRole, deleteRole, getRolePermissions, assignPermissions } from '@/api/role'
import { getPermissionList } from '@/api/permission'

const { t, locale } = useI18n()

// 搜索表单
const searchForm = reactive({
  roleName: '',
  roleCode: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 对话框控制
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const submitLoading = ref(false)
const roleFormRef = ref()

const roleForm = reactive({
  id: undefined,
  roleName: '',
  roleCode: '',
  description: ''
})

const rules = {
  roleName: [{ required: true, message: t('role.roleNamePlaceholder'), trigger: 'blur' }],
  roleCode: [{ required: true, message: t('role.roleCodePlaceholder'), trigger: 'blur' }]
}

// 权限分配
const permissionDialogVisible = ref(false)
const permissionLoading = ref(false)
const permissionTreeRef = ref<InstanceType<typeof ElTree>>()
const permissionData = ref([])
const currentRoleId = ref<number | null>(null)
const checkStrictly = ref(true) // 初始设为true，避免回显时父子联动导致全选

const defaultProps = {
  children: 'children',
  label: (data: any) => {
    if (locale.value === 'zh-TW') {
      return data.traditionalName || data.simplifiedName
    }
    if (locale.value === 'en') {
      return data.englishName || data.simplifiedName
    }
    return data.simplifiedName
  }
}

// 获取角色列表
const fetchRoleList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.currentPage,
      limit: pagination.pageSize,
      ...searchForm
    }
    const res = await getRoleList(params)
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 获取权限列表
const fetchPermissionList = async () => {
  try {
    const res = await getPermissionList()    
    permissionData.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  fetchRoleList()
}

// 重置
const handleReset = () => {
  searchForm.roleName = ''
  searchForm.roleCode = ''
  handleSearch()
}

// 分页
const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  fetchRoleList()
}

const handleCurrentChange = (val: number) => {
  pagination.currentPage = val
  fetchRoleList()
}

// 新增
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  resetForm()
}

// 编辑
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  nextTick(() => {
    Object.assign(roleForm, row)
  })
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(t('common.deleteConfirm'), t('common.tips'), {
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancel'),
    type: 'warning'
  }).then(async () => {
    try {
      await deleteRole(row.id)
      ElMessage.success(t('common.deleteSuccess'))
      fetchRoleList()
    } catch (error) {
      console.error(error)
    }
  })
}

// 提交表单
const submitForm = async () => {
  if (!roleFormRef.value) return
  await roleFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (dialogType.value === 'add') {
          await addRole(roleForm)
          ElMessage.success(t('common.addSuccess'))
        } else {
          await updateRole(roleForm)
          ElMessage.success(t('common.updateSuccess'))
        }
        dialogVisible.value = false
        fetchRoleList()
      } catch (error) {
        console.error(error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  if (roleFormRef.value) {
    roleFormRef.value.resetFields()
  }
  roleForm.id = undefined
  roleForm.roleName = ''
  roleForm.roleCode = ''
  roleForm.description = ''
}

// 分配权限
const handleAssignPermission = async (row: any) => {
  currentRoleId.value = row.id
  permissionDialogVisible.value = true
  checkStrictly.value = true // 回显时开启 strict 模式，防止父节点自动选中导致子节点全选
  
  // 确保树数据已加载
  if (permissionData.value.length === 0) {
    await fetchPermissionList()
  }

  // 获取该角色的权限
  try {
    const res = await getRolePermissions(row.id)
    
    const checkedKeys = res.data || []
    
    nextTick(() => {
      if (permissionTreeRef.value) {
        permissionTreeRef.value.setCheckedKeys(checkedKeys)
        // 回显完成后，取消 strict 模式，让用户勾选时可以父子联动
        checkStrictly.value = false
      }
    })
  } catch (error) {
    console.error(error)
  }
}

// 提交权限分配
const submitPermission = async () => {
  if (!currentRoleId.value || !permissionTreeRef.value) return
  
  permissionLoading.value = true
  try {
    // 获取选中的节点和半选中的节点
    const checkedKeys = permissionTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys()
    const allKeys = [...checkedKeys, ...halfCheckedKeys]

    await assignPermissions({
      roleId: currentRoleId.value,
      permissionIds: allKeys as number[]
    })
    
    ElMessage.success(t('common.updateSuccess'))
    permissionDialogVisible.value = false
  } catch (error) {
    console.error(error)
  } finally {
    permissionLoading.value = false
  }
}

onMounted(() => {
  fetchRoleList()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.table-card {
  min-height: 500px;
}
.table-toolbar {
  margin-bottom: 20px;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
.permission-tree-container {
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
}
</style>
