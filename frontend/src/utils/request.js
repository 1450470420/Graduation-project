import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  withCredentials: true  // 携带cookie（Session认证需要）
})

// 请求拦截器：每次请求前处理
request.interceptors.request.use(
  config => {
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器：统一处理响应和错误
request.interceptors.response.use(
  response => {
    const res = response.data
    // 业务状态码判断
    if (res.code === 200) {
      return res
    } else if (res.code === 401) {
      // 未登录，跳转登录页
      ElMessage.warning('请先登录')
      router.push('/login')
      return Promise.reject(new Error('未登录'))
    } else {
      // 其他业务错误
      ElMessage.error(res.message || '操作失败')
      return Promise.reject(new Error(res.message))
    }
  },
  error => {
    // HTTP错误
    const msg = error.response?.data?.message || error.message || '网络错误'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default request
