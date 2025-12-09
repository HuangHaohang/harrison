<template>
  <div class="header-container">
    <el-row align="middle" justify="space-between" class="header-row">
      <el-col :span="6">
        <div class="header-left">
          <el-icon class="trigger-icon" @click="toggleSidebar">
            <component :is="isCollapse ? Expand : Fold" />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index" class="breadcrumb-item"
              :class="{ 'breadcrumb-home': index === 0 }">
              <span v-if="index === 0" @click="router.push('/')" style="cursor: pointer;">{{ getMenuName(item) }}</span>
              <span v-else>{{ getMenuName(item) }}</span>
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </el-col>

      <!-- 中间：搜索框 -->
      <el-col :span="6">
        <div class="search-section">
          <el-input :placeholder="$t('header.searchPlaceholder')" class="search-input"
            :style="{ borderRadius: '20px' }">
            <template #prefix>
              <el-icon>
                <Search />
              </el-icon>
            </template>
          </el-input>
        </div>
      </el-col>

      <!-- 右侧：功能图标 -->
      <el-col :span="10">
        <div class="action-section">
          <!-- 亮色/暗色切换 -->
          <el-switch v-model="isDark" :active-icon="Moon" :inactive-icon="Sunny" @change="toggleDark" />

          <!-- 语言国际化 -->
          <el-dropdown @command="handleLangChange">
            <span style="cursor: pointer; outline: none; display: flex; align-items: center;">
              <el-icon :size="20">
                <Location />
              </el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="zh-CN" :disabled="currentLang === 'zh-CN'">简体中文</el-dropdown-item>
                <el-dropdown-item command="zh-TW" :disabled="currentLang === 'zh-TW'">繁體中文</el-dropdown-item>
                <el-dropdown-item command="en" :disabled="currentLang === 'en'">English</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>


          <!-- 日期国际化 -->
          <el-tooltip :content="$t('header.dateFormat')" placement="bottom">
            <el-icon :size="20" style="cursor: pointer;">
              <Calendar />
            </el-icon>
          </el-tooltip>

          <!-- GitHub图标 -->
          <img 
            src="@/assets/github.svg" 
            style="width: 20px; height: 20px; cursor: pointer;"
            alt="GitHub Repo" 
            @click="openGitHubRepo" 
          />

          <!-- 个人头像 -->
          <el-tooltip :content="$t('header.userCenter')" placement="bottom">
            <el-avatar :size="32" :src="userAvatar" />
          </el-tooltip>
        </div>
      </el-col>
    </el-row>
  </div>

  <!-- 标签页栏 -->
  <div class="tags-view-container">
    <el-scrollbar class="tags-view-wrapper">
      <router-link v-for="tag in visitedViews" :key="tag.path" :to="{ path: tag.path, query: tag.query }"
        class="tags-view-item" :class="{ active: isActive(tag) }">
        <span v-if="isActive(tag)" class="active-dot"></span>
        {{ getTagName(tag) }}
        <el-icon v-if="!isAffix(tag)" class="close-icon" @click.prevent.stop="closeSelectedTag(tag)">
          <Close />
        </el-icon>
      </router-link>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { Sunny, Moon, Close, Expand, Fold } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useTagsViewStore } from '@/stores/tagsView'
import { useAppStore } from '@/stores/app'
import { storeToRefs } from 'pinia'

const { locale, t } = useI18n()
const currentLang = computed(() => locale.value)
const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const tagsViewStore = useTagsViewStore()
const appStore = useAppStore()
const { isCollapse } = storeToRefs(appStore)

const toggleSidebar = () => {
  appStore.toggleSidebar()
}
const breadcrumbs = ref<any[]>([])

const visitedViews = computed(() => tagsViewStore.visitedViews)

// 获取标签名称（支持国际化）
const getTagName = (tag: any) => {
  // 首页特殊处理
  if (tag.path === '/' || tag.path === '/home' || tag.path === '/dashboard') {
    return t('home.title')
  }

  if (tag.name === 'Dashboard') {
    return t('home.title')
  }

  // 尝试从菜单中找到对应的名称进行国际化
  const menu = findMenuByPath(authStore.menus, tag.path)
  if (menu) {
    return getMenuName(menu)
  }

  // 降级使用 meta.title 或默认名称
  return tag.meta?.title || tag.title || 'no-name'
}

// 递归查找菜单
const findMenuByPath = (menus: any[], path: string): any => {
  for (const menu of menus) {
    if (menu.path === path) return menu
    if (menu.children) {
      const found = findMenuByPath(menu.children, path)
      if (found) return found
    }
  }
  return null
}

const isActive = (tag: any) => {
  return tag.path === route.path
}

const isAffix = (tag: any) => {
  return tag.path === '/' || tag.path === '/home' || tag.path === '/dashboard' || tag.meta?.affix
}

const addTags = () => {
  const { name } = route
  if (name) {
    tagsViewStore.addVisitedView(route)
  }
}

const closeSelectedTag = (view: any) => {
  tagsViewStore.delVisitedView(view).then((visitedViews) => {
    if (isActive(view)) {
      toLastView(visitedViews, view)
    }
  })
}

const toLastView = (visitedViews: any[], view: any) => {
  const latestView = visitedViews.slice(-1)[0]
  if (latestView) {
    router.push(latestView.path)
  } else {
    // 如果没有其他标签，跳转回首页
    if (view.name === 'HomeView') {
      // 已经是在首页了，重定向（一般不会发生，因为首页是 affix 的）
      router.replace({ path: '/redirect' + view.fullPath })
    } else {
      router.push('/')
    }
  }
}

// 初始化首页标签
const initTags = () => {
  const homeTag = {
    path: '/dashboard',
    name: 'Dashboard',
    meta: { title: t('home.title'), affix: true }
  }
  tagsViewStore.addVisitedView(homeTag as any)
}

watch(
  () => route.path,
  () => {
    addTags()
  },
  { immediate: true }
)

onMounted(() => {
  initTags()
})

const handleLangChange = (lang: string) => {
  locale.value = lang
  localStorage.setItem('lang', lang)
}

const getMenuName = (menu: any) => {
  if (menu.name === 'Dashboard' || menu.path === '/dashboard') {
    return t('home.title')
  }
  if (locale.value === 'zh-TW') {
    return menu.traditionalName || menu.simplifiedName || menu.name
  }
  if (locale.value === 'en') {
    return menu.englishName || menu.simplifiedName || menu.name
  }
  return menu.simplifiedName || menu.name
}

const findMenuPath = (menus: any[], targetPath: string): any[] => {
  for (const menu of menus) {
    if (menu.path === targetPath) {
      return [menu]
    }
    if (menu.children && menu.children.length > 0) {
      const found = findMenuPath(menu.children, targetPath)
      if (found.length > 0) {
        return [menu, ...found]
      }
    }
  }
  return []
}

watch(
  [() => route.path, () => authStore.menus],
  ([newPath, menus]) => {
    if (newPath === '/' || newPath === '/home') {
      breadcrumbs.value = []
      return
    }
    // 递归查找菜单路径
    const menuPath = findMenuPath(menus, newPath)
    if (newPath === '/dashboard') {
      breadcrumbs.value = [{ path: '/dashboard', meta: { title: t('home.title') }, name: 'Dashboard' }]
    } else {
      breadcrumbs.value = [{ path: '/dashboard', meta: { title: t('home.title') }, name: 'Dashboard' }, ...menuPath]
    }
  },
  { immediate: true }
)

const userAvatar = computed(() => {
  return authStore.user?.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
})
import { useDark, useToggle } from '@vueuse/core'
const isDark = useDark({
  selector: 'html', // 关键：作用于html元素
  attribute: 'class',
  valueDark: 'dark',
  valueLight: 'light',
  storageKey: 'el-theme'
})
const openGitHubRepo = () => {
  window.open('https://github.com/HuangHaohang/harrison', '_blank')
}

const toggleDark = () => {
  useToggle(isDark)
}
</script>

<style scoped>
.header-container {
  height: 48px;
}

.header-left {
  display: flex;
  align-items: center;
  height: 100%;
}

.trigger-icon {
  font-size: 20px;
  cursor: pointer;
  margin: 0 16px;
  color: var(--el-text-color-primary);

  &:hover {
    color: var(--el-color-primary);
  }
}

.header-row {
  height: 100%;
}

.search-section {
  display: flex;
  justify-content: center;
}

.search-input {
  width: 100%;
  max-width: 300px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 20px;
}

.action-section {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-right: 32px;
  gap: 24px;
}

/* 面包屑样式 */
.breadcrumb-home :deep(.el-breadcrumb__inner) {
  font-weight: bold;
  color: var(--el-text-color-primary) !important;
}

.breadcrumb-home :deep(.el-breadcrumb__inner:hover) {
  color: var(--el-color-primary) !important;
  cursor: pointer;
}

.breadcrumb-item :deep(.el-breadcrumb__inner) {
  color: #909399 !important;
  /* 浅灰色 */
  font-weight: normal;
}

/* 标签页样式 */
.tags-view-container {
  height: 34px;
  width: 100%;
  background: var(--el-bg-color);
}

.tags-view-wrapper {
  text-align: left;
  /* 确保内容左对齐 */

  .tags-view-item {
    display: inline-block;
    position: relative;
    cursor: pointer;
    height: 26px;
    line-height: 26px;
    border: 1px solid var(--el-border-color-light);
    color: var(--el-text-color-regular);
    background: var(--el-bg-color);
    padding: 0 8px;
    font-size: 12px;
    margin-left: 5px;
    margin-top: 4px;
    text-decoration: none;

    &:first-of-type {
      margin-left: 15px;
    }

    &:last-of-type {
      margin-right: 15px;
    }

    &.active {
      background-color: var(--el-color-primary);
      color: #fff;
      border-color: var(--el-color-primary);

      &::before {
        content: '';
        background: #fff;
        display: inline-block;
        width: 8px;
        height: 8px;
        border-radius: 50%;
        position: relative;
        margin-right: 2px;
      }
    }
  }
}

.tags-view-item .close-icon {
  width: 16px;
  height: 16px;
  vertical-align: 2px;
  border-radius: 50%;
  text-align: center;
  transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  transform-origin: 100% 50%;
  margin-left: 2px;

  &:hover {
    background-color: #b4bccc;
    color: #fff;
  }
}
</style>
