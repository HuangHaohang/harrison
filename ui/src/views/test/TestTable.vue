<template>
  <ProTable
    ref="proTable"
    :request-api="getTableList"
    :init-param="initParam"
    :columns="columns"
  >
    <!-- 工具栏区域 -->
    <template #toolbar>
      <el-button type="primary" :icon="Plus" plain>{{ $t('user.addUser') }}</el-button>
      <el-button type="danger" :icon="Delete" plain>{{ $t('common.batchDelete') }}</el-button>
      <el-button type="warning" :icon="Download" plain>{{ $t('common.export') }}</el-button>
    </template>

    <!-- Custom column slot example -->
    <template #gender="{ row }">
      {{ row.gender === 1 ? $t('test.male') : $t('test.female') }}
    </template>

    <template #status="{ row }">
      <el-tag :type="row.status === 1 ? 'success' : 'danger'">
        {{ row.status === 1 ? $t('user.enable') : $t('user.disable') }}
      </el-tag>
    </template>

    <template #operation>
      <el-button link type="primary" :icon="Edit">{{ $t('common.edit') }}</el-button>
      <el-button link type="danger" :icon="Delete">{{ $t('common.delete') }}</el-button>
    </template>
  </ProTable>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { Plus, Delete, Download, Edit } from '@element-plus/icons-vue'
import ProTable from '@/components/Common/ProTable/index.vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

// 初始参数
const initParam = reactive({
  type: 1
})

// 表格配置
const columns = [
  { type: 'selection', fixed: 'left', width: 55 },
  { type: 'index', label: '#', width: 80 },
  {
    prop: 'username',
    label: t('test.name'),
    minWidth: 120,
    search: { el: 'input', props: { placeholder: t('test.namePlaceholder') } }
  },
  {
    prop: 'status',
    label: t('user.status'),
    width: 100,
    align: 'center',
    slot: 'status',
    search: {
      el: 'select',
      props: { placeholder: t('user.status') },
    },
    enum: [
      { label: t('user.enable'), value: 1 },
      { label: t('user.disable'), value: 0 }
    ]
  },
  {
    prop: 'age',
    label: t('test.age'),
    width: 100,
    sortable: true
  },
  {
    prop: 'gender',
    label: t('test.gender'),
    width: 100,
    slot: 'gender'
  },
  {
    prop: 'mobile',
    label: t('user.mobile'),
    minWidth: 150
  },
  {
    prop: 'email',
    label: t('user.email'),
    minWidth: 200,
    showOverflowTooltip: true
  },
  {
    prop: 'createTime',
    label: t('user.createTime'),
    minWidth: 180,
    sortable: true
  },
  {
    prop: 'operation',
    label: t('common.operation'),
    width: 200,
    fixed: 'right',
    align: 'center',
    slot: 'operation'
  }
]

// 模拟后端 API 请求
const getTableList = (params: any) => {
  return new Promise((resolve) => {
    console.log('请求参数：', params)
    
    setTimeout(() => {
      // 模拟数据
      const data = Array.from({ length: 10 }).map((_, index) => {
        const id = (params.pageNum - 1) * params.pageSize + index + 1
        return {
          id,
          username: `测试用户${id}`,
          age: Math.floor(Math.random() * 50) + 18,
          gender: Math.random() > 0.5 ? 1 : 2,
          mobile: `13800138${String(id).padStart(3, '0')}`,
          email: `test${id}@example.com`,
          status: Math.random() > 0.3 ? 1 : 0,
          createTime: '2023-12-08 12:00:00'
        }
      })

      // 模拟根据搜索条件过滤（实际由后端处理）
      let list = data
      if (params.username) {
        list = list.filter(item => item.username.includes(params.username))
      }
      if (params.status !== undefined && params.status !== '') {
        list = list.filter(item => item.status === params.status)
      }

      resolve({
        code: 200,
        data: {
          list: list,
          total: 100 // 模拟总数
        },
        msg: 'success'
      })
    }, 500) // 模拟网络延迟
  })
}
</script>

<style scoped>
.test-table-container {
  height: 100%;
  padding: 20px;
  background-color: #f0f2f5;
}
</style>
