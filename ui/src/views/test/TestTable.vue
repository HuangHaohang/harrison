<template>
  
    <ProTable
      ref="proTable"
      :request-api="getTableList"
      :init-param="initParam"
    >
      <!-- 1. 搜索区域 -->
      <template #search="{ search, reset }">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item :label="$t('test.name')">
            <el-input v-model="searchForm.username" :placeholder="$t('test.namePlaceholder')" clearable />
          </el-form-item>
          <el-form-item :label="$t('user.status')">
            <el-select v-model="searchForm.status" :placeholder="$t('user.status')" clearable style="width: 180px">
              <el-option :label="$t('user.enable')" :value="1" />
              <el-option :label="$t('user.disable')" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="search">{{ $t('common.search') }}</el-button>
            <el-button :icon="Refresh" @click="reset">{{ $t('common.reset') }}</el-button>
          </el-form-item>
        </el-form>
      </template>

      <!-- 2. 工具栏区域 -->
      <template #toolbar>
        <el-button type="primary" :icon="Plus" plain>{{ $t('user.addUser') }}</el-button>
        <el-button type="danger" :icon="Delete" plain>{{ $t('common.batchDelete') }}</el-button>
        <el-button type="warning" :icon="Download" plain>{{ $t('common.export') }}</el-button>
      </template>

      <!-- 3. 表格列 -->
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="index" label="#" width="80" align="center" />
      <el-table-column prop="username" :label="$t('test.name')" min-width="120" />
      <el-table-column prop="age" :label="$t('test.age')" width="100" sortable />
      <el-table-column prop="gender" :label="$t('test.gender')" width="100">
        <template #default="scope">
          {{ scope.row.gender === 1 ? $t('test.male') : $t('test.female') }}
        </template>
      </el-table-column>
      <el-table-column prop="mobile" :label="$t('user.mobile')" min-width="150" />
      <el-table-column prop="email" :label="$t('user.email')" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" :label="$t('user.status')" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? $t('user.enable') : $t('user.disable') }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" :label="$t('user.createTime')" min-width="180" sortable />
      <el-table-column :label="$t('common.operation')" width="200" fixed="right" align="center">
        <template #default>
          <el-button link type="primary" :icon="Edit">{{ $t('common.edit') }}</el-button>
          <el-button link type="danger" :icon="Delete">{{ $t('common.delete') }}</el-button>
        </template>
      </el-table-column>
    </ProTable>

</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { Search, Refresh, Plus, Delete, Download, Edit } from '@element-plus/icons-vue'
import ProTable from '@/components/ProTable.vue'

// 搜索表单数据
const searchForm = reactive({
  username: '',
  status: undefined
})

// 初始参数
const initParam = reactive({
  type: 1
})

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
  background-color: #f0f2f5; /* 模拟灰色背景，以突显 ProTable 的白色背景 */
}
</style>
