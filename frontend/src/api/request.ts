import axios from 'axios'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api/v1',
  timeout: 15000,
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 统一返回后端响应体数据，调用方直接拿业务数据
request.interceptors.response.use(
  (res) => res.data,
  (err) => Promise.reject(err),
)

export default request
