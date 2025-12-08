<template>
    <div class="logo-section" :class="{ 'is-collapse': isCollapse }">
        <img src="@/assets/vue.svg" alt="Logo" class="logo" />
        <span class="system-name" v-show="!isCollapse">{{ $t('system.name') }}</span>
    </div>
    <el-scrollbar max-height="calc(100vh - 60px)">
        <el-menu
            style="height: calc(100vh - 60px);"
            :default-active="activeMenu"
            class="el-menu-vertical-demo"
            :collapse="isCollapse"
            router
            unique-opened
        >
            <menu-item v-for="menu in menus" :key="menu.id" :item="menu" />
        </el-menu>
    </el-scrollbar>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'
import { storeToRefs } from 'pinia'
import { useI18n } from 'vue-i18n'
import MenuItem from './MenuItem.vue'

const { t } = useI18n()
const authStore = useAuthStore()
const appStore = useAppStore()
const { isCollapse } = storeToRefs(appStore)
const route = useRoute()

// 获取菜单数据
const menus = computed(() => authStore.menus)

// 当前激活的菜单项
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.logo-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  height: 60px;
  border-bottom: 1px solid var(--el-border-color-light);
  box-sizing: border-box;
  overflow: hidden;
  transition: all 0.3s;
}

.logo-section.is-collapse {
    gap: 0;
}

.logo {
  width: 32px;
  height: 32px;
}

.system-name {
  font-size: 18px;
  font-weight: bold;
  white-space: nowrap;
}

.el-menu-vertical-demo {
    border-right: none;
}
</style>
