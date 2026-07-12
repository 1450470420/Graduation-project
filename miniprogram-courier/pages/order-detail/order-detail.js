const { orderApi, locationApi } = require('../../utils/api')
const { getBicyclingRoute, buildPolyline } = require('../../utils/amap')

Page({
  data: {
    order: null, loading: true,
    faceVerified: false,
    mapMarkers: [], mapPolyline: [],
    mapLat: 39.908823, mapLng: 116.39747
  },

  onLoad(o) { this.orderId = o.id; this.loadDetail() },
  onShow() { if (this.orderId) this.loadDetail() },
  onHide() { this.stopLocationUpload() },
  onUnload() { this.stopLocationUpload() },

  async loadDetail() {
    this.setData({ loading: true })
    try {
      const r = await orderApi.getDetail(this.orderId)
      const order = r.data
      this.setData({ order, loading: false, faceVerified: !!order.faceVerified })
      this.refreshMapMarkers(order)
      // 绘制高德路线
      if (order.pickupLat && order.deliveryLat) {
        getBicyclingRoute(order.pickupLng, order.pickupLat, order.deliveryLng, order.deliveryLat)
          .then(pts => { if (pts.length > 1) this.setData({ mapPolyline: buildPolyline(pts, '#2E7D32CC') }) })
          .catch(() => {})
      }
      // 配送中时自动开始上报位置
      if (order.status === 2) {
        this.startLocationUpload()
      } else {
        this.stopLocationUpload()
      }
    } catch(e) { this.setData({ loading: false }); wx.showToast({ title: '加载失败', icon: 'none' }) }
  },

  // 更新地图标记
  refreshMapMarkers(order) {
    if (!order) return
    const markers = []
    if (order.pickupLat) {
      markers.push({
        id: 1, latitude: order.pickupLat, longitude: order.pickupLng,
        callout: { content: '取件点', display: 'ALWAYS', color: '#D32F2F', bgColor: '#fff', padding: 8, borderRadius: 4 },
        iconPath: '/assets/map-pickup.png', width: 36, height: 36
      })
    }
    if (order.deliveryLat) {
      markers.push({
        id: 2, latitude: order.deliveryLat, longitude: order.deliveryLng,
        callout: { content: '送达点', display: 'ALWAYS', color: '#2E7D32', bgColor: '#fff', padding: 8, borderRadius: 4 },
        iconPath: '/assets/map-delivery.png', width: 36, height: 36
      })
    }
    if (markers.length > 0) {
      const lat = markers.reduce((s, m) => s + m.latitude, 0) / markers.length
      const lng = markers.reduce((s, m) => s + m.longitude, 0) / markers.length
      this.setData({ mapMarkers: markers, mapLat: lat, mapLng: lng })
    } else {
      this.setData({ mapMarkers: markers })
    }
  },

  // 导航到取件点
  navigateToPickup() {
    const o = this.data.order
    if (!o || !o.pickupLat) { wx.showToast({ title: '暂无地图坐标', icon: 'none' }); return }
    wx.openLocation({
      latitude: o.pickupLat, longitude: o.pickupLng,
      name: '取件点', address: o.pickupAddress, scale: 18
    })
  },

  // 导航到送达点
  navigateToDelivery() {
    const o = this.data.order
    if (!o || !o.deliveryLat) { wx.showToast({ title: '暂无地图坐标', icon: 'none' }); return }
    wx.openLocation({
      latitude: o.deliveryLat, longitude: o.deliveryLng,
      name: '送达点', address: o.deliveryAddress, scale: 18
    })
  },

  // 配送中开始定时上报位置（每3秒）
  startLocationUpload() {
    if (this._locationTimer) return
    const upload = () => {
      wx.getLocation({
        type: 'gcj02',
        success: (res) => {
          locationApi.updateLocation(res.latitude, res.longitude).catch(() => {})
        }
      })
    }
    upload()  // 立即上报一次
    this._locationTimer = setInterval(upload, 3000)
  },

  stopLocationUpload() {
    if (this._locationTimer) { clearInterval(this._locationTimer); this._locationTimer = null }
  },

  async updateStatus(e) {
    const s = parseInt(e.currentTarget.dataset.status)
    const labels = ['', '待取件', '配送中', '已送达']
    wx.showModal({
      title: '更新状态', content: `确认更新为【${labels[s]}】？`,
      success: async(r) => {
        if (r.confirm) {
          await orderApi.updateStatus(this.orderId, s)
          wx.showToast({ title: '状态已更新' })
          this.loadDetail()
        }
      }
    })
  },

  goChat() { wx.navigateTo({ url: `/pages/chat/chat?orderId=${this.orderId}&receiverId=${this.data.order.studentId}` }) }
})
