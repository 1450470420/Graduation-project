// 学生端 - 登录页
const { authApi } = require('../../utils/api')
var getImgUrl = require('../../utils/image').getImgUrl

Page({
  data: {
    username: '',
    password: '',
    loading: false
  },
  // 输入同步：username
  onUsernameInput(e) { this.setData({ username: e.detail.value }) },
  // 输入同步：password
  onPasswordInput(e) { this.setData({ password: e.detail.value }) },
  // 处理登录
  async handleLogin() {
    if (!this.data.username || !this.data.password) {
      wx.showToast({ title: '请填写用户名和密码', icon: 'none' })
      return
    }
    this.setData({ loading: true })
    try {
      const res = await authApi.login({ username: this.data.username, password: this.data.password })
      // 保存用户信息（将头像转为完整 URL）
      const app = getApp()
      const user = res.data || {}
      user.avatar = getImgUrl(user.avatar)
      app.globalData.userInfo = user
      app.globalData.isLogin = true
      wx.setStorageSync('userInfo', user)
      wx.switchTab({ url: '/pages/index/index' })
    } catch(e) {} finally {
      this.setData({ loading: false })
    }
  },
  // 跳转注册页
  goRegister() {
    wx.navigateTo({ url: '/pages/register/register' })
  }
})
