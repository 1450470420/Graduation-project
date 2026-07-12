const { profileApi } = require('../../utils/api')
const { getImgUrl } = require('../../utils/image')
const app = getApp()

Page({
  data: { form: { realName: '', phone: '', dormAddress: '', avatar: '' }, uploading: false, saving: false },

  onLoad: function() {
    const info = app.globalData.userInfo || {}
    this.setData({
      'form.realName': info.realName || '',
      'form.phone': info.phone || '',
      'form.dormAddress': info.dormAddress || '',
      'form.avatar': info.avatar || '',
      'form.avatarUrl': getImgUrl(info.avatar)
    })
  },

  onInput: function(e) {
    var d = {}; d[e.currentTarget.dataset.field] = e.detail.value; this.setData(d)
  },

  changeAvatar: function() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        this.uploadAvatar(res.tempFiles[0].tempFilePath)
      }
    })
  },

  uploadAvatar: function(filePath) {
    this.setData({ uploading: true })
    const cookie = wx.getStorageSync('jsessionid') || ''
    wx.uploadFile({
      url: app.globalData.baseUrl + '/upload/avatar',
      filePath: filePath,
      name: 'file',
      header: cookie ? { Cookie: cookie } : {},
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            this.setData({ 'form.avatar': data.data, 'form.avatarUrl': getImgUrl(data.data) })
            wx.showToast({ title: '头像已更新', icon: 'success' })
          } else {
            wx.showToast({ title: data.message || '上传失败', icon: 'none' })
          }
        } catch(e) { wx.showToast({ title: '上传失败', icon: 'none' }) }
      },
      fail: function() { wx.showToast({ title: '上传失败，请重试', icon: 'none' }) },
      complete: () => { this.setData({ uploading: false }) }
    })
  },

  save: async function() {
    const f = this.data.form
    if (!f.realName) { wx.showToast({ title: '请填写姓名', icon: 'none' }); return }
    this.setData({ saving: true })
    try {
      await profileApi.updateProfile(f)
      // 同步更新全局缓存
      app.globalData.userInfo = Object.assign({}, app.globalData.userInfo, f)
      wx.setStorageSync('courierInfo', app.globalData.userInfo)
      wx.showToast({ title: '保存成功', icon: 'success' })
      setTimeout(function() { wx.navigateBack() }, 1500)
    } catch(e) {}
    finally { this.setData({ saving: false }) }
  }
})
