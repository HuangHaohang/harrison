<template>
  <el-container class="layout-container">
    <el-aside width="240px" class="layout-aside">
      <AppAside />
    </el-aside>
    
    <el-container>
      <el-header class="layout-header-wrapper">
        <AppHeader />
      </el-header>
      
      <el-main class="layout-main-wrapper">
        <AppMain />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import AppAside from './components/AppAside.vue'
import AppHeader from './components/AppHeader.vue'
import AppMain from './components/AppMain.vue'
</script>

<style scoped>
.layout-container {
  height: 100vh;
  background-color: var(--el-bg-color);
  color: var(--el-text-color-primary);
}

.layout-aside {
  background-color: var(--el-bg-color);
  border-right: 1px solid var(--el-border-color-light);
  display: flex;
  flex-direction: column;
  transition: width 0.3s, background-color 0.3s;
}

.layout-header-wrapper {
  padding: 0;
  height: auto;
}

.layout-main-wrapper {
  padding: 0;
  overflow: hidden; /* AppMain handles scrolling if needed */
}
</style>