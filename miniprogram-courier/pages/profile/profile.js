const { profileApi, courierApi, authApi } = require('../../utils/api')
const { getImgUrl } = require('../../utils/image')
Page({
  data: { userInfo: null, reviews: [] },
  onShow: function() { this.loadProfile() },
  loadProfile: async function() {
    try {
      var results = await Promise.all([profileApi.getProfile(), courierApi.getReviews()])
      var pRes = results[0], rRes = results[1]
      var reviews = (rRes.data||[]).slice(0,3).map(function(r) {
        return Object.assign({}, r, { stars: '★'.repeat(r.rating||0)+'☆'.repeat(5-(r.rating||0)) })
      })
      var userInfo = pRes.data
      // 将 avatar 转为完整 URL，wxml 中直接用 userInfo.avatar
      if (userInfo) userInfo.avatar = getImgUrl(userInfo.avatar)
      this.setData({ userInfo: userInfo, reviews: reviews })
    } catch(e) {}
  },
  openEdit: function() { wx.navigateTo({ url: '/pages/edit-profile/edit-profile' }) },
  goReviews: function() { wx.navigateTo({ url: '/pages/reviews/reviews' }) },
  goEarnings: function() { wx.switchTab({ url: '/pages/earnings/earnings' }) },
  goApplication: function() { wx.navigateTo({ url: '/pages/application/application' }) },
  goAnnouncement: function() { wx.navigateTo({ url: '/pages/announcement/announcement' }) },
  goMessage: function() { wx.navigateTo({ url: '/pages/message/message' }) },
  logout: function() {
    var self = this
    wx.showModal({ title: '退出登录', content: '确定退出吗？', success: function(r) {
      if (r.confirm) {
        authApi.logout().catch(function(){}).finally(function() {
          var app = getApp()
          app.globalData.userInfo = null
          app.globalData.isLogin = false
          wx.removeStorageSync('courierInfo')
          wx.redirectTo({ url: '/pages/login/login' })
        })
      }
    }})
  },
  onInput: function(e) { var d = {}; d[e.currentTarget.dataset.field] = e.detail.value; this.setData(d) }
})
