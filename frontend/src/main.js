import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
// Element Plus UI组件库
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
// 中文语言包
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

const app = createApp(App)

// 注册所有Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 使用Element Plus（中文语言）
app.use(ElementPlus, { locale: zhCn })
app.use(router)
app.mount('#app')
