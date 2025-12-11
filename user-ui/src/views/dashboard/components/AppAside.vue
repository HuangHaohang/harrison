<template>
  <el-scrollbar>
    <el-menu
      :default-active="activeMenu"
      class="el-menu-vertical"
      :collapse="false"
      router
      background-color="transparent"
      :text-color="isDark ? '#a0a0a0' : '#303133'"
      :active-text-color="isDark ? '#fff' : '#409EFF'"
    >
      <div class="logo-wrapper">
        <el-icon :size="24" color="#8b5cf6"><Platform /></el-icon>
        <span class="logo-text">{{ $t('systemName') }}</span>
      </div>
      
      <template v-for="menu in menus" :key="menu.id">
        <!-- 目录 -->
        <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="String(menu.id)">
          <template #title>
            <el-icon v-if="menu.icon"><component :is="menu.icon" /></el-icon>
            <span>{{ getMenuName(menu) }}</span>
          </template>
          
          <template v-for="child in menu.children" :key="child.id">
            <el-menu-item :index="'/index/' + child.path.replace(/^\/+/, '')">
              <el-icon v-if="child.icon"><component :is="child.icon" /></el-icon>
              <template #title>{{ getMenuName(child) }}</template>
            </el-menu-item>
          </template>
        </el-sub-menu>
        
        <!-- 菜单 -->
        <el-menu-item v-else :index="'/index/' + menu.path.replace(/^\/+/, '')">
          <el-icon v-if="menu.icon"><component :is="menu.icon" /></el-icon>
          <template #title>{{ getMenuName(menu) }}</template>
        </el-menu-item>
      </template>
    </el-menu>
  </el-scrollbar>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Platform } from '@element-plus/icons-vue'
import { useDark } from '@vueuse/core'

const authStore = useAuthStore()
const route = useRoute()
const { locale } = useI18n()
const isDark = useDark()

const menus = computed(() => authStore.menus)
const activeMenu = computed(() => route.path)

const getMenuName = (menu: any) => {
  if (locale.value === 'zh-CN') return menu.simplifiedName
  if (locale.value === 'en') return menu.englishName || menu.simplifiedName
  return menu.simplifiedName
}
</script>

<style scoped>
.logo-wrapper {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 12px;
  border-bottom: 1px solid var(--el-border-color-light);
  margin-bottom: 10px;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(to right, #7c3aed, #a78bfa);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.el-menu-vertical {
  border-right: none;
}

:deep(.el-menu-item), :deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;
  margin: 4px 12px;
  border-radius: 8px;
}

:deep(.el-menu-item:hover), :deep(.el-sub-menu__title:hover) {
  background-color: var(--el-fill-color-light) !important;
}

:deep(.el-menu-item.is-active) {
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary) !important;
  font-weight: 600;
  box-shadow: 0 2px 6px var(--el-color-primary-light-8);
}

html.dark :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, rgba(139, 92, 246, 0.2) 0%, rgba(139, 92, 246, 0.1) 100%);
  color: #c4b5fd !important;
  box-shadow: 0 2px 12px rgba(139, 92, 246, 0.15);
}
</style>
