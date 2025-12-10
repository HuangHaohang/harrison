<template>
  <div class="login-container">
    <div class="lang-switch">
      <el-dropdown @command="handleLangChange">
        <span class="el-dropdown-link">
          {{ currentLang === 'zh-CN' ? '简体中文' : currentLang === 'zh-TW' ? '繁體中文' : 'English' }}
          <el-icon class="el-icon--right">
            <arrow-down />
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
    </div>
    <div class="login-content">
      <div class="login-header">
        <img src="@/assets/vue.svg" alt="logo" class="logo" />
        <h2 class="title">{{ $t('system.name') }}</h2>
      </div>
      <el-card class="login-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>{{ $t('auth.title') }}</span>
          </div>
        </template>
        <el-form :model="form" ref="formRef" :rules="rules" label-position="top" size="large">
          <el-form-item :label="$t('auth.username')" prop="username">
            <el-input v-model="form.username" :placeholder="$t('auth.usernamePlaceholder')">
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item :label="$t('auth.password')" prop="password">
            <el-input v-model="form.password" type="password" :placeholder="$t('auth.passwordPlaceholder')" show-password @keyup.enter="onSubmit">
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="login-button" @click="onSubmit" :loading="loading">{{ $t('auth.login') }}</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, getCurrentInstance } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { User, Lock, ArrowDown } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n()
const router = useRouter()
const authStore = useAuthStore()
const { proxy } = getCurrentInstance()! as any

const formRef = ref()
const loading = ref(false)
const currentLang = computed(() => locale.value)

const form = reactive({
  username: 'admin',
  password: '123456'
})

const rules = computed(() => ({
  username: [{ required: true, message: t('auth.usernamePlaceholder'), trigger: 'blur' }],
  password: [{ required: true, message: t('auth.passwordPlaceholder'), trigger: 'blur' }]
}))

const handleLangChange = (lang: string) => {
  locale.value = lang
  localStorage.setItem('lang', lang)
}

const onSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      const success = await authStore.login(form)
      loading.value = false
      if (success) {
        proxy.$message.success(t('auth.loginSuccess'))
        router.push('/')
      } else {
        proxy.$message.error(t('auth.loginFailed'))
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: url('@/assets/login.jpg') no-repeat center center;
  background-size: cover;
  overflow: hidden;
  position: relative;
}

.lang-switch {
  position: absolute;
  top: 20px;
  right: 20px;
  cursor: pointer;
}

.el-dropdown-link {
  color: #fff;
  display: flex;
  align-items: center;
  font-size: 14px;
  text-shadow: 0 1px 2px rgba(0,0,0,0.5);
}

.login-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 400px;
  padding: 20px;
}

.login-header {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
}

.logo {
  width: 48px;
  height: 48px;
  margin-right: 15px;
}

.title {
  color: #fff;
  font-size: 28px;
  font-weight: 600;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0,0,0,0.5);
}

.login-card {
  width: 100%;
  border-radius: 8px;
  border: none;
  background-color: rgba(255, 255, 255, 0.2); /* 毛玻璃背景透明度 */
  backdrop-filter: blur(10px); /* 毛玻璃模糊效果 */
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37);
}

.card-header {
  text-align: center;
  font-weight: bold;
  font-size: 18px;
  color: #fff; /* 标题颜色改为白色以适应深色/图片背景 */
  text-shadow: 0 1px 2px rgba(0,0,0,0.3);
}

.login-button {
  width: 100%;
  margin-top: 10px;
  font-weight: bold;
  background: linear-gradient(90deg, #409eff, #36d1dc);
  border: none;
}

.login-button:hover {
  background: linear-gradient(90deg, #66b1ff, #5ce1e6);
}

:deep(.el-form-item__label) {
  color: #fff !important; /* 表单标签颜色改为白色 */
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0,0,0,0.3);
}

:deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.8); /* 输入框半透明 */
  box-shadow: none;
}

:deep(.el-input__wrapper:hover) {
  background-color: rgba(255, 255, 255, 0.95);
}

:deep(.el-input__wrapper.is-focus) {
  background-color: #fff;
  box-shadow: 0 0 0 1px #409eff inset;
}
</style>
