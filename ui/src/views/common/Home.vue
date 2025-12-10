<template>
  <div class="home-container">
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">
          <span class="gradient-text">Harrison</span> {{ t('home.heroTitle').replace('Harrison ', '') }}
        </h1>
        <p class="hero-subtitle">
          {{ t('home.heroSubtitle') }}
        </p>
        <div class="hero-actions">
          <el-button type="primary" size="large" class="start-btn" @click="toggleChat">
            {{ t('home.startChat') }}
            <el-icon class="el-icon--right"><ChatDotRound /></el-icon>
          </el-button>
          <el-button size="large" class="doc-btn" @click="openDocs">
            {{ t('home.docs') }}
            <el-icon class="el-icon--right"><Document /></el-icon>
          </el-button>
        </div>
      </div>
      <div class="hero-image">
        <div class="floating-card card-1">
          <el-icon><Cpu /></el-icon>
          <span>{{ t('home.aiCore') }}</span>
        </div>
        <div class="floating-card card-2">
          <el-icon><Lock /></el-icon>
          <span>{{ t('home.rbac') }}</span>
        </div>
        <div class="floating-card card-3">
          <el-icon><Connection /></el-icon>
          <span>{{ t('home.gateway') }}</span>
        </div>
        <div class="glow-effect"></div>
      </div>
    </div>

    <div class="features-section">
      <div class="feature-card" v-for="(feature, index) in features" :key="index">
        <div class="feature-icon" :style="{ background: feature.bg }">
          <component :is="feature.icon" :color="feature.color" />
        </div>
        <h3>{{ feature.title }}</h3>
        <p>{{ feature.desc }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ChatDotRound, Document, Cpu, Lock, Connection, DataBoard, Service } from '@element-plus/icons-vue'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const features = computed(() => [
  {
    title: t('home.features.ai.title'),
    desc: t('home.features.ai.desc'),
    icon: Cpu,
    color: '#409EFF',
    bg: 'rgba(64, 158, 255, 0.1)'
  },
  {
    title: t('home.features.rbac.title'),
    desc: t('home.features.rbac.desc'),
    icon: Lock,
    color: '#67C23A',
    bg: 'rgba(103, 194, 58, 0.1)'
  },
  {
    title: t('home.features.microservices.title'),
    desc: t('home.features.microservices.desc'),
    icon: Service,
    color: '#E6A23C',
    bg: 'rgba(230, 162, 60, 0.1)'
  },
  {
    title: t('home.features.ui.title'),
    desc: t('home.features.ui.desc'),
    icon: DataBoard,
    color: '#F56C6C',
    bg: 'rgba(245, 108, 108, 0.1)'
  }
])

// 触发 AI 对话框打开
const toggleChat = () => {
  // 模拟点击右下角的 AI 图标
  const trigger = document.querySelector('.chat-trigger') as HTMLElement
  if (trigger) trigger.click()
}

const openDocs = () => {
  window.open('https://github.com/HuangHaohang/harrison', '_blank')
}
</script>

<style scoped>
.home-container {
  min-height: calc(100vh - 84px); /* 减去 header/tabs 高度 */
  background: radial-gradient(circle at top right, #f0f4ff 0%, #fff 40%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0 10%;
  position: relative;
  overflow: hidden;
  transition: background 0.3s ease;
}

/* Dark Mode Background */
html.dark .home-container {
  background: radial-gradient(circle at top right, #1e293b 0%, #0f172a 60%);
}

.hero-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 80px;
  position: relative;
  z-index: 1;
}

.hero-content {
  max-width: 600px;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 800;
  line-height: 1.2;
  margin-bottom: 20px;
  color: #1a1a1a;
  letter-spacing: -1px;
  transition: color 0.3s;
}

html.dark .hero-title {
  color: #e2e8f0;
}

.gradient-text {
  background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

html.dark .gradient-text {
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.hero-subtitle {
  font-size: 1.25rem;
  color: #606266;
  margin-bottom: 40px;
  line-height: 1.6;
  transition: color 0.3s;
}

html.dark .hero-subtitle {
  color: #94a3b8;
}

.hero-actions {
  display: flex;
  gap: 20px;
}

.start-btn {
  padding: 24px 32px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%);
  border: none;
  transition: transform 0.2s;
}

html.dark .start-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.start-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.3);
}

.doc-btn {
  padding: 24px 32px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
}

/* 装饰性元素 */
.hero-image {
  position: relative;
  width: 500px;
  height: 400px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.glow-effect {
  position: absolute;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.15) 0%, transparent 70%);
  filter: blur(40px);
  z-index: -1;
}

html.dark .glow-effect {
  background: radial-gradient(circle, rgba(59, 130, 246, 0.2) 0%, transparent 70%);
}

.floating-card {
  position: absolute;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  padding: 15px 25px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  color: #303133;
  border: 1px solid rgba(255, 255, 255, 0.5);
  animation: float 6s ease-in-out infinite;
  transition: background-color 0.3s, border-color 0.3s, color 0.3s;
}

html.dark .floating-card {
  background: rgba(30, 41, 59, 0.8);
  border-color: rgba(255, 255, 255, 0.1);
  color: #e2e8f0;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.floating-card .el-icon {
  font-size: 24px;
}

.card-1 {
  top: 10%;
  left: 10%;
  color: #409EFF;
  animation-delay: 0s;
}

.card-2 {
  bottom: 20%;
  left: 20%;
  color: #67C23A;
  animation-delay: 2s;
}

.card-3 {
  top: 40%;
  right: 10%;
  color: #E6A23C;
  animation-delay: 4s;
}

@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
  100% { transform: translateY(0px); }
}

.features-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
  padding: 40px 0;
}

.feature-card {
  background: #fff;
  padding: 30px;
  border-radius: 20px;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

html.dark .feature-card {
  background: #1e293b;
  border-color: rgba(255, 255, 255, 0.05);
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  border-color: rgba(0, 0, 0, 0.05);
}

html.dark .feature-card:hover {
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  background: #263548;
}

.feature-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 24px;
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 10px;
  color: #1a1a1a;
  transition: color 0.3s;
}

html.dark .feature-card h3 {
  color: #e2e8f0;
}

.feature-card p {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  transition: color 0.3s;
}

html.dark .feature-card p {
  color: #94a3b8;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .hero-section {
    flex-direction: column;
    text-align: center;
    margin-top: 40px;
  }
  
  .hero-content {
    margin-bottom: 60px;
  }
  
  .hero-actions {
    justify-content: center;
  }
  
  .features-section {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .features-section {
    grid-template-columns: 1fr;
  }
  
  .hero-image {
    display: none;
  }
}
</style>
