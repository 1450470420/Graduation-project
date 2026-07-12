const { courierApi } = require('../../utils/api')
Page({
  data: { amount: '', method: '微信', accountInfo: '', records: [], methods: ['微信', '支付宝', '银行卡'], balance: '0.00' },
  onLoad: function() { this.loadRecords(); this.loadBalance() },
  loadBalance: async function() { try { var r = await courierApi.getEarnings(); this.setData({ balance: Number(r.data.balance||0).toFixed(2) }) } catch(e) {} },
  loadRecords: async function() { try { var r = await courierApi.getWithdrawals(); this.setData({ records: r.data||[] }) } catch(e) {} },
  changeMethod: function(e) { this.setData({ method: this.data.methods[e.detail.value] }) },
  submit: async function() {
    if (!this.data.amount||!this.data.accountInfo) { wx.showToast({ title: '请填写完整', icon: 'none' }); return }
    if (Number(this.data.amount)<50) { wx.showToast({ title: '最低提现50元', icon: 'none' }); return }
    try {
      await courierApi.applyWithdrawal({ amount: Number(this.data.amount), paymentMethod: this.data.method, accountInfo: this.data.accountInfo })
      wx.showToast({ title: '申请已提交' })
      this.setData({ amount: '', accountInfo: '' })
      this.loadRecords(); this.loadBalance()
    } catch(e) {}
  },
  onInput: function(e) { var d = {}; d[e.currentTarget.dataset.field] = e.detail.value; this.setData(d) }
})
