<template>
  <div class="search-form" v-if="columns.length">
    <el-form ref="formRef" :model="searchParam" label-width="80px">
      <el-row :gutter="20">
        <el-col v-for="item in columns" :key="item.prop" :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
          <el-form-item :label="item.label" :prop="item.prop">
            <!-- Input -->
            <el-input v-if="item.search?.el === 'input'" v-model="searchParam[item.prop]"
              :placeholder="item.search?.props?.placeholder || item.label" clearable v-bind="item.search?.props" />
            <!-- Select -->
            <el-select v-else-if="item.search?.el === 'select'" v-model="searchParam[item.prop]"
              :placeholder="item.search?.props?.placeholder || item.label" clearable style="width: 100%"
              v-bind="item.search?.props">
              <el-option v-for="option in item.enum" :key="option.value" :label="option.label" :value="option.value" />
            </el-select>
            <!-- Date Picker -->
            <el-date-picker v-else-if="item.search?.el === 'date-picker'" v-model="searchParam[item.prop]" type="date"
              :placeholder="item.search?.props?.placeholder || item.label" clearable style="width: 100%"
              v-bind="item.search?.props" />
          </el-form-item>
        </el-col>

        <!-- Buttons -->

      </el-row>
    </el-form>
    <el-row class="search-button">
      <el-col :span="24">
        <el-form-item>
          <template v-if="searchToolButton && searchToolButton.length">
            <el-button v-for="(btn, index) in searchToolButton" :key="index" :type="btn.type || 'primary'"
              :icon="btn.icon" @click="handleButtonClick(btn)">
              {{ btn.label }}
            </el-button>
          </template>
          <template v-else>
            <el-button type="primary" :icon="Search" @click="search">{{ $t('common.search') }}</el-button>
            <el-button :icon="Refresh" @click="reset">{{ $t('common.reset') }}</el-button>
          </template>
        </el-form-item>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'

const props = defineProps({
  columns: {
    type: Array as PropType<any[]>,
    default: () => []
  },
  searchParam: {
    type: Object,
    default: () => ({})
  },
  searchToolButton: {
    type: Array as PropType<any[]>,
    default: () => []
  },
  search: {
    type: Function,
    default: () => { }
  },
  reset: {
    type: Function,
    default: () => { }
  }
})

const handleButtonClick = (btn: any) => {
  if (btn.click === 'search') {
    props.search()
  } else if (btn.click === 'reset') {
    props.reset()
  } else if (typeof btn.click === 'function') {
    btn.click()
  }
}
</script>

<style scoped>
.search-form {
  padding: 0;
}

.search-button {
  margin-left: 16px
}
</style>
