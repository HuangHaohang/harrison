<template>
  <div class="not-found">
    <h1>404</h1>
    <p>{{ $t('error.notFound') }}</p>
    <el-button type="primary" @click="$router.push('/')">{{ $t('error.goHome') }}</el-button>
  </div>
</template>

<style scoped>
.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
}
</style>