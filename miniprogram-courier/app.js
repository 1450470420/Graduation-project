App({
  globalData: {
    userInfo: null,
    baseUrl: 'http://localhost:8080/api',
    isLogin: false,
    amapKey: '41326f60d51aa849ca28fdb66d579056'  // 高德地图 WebService Key
  },
  onLaunch() {
    const userInfo = wx.getStorageSync('courierInfo')
    if(userInfo) { this.globalData.userInfo=userInfo; this.globalData.isLogin=true }
  }
})
