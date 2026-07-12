import request from '@/utils/request'

// ===========================
// 管理员认证 API
// ===========================
export const adminApi = {
  login: (data) => request.post('/admin/login', data),
  logout: () => request.post('/admin/logout'),
  getInfo: () => request.get('/admin/info')
}

// ===========================
// 用户管理 API
// ===========================
export const userApi = {
  getStudents: (params) => request.get('/admin/users/students', { params }),
  getCouriers: (params) => request.get('/admin/users/couriers', { params }),
  getUserById: (id) => request.get(`/admin/users/${id}`),
  banUser: (id) => request.put(`/admin/users/${id}/ban`),
  unbanUser: (id) => request.put(`/admin/users/${id}/unban`)
}

// ===========================
// 跑腿员申请 API
// ===========================
export const applicationApi = {
  getList: (params) => request.get('/admin/courier-applications', { params }),
  approve: (id) => request.put(`/admin/courier-applications/${id}/approve`),
  reject: (id, data) => request.put(`/admin/courier-applications/${id}/reject`, data)
}

// ===========================
// 校园区域 API
// ===========================
export const areaApi = {
  getList: (params) => request.get('/admin/areas', { params }),
  getAll: () => request.get('/admin/areas/all'),
  add: (data) => request.post('/admin/areas', data),
  update: (id, data) => request.put(`/admin/areas/${id}`, data),
  delete: (id) => request.delete(`/admin/areas/${id}`)
}

// ===========================
// 取件点 API
// ===========================
export const pickupPointApi = {
  getList: (params) => request.get('/admin/pickup-points', { params }),
  getAll: () => request.get('/admin/pickup-points/all'),
  add: (data) => request.post('/admin/pickup-points', data),
  update: (id, data) => request.put(`/admin/pickup-points/${id}`, data),
  delete: (id) => request.delete(`/admin/pickup-points/${id}`)
}

// ===========================
// 订单管理 API
// ===========================
export const orderApi = {
  getList: (params) => request.get('/admin/orders', { params }),
  getDetail: (id) => request.get(`/admin/orders/${id}`),
  cancel: (id, data) => request.put(`/admin/orders/${id}/cancel`, data)
}

// ===========================
// 退款管理 API
// ===========================
export const refundApi = {
  getList: (params) => request.get('/admin/refunds', { params }),
  approve: (id, data) => request.put(`/admin/refunds/${id}/approve`, data),
  reject: (id, data) => request.put(`/admin/refunds/${id}/reject`, data)
}

// ===========================
// 投诉管理 API
// ===========================
export const complaintApi = {
  getList: (params) => request.get('/admin/complaints', { params }),
  handle: (id, data) => request.put(`/admin/complaints/${id}/handle`, data)
}

// ===========================
// 提现管理 API
// ===========================
export const withdrawalApi = {
  getList: (params) => request.get('/admin/withdrawals', { params }),
  approve: (id, data) => request.put(`/admin/withdrawals/${id}/approve`, data),
  reject: (id, data) => request.put(`/admin/withdrawals/${id}/reject`, data)
}

// ===========================
// 公告管理 API
// ===========================
export const announcementApi = {
  getList: (params) => request.get('/admin/announcements', { params }),
  add: (data) => request.post('/admin/announcements', data),
  update: (id, data) => request.put(`/admin/announcements/${id}`, data),
  delete: (id) => request.delete(`/admin/announcements/${id}`),
  offline: (id) => request.put(`/admin/announcements/${id}/offline`)
}

// ===========================
// 客服工单 API
// ===========================
export const ticketApi = {
  getList: (params) => request.get('/admin/tickets', { params }),
  reply: (id, data) => request.put(`/admin/tickets/${id}/reply`, data),
  close: (id) => request.put(`/admin/tickets/${id}/close`)
}

// ===========================
// 数据统计 API
// ===========================
export const statisticsApi = {
  getOverview: () => request.get('/admin/statistics/overview'),
  getOrderTrend: () => request.get('/admin/statistics/order-trend'),
  getServiceTypeDistribution: () => request.get('/admin/statistics/service-type-distribution'),
  getUserGrowth: () => request.get('/admin/statistics/user-growth'),
  getCourierRanking: () => request.get('/admin/statistics/courier-ranking'),
  getRevenueTrend: () => request.get('/admin/statistics/revenue-trend')
}

// ===========================
// 系统配置 API
// ===========================
export const configApi = {
  getList: () => request.get('/admin/configs'),
  save: (data) => request.post('/admin/configs', data),
  update: (id, data) => request.put(`/admin/configs/${id}`, data)
}

// ===========================
// 操作日志 API
// ===========================
export const logApi = {
  getList: (params) => request.get('/admin/logs', { params })
}

// ===========================
// 信誉分 API
// ===========================
export const reputationApi = {
  getRecords: (params) => request.get('/admin/reputation/records', { params }),
  adjust: (data) => request.put('/admin/reputation/adjust', data)
}

// ===========================
// 文件上传 API
// ===========================
export const uploadApi = {
  uploadFile: (formData) => request.post('/upload/file', formData, { headers: { 'Content-Type': 'multipart/form-data' } }),
  uploadAvatar: (formData) => request.post('/upload/avatar', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
}
