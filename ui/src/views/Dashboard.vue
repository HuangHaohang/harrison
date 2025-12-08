<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <!-- 欢迎卡片 -->
      <el-col :span="24" class="mb-20">
        <el-card class="welcome-card">
          <div class="welcome-content">
            <div class="welcome-left">
              <el-avatar :size="64" :src="userAvatar" />
              <div class="welcome-text">
                <h2>{{ t('dashboard.welcome', { time: t(getTimeState()), name: userStore.user?.nickname || userStore.user?.username || 'Admin' }) }}</h2>
                <p>{{ t('dashboard.goodDay') }}</p>
              </div>
            </div>
            <div class="welcome-right">
              <div class="stat-item">
                <span class="label">{{ t('dashboard.todo') }}</span>
                <span class="value">4</span>
              </div>
              <div class="stat-item">
                <span class="label">{{ t('dashboard.message') }}</span>
                <span class="value">12</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 数据统计卡片 -->
      <el-col :xs="24" :sm="12" :md="6" class="mb-20" v-for="(item, index) in statistics" :key="index">
        <el-card class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-icon" :style="{ background: item.color }">
            <el-icon color="#fff" :size="24">
              <component :is="item.icon" />
            </el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-title">{{ item.title }}</div>
            <div class="stat-number">
              <count-to :start-val="0" :end-val="item.value" :duration="2000" />
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 图表区域 -->
      <el-col :xs="24" :lg="16" class="mb-20">
        <el-card :title="t('dashboard.visitTrend')">
          <div ref="chartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>

      <!-- 快捷入口 -->
      <el-col :xs="24" :lg="8" class="mb-20">
        <el-card :title="t('dashboard.shortcuts')">
          <div class="shortcut-grid">
            <div class="shortcut-item" v-for="item in shortcuts" :key="item.title">
              <div class="shortcut-icon" :style="{ background: item.color }">
                <el-icon color="#fff" :size="20">
                  <component :is="item.icon" />
                </el-icon>
              </div>
              <span>{{ item.title }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { User, View, Message, Goods, Setting, Monitor, DataAnalysis, Document } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const userStore = useAuthStore()
const userAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 欢迎语
const getTimeState = () => {
  const hours = new Date().getHours()
  if (hours <= 9) return 'dashboard.morning'
  if (hours <= 12) return 'dashboard.forenoon'
  if (hours <= 18) return 'dashboard.afternoon'
  return 'dashboard.evening'
}

// 统计数据
const statistics = computed(() => [
  { title: t('dashboard.visits'), value: 12000, icon: View, color: '#409EFF' },
  { title: t('dashboard.users'), value: 800, icon: User, color: '#67C23A' },
  { title: t('dashboard.messages'), value: 120, icon: Message, color: '#E6A23C' },
  { title: t('dashboard.orders'), value: 5600, icon: Goods, color: '#F56C6C' }
])

// 快捷入口
const shortcuts = computed(() => [
  { title: t('dashboard.sysManage'), icon: Setting, color: '#409EFF' },
  { title: t('dashboard.sysMonitor'), icon: Monitor, color: '#67C23A' },
  { title: t('dashboard.dataAnalysis'), icon: DataAnalysis, color: '#E6A23C' },
  { title: t('dashboard.logView'), icon: Document, color: '#F56C6C' },
])

// 简单的数字滚动组件逻辑 (实际项目建议封装为单独组件)
const CountTo = {
  props: ['startVal', 'endVal', 'duration'],
  setup(props) {
    const displayValue = ref(props.startVal)
    onMounted(() => {
      const step = (props.endVal - props.startVal) / (props.duration / 16)
      let current = props.startVal
      const timer = setInterval(() => {
        current += step
        if (current >= props.endVal) {
          current = props.endVal
          clearInterval(timer)
        }
        displayValue.value = Math.floor(current)
      }, 16)
    })
    return () => displayValue.value
  }
}

// 图表
const chartRef = ref()
let chartInstance: echarts.ECharts | null = null

onMounted(() => {
  if (chartRef.value) {
    chartInstance = echarts.init(chartRef.value)
    chartInstance.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
      },
      yAxis: { type: 'value' },
      series: [
        {
          data: [820, 932, 901, 934, 1290, 1330, 1320],
          type: 'line',
          smooth: true,
          areaStyle: {}
        }
      ]
    })
    
    window.addEventListener('resize', resizeChart)
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeChart)
  chartInstance?.dispose()
})

const resizeChart = () => {
  chartInstance?.resize()
}
</script>

<style scoped>
.dashboard-container {
  /* padding: 20px; 已在 layout 中设置 */
}

.mb-20 {
  margin-bottom: 20px;
}

.welcome-card {
  background: linear-gradient(135deg, #fff 0%, #f0f2f5 100%);
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.welcome-text h2 {
  margin: 0 0 10px 0;
  font-size: 20px;
  color: #303133;
}

.welcome-text p {
  margin: 0;
  color: #909399;
}

.welcome-right {
  display: flex;
  gap: 40px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-item .label {
  color: #909399;
  font-size: 14px;
  margin-bottom: 5px;
}

.stat-item .value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-card {
  display: flex;
  align-items: center;
  transition: all 0.3s;
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 20px;
}

.stat-info {
  flex: 1;
}

.stat-title {
  color: #909399;
  font-size: 14px;
  margin-bottom: 5px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.shortcut-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.shortcut-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  padding: 15px;
  border-radius: 4px;
  transition: background 0.3s;
}

.shortcut-item:hover {
  background: #f5f7fa;
}

.shortcut-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 10px;
}
</style>
