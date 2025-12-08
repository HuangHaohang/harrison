<template>
  <div class="permission-manage">
    <!-- 工具栏 -->
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="handleAdd()">{{ $t('permission.addPermission') }}</el-button>
      <el-button :icon="Refresh" @click="getList">{{ $t('common.refresh') }}</el-button>
    </div>

    <!-- 权限列表表格 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      row-key="id"
      border
      default-expand-all
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      style="width: 100%"
    >
      <el-table-column prop="simplifiedName" :label="$t('permission.name')" min-width="200" show-overflow-tooltip>
        <template #default="{ row }">
          <span>{{ getPermissionName(row) }}</span>
        </template>
      </el-table-column>
      
      <el-table-column prop="icon" :label="$t('permission.icon')" width="80" align="center">
        <template #default="{ row }">
          <el-icon v-if="row.icon">
            <component :is="row.icon" />
          </el-icon>
        </template>
      </el-table-column>

      <el-table-column prop="type" :label="$t('permission.type')" width="100" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.type === 1" type="primary">{{ $t('permission.menu') }}</el-tag>
          <el-tag v-else-if="row.type === 2" type="success">{{ $t('permission.button') }}</el-tag>
          <el-tag v-else type="info">{{ $t('permission.other') }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="path" :label="$t('permission.path')" min-width="180" show-overflow-tooltip />
      <el-table-column prop="component" :label="$t('permission.component')" min-width="220" show-overflow-tooltip />
      <el-table-column prop="code" :label="$t('permission.code')" min-width="180" show-overflow-tooltip />
      <el-table-column prop="sort" :label="$t('permission.sort')" width="80" align="center" />
      
      <el-table-column :label="$t('common.operation')" width="260" align="center" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">{{ $t('common.edit') }}</el-button>
          <el-button link type="primary" :icon="Plus" @click="handleAdd(row)" v-if="row.type === 1">{{ $t('permission.addChild') }}</el-button>
          <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">{{ $t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item :label="$t('permission.parentId')" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="treeSelectData"
            :render-after-expand="false"
            check-strictly
            :placeholder="$t('permission.selectParent')"
            :props="{ label: 'simplifiedName', value: 'id' }"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item :label="$t('permission.menuType')" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :value="1">{{ $t('permission.menu') }}</el-radio>
            <el-radio :value="2">{{ $t('permission.button') }}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item :label="$t('permission.simplifiedName')" prop="simplifiedName">
              <el-input v-model="form.simplifiedName" :placeholder="$t('permission.simplifiedPlaceholder')" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="$t('permission.traditionalName')" prop="traditionalName">
              <el-input v-model="form.traditionalName" :placeholder="$t('permission.traditionalPlaceholder')" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="$t('permission.englishName')" prop="englishName">
              <el-input v-model="form.englishName" :placeholder="$t('permission.englishPlaceholder')" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item :label="$t('permission.code')" prop="code">
          <el-input v-model="form.code" :placeholder="$t('permission.codePlaceholder')" />
        </el-form-item>

        <template v-if="form.type === 1">
          <el-form-item :label="$t('permission.path')" prop="path">
            <el-input v-model="form.path" :placeholder="$t('permission.pathPlaceholder')" />
          </el-form-item>

          <el-form-item :label="$t('permission.component')" prop="component">
            <el-input v-model="form.component" :placeholder="$t('permission.componentPlaceholder')">
              <template #prepend>src/views/</template>
            </el-input>
             <div style="font-size: 12px; color: #999; line-height: 1.5;">
              {{ $t('permission.componentTip') }}
            </div>
          </el-form-item>

          <el-form-item :label="$t('permission.icon')" prop="icon">
            <el-input v-model="form.icon" :placeholder="$t('permission.iconPlaceholder')" />
          </el-form-item>
        </template>

        <el-form-item :label="$t('permission.sort')" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" controls-position="right" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">{{ $t('common.confirm') }}</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Refresh } from '@element-plus/icons-vue'
import { getPermissionList, addPermission, updatePermission, deletePermission } from '@/api/permission'
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n()

const currentLang = computed(() => locale.value)

const getPermissionName = (row: Permission) => {
  const lang = locale.value
  if (lang === 'zh-TW') {
    return row.traditionalName || row.simplifiedName
  }
  if (lang === 'en') {
    return row.englishName || row.simplifiedName
  }
  return row.simplifiedName
}

// 类型定义
interface Permission {
  id?: number
  parentId: number
  simplifiedName: string
  traditionalName?: string
  englishName?: string
  code?: string
  type: 1 | 2
  path?: string
  component?: string
  icon?: string
  sort: number
  children?: Permission[]
}

// 状态
const loading = ref(false)
const tableData = ref<Permission[]>([])
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref()

// 表单数据
const form = reactive<Permission>({
  id: undefined,
  parentId: 0,
  simplifiedName: '',
  traditionalName: '',
  englishName: '',
  code: '',
  type: 1,
  path: '',
  component: '',
  icon: '',
  sort: 0
})

// 树形选择数据（包含一个根节点）
const treeSelectData = computed(() => {
  const root = { id: 0, simplifiedName: t('permission.selectParent'), children: [] }
  return [root, ...tableData.value]
})

const dialogTitle = computed(() => form.id ? t('permission.editPermission') : t('permission.addPermission'))

// 校验规则
const rules = computed(() => ({
  parentId: [{ required: true, message: t('permission.selectParent'), trigger: 'change' }],
  simplifiedName: [{ required: true, message: t('permission.simplifiedPlaceholder'), trigger: 'blur' }],
  type: [{ required: true, message: t('permission.menuType'), trigger: 'change' }]
}))

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res: any = await getPermissionList()
    if (res.code === 200) {
      tableData.value = res.data || [] 
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 打开新增弹窗
const handleAdd = (row?: Permission) => {
  resetForm()
  if (row) {
    form.parentId = row.id!
  }
  dialogVisible.value = true
}

// 打开编辑弹窗
const handleEdit = (row: Permission) => {
  resetForm()
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = (row: Permission) => {
  ElMessageBox.confirm(t('permission.deleteConfirm'), t('common.tips'), {
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancel'),
    type: 'warning'
  }).then(async () => {
    try {
      const res: any = await deletePermission(row.id!)
      if (res.code === 200) {
        ElMessage.success(t('common.deleteSuccess'))
        getList()
      }
    } catch (error) {
      console.error(error)
    }
  })
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true
      try {
        const api = form.id ? updatePermission : addPermission
        const res: any = await api(form)
        if (res.code === 200) {
          ElMessage.success(form.id ? t('common.updateSuccess') : t('common.addSuccess'))
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

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  // 手动重置所有字段
  Object.assign(form, {
    id: undefined,
    parentId: 0,
    simplifiedName: '',
    traditionalName: '',
    englishName: '',
    code: '',
    type: 1,
    path: '',
    component: '',
    icon: '',
    sort: 0
  })
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.permission-manage {
  padding: 20px;
}
.toolbar {
  margin-bottom: 20px;
}
</style>
