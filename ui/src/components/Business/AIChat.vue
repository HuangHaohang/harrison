<template>
  <div class="ai-chat-container">
    <!-- 悬浮图标 -->
    <div class="chat-trigger" @click="toggleChat" v-show="!isOpen">
      <img src="@/assets/nerd_face_color.svg" alt="AI Chat" class="trigger-icon" />
    </div>

    <!-- 对话框 -->
    <transition name="chat-fade">
      <div class="chat-window" v-show="isOpen">
        <!-- 头部 -->
        <div class="chat-header">
          <div class="header-left">
            <img src="@/assets/nerd_face_color.svg" alt="AI" class="header-icon" />
            <span class="header-title">AI Assistant</span>
          </div>
          <div class="header-right">
            <el-icon class="close-icon" @click="toggleChat"><Close /></el-icon>
          </div>
        </div>

        <!-- 消息列表 -->
        <div class="chat-messages" ref="messagesRef">
          <div 
            v-for="(msg, index) in messages" 
            :key="index" 
            class="message-item" 
            :class="{ 'message-user': msg.role === 'user', 'message-ai': msg.role === 'assistant' }"
          >
            <div class="message-avatar" v-if="msg.role === 'assistant'">
              <img src="@/assets/nerd_face_color.svg" alt="AI" />
            </div>
            <div class="message-content-wrapper">
              <div class="message-content">
                <div v-if="msg.role === 'assistant' && msg.content === '' && msg.isStreaming" class="streaming-indicator">
                  <span class="dot"></span><span class="dot"></span><span class="dot"></span>
                </div>
                <div v-else class="message-text" v-html="formatMessage(msg.content)"></div>
              </div>
              <div class="message-meta">
                <span class="message-time">{{ formatTime(msg.timestamp) }}</span>
                <el-button 
                  v-if="msg.role === 'assistant' && msg.content" 
                  size="small" 
                  text 
                  class="copy-btn"
                  @click="copyMessage(msg.content)"
                >
                  <el-icon><CopyDocument /></el-icon>
                </el-button>
              </div>
            </div>
            <div class="message-avatar" v-if="msg.role === 'user'">
              <el-avatar :size="36" :src="authStore.user?.avatar || ''" icon="UserFilled" />
            </div>
          </div>
        </div>

        <!-- 输入框区域 -->
        <div class="chat-input-area">
          <div class="input-wrapper">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="3"
              placeholder="输入消息..."
              resize="none"
              @keyup.enter.prevent="handleEnterKey"
              class="message-input"
            />
            
            <div class="input-toolbar">
              <div class="toolbar-left">
                <!-- Model Selector -->
                <el-select 
                  v-model="selectedModel" 
                  size="small" 
                  style="width: 160px;" 
                  class="model-select-input"
                  @change="onModelChange"
                  placement="top-start"
                >
                  <template #prefix>
                    <el-icon><Cpu /></el-icon>
                  </template>
                  <el-option 
                    v-for="model in availableModels" 
                    :key="model.id"
                    :label="model.name" 
                    :value="model.id"
                  >
                    <div class="model-option">
                      <span class="model-name">{{ model.name }}</span>
                      <span class="model-desc">{{ model.description }}</span>
                    </div>
                  </el-option>
                </el-select>
                
                <!-- Deep Think Toggle -->
                <div v-if="currentModel?.supports_deep_think" class="deep-think-wrapper">
                  <el-switch
                    v-model="deepThinkEnabled"
                    size="small"
                    active-text="深度思考"
                    inactive-text=""
                    class="deep-think-switch"
                  />
                </div>
              </div>
              
              <div class="toolbar-right">
                <el-button 
                  type="primary" 
                  class="send-btn" 
                  @click="sendMessage" 
                  :disabled="!inputMessage.trim() || loading"
                  :loading="loading"
                  circle
                >
                  <el-icon><Position /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
          
          <!-- 免责声明 -->
          <div class="disclaimer">
            内容由 AI 生成，仅供参考，您据此所作判断及操作均由您自行承担责任。
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, watch, onUnmounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Close, Position, UserFilled, CopyDocument, Cpu } from '@element-plus/icons-vue'
import MarkdownIt from 'markdown-it'
import { useAuthStore } from '@/stores/auth'

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'
const authStore = useAuthStore()

const isOpen = ref(false)
const inputMessage = ref('')
const loading = ref(false)
const messagesRef = ref<HTMLElement | null>(null)
const selectedModel = ref('deepseek-v3.2')
const deepThinkEnabled = ref(false)
const threadId = ref<string>('')
const es = ref<EventSource | null>(null)

// 可用模型列表
const availableModels = ref([
  {
    id: 'qwen3-vl-plus',
    name: '通义千问3VL Plus',
    supports_deep_think: false,
    description: '视觉理解模型'
  },
  {
    id: 'wan2.5-i2v-perview',
    name: '万相2.5图像生成',
    supports_deep_think: false,
    description: '图像生成模型'
  },
  {
    id: 'deepseek-v3.2',
    name: 'DeepSeek V3.2',
    supports_deep_think: true,
    description: '支持深度思考的推理模型'
  }
])

// 当前选中的模型
const currentModel = computed(() => {
  return availableModels.value.find(model => model.id === selectedModel.value)
})

interface Message {
  role: 'user' | 'assistant'
  content: string
  timestamp: Date
  isStreaming?: boolean
  model?: string
  deep_think?: boolean
}

const messages = ref<Message[]>([
  { 
    role: 'assistant', 
    content: '你好！我是AI助手，有什么可以帮助你的吗？',
    timestamp: new Date()
  }
])

const md = new MarkdownIt({
  html: false,
  linkify: true,
  breaks: true
})

const toggleChat = () => {
  isOpen.value = !isOpen.value
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

const formatMessage = (content: string) => {
  if (!content) return ''
  return md.render(content)
}

const formatTime = (date: Date) => {
  return date.toLocaleTimeString('zh-CN', { 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

const copyMessage = async (content: string) => {
  try {
    await navigator.clipboard.writeText(content)
    ElMessage.success('已复制到剪贴板')
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

const handleEnterKey = (event: KeyboardEvent) => {
  if (!event.shiftKey) {
    event.preventDefault()
    sendMessage()
  }
}

const onModelChange = () => {
  // 模型切换时重置深度思考状态
  if (!currentModel.value?.supports_deep_think) {
    deepThinkEnabled.value = false
  }
}

const initThread = () => {
  const saved = localStorage.getItem('ai_thread_id')
  if (saved) {
    threadId.value = saved
  } else {
    threadId.value = crypto.randomUUID()
    localStorage.setItem('ai_thread_id', threadId.value)
  }
}

const sendMessage = () => {
  if (!inputMessage.value.trim() || loading.value) return

  // 添加用户消息
  const userMessage: Message = {
    role: 'user',
    content: inputMessage.value,
    timestamp: new Date()
  }
  messages.value.push(userMessage)
  
  const userMsg = inputMessage.value
  inputMessage.value = ''
  scrollToBottom()

  // 开始加载状态
  loading.value = true

  // 创建AI消息（用于流式响应）
  const aiMessage: Message = {
    role: 'assistant',
    content: '',
    timestamp: new Date(),
    isStreaming: true,
    model: selectedModel.value,
    deep_think: deepThinkEnabled.value
  }
  messages.value.push(aiMessage)
  const aiIndex = messages.value.length - 1

  try {
    const params = new URLSearchParams({
      question: userMsg,
      modelId: selectedModel.value,
      threadId: threadId.value,
      ...(deepThinkEnabled.value && { deep_think: 'true' })
    })
    
    const url = `${baseURL}/ai/chat/stream?${params.toString()}`
    es.value = new EventSource(url)
    
    es.value.onmessage = (e) => {
      if (e.data && messages.value[aiIndex]) {
        try {
          const data = JSON.parse(e.data)
          if (data.content !== undefined) {
            messages.value[aiIndex].content = data.content
            messages.value[aiIndex].isStreaming = !data.done
            scrollToBottom()
          }
        } catch (err) {
          // 如果不是JSON格式，直接追加文本
          messages.value[aiIndex].content += e.data
          scrollToBottom()
        }
      }
    }
    
    es.value.onerror = () => {
      loading.value = false
      if (messages.value[aiIndex]) {
        messages.value[aiIndex].isStreaming = false
        if (!messages.value[aiIndex].content) {
          messages.value[aiIndex].content = '服务异常或网络错误'
        }
      }
      es.value?.close()
      es.value = null
      scrollToBottom()
    }
    
    es.value.onopen = () => {}
    
  } catch (error) {
    console.error(error)
    loading.value = false
    if (messages.value[aiIndex]) {
      messages.value[aiIndex].isStreaming = false
      messages.value[aiIndex].content = '服务异常或网络错误'
    }
    scrollToBottom()
  }
}

watch(isOpen, (newVal) => {
  if (newVal) {
    scrollToBottom()
  }
})

onUnmounted(() => {
  es.value?.close()
})

initThread()
</script>

<style scoped>
/* Reset & Base */
.ai-chat-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1500;
}

/* Chat Trigger */
.chat-trigger {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.chat-trigger:hover {
  transform: scale(1.1);
}

.trigger-icon {
  width: 40px;
  height: 40px;
}

/* Chat Window */
.chat-window {
  width: 800px;
  height: 700px;
  background-color: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid rgba(0,0,0,0.1);
}

/* Header */
.chat-header {
  height: 60px;
  background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%);
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-icon {
  width: 28px;
  height: 28px;
  margin-right: 12px;
  background: #fff;
  border-radius: 50%;
  padding: 2px;
}

.header-title {
  font-weight: 600;
  font-size: 16px;
}

.close-icon {
  cursor: pointer;
  font-size: 20px;
  transition: transform 0.2s ease;
  opacity: 0.8;
}

.close-icon:hover {
  transform: rotate(90deg);
  opacity: 1;
}

/* Messages Area */
.chat-messages {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background: #f8fafc;
}

.message-item {
  display: flex;
  margin-bottom: 24px;
  align-items: flex-start;
  animation: messageFadeIn 0.3s ease;
}

.message-user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  margin: 0 16px;
  flex-shrink: 0;
}

.message-avatar img, 
.message-avatar .el-avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  background: #fff;
}

.message-content-wrapper {
  max-width: 80%;
  display: flex;
  flex-direction: column;
}

.message-content {
  padding: 14px 18px;
  border-radius: 12px;
  font-size: 15px;
  line-height: 1.6;
  word-break: break-word;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.message-ai .message-content {
  background: #fff;
  color: #1f2937;
  border-top-left-radius: 2px;
  border: 1px solid #e5e7eb;
}

.message-user .message-content {
  background: #3b82f6;
  color: #fff;
  border-top-right-radius: 2px;
}

/* Markdown Styles */
.message-text :deep(p) {
  margin-bottom: 8px;
}

.message-text :deep(p:last-child) {
  margin-bottom: 0;
}

.message-text :deep(pre) {
  background: #1e293b;
  color: #e2e8f0;
  padding: 16px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 12px 0;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 13px;
}

.message-text :deep(code) {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  background: rgba(0,0,0,0.05);
  padding: 2px 4px;
  border-radius: 4px;
  font-size: 0.9em;
}

.message-user .message-text :deep(code) {
  background: rgba(255,255,255,0.2);
  color: #fff;
}

.message-text :deep(pre code) {
  background: transparent;
  padding: 0;
  color: inherit;
}

/* Message Meta */
.message-meta {
  display: flex;
  align-items: center;
  margin-top: 4px;
  font-size: 12px;
  color: #9ca3af;
  padding: 0 4px;
}

.message-user .message-meta {
  justify-content: flex-end;
}

.copy-btn {
  padding: 0 8px;
  height: 20px;
  margin-left: 8px;
}

/* Input Area & Toolbar */
.chat-input-area {
  background: #fff;
  border-top: 1px solid #e5e7eb;
  padding: 20px;
}

.input-wrapper {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.02);
  transition: all 0.2s ease;
  padding: 8px;
  display: flex;
  flex-direction: column;
}

.input-wrapper:focus-within {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.message-input :deep(.el-textarea__inner) {
  border: none !important;
  box-shadow: none !important;
  padding: 8px;
  font-size: 15px;
  line-height: 1.5;
  background: transparent;
}

.input-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 8px;
  margin-top: 4px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.model-select-input :deep(.el-input__wrapper) {
  box-shadow: none !important;
  background: #f3f4f6;
  border-radius: 6px;
  padding: 4px 8px;
}

.model-select-input :deep(.el-input__inner) {
  font-size: 13px;
  color: #4b5563;
  height: 24px;
}

.deep-think-wrapper {
  display: flex;
  align-items: center;
}

.deep-think-switch :deep(.el-switch__label) {
  color: #6b7280;
  font-size: 12px;
}

.send-btn {
  width: 36px;
  height: 36px;
  font-size: 16px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.disclaimer {
  text-align: center;
  font-size: 11px;
  color: #9ca3af;
  margin-top: 12px;
}

/* Streaming Animation */
.streaming-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 0;
}

.dot {
  width: 6px;
  height: 6px;
  background-color: #94a3b8;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.dot:nth-child(1) { animation-delay: -0.32s; }
.dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

@keyframes messageFadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Scrollbar */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}
.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}
.chat-messages::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 3px;
}
.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #cbd5e1;
}

/* Mobile Responsive */
@media (max-width: 768px) {
  .chat-window {
    width: 100vw;
    height: 100vh;
    border-radius: 0;
    bottom: 0;
    right: 0;
    position: fixed;
  }
  
  .ai-chat-container {
    bottom: 0;
    right: 0;
  }

  .message-content-wrapper {
    max-width: 85%;
  }
  
  .input-toolbar {
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .model-select-input {
    width: 130px !important;
  }
}
</style>