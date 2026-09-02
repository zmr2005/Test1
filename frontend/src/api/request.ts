import axios from 'axios'
import { ElMessage } from 'element-plus'

// baseURL 通过环境变量配置；默认走同源，需在 vite.config.ts 配置 proxy：
//   server.proxy = { '/api': { target: 'http://localhost:8080', changeOrigin: true, rewrite: p => p.replace(/^\/api/, '') } }
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  timeout: 15000,
})

request.interceptors.request.use((config) => {
  // 后端无登录模块，当前用户通过请求头传递（占位，后续接入认证后替换为 token）
  config.headers['X-User-Id'] = localStorage.getItem('userId') || '0'
  return config
})

request.interceptors.response.use(
  (res) => {
    const result = res.data
    if (result && result.code === 0) {
      return result.data
    }
    const msg = result?.message || '请求失败'
    ElMessage.error(msg)
    return Promise.reject(new Error(msg))
  },
  (err) => {
    const msg = err.response?.data?.message || err.message || '网络错误'
    ElMessage.error(msg)
    return Promise.reject(err)
  },
)

export default request
