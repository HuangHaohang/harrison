<template>
    <el-container class="layout-container">
        <el-aside :width="isCollapse ? '64px' : '200px'" class="aside-container">
            <Aside></Aside>
        </el-aside>
        <el-container>
            <el-header class="header-container">
                <Header></Header>
            </el-header>
            <el-main class="main-container">
                <router-view></router-view>
            </el-main>
        </el-container>
    </el-container>
</template>

<script setup lang="ts">
import Header from '@/components/Header.vue'
import Aside from '@/components/Aside.vue'
import { useAppStore } from '@/stores/app'
import { storeToRefs } from 'pinia'

const appStore = useAppStore()
const { isCollapse } = storeToRefs(appStore)
</script>

<style scoped>
.layout-container {
    height: 100vh;
}

.aside-container {
    height: 100%;
    overflow: hidden;
    transition: width 0.3s;
}

.header-container {
    padding: 0;
    height: auto !important; /* 让 Header 组件决定高度 */
}

.main-container {
    padding: 0;
    overflow-y: auto;
}
</style>
