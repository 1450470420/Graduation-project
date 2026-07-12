const { courierApi } = require('../../utils/api')
const { getImgUrl } = require('../../utils/image')

Page({
  data: { reviews: [], loading: true },

  onLoad: function() {
    this.loadReviews()
  },

  loadReviews: async function() {
    this.setData({ loading: true })
    try {
      const res = await courierApi.getReviews()
      const reviews = (res.data || []).map(function(r) {
        return Object.assign({}, r, {
          stars: '★'.repeat(r.rating || 0) + '☆'.repeat(5 - (r.rating || 0)),
          studentAvatarUrl: getImgUrl(r.studentAvatar),
          dateStr: r.createTime ? r.createTime.slice(0, 10) : ''
        })
      })
      this.setData({ reviews: reviews })
    } catch(e) {}
    finally { this.setData({ loading: false }) }
  }
})
