// 学生端 - 信誉分页面
const { profileApi } = require('../../utils/api')

Page({
  data: {
    score: 100,     // 当前信誉分
    records: [],    // 变动记录
    loading: true
  },

  onShow() {
    this.loadReputation()
  },

  // 加载信誉分和变动记录
  async loadReputation() {
    this.setData({ loading: true })
    try {
      const res = await profileApi.getReputation()
      const data = res.data || {}
      this.setData({
        score: data.score || 100,
        records: data.records || [],
        loading: false
      })
    } catch(e) {
      this.setData({ loading: false })
    }
  },

  // 计算信誉等级
  getLevel(score) {
    if (score >= 90) return { text: '信用优秀', color: '#27AE60' }
    if (score >= 70) return { text: '信用良好', color: '#F39C12' }
    if (score >= 60) return { text: '信用一般', color: '#E67E22' }
    return { text: '信用较差', color: '#E74C3C' }
  }
})
