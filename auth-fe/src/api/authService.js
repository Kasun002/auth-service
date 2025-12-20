import api from './axios'

export default {
  async login({ username, password }) {
    const response = await api.post('/auth/login', { username, password }, { skipAuth: true })
    return response.data
  },
  async register({ username, password, email, role }) {
    const response = await api.post('/auth/register', { username, password, email, role }, { skipAuth: true })
    return response.data
  },
}
