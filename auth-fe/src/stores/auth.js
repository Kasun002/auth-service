import { defineStore } from 'pinia'
import authService from '../api/authService'
import { useRouter } from 'vue-router'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('access_token') || null)
  const loading = ref(false)
  const error = ref(null)
  const router = useRouter()

  async function login({ username, password }) {
    loading.value = true
    error.value = null
    try {
      const data = await authService.login({ username, password })
      token.value = data.accessToken
      user.value = data.user
      localStorage.setItem('access_token', data.accessToken)
      loading.value = false
      router.push('/dashboard')
    } catch (err) {
      error.value = err.response?.data?.message || 'Login failed'
      loading.value = false
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('access_token')
    router.push('/')
  }

  return { user, token, loading, error, login, logout }
})
