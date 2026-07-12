/**
 * 高德地图工具函数
 * 使用高德 WebService API 进行路线规划
 * 坐标系：GCJ-02（与 wx.chooseLocation 返回一致，无需转换）
 */

const AMAP_BASE = 'https://restapi.amap.com/v3'

/**
 * 解析高德路线 API 返回的 polyline 字符串
 * 格式: "lng1,lat1;lng2,lat2;..."
 * 转换为微信 <map> 组件所需的 [{longitude, latitude}] 格式
 */
function parsePolyline(polylineStr) {
  if (!polylineStr) return []
  return polylineStr.split(';').map(p => {
    const [lng, lat] = p.split(',')
    return { longitude: parseFloat(lng), latitude: parseFloat(lat) }
  }).filter(p => !isNaN(p.longitude) && !isNaN(p.latitude))
}

/**
 * 获取骑行路线（适合跑腿员接单配送场景）
 * @param {number} originLng  出发点经度
 * @param {number} originLat  出发点纬度
 * @param {number} destLng    目的地经度
 * @param {number} destLat    目的地纬度
 * @returns Promise<Array>  微信 map polyline points 格式的坐标数组
 */
function getBicyclingRoute(originLng, originLat, destLng, destLat) {
  const key = getApp().globalData.amapKey
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${AMAP_BASE}/direction/bicycling`,
      data: {
        origin: `${originLng},${originLat}`,
        destination: `${destLng},${destLat}`,
        key
      },
      success: (res) => {
        const data = res.data
        if (data.errcode && data.errcode !== 0) {
          reject(new Error(data.errmsg || '路线规划失败'))
          return
        }
        // 骑行API返回格式: data.data.paths[0].steps
        const paths = data.data && data.data.paths
        if (!paths || paths.length === 0) { resolve([]); return }
        // 合并所有分段的 polyline 坐标
        const allPoints = []
        const steps = paths[0].steps || []
        steps.forEach(step => {
          const pts = parsePolyline(step.polyline)
          allPoints.push(...pts)
        })
        resolve(allPoints)
      },
      fail: (err) => reject(err)
    })
  })
}

/**
 * 获取步行路线（适合校园内短距离场景）
 */
function getWalkingRoute(originLng, originLat, destLng, destLat) {
  const key = getApp().globalData.amapKey
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${AMAP_BASE}/direction/walking`,
      data: {
        origin: `${originLng},${originLat}`,
        destination: `${destLng},${destLat}`,
        key
      },
      success: (res) => {
        const data = res.data
        if (String(data.status) !== '1') { resolve([]); return }
        const paths = data.route && data.route.paths
        if (!paths || paths.length === 0) { resolve([]); return }
        const allPoints = []
        const steps = paths[0].steps || []
        steps.forEach(step => {
          const pts = parsePolyline(step.polyline)
          allPoints.push(...pts)
        })
        resolve(allPoints)
      },
      fail: () => resolve([])  // 路线失败不影响主流程
    })
  })
}

/**
 * 构建微信 <map> polyline 属性格式
 * @param {Array} points  坐标数组 [{longitude, latitude}]
 * @param {string} color  线条颜色，默认红色
 */
function buildPolyline(points, color = '#D32F2FCC') {
  if (!points || points.length < 2) return []
  return [{
    points,
    color,
    width: 5,
    dottedLine: false,
    borderColor: '#ffffff88',
    borderWidth: 1
  }]
}

module.exports = { getBicyclingRoute, getWalkingRoute, buildPolyline }
