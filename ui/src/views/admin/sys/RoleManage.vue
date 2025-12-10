<template>
  <div class="role-manage">
    <ProTable
      ref="proTable"
      :columns="columns"
      :request-api="getRoleList"
      :search-tool-button="searchToolButton"
      :page-param-keys="{ pageNum: 'page', pageSize: 'limit' }"
    >
      <!-- Operation Slot -->
      <template #operation="{ row }">
        <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">{{ $t('common.edit') }}</el-button>
        <el-button link type="primary" :icon="Setting" @click="handleAssignPermission(row)">{{ $t('role.assignPermission') }}</el-button>
        <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">{{ $t('common.delete') }}</el-button>
      </template>
    </ProTable>

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
import { ref, reactive, nextTick, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox, ElTree } from 'element-plus'
import { Plus, Edit, Delete, Setting } from '@element-plus/icons-vue'
import { getRoleList, addRole, updateRole, deleteRole, getRolePermissions, assignPermissions } from '@/api/role'
import { getPermissionList } from '@/api/permission'
import ProTable from '@/components/Common/ProTable/index.vue'
import type { ColumnProps } from '@/components/Common/ProTable/interface'

const { t, locale } = useI18n()

// ProTable 实例
const proTable = ref()

// 表格列配置
const columns: ColumnProps[] = [
  { type: 'selection', fixed: 'left', width: 55 },
  { type: 'index', label: '#', width: 80 },
  { 
    prop: 'roleName', 
    label: t('role.roleName'), 
    minWidth: 150, 
    search: { el: 'input', props: { placeholder: t('role.roleNamePlaceholder') } } 
  },
  { 
    prop: 'roleCode', 
    label: t('role.roleCode'), 
    minWidth: 150, 
    search: { el: 'input', props: { placeholder: t('role.roleCodePlaceholder') } } 
  },
  { prop: 'description', label: t('role.description'), minWidth: 200, showOverflowTooltip: true },
  { prop: 'createTime', label: t('user.createTime'), width: 180, align: 'center' },
  { prop: 'operation', label: t('common.operation'), width: 250, fixed: 'right', align: 'center', slot: 'operation' }
]

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

// 新增
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  resetForm()
}

// 搜索按钮配置
const searchToolButton = [
  { label: t('role.addRole'), type: 'primary', icon: 'Plus', click: handleAdd }
]

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
      proTable.value?.refresh()
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
        proTable.value?.refresh()
      } catch (error) {
        console.error(error)
      } finally {
        submitLoading.value = false
      }
    }
  })
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
</script>

<style scoped>
.role-manage {
  height: 100%;
}
.permission-tree-container {
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
}
</style>
