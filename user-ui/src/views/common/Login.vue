<template>
  <div class="login-container">
    <!-- Top Right Controls -->
    <div class="top-controls">
      <div class="control-btn home-btn" @click="router.push('/')">
        <el-icon :size="16"><House /></el-icon>
        <span class="btn-text">{{ t('nav.home') }}</span>
      </div>
      
      <el-dropdown @command="handleLangChange" popper-class="dark-dropdown">
        <div class="control-btn lang-btn">
          <el-icon :size="16"><Location /></el-icon>
          <span class="btn-text">{{ currentLang === 'zh-CN' ? '简体中文' : 'English' }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="zh-CN" :disabled="currentLang === 'zh-CN'">简体中文</el-dropdown-item>
            <el-dropdown-item command="en" :disabled="currentLang === 'en'">English</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <div class="login-content-wrapper">
      <!-- Left Side: Visual/Features - Completely Separated -->
      <div class="visual-side">
        <div class="brand-area">
          <div class="logo-area">
            <el-icon :size="48" color="#8b5cf6"><Platform /></el-icon>
            <h1 class="brand-title">{{ $t('systemName') }}</h1>
          </div>
          <p class="brand-slogan">{{ $t('hero.subtitle').replace(/<br \/>/g, ' ') }}</p>
        </div>
        
        <div class="features-circle">
          <div class="feature-item item-1">
            <div class="icon-box"><el-icon><TrendCharts /></el-icon></div>
            <span>{{ $t('features.list.hotVideos.title') }}</span>
          </div>
          <div class="feature-item item-2">
            <div class="icon-box"><el-icon><Edit /></el-icon></div>
            <span>{{ $t('features.list.copywriting.title') }}</span>
          </div>
          <div class="feature-item item-3">
            <div class="icon-box"><el-icon><Picture /></el-icon></div>
            <span>{{ $t('features.list.imageGen.title') }}</span>
          </div>
          <div class="feature-item item-4">
            <div class="icon-box"><el-icon><VideoPlay /></el-icon></div>
            <span>{{ $t('features.list.videoGen.title') }}</span>
          </div>
          <div class="feature-item item-5">
            <div class="icon-box"><el-icon><Upload /></el-icon></div>
            <span>{{ $t('features.list.autoPublish.title') }}</span>
          </div>
          <div class="feature-item item-6">
            <div class="icon-box"><el-icon><ChatDotRound /></el-icon></div>
            <span>{{ $t('features.list.autoReply.title') }}</span>
          </div>
          
          <!-- Center Decoration -->
          <div class="center-decoration">
            <div class="glow-orb"></div>
          </div>
        </div>
      </div>

      <!-- Right Side: Login Form - Glassmorphism Card -->
      <div class="form-side">
        <div class="login-box">
          <!-- Login Mode -->
          <template v-if="mode === 'login'">
            <div class="login-header">
              <h2 class="title">{{ $t('auth.login') }}</h2>
              <p class="subtitle">{{ $t('auth.welcomeBack') }}</p>
            </div>

            <el-form :model="form" ref="formRef" :rules="rules" class="login-form" size="large">
              <el-form-item prop="username">
                <el-input 
                  v-model="form.username" 
                  :placeholder="$t('auth.usernamePlaceholder')"
                  class="custom-input"
                >
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
              
              <el-form-item prop="password">
                <el-input 
                  v-model="form.password" 
                  type="password" 
                  :placeholder="$t('auth.passwordPlaceholder')" 
                  show-password 
                  @keyup.enter="onSubmit"
                  class="custom-input"
                >
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item>
                <el-button type="primary" class="submit-btn" @click="onSubmit" :loading="loading">
                  {{ $t('auth.login') }}
                </el-button>
              </el-form-item>
              
              <div class="form-footer">
                <el-link type="info" :underline="false" @click="mode = 'forgot'">{{ $t('auth.forgotPassword') }}</el-link>
                <el-link type="primary" :underline="false" @click="mode = 'register'">{{ $t('auth.registerAccount') }}</el-link>
              </div>
            </el-form>
          </template>

          <!-- Register Mode -->
          <template v-else-if="mode === 'register'">
            <div class="login-header">
              <h2 class="title">{{ $t('auth.registerAccount') }}</h2>
              <p class="subtitle">{{ $t('systemName') }}</p>
            </div>

            <el-form :model="registerForm" ref="registerFormRef" :rules="registerRules" class="login-form" size="large">
              <el-form-item prop="username">
                <el-input v-model="registerForm.username" :placeholder="$t('auth.usernamePlaceholder')" class="custom-input">
                  <template #prefix><el-icon><User /></el-icon></template>
                </el-input>
              </el-form-item>

              <el-form-item prop="mobile">
                <el-input v-model="registerForm.mobile" :placeholder="$t('auth.mobilePlaceholder')" class="custom-input">
                  <template #prefix><el-icon><Iphone /></el-icon></template>
                </el-input>
              </el-form-item>
              
              <el-form-item prop="password">
                <el-input v-model="registerForm.password" type="password" :placeholder="$t('auth.passwordPlaceholder')" show-password class="custom-input">
                  <template #prefix><el-icon><Lock /></el-icon></template>
                </el-input>
              </el-form-item>

              <el-form-item prop="confirmPassword">
                <el-input v-model="registerForm.confirmPassword" type="password" :placeholder="$t('auth.confirmPasswordPlaceholder')" show-password class="custom-input">
                  <template #prefix><el-icon><Lock /></el-icon></template>
                </el-input>
              </el-form-item>

              <el-form-item>
                <el-button type="primary" class="submit-btn" @click="onRegister" :loading="loading">
                  {{ $t('auth.submitRegister') }}
                </el-button>
              </el-form-item>
              
              <div class="form-footer" style="justify-content: center;">
                <el-link type="info" :underline="false" @click="mode = 'login'">{{ $t('auth.backToLogin') }}</el-link>
              </div>
            </el-form>
          </template>

          <!-- Forgot Password Mode -->
          <template v-else-if="mode === 'forgot'">
            <div class="login-header">
              <h2 class="title">{{ $t('auth.resetPasswordTitle') }}</h2>
              <p class="subtitle">{{ $t('systemName') }}</p>
            </div>

            <el-form :model="forgotForm" ref="forgotFormRef" :rules="forgotRules" class="login-form" size="large">
              <el-form-item prop="mobile">
                <el-input v-model="forgotForm.mobile" :placeholder="$t('auth.mobilePlaceholder')" class="custom-input">
                  <template #prefix><el-icon><Iphone /></el-icon></template>
                </el-input>
              </el-form-item>

              <el-form-item prop="code">
                <div style="display: flex; gap: 10px; width: 100%;">
                  <el-input v-model="forgotForm.code" :placeholder="$t('auth.codePlaceholder')" class="custom-input">
                    <template #prefix><el-icon><Message /></el-icon></template>
                  </el-input>
                  <el-button type="primary" class="custom-input" style="width: 120px;" @click="handleSendCode" :disabled="codeCountdown > 0">
                    {{ codeCountdown > 0 ? `${codeCountdown}s` : $t('auth.sendCode') }}
                  </el-button>
                </div>
              </el-form-item>
              
              <el-form-item prop="newPassword">
                <el-input v-model="forgotForm.newPassword" type="password" :placeholder="$t('auth.newPasswordPlaceholder')" show-password class="custom-input">
                  <template #prefix><el-icon><Lock /></el-icon></template>
                </el-input>
              </el-form-item>

              <el-form-item prop="confirmPassword">
                <el-input v-model="forgotForm.confirmPassword" type="password" :placeholder="$t('auth.confirmPasswordPlaceholder')" show-password class="custom-input">
                  <template #prefix><el-icon><Lock /></el-icon></template>
                </el-input>
              </el-form-item>

              <el-form-item>
                <el-button type="primary" class="submit-btn" @click="onResetPassword" :loading="loading">
                  {{ $t('auth.submitReset') }}
                </el-button>
              </el-form-item>
              
              <div class="form-footer" style="justify-content: center;">
                <el-link type="info" :underline="false" @click="mode = 'login'">{{ $t('auth.backToLogin') }}</el-link>
              </div>
            </el-form>
          </template>
        </div>
      </div>
    </div>
    
    <!-- Background Orb -->
    <div class="bg-orb"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, getCurrentInstance } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { 
  User, 
  Lock, 
  Location,
  Platform,
  TrendCharts,
  Edit,
  Picture,
  VideoPlay,
  Upload,
  ChatDotRound,
  House,
  Iphone,
  Message
} from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import { register, sendCode, resetPasswordByMobile } from '@/api/user'
import { useRoute } from 'vue-router'

const { t, locale } = useI18n()
const router = useRouter()
const route = useRoute()
const { proxy } = getCurrentInstance()! as any
const authStore = useAuthStore()

const formRef = ref()
const registerFormRef = ref()
const forgotFormRef = ref()
const loading = ref(false)
const currentLang = computed(() => locale.value)
const mode = ref<'login' | 'register' | 'forgot'>('login')
const codeCountdown = ref(0)
let timer: any = null

// Initialize mode from query param
if (route.query.mode === 'register') {
  mode.value = 'register'
} else if (route.query.mode === 'forgot') {
  mode.value = 'forgot'
}

const form = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  mobile: '',
  email: '',
  nickname: ''
})

const forgotForm = reactive({
  mobile: '',
  code: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = computed(() => ({
  username: [{ required: true, message: t('auth.usernamePlaceholder'), trigger: 'blur' }],
  password: [{ required: true, message: t('auth.passwordPlaceholder'), trigger: 'blur' }]
}))

const registerRules = computed(() => ({
  username: [{ required: true, message: t('auth.usernamePlaceholder'), trigger: 'blur' }],
  password: [
    { required: true, message: t('auth.passwordPlaceholder'), trigger: 'blur' },
    { 
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/,
      message: t('auth.passwordFormatError'),
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: t('auth.confirmPasswordPlaceholder'), trigger: 'blur' },
    { 
      validator: (rule: any, value: string, callback: any) => {
        if (value !== registerForm.password) {
          callback(new Error(t('auth.passwordMatchError')))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  mobile: [
    { required: true, message: t('auth.mobilePlaceholder'), trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: t('auth.mobileFormatError'), trigger: 'blur' }
  ]
}))

const forgotRules = computed(() => ({
  mobile: [
    { required: true, message: t('auth.mobilePlaceholder'), trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: t('auth.mobileFormatError'), trigger: 'blur' }
  ],
  code: [{ required: true, message: t('auth.codePlaceholder'), trigger: 'blur' }],
  newPassword: [
    { required: true, message: t('auth.newPasswordPlaceholder'), trigger: 'blur' },
    { 
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/,
      message: t('auth.passwordFormatError'),
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: t('auth.confirmPasswordPlaceholder'), trigger: 'blur' },
    { 
      validator: (rule: any, value: string, callback: any) => {
        if (value !== forgotForm.newPassword) {
          callback(new Error(t('auth.passwordMatchError')))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
}))

const handleLangChange = (lang: string) => {
  locale.value = lang
  localStorage.setItem('lang', lang)
}

const handleSendCode = async () => {
  if (!forgotForm.mobile) {
    proxy.$message.warning(t('auth.mobilePlaceholder'))
    return
  }
  
  try {
    loading.value = true
    const res = await sendCode({ mobile: forgotForm.mobile })
    if (res.code === 200) {
      proxy.$message.success(t('auth.codeSent'))
      codeCountdown.value = 60
      timer = setInterval(() => {
        codeCountdown.value--
        if (codeCountdown.value <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    } else {
      // Use business error message from backend if available, otherwise fallback
      proxy.$message.error(res.msg || t('http.serverError'))
    }
  } catch (e: any) {
    console.error(e)
    // Error is typically handled by interceptor, but if we catch it here:
    if (e.message) {
      // proxy.$message.error(e.message) // Interceptor might have already shown it
    }
  } finally {
    loading.value = false
  }
}

const onRegister = async () => {
  if (!registerFormRef.value) return
  await registerFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        const res = await register(registerForm)
        if (res.code === 200) {
          proxy.$message.success(t('auth.registerSuccess'))
          mode.value = 'login'
        } else {
          proxy.$message.error(res.msg || t('http.serverError'))
        }
      } catch (e) {
        console.error(e)
      } finally {
        loading.value = false
      }
    }
  })
}

const onResetPassword = async () => {
  if (!forgotFormRef.value) return
  await forgotFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        const res = await resetPasswordByMobile({
          mobile: forgotForm.mobile,
          code: forgotForm.code,
          password: forgotForm.newPassword
        })
        if (res.code === 200) {
          proxy.$message.success(t('auth.resetSuccess'))
          mode.value = 'login'
        } else {
          proxy.$message.error(res.msg || t('http.serverError'))
        }
      } catch (e) {
        console.error(e)
      } finally {
        loading.value = false
      }
    }
  })
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
        // 登录成功后跳转到首页
        router.push('/index')
      }
      // 失败时不需要额外提示，因为 http 拦截器已经处理了具体的错误消息
    }
  })
}
</script>

<style>
/* Global Dropdown Styles for Dark Theme */
.el-dropdown__popper.dark-dropdown {
  background: rgba(15, 17, 21, 0.9) !important;
  backdrop-filter: blur(12px) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  border-radius: 12px !important;
}

.el-dropdown__popper.dark-dropdown .el-dropdown-menu {
  background: transparent !important;
  padding: 6px !important;
}

.el-dropdown__popper.dark-dropdown .el-dropdown-menu__item {
  color: rgba(255, 255, 255, 0.8) !important;
  border-radius: 8px !important;
  margin-bottom: 2px !important;
}

.el-dropdown__popper.dark-dropdown .el-dropdown-menu__item:hover,
.el-dropdown__popper.dark-dropdown .el-dropdown-menu__item:focus {
  background: rgba(139, 92, 246, 0.2) !important;
  color: #fff !important;
}

.el-dropdown__popper.dark-dropdown .el-dropdown-menu__item.is-disabled {
  color: #8b5cf6 !important;
  background: transparent !important;
  opacity: 1 !important;
  font-weight: 600 !important;
}

.el-dropdown__popper.dark-dropdown .el-popper__arrow::before {
  background: rgba(15, 17, 21, 0.9) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
}
</style>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #0f1115;
  position: relative;
  overflow: hidden;
  font-family: 'Inter', system-ui, sans-serif;
}

.login-content-wrapper {
  display: flex;
  width: 100%;
  max-width: 1400px;
  height: 80vh;
  min-height: 600px;
  gap: 80px;
  padding: 0 40px;
  align-items: center;
  z-index: 1;
}

/* Left Visual Side */
.visual-side {
  flex: 1.2;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
}

.brand-area {
  margin-bottom: 60px;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.brand-title {
  font-size: 42px;
  font-weight: 800;
  background: linear-gradient(to right, #fff, #c4b5fd);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0;
}

.brand-slogan {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.6);
  line-height: 1.6;
  max-width: 600px;
}

/* Floating Features - Random Layout */
.features-circle {
  position: relative;
  width: 100%;
  height: 600px;
  margin: 0;
  overflow: visible;
}

.feature-item {
  position: absolute;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  width: auto;
  text-align: center;
  animation: float 6s ease-in-out infinite;
}

.feature-item span {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0,0,0,0.8);
  background: rgba(0, 0, 0, 0.3);
  padding: 4px 12px;
  border-radius: 12px;
  backdrop-filter: blur(4px);
}

.icon-box {
  width: 72px;
  height: 72px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.3);
  transition: all 0.3s ease;
}

.feature-item:hover .icon-box {
  transform: scale(1.1);
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 0 30px rgba(139, 92, 246, 0.5);
}

/* Individual Icon Colors & Random Positions - Spread Out */
.item-1 { top: 0%; left: 0%; animation-delay: 0s; }
.item-1 .icon-box { color: #409EFF; border-color: rgba(64, 158, 255, 0.3); }

.item-2 { top: 15%; right: 5%; animation-delay: 1.2s; }
.item-2 .icon-box { color: #67C23A; border-color: rgba(103, 194, 58, 0.3); }

.item-3 { bottom: 25%; left: 10%; animation-delay: 2.4s; }
.item-3 .icon-box { color: #E6A23C; border-color: rgba(230, 162, 60, 0.3); }

.item-4 { bottom: 5%; right: 15%; animation-delay: 3.6s; }
.item-4 .icon-box { color: #F56C6C; border-color: rgba(245, 108, 108, 0.3); }

.item-5 { top: 40%; left: 40%; animation-delay: 0.8s; }
.item-5 .icon-box { color: #909399; border-color: rgba(144, 147, 153, 0.3); }

.item-6 { bottom: 0%; left: -5%; animation-delay: 4.2s; }
.item-6 .icon-box { color: #B37FEB; border-color: rgba(179, 127, 235, 0.3); }

/* Center Decoration - Gradient Orb */
.center-decoration {
  position: absolute;
  top: 50%;
  left: 45%;
  transform: translate(-50%, -50%);
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
  z-index: -1;
}

.glow-orb {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.15) 0%, rgba(139, 92, 246, 0.05) 40%, rgba(0, 0, 0, 0) 70%);
  border-radius: 50%;
  filter: blur(50px);
  animation: pulse-glow 4s ease-in-out infinite alternate;
}

@keyframes pulse-glow {
  0% { transform: scale(1); opacity: 0.6; }
  100% { transform: scale(1.2); opacity: 0.9; }
}

@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
  100% { transform: translateY(0px); }
}

/* Right Form Side */
.form-side {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-box {
  width: 100%;
  max-width: 440px;
  padding: 48px;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(20px);
  border-radius: 32px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
}

/* Rest of the styles (Lang switch, form elements) remain similar but optimized */
.top-controls {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 12px;
}

.control-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  transition: all 0.3s;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 12px;
}

.control-btn:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
}

.btn-text {
  font-weight: 500;
}

.login-header {
  text-align: left;
  margin-bottom: 40px;
}

.title {
  font-size: 32px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 8px;
}

.subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.5);
}

/* Custom Input Styles - Fixed Background */
.custom-input :deep(.el-input__wrapper) {
  background-color: rgba(0, 0, 0, 0.3) !important;
  box-shadow: none !important;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 8px 15px;
  transition: all 0.3s;
  height: 48px;
}

.custom-input :deep(.el-input__wrapper:hover) {
  background-color: rgba(0, 0, 0, 0.4) !important;
  border-color: rgba(255, 255, 255, 0.2);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  background-color: rgba(0, 0, 0, 0.4) !important;
  border-color: #8b5cf6;
  box-shadow: 0 0 0 1px #8b5cf6 inset !important;
}

.custom-input :deep(.el-input__inner) {
  color: #fff !important;
  height: 100%;
}

.custom-input :deep(.el-input__inner:-webkit-autofill) {
  -webkit-box-shadow: 0 0 0 1000px #1a1b26 inset !important;
  -webkit-text-fill-color: #fff !important;
  caret-color: #fff;
}

.custom-input :deep(.el-icon) {
  color: rgba(255, 255, 255, 0.5);
  font-size: 18px;
}

/* Submit Button */
.submit-btn {
  width: 100%;
  height: 52px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
  border-radius: 12px;
  font-weight: 600;
  font-size: 16px;
  letter-spacing: 0.5px;
  margin-top: 10px;
  box-shadow: 0 4px 14px 0 rgba(99, 102, 241, 0.39);
  transition: all 0.3s;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #818cf8 0%, #a78bfa 100%);
  transform: translateY(-1px);
  box-shadow: 0 6px 20px 0 rgba(99, 102, 241, 0.5);
}

.submit-btn:active {
  transform: translateY(0);
}

.form-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 24px;
}

/* Background Orb */
.bg-orb {
  position: absolute;
  top: -10%;
  left: -10%;
  width: 800px;
  height: 800px;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.1) 0%, rgba(0, 0, 0, 0) 70%);
  border-radius: 50%;
  filter: blur(80px);
  pointer-events: none;
  z-index: 0;
}
</style>
