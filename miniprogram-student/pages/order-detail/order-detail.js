const { orderApi, locationApi } = require('../../utils/api')
const { getBicyclingRoute, buildPolyline } = require('../../utils/amap')

Page({
  data: {
    order: null, loading: true, loadError: false,
    faceVerified: false,
    mapMarkers: [],
    mapPolyline: [],
    mapLat: 39.908823, mapLng: 116.39747, mapScale: 14,
    locationUpdateText: '',
    courierLat: null, courierLng: null
  },

  onLoad(options) {
    this.orderId = options.id
    this.loadDetail()
  },

  onShow() { if (this.orderId) this.loadDetail() },

  // 页面隐藏时停止轮询备电
  onHide() { this.stopLocationPolling() },
  onUnload() { this.stopLocationPolling() },

  async loadDetail() {
    this.setData({ loading: true })
    try {
      const res = await orderApi.getOrderDetail(this.orderId)
      const order = res.data
      this.setData({ order, loading: false, faceVerified: !!order.faceVerified })
      // 初始化地图标记
      this.refreshMapMarkers(order)
      // 有取件和送达坐标时绘制高德路线
      if (order.pickupLat && order.deliveryLat) {
        getBicyclingRoute(order.pickupLng, order.pickupLat, order.deliveryLng, order.deliveryLat)
          .then(pts => { if (pts.length > 1) this.setData({ mapPolyline: buildPolyline(pts, '#D32F2FCC') }) })
          .catch(() => {})
      }
      // 配送中状态开启跑腿员位置轮询
      if (order.status === 2) {
        this.startLocationPolling()
      } else {
        this.stopLocationPolling()
      }
    } catch(e) { this.setData({ loading: false, loadError: true }) }
  },

  // 更新地图标记（取件点、送达点、跑腿员实时位置）
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
    // 跑腿员实时位置标记
    if (this.data.courierLat) {
      markers.push({
        id: 3, latitude: this.data.courierLat, longitude: this.data.courierLng,
        callout: { content: '跑腿员', display: 'ALWAYS', color: '#FF8F00', bgColor: '#fff', padding: 8, borderRadius: 4 },
        iconPath: '/assets/map-courier.png', width: 40, height: 40
      })
    }
    // 地图中心取均唃
    let mapLat = this.data.mapLat, mapLng = this.data.mapLng
    if (markers.length > 0) {
      mapLat = markers.reduce((s, m) => s + m.latitude, 0) / markers.length
      mapLng = markers.reduce((s, m) => s + m.longitude, 0) / markers.length
    }
    this.setData({ mapMarkers: markers, mapLat, mapLng })
  },

  // 开启跑腿员位置轮询（配送中每3秒一次）
  startLocationPolling() {
    if (this._locationTimer) return
    this._locationTimer = setInterval(async () => {
      try {
        const res = await locationApi.getCourierLocation(this.orderId)
        const { lat, lng, updateTime } = res.data
        if (lat && lng) {
          const prevLat = this.data.courierLat
          this.setData({
            courierLat: lat, courierLng: lng,
            locationUpdateText: '跑腿员位置 ' + (updateTime ? updateTime.slice(11, 19) : '')
          })
          // 位置发生变化时刷新标记
          if (prevLat !== lat) this.refreshMapMarkers(this.data.order)
        }
      } catch(e) {}
    }, 3000)
  },

  stopLocationPolling() {
    if (this._locationTimer) { clearInterval(this._locationTimer); this._locationTimer = null }
  },

  async cancelOrder() {
    wx.showModal({
      title: '取消订单', content: '请确认是否取消该订单',
      editable: true, placeholderText: '请输入取消原因（如:不需要了）',
      success: async(res) => {
        if (res.confirm) {
          const reason = res.content || '用户主动取消'
          try { await orderApi.cancelOrder(this.orderId, reason); wx.showToast({ title: '已取消' }); this.loadDetail() } catch(e) {}
        }
      }
    })
  },

  startFaceVerify() {
    if (!this.data.order.studentHasFace) {
      wx.showModal({
        title: '未注册人脸',
        content: '您尚未注册人脸，请先到"我的→人脸识别管理"完成人脸采集',
        confirmText: '去采集',
        success: (r) => { if (r.confirm) wx.navigateTo({ url: '/pages/face/face' }) }
      })
      return
    }
    const order = this.data.order
    wx.navigateTo({
      url: `/pages/face-verify/face-verify?orderId=${this.orderId}&orderNo=${order.orderNo || ''}`
    })
  },

  async confirmReceive() {
    if (!this.data.faceVerified) {
      this.startFaceVerify()
      return
    }
    wx.showModal({ title: '确认收货', content: '确认已收到物品？', success: async(r) => {
      if (r.confirm) {
        try { await orderApi.confirmReceive(this.orderId); wx.showToast({ title: '确认成功' }); this.loadDetail() } catch(e) {}
      }
    }})
  },

  goEvaluate() { wx.navigateTo({ url: `/pages/evaluate/evaluate?id=${this.orderId}` }) },
  goChat() { wx.navigateTo({ url: `/pages/chat/chat?orderId=${this.orderId}&receiverId=${this.data.order.courierId}` }) },
  goRefund() { wx.navigateTo({ url: `/pages/refund/refund?orderId=${this.orderId}` }) },
  goComplaint() { wx.navigateTo({ url: `/pages/complaint/complaint?orderId=${this.orderId}` }) }
})
