// 校园跑腿 - 学生端 API 请求封装

const app = getApp()

// 从响应头提取并保存 JSESSIONID
const saveSessionCookie = (header) => {
  const raw = header['Set-Cookie'] || header['set-cookie'] || ''
  const match = raw.match(/JSESSIONID=[^;]+/)
  if (match) wx.setStorageSync('jsessionid', match[0])
}

// 读取已保存的 cookie
const getSessionCookie = () => wx.getStorageSync('jsessionid') || ''

/**
 * 通用HTTP请求封装
 * 手动管理 Session Cookie（小程序不会自动带 Cookie）
 */
const request = (options) => {
  return new Promise((resolve, reject) => {
    const cookie = getSessionCookie()
    wx.request({
      url: app.globalData.baseUrl + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        ...(cookie ? { Cookie: cookie } : {}),
        ...options.header
      },
      success: (res) => {
        saveSessionCookie(res.header || {})
        const { data } = res
        if (data.code === 200) {
          resolve(data)
        } else if (data.code === 401) {
          wx.showToast({ title: '请先登录', icon: 'none' })
          wx.redirectTo({ url: '/pages/login/login' })
          reject(new Error('未登录'))
        } else {
          wx.showToast({ title: data.message || '操作失败', icon: 'none' })
          reject(new Error(data.message))
        }
      },
      fail: (err) => {
        wx.showToast({ title: '网络连接失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

// =================== 认证相关 ===================
const authApi = {
  // 注册
  register: (data) => request({ url: '/app/auth/register', method: 'POST', data }),
  // 登录
  login: (data) => request({ url: '/app/auth/login', method: 'POST', data }),
  // 退出
  logout: () => request({ url: '/app/auth/logout', method: 'POST' }),
  // 修改密码
  changePassword: (data) => request({ url: '/app/auth/password', method: 'PUT', data })
}

// =================== 个人信息相关 ===================
const profileApi = {
  // 获取个人信息
  getProfile: () => request({ url: '/app/student/profile' }),
  // 更新个人信息
  updateProfile: (data) => request({ url: '/app/student/profile', method: 'PUT', data }),
  // 实名认证
  certify: (data) => request({ url: '/app/student/certify', method: 'POST', data }),
  // 信誉分
  getReputation: () => request({ url: '/app/student/reputation' }),
  // 余额
  getBalance: () => request({ url: '/app/student/balance' })
}

// =================== 订单相关 ===================
const orderApi = {
  // 发布订单
  createOrder: (data) => request({ url: '/app/orders', method: 'POST', data }),
  // 我的订单
  getMyOrders: (status) => request({ url: '/app/orders/my', data: status != null ? { status } : {} }),
  // 订单详情
  getOrderDetail: (id) => request({ url: `/app/orders/${id}` }),
  // 取消订单
  cancelOrder: (id, reason) => request({ url: `/app/orders/${id}/cancel`, method: 'PUT', data: { reason } }),
  // 确认收货
  confirmReceive: (id) => request({ url: `/app/orders/${id}/confirm`, method: 'PUT', data: {} }),
  // 评价
  evaluate: (id, data) => request({ url: `/app/orders/${id}/evaluate`, method: 'POST', data }),
  // 申请退款
  applyRefund: (id, reason) => request({ url: `/app/orders/${id}/refund`, method: 'POST', data: { reason } }),
  // 投诉
  submitComplaint: (id, data) => request({ url: `/app/orders/${id}/complaint`, method: 'POST', data })
}

// =================== 地址相关 ===================
const addressApi = {
  getList: () => request({ url: '/app/addresses' }),
  add: (data) => request({ url: '/app/addresses', method: 'POST', data }),
  update: (id, data) => request({ url: `/app/addresses/${id}`, method: 'PUT', data }),
  delete: (id) => request({ url: `/app/addresses/${id}`, method: 'DELETE' })
}

// =================== 消息相关 ===================
const messageApi = {
  getList: () => request({ url: '/app/student/messages' }),
  markRead: (id) => request({ url: `/app/student/messages/${id}/read`, method: 'PUT', data: {} })
}

// =================== 聊天相关 ===================
const chatApi = {
  getHistory: (orderId) => request({ url: `/app/chat/${orderId}` }),
  getNewMessages: (orderId, lastId) => request({ url: `/app/chat/${orderId}/new`, data: { lastId: lastId || 0 } }),
  send: (data) => request({ url: '/app/chat', method: 'POST', data })
}

// =================== 公告相关 ===================
const announcementApi = {
  getList: (target) => request({ url: '/app/announcements', data: { target } }),
  getDetail: (id) => request({ url: `/app/announcements/${id}` })
}

// =================== 工单相关 ===================
const ticketApi = {
  getList: () => request({ url: '/app/student/tickets' }),
  submit: (data) => request({ url: '/app/student/tickets', method: 'POST', data })
}

// =================== 区域和取件点 ===================
const areaApi = {
  getAll: () => request({ url: '/admin/areas/all' }),
  getPickupPoints: () => request({ url: '/admin/pickup-points/all' })
}

// =================== 跑腿员实时位置 ===================
const locationApi = {
  getCourierLocation: (orderId) => request({ url: `/app/orders/${orderId}/courier-location` })
}

// =================== 人脸识别相关 ===================
const faceApi = {
  getStatus: () => request({ url: '/app/face/status' }),
  getOrderFaceStatus: (orderId) => request({ url: `/app/face/order-status/${orderId}` })
}

// CommonJS 导出，兼容 require() 引入
module.exports = { authApi, profileApi, orderApi, addressApi, messageApi, chatApi, announcementApi, ticketApi, areaApi, locationApi, faceApi }

