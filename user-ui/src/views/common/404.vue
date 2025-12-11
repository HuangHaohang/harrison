<template>
  <div class="not-found">
    <el-result
      icon="warning"
      title="404"
      sub-title="Sorry, the page you visited does not exist."
    >
      <template #extra>
        <el-button type="primary" @click="$router.push('/')">Back Home</el-button>
      </template>
    </el-result>
  </div>
</template>

<style scoped>
.not-found {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: var(--el-bg-color); /* Use theme variable */
}
</style>
