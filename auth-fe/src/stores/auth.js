import { defineStore } from 'pinia'
import authService from '../api/authService'
import { useRouter } from 'vue-router'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const token = ref(sessionStorage.getItem('auth') ? JSON.parse(sessionStorage.getItem('auth')).accessToken : null)
  const loading = ref(false)
  const error = ref(null)
  const router = useRouter()

  async function login({ username, password }) {
    loading.value = true
    error.value = null
    try {
      const data = await authService.login({ username, password })
      const authObj = {
        accessToken: data.accessToken,
        refreshToken: data.refreshToken,
        user: data.user
      }
      sessionStorage.setItem('auth', JSON.stringify(authObj))
      token.value = data.accessToken
      user.value = data.user
      loading.value = false
      router.push('/dashboard')
    } catch (err) {
      error.value = err.response?.data?.message || 'Login failed'
      loading.value = false
    }
  }

  async function register({ username, password, email, role }) {
    loading.value = true
    error.value = null
    try {
      const data = await authService.register({ username, password, email, role })
      const authObj = {
        accessToken: data.accessToken,
        refreshToken: data.refreshToken,
        user: data.user
      }
      sessionStorage.setItem('auth', JSON.stringify(authObj))
      user.value = data.user
      token.value = data.accessToken
      loading.value = false
      router.push('/dashboard')
    } catch (err) {
      error.value = err.response?.data?.message || 'Registration failed'
      loading.value = false
      throw err
    }
  }

  function logout() {
    token.value = null
    user.value = null
    sessionStorage.removeItem('auth')
    router.push('/')
  }

  return { user, token, loading, error, login, logout, register }
})
