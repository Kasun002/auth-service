import { createRouter, createWebHistory } from 'vue-router'
import Auth from '../pages/Auth.vue'
import Dashboard from '../pages/Dashboard.vue'

const routes = [
  {
    path: '/',
    name: 'Auth',
    component: Auth
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
