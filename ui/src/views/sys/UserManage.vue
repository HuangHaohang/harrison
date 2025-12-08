<template>
  <div class="user-manage">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="hover">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item :label="$t('user.username')">
          <el-input v-model="searchForm.username" :placeholder="$t('user.usernamePlaceholder')" clearable />
        </el-form-item>
        <el-form-item :label="$t('user.mobile')">
          <el-input v-model="searchForm.mobile" :placeholder="$t('user.mobilePlaceholder')" clearable />
        </el-form-item>
        <el-form-item :label="$t('user.status')">
          <el-select v-model="searchForm.status" :placeholder="$t('user.status')" clearable style="width: 120px">
            <el-option :label="$t('user.enable')" :value="1" />
            <el-option :label="$t('user.disable')" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">{{ $t('common.search') }}</el-button>
          <el-button :icon="Refresh" @click="handleReset">{{ $t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="hover">
      <div class="toolbar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">{{ $t('user.addUser') }}</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border style="width: 100%">
        <el-table-column prop="avatar" :label="$t('user.avatar')" width="80" align="center">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.avatar">{{ row.username?.charAt(0).toUpperCase() }}</el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" :label="$t('user.username')" min-width="120" />
        <el-table-column prop="nickname" :label="$t('user.nickname')" min-width="120" />
        <el-table-column prop="email" :label="$t('user.email')" min-width="180" show-overflow-tooltip />
        <el-table-column prop="mobile" :label="$t('user.mobile')" min-width="120" />
        <el-table-column prop="roleName" :label="$t('user.role')" min-width="120" show-overflow-tooltip />
        <el-table-column prop="status" :label="$t('user.status')" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? $t('user.enable') : $t('user.disable') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" :label="$t('user.createTime')" min-width="180" />
        <el-table-column :label="$t('common.operation')" width="340" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">{{ $t('common.edit') }}</el-button>
            <el-button link type="primary" :icon="Setting" @click="handleAssignRole(row)">{{ $t('user.assignRole') }}</el-button>
            <el-button link type="warning" :icon="Key" @click="handleResetPassword(row)">{{ $t('user.resetPassword') }}</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">{{ $t('common.delete') }}</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item :label="$t('user.username')" prop="username">
          <el-input v-model="form.username" :placeholder="$t('user.usernamePlaceholder')" :disabled="!!form.userId" />
        </el-form-item>
        <el-form-item :label="$t('user.nickname')" prop="nickname">
          <el-input v-model="form.nickname" :placeholder="$t('user.nickname')" />
        </el-form-item>
        <el-form-item :label="$t('user.password')" prop="password" v-if="!form.userId">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item :label="$t('user.email')" prop="email">
          <el-input v-model="form.email" :placeholder="$t('user.emailPlaceholder')" />
        </el-form-item>
        <el-form-item :label="$t('user.mobile')" prop="mobile">
          <el-input v-model="form.mobile" :placeholder="$t('user.mobilePlaceholder')" />
        </el-form-item>
        <el-form-item :label="$t('user.status')" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">{{ $t('user.enable') }}</el-radio>
            <el-radio :value="0">{{ $t('user.disable') }}</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">{{ $t('common.confirm') }}</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog v-model="roleDialogVisible" :title="$t('user.assignRole')" width="400px">
      <el-row>
        <el-col :span="24">
          <el-form label-width="80px">
            <el-form-item :label="$t('user.role')">
              <el-select v-model="selectedRoleId" :placeholder="$t('user.rolePlaceholder')" style="width: 100%">
                <el-option
                  v-for="item in roleList"
                  :key="item.id"
                  :label="item.roleName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roleDialogVisible = false">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="submitRoleAssignment" :loading="roleLoading">
            {{ $t('common.confirm') }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Key, Setting } from '@element-plus/icons-vue'
import { getUserList, addUser, updateUser, deleteUser, resetPassword, assignUserRole } from '@/api/user'
import { getRoleList } from '@/api/role'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

// 搜索表单
const searchForm = reactive({
  username: '',
  mobile: '',
  status: undefined
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const loading = ref(false)
const tableData = ref([])

// 弹窗控制
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref()

const form = reactive({
  userId: undefined,
  username: '',
  nickname: '',
  password: '',
  email: '',
  mobile: '',
  status: 1,
  avatar: ''
})

const dialogTitle = computed(() => form.userId ? t('user.editUser') : t('user.addUser'))

// 校验规则
const rules = computed(() => ({
  username: [{ required: true, message: t('user.usernamePlaceholder'), trigger: 'blur' }],
  password: [{ required: !form.userId, message: '请输入密码', trigger: 'blur' }]
}))

// 角色分配状态
const roleDialogVisible = ref(false)
const roleLoading = ref(false)
const roleList = ref([])
const currentUserId = ref<number | undefined>(undefined)
const selectedRoleId = ref<number | undefined>(undefined)

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      page: pagination.currentPage,
      size: pagination.pageSize
    }
    const res: any = await getUserList(params)
    console.log(res)
    if (res.code === 200) {
      // 假设后端返回结构 { data: { list: [], total: 100 } }
      // 如果后端直接返回 { data: [] }，需要调整
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 获取角色列表
const fetchRoleList = async () => {
  try {
    const res: any = await getRoleList({ page: 1, limit: 1000 })
    roleList.value = res.data.list || []
  } catch (error) {
    console.error(error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  getList()
}

// 重置搜索
const handleReset = () => {
  searchForm.username = ''
  searchForm.mobile = ''
  searchForm.status = undefined
  handleSearch()
}

// 分页改变
const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  getList()
}

const handleCurrentChange = (val: number) => {
  pagination.currentPage = val
  getList()
}

// 新增
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  resetForm()
  Object.assign(form, row)
  // 清空密码字段，避免编辑时回显
  form.password = ''
  dialogVisible.value = true
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(t('common.deleteConfirm'), t('common.tips'), {
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancel'),
    type: 'warning'
  }).then(async () => {
    try {
      const res: any = await deleteUser(row.id)
      if (res.code === 200) {
        ElMessage.success(t('common.deleteSuccess'))
        getList()
      }
    } catch (error) {
      console.error(error)
    }
  })
}

// 重置密码
const handleResetPassword = (row: any) => {
  ElMessageBox.confirm(`确认重置用户 ${row.username} 的密码吗？`, t('common.tips'), {
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancel'),
    type: 'warning'
  }).then(async () => {
    try {
      const res: any = await resetPassword(row.id)
      if (res.code === 200) {
        ElMessage.success('密码重置成功')
      }
    } catch (error) {
      console.error(error)
    }
  })
}

// 分配角色
const handleAssignRole = (row: any) => {
  currentUserId.value = row.id
  selectedRoleId.value = row.roleId // 后端返回的当前角色ID
  roleDialogVisible.value = true
  if (roleList.value.length === 0) {
    fetchRoleList()
  }
}

// 提交角色分配
const submitRoleAssignment = async () => {
  if (!currentUserId.value || !selectedRoleId.value) return
  roleLoading.value = true
  try {
    const res: any = await assignUserRole({ userId: currentUserId.value, roleId: selectedRoleId.value })
    if (res.code === 200) {
      ElMessage.success(t('common.updateSuccess'))
      roleDialogVisible.value = false
      getList() // 刷新列表以更新角色显示
    }
  } catch (error) {
    console.error(error)
  } finally {
    roleLoading.value = false
  }
}

// 提交
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true
      try {
        const api = form.userId ? updateUser : addUser
        const res: any = await api(form)
        if (res.code === 200) {
          ElMessage.success(form.userId ? t('common.updateSuccess') : t('common.addSuccess'))
          dialogVisible.value = false
          getList()
        }
      } catch (error) {
        console.error(error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(form, {
    userId: undefined,
    username: '',
    nickname: '',
    password: '',
    email: '',
    mobile: '',
    status: 1,
    avatar: ''
  })
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.user-manage {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.search-form .el-form-item {
  margin-bottom: 0; /* 让搜索栏更紧凑 */
}
.table-card {
  min-height: 500px;
}
.toolbar {
  margin-bottom: 20px;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
