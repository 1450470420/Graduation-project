/**
 * 将后端返回的相对路径转为可访问的完整图片 URL
 * baseUrl = http://host/api，图片存在 /upload/... 下
 */
module.exports.getImgUrl = function(path) {
  if (!path) return ''
  if (path.startsWith('http')) return path
  var app = getApp()
  if (!app || !app.globalData) return ''
  return app.globalData.baseUrl.replace('/api', '') + path
}
