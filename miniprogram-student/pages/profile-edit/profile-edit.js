const { profileApi } = require('../../utils/api')
var getImgUrl = require('../../utils/image').getImgUrl

Page({
  data: {
    form: {
      realName: '',
      phone: '',
      studentId: '',
      dormAddress: '',
      avatar: ''
    },
    avatarSrc: '/assets/default-avatar.png',  // 页面展示用（完整URL）
    loading: false,
    uploading: false
  },

  onLoad: function() {
    this.loadProfile()
  },

  // 加载当前个人信息填充表单
  loadProfile: function() {
    var that = this
    profileApi.getProfile().then(function(r) {
      var u = r.data || {}
      var avatarSrc = getImgUrl(u.avatar) || '/assets/default-avatar.png'
      that.setData({
        form: {
          realName: u.realName || '',
          phone:    u.phone || '',
          studentId: u.studentId || '',
          dormAddress: u.dormAddress || '',
          avatar: u.avatar || ''
        },
        avatarSrc: avatarSrc
      })
    }).catch(function() {})
  },

  // 点击头像 → 选图 → 上传
  changeAvatar: function() {
    var that = this
    var app  = getApp()
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: function(res) {
        var filePath = res.tempFiles[0].tempFilePath
        // 先在本地预览
        that.setData({ avatarSrc: filePath, uploading: true })
        wx.showLoading({ title: '上传中...' })
        wx.uploadFile({
          url: app.globalData.baseUrl + '/upload/avatar',
          filePath: filePath,
          name: 'file',
          withCredentials: true,
          success: function(uploadRes) {
            wx.hideLoading()
            try {
              var result = JSON.parse(uploadRes.data)
              if (result.code === 200) {
                var serverRoot = app.globalData.baseUrl.replace('/api', '')
                var fullUrl    = serverRoot + result.data
                that.setData({
                  avatarSrc: fullUrl,
                  'form.avatar': result.data,   // 存相对路径到 form
                  uploading: false
                })
                wx.showToast({ title: '头像上传成功' })
              } else {
                wx.showToast({ title: result.message || '上传失败', icon: 'none' })
                that.setData({ uploading: false })
              }
            } catch(e) {
              wx.showToast({ title: '上传失败', icon: 'none' })
              that.setData({ uploading: false })
            }
          },
          fail: function() {
            wx.hideLoading()
            wx.showToast({ title: '上传失败', icon: 'none' })
            that.setData({ uploading: false })
          }
        })
      }
    })
  },

  // 保存所有资料
  saveProfile: function() {
    var form = this.data.form
    if (!form.realName) {
      wx.showToast({ title: '请填写姓名', icon: 'none' })
      return
    }
    this.setData({ loading: true })
    var that = this
    profileApi.updateProfile(form).then(function() {
      wx.showToast({ title: '保存成功' })
      // 更新全局 userInfo
      var app = getApp()
      if (app.globalData.userInfo) {
        app.globalData.userInfo.realName    = form.realName
        app.globalData.userInfo.phone       = form.phone
        app.globalData.userInfo.studentId   = form.studentId
        app.globalData.userInfo.dormAddress = form.dormAddress
        if (form.avatar) {
          app.globalData.userInfo.avatar = getImgUrl(form.avatar)
        }
        wx.setStorageSync('userInfo', app.globalData.userInfo)
      }
      setTimeout(function() { wx.navigateBack() }, 1200)
    }).catch(function() {
      that.setData({ loading: false })
    })
  },

  onInput: function(e) {
    var d = {}
    d[e.currentTarget.dataset.field] = e.detail.value
    this.setData(d)
  }
})
