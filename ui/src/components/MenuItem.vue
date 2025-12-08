<template>
  <template v-if="!item.children || item.children.length === 0">
    <el-menu-item :index="item.path">
      <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
      <el-icon v-else><MenuIcon /></el-icon>
      <template #title>
        <span>{{ getMenuName(item) }}</span>
      </template>
    </el-menu-item>
  </template>

  <template v-else>
    <el-sub-menu :index="String(item.id)">
      <template #title>
        <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
        <el-icon v-else><Setting /></el-icon>
        <span>{{ getMenuName(item) }}</span>
      </template>
      <menu-item v-for="child in item.children" :key="child.id" :item="child" />
    </el-sub-menu>
  </template>
</template>

<script setup lang="ts">
import {
  Menu as MenuIcon,
  Setting
} from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'

defineOptions({
  name: 'MenuItem'
})

defineProps<{
  item: any
}>()

const { locale } = useI18n()

const getMenuName = (item: any) => {
  const lang = locale.value
  // 根据数据库字段名映射：simplified_name -> simplifiedName, etc.
  // 假设后端返回的数据已经转为驼峰，或者是直接保持数据库字段名
  // 这里做一个兼容处理，优先取驼峰，取不到取下划线，最后回退到 name
  
  if (lang === 'zh-TW') {
    return item.traditionalName
  }
  if (lang === 'en') {
    return item.englishName
  }
  // zh-CN
  return item.simplifiedName
}
</script>