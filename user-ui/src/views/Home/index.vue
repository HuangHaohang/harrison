<script setup lang="ts">
import { 
  Monitor, 
  Edit, 
  Picture, 
  VideoPlay, 
  Upload, 
  ChatDotRound,
  Platform,
  TrendCharts,
  Location
} from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import { computed } from 'vue'

const { locale, t } = useI18n()
const currentLang = computed(() => locale.value)

const handleLangChange = (lang: string) => {
  locale.value = lang
  localStorage.setItem('lang', lang)
}

const openGitHubRepo = () => {
  window.open('https://github.com/HuangHaohang/harrison', '_blank')
}

const features = computed(() => [
  {
    title: t('features.list.hotVideos.title'),
    desc: t('features.list.hotVideos.desc'),
    icon: TrendCharts,
    color: '#409EFF'
  },
  {
    title: t('features.list.copywriting.title'),
    desc: t('features.list.copywriting.desc'),
    icon: Edit,
    color: '#67C23A'
  },
  {
    title: t('features.list.imageGen.title'),
    desc: t('features.list.imageGen.desc'),
    icon: Picture,
    color: '#E6A23C'
  },
  {
    title: t('features.list.videoGen.title'),
    desc: t('features.list.videoGen.desc'),
    icon: VideoPlay,
    color: '#F56C6C'
  },
  {
    title: t('features.list.autoPublish.title'),
    desc: t('features.list.autoPublish.desc'),
    icon: Upload,
    color: '#909399'
  },
  {
    title: t('features.list.autoReply.title'),
    desc: t('features.list.autoReply.desc'),
    icon: ChatDotRound,
    color: '#B37FEB'
  }
])

import rednoteIcon from '@/assets/rednote.svg'
import tiktokIcon from '@/assets/tiktok.svg'
import channelIcon from '@/assets/channel.svg'

import { ref, onMounted, onUnmounted } from 'vue'

const activeSection = ref('')

const checkActiveSection = () => {
  const sections = ['features', 'about'] // 'home' is top
  const scrollY = window.scrollY
  
  if (scrollY < 100) {
    activeSection.value = 'home'
    return
  }

  for (const section of sections) {
    const el = document.getElementById(section)
    if (el) {
      const rect = el.getBoundingClientRect()
      if (rect.top <= 100 && rect.bottom >= 100) {
        activeSection.value = section
        return
      }
    }
  }
}

const scrollToSection = (sectionId: string) => {
  const el = document.getElementById(sectionId)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth' })
    activeSection.value = sectionId
  }
}

onMounted(() => {
  window.addEventListener('scroll', checkActiveSection)
})

onUnmounted(() => {
  window.removeEventListener('scroll', checkActiveSection)
})
const platforms = computed(() => [
  { name: t('platforms.tiktok'), icon: tiktokIcon, class: 'icon-tiktok' },
  { name: t('platforms.rednote'), icon: rednoteIcon, class: 'icon-rednote' },
  { name: t('platforms.channel'), icon: channelIcon, class: 'icon-channel' }
])
</script>

<template>
  <el-scrollbar class="home-scrollbar" wrap-class="home-scroll-wrap">
    <div class="home-container">
      <!-- Fixed Header -->
      <header class="fixed-header">
        <div class="header-content">
          <div class="logo">
            <el-icon :size="32" color="#8b5cf6"><Platform /></el-icon>
            <span class="logo-text">{{ $t('systemName') }}</span>
          </div>
          <nav class="nav-links">
            <a href="#" :class="{ active: activeSection === 'home' }" @click.prevent="scrollToSection('home')">{{ $t('nav.home') }}</a>
            <a href="#features" :class="{ active: activeSection === 'features' }" @click.prevent="scrollToSection('features')">{{ $t('nav.features') }}</a>
            <a href="#about" :class="{ active: activeSection === 'about' }" @click.prevent="scrollToSection('about')">{{ $t('nav.about') }}</a>
          </nav>
          <div class="header-right">
            <!-- 语言国际化 -->
            <el-dropdown @command="handleLangChange" popper-class="dark-dropdown">
              <span style="cursor: pointer; outline: none; display: flex; align-items: center; color: var(--el-text-color-primary);">
                <el-icon :size="20" color="#fff">
                  <Location />
                </el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="zh-CN" :disabled="currentLang === 'zh-CN'">简体中文</el-dropdown-item>
                  <el-dropdown-item command="en" :disabled="currentLang === 'en'">English</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>

            <!-- GitHub图标 -->
            <div class="github-icon" @click="openGitHubRepo">
              <img src="@/assets/github.svg" alt="GitHub Repo" />
            </div>

            <div class="auth-buttons">
              <el-button text class="login-btn" @click="$router.push('/login')">{{ $t('auth.login') }}</el-button>
              <el-button type="primary" round @click="$router.push('/login?mode=register')">{{ $t('auth.register') }}</el-button>
            </div>
          </div>
        </div>
      </header>

      <!-- Main Content -->
      <main>
        <!-- Hero Section -->
        <section id="home" class="hero-section">
          <div class="hero-container">
            <div class="hero-left">
              <h1 class="hero-title">{{ $t('hero.title') }}</h1>
              <p class="hero-subtitle" v-html="$t('hero.subtitle')"></p>
              <div class="hero-actions">
                <el-button type="primary" size="large" class="cta-button">{{ $t('hero.cta') }}</el-button>
                <el-button size="large" class="secondary-button">{{ $t('hero.more') }}</el-button>
              </div>
            </div>
            
            <div class="hero-right">
              <div class="hero-bg-orb"></div>
              <!-- Floating Icons Decoration -->
              <div class="floating-icons">
                <div v-if="platforms[0]" class="float-icon icon-1">
                  <div class="platform-icon-wrapper">
                    <img :src="platforms[0].icon" :alt="platforms[0].name" />
                    <span class="platform-label">{{ platforms[0].name }}</span>
                  </div>
                </div>
                <div v-if="platforms[1]" class="float-icon icon-2">
                  <div class="platform-icon-wrapper">
                    <img :src="platforms[1].icon" :alt="platforms[1].name" />
                    <span class="platform-label">{{ platforms[1].name }}</span>
                  </div>
                </div>
                <div v-if="platforms[2]" class="float-icon icon-3">
                  <div class="platform-icon-wrapper">
                    <img :src="platforms[2].icon" :alt="platforms[2].name" />
                    <span class="platform-label">{{ platforms[2].name }}</span>
                  </div>
                </div>
                <div class="center-glow"></div>
              </div>
            </div>
          </div>
        </section>

        <!-- Features Section -->
        <section id="features" class="features-section">
          <h2 class="section-title">{{ $t('features.title') }}</h2>
          <div class="features-grid">
            <div v-for="(item, index) in features" :key="index" class="feature-card">
              <div class="feature-icon" :style="{ backgroundColor: item.color + '20', color: item.color }">
                <el-icon><component :is="item.icon" /></el-icon>
              </div>
              <h3 class="feature-title">{{ item.title }}</h3>
              <p class="feature-desc">{{ item.desc }}</p>
            </div>
          </div>
        </section>
      </main>

      <!-- Footer -->
      <footer id="about" class="footer">
        <div class="footer-content">
          <p>{{ $t('footer.copyright') }}</p>
          <div class="legal-links">
            <a href="#">{{ $t('footer.userAgreement') }}</a>
            <span class="divider">|</span>
            <a href="#">{{ $t('footer.privacyPolicy') }}</a>
            <span class="divider">|</span>
            <a href="#">{{ $t('footer.disclaimer') }}</a>
          </div>
        </div>
      </footer>
    </div>
  </el-scrollbar>
</template>

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
/* Scrollbar Styles */
.home-scrollbar {
  height: 100vh;
}

.home-scroll-wrap {
  overflow-x: hidden !important;
}

.home-container {
  min-height: 100vh;
  background-color: #0f1115; /* Dark background for quality feel */
  color: #fff;
  font-family: 'Inter', system-ui, sans-serif;
}

/* Header Styles */
.fixed-header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 72px;
  background: rgba(10, 10, 12, 0.6);
  backdrop-filter: blur(20px) saturate(180%);
  z-index: 1000;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.header-content {
  max-width: 1200px;
  height: 100%;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 22px;
  font-weight: 800;
  color: #fff;
  letter-spacing: -0.5px;
}

.logo-text {
  background: linear-gradient(to right, #fff, #c4b5fd); /* White to light violet */
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.nav-links {
  display: flex;
  gap: 32px;
}

.nav-links a {
  position: relative;
  color: #a0a0a0;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: color 0.3s;
}

.nav-links a:hover {
  color: #fff;
  text-shadow: 0 0 8px rgba(255, 255, 255, 0.3);
}

.nav-links a::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 0;
  height: 2px;
  background: #8b5cf6; /* Violet underline */
  transition: width 0.3s ease;
}

.nav-links a:hover::after {
  width: 100%;
}

/* Button Styles */
.login-btn {
  color: rgba(255, 255, 255, 0.7) !important;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
  background: transparent !important; /* Force transparent background */
}

.nav-links a.active {
  color: #fff;
  text-shadow: 0 0 8px rgba(255, 255, 255, 0.3);
}

.nav-links a.active::after {
  width: 100%;
}

.login-btn:hover {
  color: #fff !important;
  background: transparent !important; /* Force transparent background on hover */
  text-shadow: 0 0 12px rgba(255, 255, 255, 0.8), 0 0 20px rgba(139, 92, 246, 0.6);
}

/* Override Element Plus default hover background for text button */
.login-btn.el-button.is-text:not(.is-disabled):hover,
.login-btn.el-button.is-text:not(.is-disabled):focus {
  background-color: transparent !important;
}

/* Primary Button Gradient - Purple/Indigo Theme */
.el-button--primary {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%); /* Indigo to Violet */
  border: none;
  font-weight: 600;
  letter-spacing: 0.5px;
  box-shadow: 0 4px 14px 0 rgba(99, 102, 241, 0.39);
  transition: all 0.3s ease;
}

.el-button--primary:hover {
  background: linear-gradient(135deg, #818cf8 0%, #a78bfa 100%);
  transform: translateY(-1px);
  box-shadow: 0 6px 20px 0 rgba(99, 102, 241, 0.5);
}

.el-button--primary:active {
  transform: translateY(0);
}

/* Secondary Button Glassmorphism */
.secondary-button {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  color: #fff !important;
  backdrop-filter: blur(10px);
  font-weight: 600;
  transition: all 0.3s ease;
}

.secondary-button:hover {
  background: rgba(255, 255, 255, 0.1) !important;
  border-color: rgba(255, 255, 255, 0.2) !important;
  transform: translateY(-1px);
}

.cta-button {
  padding: 12px 36px;
  font-size: 16px;
  height: auto;
}

.github-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  transition: background-color 0.3s;
}

.github-icon:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.github-icon img {
  width: 20px;
  height: 20px;
  filter: invert(1); /* Make black icon white */
}

/* Hero Section */
.hero-section {
  padding-top: 140px;
  padding-bottom: 80px;
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
}

.hero-container {
  max-width: 1200px;
  width: 100%;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 48px;
}

.hero-left {
  flex: 1;
  text-align: left;
  z-index: 10;
}

.hero-right {
  flex: 1;
  height: 500px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-bg-orb {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 450px;
  height: 450px;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.15) 0%, rgba(139, 92, 246, 0.05) 40%, rgba(0, 0, 0, 0) 70%);
  border-radius: 50%;
  filter: blur(40px);
  z-index: 0;
  pointer-events: none;
  animation: pulse-glow 4s ease-in-out infinite alternate;
}

@keyframes pulse-glow {
  0% { transform: translate(-50%, -50%) scale(1); opacity: 0.8; }
  100% { transform: translate(-50%, -50%) scale(1.1); opacity: 1; }
}

.hero-title {
  font-size: 56px;
  font-weight: 800;
  background: linear-gradient(135deg, #fff 0%, #a5c0ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 24px;
  letter-spacing: -1px;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 18px;
  color: #8890a4;
  line-height: 1.6;
  margin-bottom: 48px;
  max-width: 600px;
}

.hero-actions {
  display: flex;
  gap: 20px;
}

.cta-button {
  /* removed duplicate */
}

/* Floating Icons */
.floating-icons {
  position: relative;
  width: 100%;
  height: 100%;
  z-index: 1; /* Ensure icons are above the orb */
}

.center-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(64, 158, 255, 0.2) 0%, rgba(0, 0, 0, 0) 70%);
  border-radius: 50%;
  filter: blur(40px);
  z-index: -1;
}

.float-icon {
  position: absolute;
  width: auto;
  height: auto;
  background: transparent;
  backdrop-filter: none;
  border-radius: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: none;
  box-shadow: none;
  animation: float 6s ease-in-out infinite;
  overflow: visible;
  transition: all 0.3s ease;
}

.float-icon:hover {
  background: transparent;
  transform: translateY(-5px) scale(1.1);
  box-shadow: none;
  border-color: transparent;
}

.platform-icon-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: auto;
  height: auto;
  padding: 0;
}

.platform-icon-wrapper img {
  width: 64px;
  height: 64px;
  margin-bottom: 12px;
  object-fit: contain;
  filter: drop-shadow(0 8px 16px rgba(0,0,0,0.3));
  transition: transform 0.3s ease;
}

.float-icon:hover img {
  transform: scale(1.1);
  filter: drop-shadow(0 12px 24px rgba(64, 158, 255, 0.4));
}

.platform-label {
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
  background: linear-gradient(120deg, #fff, #a0cfff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 4px 12px rgba(0,0,0,0.5);
  opacity: 0.9;
}

.icon-1 { top: 15%; left: 10%; animation-delay: 0s; }
.icon-2 { top: 35%; right: 5%; animation-delay: 1.5s; }
.icon-3 { bottom: 20%; left: 20%; animation-delay: 3s; }

@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
  100% { transform: translateY(0px); }
}

@media (max-width: 992px) {
  .hero-container {
    flex-direction: column;
    text-align: center;
  }
  
  .hero-left {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  .hero-right {
    width: 100%;
    height: 400px;
  }
}

/* Features Section */
.features-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 80px 24px;
}

.section-title {
  text-align: center;
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 64px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.feature-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  padding: 32px;
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.1);
}

.feature-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 24px;
}

.feature-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #fff;
}

.feature-desc {
  font-size: 14px;
  color: #8890a4;
  line-height: 1.6;
}

/* Platforms Section */
.platforms-section {
  text-align: center;
  padding: 80px 24px;
  background: rgba(255, 255, 255, 0.01);
}

.platforms-list {
  display: flex;
  justify-content: center;
  gap: 48px;
  flex-wrap: wrap;
}

.platform-item {
  font-size: 24px;
  font-weight: 600;
  color: #8890a4;
  padding: 16px 32px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 50px;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

/* Footer */
.footer {
  padding: 48px 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  color: #666;
  font-size: 14px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.legal-links {
  display: flex;
  align-items: center;
  gap: 16px;
}

.legal-links a {
  color: #666;
  text-decoration: none;
  transition: color 0.3s;
}

.legal-links a:hover {
  color: #999;
}

.divider {
  color: #333;
}

/* Responsive */
@media (max-width: 768px) {
  .hero-title {
    font-size: 36px;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .nav-links {
    display: none;
  }
  
  .footer-content {
    flex-direction: column;
    gap: 16px;
  }
}
</style>
