const { courierApi } = require('../../utils/api')
Page({
  data: { form: { realName: '', studentId: '', phone: '', idCardNo: '', remark: '' }, app: null, loading: false },
  onLoad: function() { this.loadApp() },
  loadApp: async function() { try { var r = await courierApi.getApplication(); this.setData({ app: r.data }) } catch(e) {} },
  goOrderHall: function() { wx.switchTab({ url: '/pages/index/index' }) },
  submit: async function() {
    var f = this.data.form
    if (!f.realName||!f.studentId||!f.phone||!f.idCardNo) { wx.showToast({ title: '请填写完整信息', icon: 'none' }); return }
    this.setData({ loading: true })
    try { await courierApi.applyJoin(this.data.form); wx.showToast({ title: '申请已提交，等待审核' }); this.loadApp() } catch(e) {} finally { this.setData({ loading: false }) }
  },
  onInput: function(e) { var d = {}; d[e.currentTarget.dataset.field] = e.detail.value; this.setData(d) }
})
