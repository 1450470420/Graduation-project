// 校园跑腿 - 学生端小程序入口
App({
  globalData: {
    userInfo: null,
    baseUrl: 'http://localhost:8080/api',
    isLogin: false,
    amapKey: '41326f60d51aa849ca28fdb66d579056'
  },
  onLaunch() {
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo) {
      this.globalData.userInfo = userInfo
      this.globalData.isLogin = true
    }
  }
})