const app = getApp()

// 从响应头提取并保存 JSESSIONID
const saveSessionCookie = (header) => {
  const raw = header['Set-Cookie'] || header['set-cookie'] || ''
  const match = raw.match(/JSESSIONID=[^;]+/)
  if (match) wx.setStorageSync('jsessionid', match[0])
}

// 读取已保存的 cookie
const getSessionCookie = () => wx.getStorageSync('jsessionid') || ''

const request = (options) => new Promise((resolve, reject) => {
  const cookie = getSessionCookie()
  wx.request({
    url: app.globalData.baseUrl + options.url,
    method: options.method || 'GET',
    data: options.data || {},
    header: {
      'Content-Type': 'application/json',
      ...(cookie ? { Cookie: cookie } : {}),
      ...(options.header || {})
    },
    success: (res) => {
      saveSessionCookie(res.header || {})
      const { data } = res
      if (data.code === 200) { resolve(data) }
      else if (data.code === 401) {
        wx.showToast({ title: '请先登录', icon: 'none' })
        wx.redirectTo({ url: '/pages/login/login' })
        reject(new Error('未登录'))
      } else {
        wx.showToast({ title: data.message || '操作失败', icon: 'none' })
        reject(new Error(data.message))
      }
    },
    fail: (err) => { wx.showToast({ title: '网络连接失败', icon: 'none' }); reject(err) }
  })
})
const authApi = { login:(d)=>request({url:'/app/auth/login',method:'POST',data:d}), logout:()=>request({url:'/app/auth/logout',method:'POST'}), register:(d)=>request({url:'/app/auth/register',method:'POST',data:d}) }
const profileApi = { getProfile:()=>request({url:'/app/student/profile'}), updateProfile:(d)=>request({url:'/app/student/profile',method:'PUT',data:d}) }
const orderApi = {
  getAvailable:(st)=>request({url:'/app/orders/available',data:st?{serviceType:st}:{}}),
  getMy:(status)=>request({url:'/app/courier/orders',data:status!=null?{status}:{}}),
  getDetail:(id)=>request({url:`/app/orders/${id}`}),
  grab:(id)=>request({url:`/app/orders/${id}/grab`,method:'PUT',data:{}}),
  updateStatus:(id,status)=>request({url:`/app/orders/${id}/status`,method:'PUT',data:{status}})
}
const courierApi = {
  applyJoin:(d)=>request({url:'/app/courier/application',method:'POST',data:d}),
  getApplication:()=>request({url:'/app/courier/application'}),
  getPreference:()=>request({url:'/app/courier/preference'}),
  updatePreference:(d)=>request({url:'/app/courier/preference',method:'PUT',data:d}),
  getEarnings:()=>request({url:'/app/courier/earnings'}),
  getWithdrawals:()=>request({url:'/app/courier/withdrawals'}),
  applyWithdrawal:(d)=>request({url:'/app/courier/withdrawals',method:'POST',data:d}),
  getReputation:()=>request({url:'/app/courier/reputation'}),
  getReviews:()=>request({url:'/app/courier/reviews'})
}
const chatApi = { getHistory:(id)=>request({url:`/app/chat/${id}`}), getNewMessages:(id,lastId)=>request({url:`/app/chat/${id}/new`,data:{lastId:lastId||0}}), send:(d)=>request({url:'/app/chat',method:'POST',data:d}) }
const announcementApi = { getList:()=>request({url:'/app/announcements',data:{target:'courier'}}) }
const messageApi = { getList:()=>request({url:'/app/student/messages'}), markRead:(id)=>request({url:`/app/student/messages/${id}/read`,method:'PUT',data:{}}) }
// 跑腿员实时位置上报
const locationApi = { updateLocation:(lat,lng)=>request({url:'/app/courier/location',method:'PUT',data:{lat,lng}}) }

// CommonJS 导出，兼容 require() 引入
module.exports = { authApi, profileApi, orderApi, courierApi, chatApi, announcementApi, messageApi, locationApi }
