<template>
  <div class="layout-header">
    <div class="header-left">
      <!-- Breadcrumb placeholder -->
    </div>
    <div class="header-right">
      <el-tooltip :content="$t('header.help')" placement="bottom">
        <el-button circle text class="header-btn">
          <el-icon><QuestionFilled /></el-icon>
        </el-button>
      </el-tooltip>

      <!-- Theme Switch -->
      <el-switch
        v-model="isDark"
        class="theme-switch"
        :active-action-icon="Moon"
        :inactive-action-icon="Sunny"
      />
      
      <el-dropdown @command="handleLangChange" popper-class="dark-dropdown">
        <el-button circle text class="header-btn">
          <el-icon><Location /></el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="zh-CN" :disabled="currentLang === 'zh-CN'">简体中文</el-dropdown-item>
            <el-dropdown-item command="en" :disabled="currentLang === 'en'">English</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>

      <el-dropdown popper-class="dark-dropdown">
        <div class="user-profile">
          <el-avatar :size="32" :src="user?.avatar" class="user-avatar">
            {{ user?.username?.charAt(0).toUpperCase() }}
          </el-avatar>
          <span class="username">{{ user?.username }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>
              <el-icon><User /></el-icon>
              {{ $t('header.profile') }}
            </el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              {{ $t('header.logout') }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from 'vue-i18n'
import { useDark } from '@vueuse/core'
import { 
  Location, 
  QuestionFilled, 
  User, 
  SwitchButton,
  Sunny,
  Moon
} from '@element-plus/icons-vue'

const authStore = useAuthStore()
const { t, locale } = useI18n()

// Theme handling using VueUse
const isDark = useDark()

const user = computed(() => authStore.user)
const currentLang = computed(() => locale.value)

const handleLangChange = (lang: string) => {
  locale.value = lang
  localStorage.setItem('lang', lang)
}

const handleLogout = async () => {
  await authStore.logout()
}
</script>

<style scoped>
.layout-header {
  height: 60px;
  background-color: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color-lighter);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  transition: background-color 0.3s, border-color 0.3s, box-shadow 0.3s;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 9;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-btn {
  font-size: 18px;
  color: var(--el-text-color-regular);
}

.header-btn:hover {
  color: var(--el-color-primary);
  background-color: var(--el-fill-color-light);
}

.theme-switch {
  margin-right: 8px;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 20px;
  transition: background-color 0.3s;
}

.user-profile:hover {
  background-color: var(--el-fill-color-light);
}

.user-avatar {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: #fff;
  font-weight: 600;
  font-size: 14px;
}

.username {
  font-size: 14px;
  color: var(--el-text-color-primary);
  font-weight: 500;
}
</style>
