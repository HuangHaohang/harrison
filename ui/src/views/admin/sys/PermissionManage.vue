<template>
  <div class="permission-manage">
    <ProTable
      ref="proTable"
      :columns="columns"
      :request-api="getPermissionList"
      :pagination="false"
      :search-tool-button="searchToolButton"
      row-key="id"
      default-expand-all
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <!-- Name Slot -->
      <template #simplifiedName="{ row }">
        <span>{{ getPermissionName(row) }}</span>
      </template>

      <!-- Icon Slot -->
      <template #icon="{ row }">
        <el-icon v-if="row.icon">
          <component :is="row.icon" />
        </el-icon>
      </template>

      <!-- Type Slot -->
      <template #type="{ row }">
        <el-tag v-if="row.type === 1" type="primary">{{ $t('permission.menu') }}</el-tag>
        <el-tag v-else-if="row.type === 2" type="success">{{ $t('permission.button') }}</el-tag>
        <el-tag v-else type="info">{{ $t('permission.catalog') }}</el-tag>
      </template>

      <!-- Operation Slot -->
      <template #operation="{ row }">
        <el-button link type="primary" :icon="Plus" @click="handleAdd(row.id)" v-if="row.type !== 2">
          {{ $t('common.add') }}
        </el-button>
        <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">
          {{ $t('common.edit') }}
        </el-button>
        <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">
          {{ $t('common.delete') }}
        </el-button>
      </template>
    </ProTable>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="$t('permission.parent')" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="permissionTree"
            :props="defaultProps"
            check-strictly
            node-key="id"
            :render-after-expand="false"
            style="width: 100%"
            clearable
          />
        </el-form-item>
        
        <el-form-item :label="$t('permission.type')" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :value="0">{{ $t('permission.catalog') }}</el-radio>
            <el-radio :value="1">{{ $t('permission.menu') }}</el-radio>
            <el-radio :value="2">{{ $t('permission.button') }}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item :label="$t('permission.name')" prop="simplifiedName">
          <el-input v-model="form.simplifiedName" :placeholder="$t('permission.namePlaceholder')" />
        </el-form-item>
        
        <el-form-item :label="$t('permission.code')" prop="code" v-if="form.type === 2">
          <el-input v-model="form.code" :placeholder="$t('permission.codePlaceholder')" />
        </el-form-item>

        <el-form-item :label="$t('permission.path')" prop="path" v-if="form.type !== 2">
          <el-input v-model="form.path" :placeholder="$t('permission.pathPlaceholder')" />
        </el-form-item>

        <el-form-item :label="$t('permission.component')" prop="component" v-if="form.type === 1">
          <el-input v-model="form.component" :placeholder="$t('permission.componentPlaceholder')" />
        </el-form-item>

        <el-form-item :label="$t('permission.icon')" prop="icon" v-if="form.type !== 2">
          <el-input v-model="form.icon" :placeholder="$t('permission.iconPlaceholder')" />
        </el-form-item>

        <el-form-item :label="$t('permission.sort')" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">
            {{ $t('common.confirm') }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, nextTick } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Refresh } from '@element-plus/icons-vue'
import { getPermissionList, addPermission, updatePermission, deletePermission } from '@/api/permission'
import ProTable from '@/components/Common/ProTable/index.vue'
import type { ColumnProps } from '@/components/Common/ProTable/interface'

const { t, locale } = useI18n()
const proTable = ref()
const permissionTree = ref([])

// 获取权限名称（国际化）
const getPermissionName = (row: any) => {
  if (locale.value === 'zh-TW') {
    return row.traditionalName || row.simplifiedName
  }
  if (locale.value === 'en') {
    return row.englishName || row.simplifiedName
  }
  return row.simplifiedName
}

// 表格列配置
const columns: ColumnProps[] = [
  { prop: 'simplifiedName', label: t('permission.name'), minWidth: 200, showOverflowTooltip: true, slot: 'simplifiedName' },
  { prop: 'icon', label: t('permission.icon'), width: 80, align: 'center', slot: 'icon' },
  { prop: 'type', label: t('permission.type'), width: 100, align: 'center', slot: 'type' },
  { prop: 'code', label: t('permission.code'), minWidth: 150, showOverflowTooltip: true },
  { prop: 'path', label: t('permission.path'), minWidth: 150, showOverflowTooltip: true },
  { prop: 'component', label: t('permission.component'), minWidth: 200, showOverflowTooltip: true },
  { prop: 'sort', label: t('permission.sort'), width: 80, align: 'center' },
  { prop: 'createTime', label: t('user.createTime'), width: 180, align: 'center' },
  { prop: 'operation', label: t('common.operation'), width: 280, fixed: 'right', align: 'center', slot: 'operation' }
]

// 弹窗控制
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref()

const form = reactive<{
  id: number | undefined;
  parentId: number | null;
  type: number;
  simplifiedName: string;
  code: string;
  path: string;
  component: string;
  icon: string;
  sort: number;
}>({
  id: undefined,
  parentId: null,
  type: 0,
  simplifiedName: '',
  code: '',
  path: '',
  component: '',
  icon: '',
  sort: 0
})

const dialogTitle = computed(() => form.id ? t('permission.editPermission') : t('permission.addPermission'))

const rules = {
  simplifiedName: [{ required: true, message: t('permission.namePlaceholder'), trigger: 'blur' }],
  type: [{ required: true, message: t('permission.typePlaceholder'), trigger: 'change' }]
}

const defaultProps = {
  children: 'children',
  label: (data: any) => getPermissionName(data),
  value: 'id'
}

// 获取权限树（用于下拉选择）
const fetchPermissionTree = async () => {
  try {
    const res = await getPermissionList()
    permissionTree.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

// 新增
const handleAdd = (parentId: number | null = null) => {
  resetForm()
  form.parentId = parentId
  dialogVisible.value = true
  fetchPermissionTree()
}

const searchToolButton = [
  { label: t('permission.addPermission'), type: 'primary', icon: 'Plus', click: () => handleAdd() }
]

// 编辑
const handleEdit = (row: any) => {
  resetForm()
  Object.assign(form, row)
  dialogVisible.value = true
  fetchPermissionTree()
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(t('common.deleteConfirm'), t('common.tips'), {
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancel'),
    type: 'warning'
  }).then(async () => {
    try {
      await deletePermission(row.id)
      ElMessage.success(t('common.deleteSuccess'))
      proTable.value?.refresh()
    } catch (error) {
      console.error(error)
    }
  })
}

// 提交
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (form.id) {
          await updatePermission(form)
          ElMessage.success(t('common.updateSuccess'))
        } else {
          await addPermission(form)
          ElMessage.success(t('common.addSuccess'))
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

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(form, {
    id: undefined,
    parentId: null,
    type: 0,
    simplifiedName: '',
    code: '',
    path: '',
    component: '',
    icon: '',
    sort: 0
  })
}
</script>

<style scoped>
.permission-manage {
  height: 100%;
}
</style>
