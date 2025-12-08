import { createI18n } from 'vue-i18n'
import zhCN from '@/locales/zh-CN.json'
import zhTW from '@/locales/zh-TW.json'
import en from '@/locales/en.json'

// 获取浏览器语言或缓存语言
const getLocale = () => {
  const cached = localStorage.getItem('lang')
  if (cached) {
    return cached
  }
  const language = navigator.language.toLowerCase()
  if (language.indexOf('zh-cn') > -1) {
    return 'zh-CN'
  }
  if (language.indexOf('zh-tw') > -1) {
    return 'zh-TW'
  }
  return 'en'
}

const i18n = createI18n({
  legacy: false, // 使用 Composition API
  locale: getLocale(),
  fallbackLocale: 'en',
  messages: {
    'zh-CN': zhCN,
    'zh-TW': zhTW,
    'en': en
  }
})

export default i18n
