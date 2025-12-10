<template>
  <div class="user-manage">
    <ProTable
      ref="proTable"
      :columns="columns"
      :request-api="getUserList"
      :search-tool-button="searchToolButton"
      :page-param-keys="{ pageNum: 'page', pageSize: 'limit' }"
    >
      <!-- Avatar Slot -->
      <template #avatar="{ row }">
        <el-avatar :size="40" :src="row.avatar">{{ row.username?.charAt(0).toUpperCase() }}</el-avatar>
      </template>

      <!-- Status Slot -->
      <template #status="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'danger'">
          {{ row.status === 1 ? $t('user.enable') : $t('user.disable') }}
        </el-tag>
      </template>

      <!-- Operation Slot -->
      <template #operation="{ row }">
        <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">{{ $t('common.edit') }}</el-button>
        <el-button link type="primary" :icon="Setting" @click="handleAssignRole(row)">{{ $t('user.assignRole') }}</el-button>
        <el-button link type="warning" :icon="Key" @click="handleResetPassword(row)">{{ $t('user.resetPassword') }}</el-button>
        <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">{{ $t('common.delete') }}</el-button>
      </template>
    </ProTable>

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
        <el-form-item :label="$t('user.avatar')" prop="avatar">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :auto-upload="false"
            :on-change="handleAvatarChange"
          >
            <img v-if="form.avatar" :src="form.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
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
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Key, Setting } from '@element-plus/icons-vue'
import { getUserList, addUser, updateUser, deleteUser, resetPassword, assignUserRole } from '@/api/user'
import { getRoleList } from '@/api/role'
import { useI18n } from 'vue-i18n'
import ProTable from '@/components/Common/ProTable/index.vue'

const { t } = useI18n()
const proTable = ref()

// 表格配置
const columns = [
  { type: 'selection', fixed: 'left', width: 55 },
  { prop: 'avatar', label: t('user.avatar'), width: 80, align: 'center', slot: 'avatar' },
  { 
    prop: 'username', 
    label: t('user.username'), 
    minWidth: 120, 
    search: { el: 'input', props: { placeholder: t('user.usernamePlaceholder') } } 
  },
  { prop: 'nickname', label: t('user.nickname'), minWidth: 120 },
  { prop: 'email', label: t('user.email'), minWidth: 180, showOverflowTooltip: true },
  { 
    prop: 'mobile', 
    label: t('user.mobile'), 
    minWidth: 120, 
    search: { el: 'input', props: { placeholder: t('user.mobilePlaceholder') } } 
  },
  { prop: 'roleName', label: t('user.role'), minWidth: 120, showOverflowTooltip: true },
  {
    prop: 'status', label: t('user.status'), width: 100, align: 'center', slot: 'status',
    search: {
      el: 'select',
      props: { placeholder: t('user.status') },
    },
    enum: [
      { label: t('user.enable'), value: 1 },
      { label: t('user.disable'), value: 0 }
    ]
  },
  { prop: 'createTime', label: t('user.createTime'), minWidth: 180 },
  { prop: 'operation', label: t('common.operation'), width: 340, align: 'center', fixed: 'right', slot: 'operation' }
]

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

const handleAvatarChange = (uploadFile: any) => {
  const reader = new FileReader()
  reader.readAsDataURL(uploadFile.raw)
  reader.onload = () => {
    form.avatar = reader.result as string
  }
}

// 新增
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const searchToolButton = [
  { label: t('user.addUser'), type: 'primary', icon: 'Plus', click: handleAdd }
]

const dialogTitle = computed(() => form.userId ? t('user.editUser') : t('user.addUser'))

// 校验规则
const rules = computed(() => ({
  username: [{ required: true, message: t('user.usernamePlaceholder'), trigger: 'blur' }],
  password: [{ required: !form.userId, message: '请输入密码', trigger: 'blur' }]
}))

// 角色分配状态
const roleDialogVisible = ref(false)
const roleLoading = ref(false)
const roleList = ref<any[]>([])
const currentUserId = ref<number | undefined>(undefined)
const selectedRoleId = ref<number | undefined>(undefined)

// 获取角色列表
const fetchRoleList = async () => {
  try {
    const res: any = await getRoleList({ page: 1, limit: 1000 })
    roleList.value = res.data.records || []
  } catch (error) {
    console.error(error)
  }
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
      const res: any = await deleteUser(row.userId)
      if (res.code === 200) {
        ElMessage.success(t('common.deleteSuccess'))
        proTable.value?.refresh()
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
      const res: any = await resetPassword(row.userId)
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
  currentUserId.value = row.userId
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
      proTable.value?.refresh()
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
          proTable.value?.refresh()
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
</script>

<style scoped>
.user-manage {
  padding: 0;
}
.avatar-uploader .avatar {
  width: 100px;
  height: 100px;
  display: block;
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
}
</style>
