const { orderApi, addressApi } = require('../../utils/api')
const { getBicyclingRoute, buildPolyline } = require('../../utils/amap')
Page({
  data: {
    serviceType: '代取快递',
    pickupAddress: '', pickupLat: null, pickupLng: null,
    deliveryAddress: '', deliveryLat: null, deliveryLng: null,
    itemInfo: '', reward: '', note: '',
    loading: false,
    addresses: [],
    // 地图展示相关
    mapMarkers: [],
    mapPolyline: [],   // 高德路线数据
    mapLat: 39.908823,
    mapLng: 116.39747,
    showMap: false,
    routeDistance: '', routeTime: ''  // 路线距离和预计时间
  },
  serviceTypes: ['代取快递', '代买', '代送'],

  onLoad(o) {
    if (o.type) this.setData({ serviceType: o.type })
    this.loadAddresses()
  },

  async loadAddresses() {
    try { const r = await addressApi.getList(); this.setData({ addresses: r.data || [] }) } catch(e) {}
  },

  // 常用地址快捷选择
  pickPickup(e) { this.setData({ pickupAddress: e.currentTarget.dataset.addr }) },
  pickDelivery(e) { this.setData({ deliveryAddress: e.currentTarget.dataset.addr }) },

  changeType(e) { this.setData({ serviceType: this.serviceTypes[e.detail.value] }) },

  // 地图选点——取件地址
  choosePickupLocation() {
    wx.chooseLocation({
      success: (res) => {
        this.setData({
          pickupAddress: res.address ? res.address + res.name : res.name,
          pickupLat: res.latitude,
          pickupLng: res.longitude
        })
        this.refreshMapMarkers()
      },
      fail: (err) => {
        if (err.errMsg && err.errMsg.includes('auth')) {
          wx.showToast({ title: '请开启位置权限', icon: 'none' })
        }
      }
    })
  },

  // 地图选点——送达地址
  chooseDeliveryLocation() {
    wx.chooseLocation({
      success: (res) => {
        this.setData({
          deliveryAddress: res.address ? res.address + res.name : res.name,
          deliveryLat: res.latitude,
          deliveryLng: res.longitude
        })
        this.refreshMapMarkers()
      },
      fail: (err) => {
        if (err.errMsg && err.errMsg.includes('auth')) {
          wx.showToast({ title: '请开启位置权限', icon: 'none' })
        }
      }
    })
  },

  // 调用高德路线 API，在地图上绘制真实路线
  async fetchAndDrawRoute() {
    const { pickupLat, pickupLng, deliveryLat, deliveryLng } = this.data
    if (!pickupLat || !deliveryLat) return
    try {
      const points = await getBicyclingRoute(pickupLng, pickupLat, deliveryLng, deliveryLat)
      if (points.length > 1) {
        this.setData({ mapPolyline: buildPolyline(points, '#D32F2FCC') })
      }
    } catch(e) {
      // 路线获取失败不影响主流程
    }
  },

  // 更新地图标记并调整中心
  refreshMapMarkers() {
    const markers = []
    if (this.data.pickupLat) {
      markers.push({ id: 1, latitude: this.data.pickupLat, longitude: this.data.pickupLng,
        title: '取件地', callout: { content: '取件点', display: 'ALWAYS', color: '#E74C3C', bgColor: '#fff', padding: 8, borderRadius: 4 },
        iconPath: '/assets/map-pickup.png', width: 32, height: 32
      })
    }
    if (this.data.deliveryLat) {
      markers.push({ id: 2, latitude: this.data.deliveryLat, longitude: this.data.deliveryLng,
        title: '送达地', callout: { content: '送达点', display: 'ALWAYS', color: '#2E7D32', bgColor: '#fff', padding: 8, borderRadius: 4 },
        iconPath: '/assets/map-delivery.png', width: 32, height: 32
      })
    }
    const showMap = markers.length > 0
    // 地图中心取两点中点
    let mapLat = this.data.mapLat, mapLng = this.data.mapLng
    if (markers.length > 0) {
      mapLat = markers.reduce((s, m) => s + m.latitude, 0) / markers.length
      mapLng = markers.reduce((s, m) => s + m.longitude, 0) / markers.length
    }
    this.setData({ mapMarkers: markers, showMap, mapLat, mapLng })
    // 两个坐标全部就绪时调用高德绘制路线
    if (this.data.pickupLat && this.data.deliveryLat) {
      this.fetchAndDrawRoute()
    }
  },

  // 计算两点的距离(km)
  calcDistance(lat1, lng1, lat2, lng2) {
    const R = 6371
    const dLat = (lat2 - lat1) * Math.PI / 180
    const dLng = (lng2 - lng1) * Math.PI / 180
    const a = Math.sin(dLat/2)**2 + Math.cos(lat1*Math.PI/180)*Math.cos(lat2*Math.PI/180)*Math.sin(dLng/2)**2
    return (R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))).toFixed(2)
  },

  async submitOrder() {
    const { serviceType, pickupAddress, deliveryAddress, itemInfo, reward,
            pickupLat, pickupLng, deliveryLat, deliveryLng } = this.data
    if (!pickupAddress || !deliveryAddress || !itemInfo || !reward) {
      wx.showToast({ title: '请填写完整信息', icon: 'none' }); return
    }
    if (isNaN(reward) || Number(reward) < 1) {
      wx.showToast({ title: '赏金至少1元', icon: 'none' }); return
    }
    // 自动计算距离
    let distance = null
    if (pickupLat && deliveryLat) {
      distance = this.calcDistance(pickupLat, pickupLng, deliveryLat, deliveryLng)
    }
    this.setData({ loading: true })
    try {
      await orderApi.createOrder({
        serviceType, pickupAddress, deliveryAddress, itemInfo,
        reward: Number(reward), note: this.data.note,
        pickupLat, pickupLng, deliveryLat, deliveryLng, distance
      })
      wx.showToast({ title: '发布成功' })
      setTimeout(() => wx.navigateBack(), 1500)
    } catch(e) {} finally { this.setData({ loading: false }) }
  },
  // 通用输入处理（兼容所有微信基础库版本）
  onInput: function(e) { var d = {}; d[e.currentTarget.dataset.field] = e.detail.value; this.setData(d) },
})