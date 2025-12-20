import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './style.css'
import App from './App.vue'
import router from './router/index.js'
import ToastPlugin from 'vue-toast-notification'
import 'vue-toast-notification/dist/theme-bootstrap.css'
import { plugin as FormKitPlugin, defaultConfig } from '@formkit/vue'
import '@formkit/themes/genesis'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(ToastPlugin, { position: 'top-right' })
app.use(FormKitPlugin, defaultConfig)
app.mount('#app')
