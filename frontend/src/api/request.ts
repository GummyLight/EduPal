import axios from 'axios'
import {ElMessage } from 'element-plus'

//创建axios实例
const api = axios.create({
    baseURL: '/api',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
})

//请求拦截器
api.interceptors.request.use(
    (config) => {
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response
      
      // 根据后端的响应格式处理错误
      if (data && data.code !== 200) {
        ElMessage.error(data.message || '请求失败')
      } else {
        switch (status) {
          case 401:
            ElMessage.error('未授权，请重新登录')
            localStorage.removeItem('token')
            window.location.href = '/login'
            break
          case 403:
            ElMessage.error('权限不足')
            break
          case 500:
            ElMessage.error('服务器错误')
            break
          default:
            ElMessage.error('请求失败')
        }
      }
    } else {
      ElMessage.error('网络错误，请检查连接')
    }
    return Promise.reject(error)
  }
)

export default api