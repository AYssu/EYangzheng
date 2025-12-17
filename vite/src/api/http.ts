import axios from 'axios'
import { message } from 'ant-design-vue'
import { useAuthStore } from '../stores/auth'

const http = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

http.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  if (authStore.token) {
    config.headers = {
      ...config.headers,
      Authorization: `Bearer ${authStore.token}`,
    }
  }
  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.data?.message) {
      message.error(error.response.data.message)
    } else {
      message.error('请求失败，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default http

