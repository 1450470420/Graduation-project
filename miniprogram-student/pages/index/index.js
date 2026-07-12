const { announcementApi, orderApi } = require('../../utils/api')
Page({
  data: { userInfo: null, announcements: [], recentOrders: [], pendingCount: 0 },
  onLoad() {
    const app = getApp()
    if (!app.globalData.isLogin) { wx.redirectTo({ url: '/pages/login/login' }); return }
    this.setData({ userInfo: app.globalData.userInfo })
    this.loadData()
  },
  onShow() { this.loadData() },
  async loadData() {
    try {
      const [annRes, orderRes] = await Promise.all([
        announcementApi.getList('student'),
        orderApi.getMyOrders()
      ])
      const pending = (orderRes.data || []).filter(o => o.status < 4 && o.status !== 5).length
      this.setData({ announcements: (annRes.data || []).slice(0, 3), recentOrders: (orderRes.data || []).slice(0, 3), pendingCount: pending })
    } catch(e) {}
  },
  // 跳转到发布页，透传服务类型（使用户免重复选择）
  goPublish(e) {
    const type = e.currentTarget.dataset.type || ''
    wx.navigateTo({ url: `/pages/publish-order/publish-order?type=${type}` })
  },
  goOrders() { wx.switchTab({ url: '/pages/order-list/order-list' }) },
  goAnnouncements() { wx.navigateTo({ url: '/pages/announcement/announcement' }) }
})
