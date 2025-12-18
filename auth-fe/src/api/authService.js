import api from './axios'

export default {
  async login({ username, password }) {
    const response = await api.post('/auth/login', { username, password }, { skipAuth: true })
    return response.data
  },
}
