import { createApp } from "vue";
import { createPinia } from "pinia";
import App from "./App.vue";
import router from "./router";
import i18n from "./i18n";
import './style.css'

import ElementPlus from "element-plus";
import { ElMessage } from "element-plus"; // Import ElMessage
import "element-plus/dist/index.css";
import 'element-plus/theme-chalk/dark/css-vars.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App);

app.use(createPinia());
app.use(router);
app.use(i18n);
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.use(ElementPlus);

// Mount global properties
app.config.globalProperties.$message = ElMessage;

app.mount("#app");
