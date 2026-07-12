// 学生端 - 账户余额页面
const { profileApi } = require('../../utils/api')

Page({
  data: {
    balance: '0.00',  // 当前余额
    records: [],       // 流水记录
    loading: true
  },

  onShow() {
    this.loadBalance()
  },

  // 加载余额和流水记录
  async loadBalance() {
    this.setData({ loading: true })
    try {
      const res = await profileApi.getBalance()
      const data = res.data || {}
      this.setData({
        balance: (data.balance || 0).toFixed(2),
        records: data.records || [],
        loading: false
      })
    } catch(e) {
      this.setData({ loading: false })
    }
  },

  // 格式化金额显示（正数绿色，负数红色）
  formatAmount(amount) {
    return amount > 0 ? `+${amount.toFixed(2)}` : amount.toFixed(2)
  }
})
