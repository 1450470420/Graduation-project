const { authApi } = require('../../utils/api')
Page({
  data: { username: '', password: '', confirmPwd: '', phone: '', loading: false },
  goLogin: function() { wx.navigateBack() },
  register: async function() {
    var d = this.data
    if (!d.username||!d.password||!d.phone) { wx.showToast({ title: '请填写完整', icon: 'none' }); return }
    if (d.password.length < 6) { wx.showToast({ title: '密码至6位', icon: 'none' }); return }
    if (d.password !== d.confirmPwd) { wx.showToast({ title: '两次密码不一致', icon: 'none' }); return }
    this.setData({ loading: true })
    try {
      await authApi.register({ username: d.username, password: d.password, phone: d.phone, userType: 2 })
      wx.showToast({ title: '注册成功' })
      setTimeout(function() { wx.navigateBack() }, 1500)
    } catch(e) {} finally { this.setData({ loading: false }) }
  },
  onInput: function(e) { var d = {}; d[e.currentTarget.dataset.field] = e.detail.value; this.setData(d) }
})
