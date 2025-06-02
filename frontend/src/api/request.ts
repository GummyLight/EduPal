import axios from 'axios'
import {ElMessage } from 'element-plus'

// 根据环境设置基础URL
const getBaseURL = () => {
  // 优先使用环境变量
  return '/api'
}

//创建axios实例
const api = axios.create({
    baseURL: getBaseURL(),
    timeout: 10000, // 默认10秒超时，AI请求会单独设置更长的超时
    headers: {
        'Content-Type': 'application/json'
    }
})

//请求拦截器
api.interceptors.request.use(
    (config) => {
        // 暂时不使用token认证，后端可能使用其他认证方式
        // const token = localStorage.getItem('token');
        // if (token) {
        //     config.headers['Authorization'] = `Bearer ${token}`;
        // }
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
    // 检查是否为用户主动取消的请求
    if (axios.isCancel(error)) {
      // 用户主动取消请求，不显示错误消息，直接抛出错误
      return Promise.reject(error)
    }
    
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
          case 408:
            ElMessage.error('请求超时，请稍后重试')
            break
          default:
            ElMessage.error('请求失败')
        }
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请检查网络连接或稍后重试')
    } else {
      ElMessage.error('网络错误，请检查连接')
    }
    return Promise.reject(error)
  }
)

export default api