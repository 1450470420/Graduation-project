Page({
  data: {
    orderId: null,
    orderNo: '',
    capturing: false,
    cameraReady: false,
    devtoolsMode: false
  },

  onLoad(o) {
    this.setData({ orderId: o.orderId, orderNo: o.orderNo || '' })
    if (this.isDevtools()) {
      this.setData({ devtoolsMode: true })
      return
    }
    this.ensureCameraPermission().then((granted) => {
      if (!granted) this.exitByPermissionDenied()
    })
  },

  isDevtools() {
    try {
      return wx.getSystemInfoSync().platform === 'devtools'
    } catch (e) {
      return false
    }
  },

  onCameraInitDone() {
    this.setData({ cameraReady: true })
  },

  exitByPermissionDenied() {
    wx.showToast({ title: '未获得摄像头权限', icon: 'none' })
    setTimeout(() => { wx.navigateBack() }, 1200)
  },

  onCameraError() {
    wx.showToast({ title: '摄像头启动失败，请检查权限', icon: 'none' })
    setTimeout(() => { wx.navigateBack() }, 1500)
  },

  capture() {
    if (this.data.devtoolsMode) {
      this.chooseDebugPhoto()
      return
    }
    if (!this.data.cameraReady) {
      wx.showToast({ title: '摄像头初始化中，请稍后', icon: 'none' })
      return
    }

    const that = this
    const ctx = wx.createCameraContext()
    this.setData({ capturing: true })

    ctx.takePhoto({
      quality: 'normal',
      success(res) {
        that.doVerify(res.tempImagePath)
      },
      fail() {
        that.setData({ capturing: false })
        wx.showToast({ title: '拍照失败，请重试', icon: 'none' })
      }
    })
  },

  chooseDebugPhoto() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album'],
      success: (res) => {
        const file = res.tempFiles && res.tempFiles[0] && res.tempFiles[0].tempFilePath
        if (!file) {
          wx.showToast({ title: '未获取到照片', icon: 'none' })
          return
        }
        this.setData({ capturing: true })
        this.doVerify(file)
      },
      fail: () => {
        wx.showToast({ title: '选择照片失败', icon: 'none' })
      }
    })
  },

  ensureCameraPermission() {
    return new Promise((resolve) => {
      wx.getSetting({
        success: (res) => {
          const status = res.authSetting['scope.camera']
          if (status === true) {
            resolve(true)
            return
          }
          if (status === false) {
            this.openCameraSetting(resolve)
            return
          }
          wx.authorize({
            scope: 'scope.camera',
            success: () => resolve(true),
            fail: () => this.openCameraSetting(resolve)
          })
        },
        fail: () => resolve(true)
      })
    })
  },

  openCameraSetting(resolve) {
    wx.showModal({
      title: '需要摄像头权限',
      content: '请允许使用摄像头，用于收货人脸验证',
      confirmText: '去设置',
      success: (r) => {
        if (!r.confirm) {
          resolve(false)
          return
        }
        wx.openSetting({
          success: (s) => resolve(!!s.authSetting['scope.camera']),
          fail: () => resolve(false)
        })
      },
      fail: () => resolve(false)
    })
  },

  doVerify(filePath) {
    const app = getApp()
    const that = this
    wx.showLoading({ title: '人脸比对中...' })
    const cookie = wx.getStorageSync('jsessionid') || ''

    wx.uploadFile({
      url: app.globalData.baseUrl + '/app/face/verify',
      filePath: filePath,
      name: 'file',
      formData: { orderId: String(this.data.orderId) },
      header: cookie ? { Cookie: cookie } : {},
      success(uploadRes) {
        wx.hideLoading()
        that.setData({ capturing: false })
        try {
          const result = JSON.parse(uploadRes.data)
          if (result.code === 200 && result.data && result.data.passed) {
            wx.showModal({
              title: '✅ 验证通过',
              content: '人脸验证通过（相似度' + result.data.similarity + '%），返回订单确认收货',
              showCancel: false,
              success() {
                const pages = getCurrentPages()
                const prevPage = pages[pages.length - 2]
                if (prevPage) {
                  prevPage.setData({ faceVerified: true })
                }
                wx.navigateBack()
              }
            })
          } else {
            const msg = (result.data && result.data.message) ? result.data.message : (result.message || '验证失败')
            wx.showModal({
              title: '验证未通过',
              content: msg + '，是否重新拍照？',
              confirmText: '重新拍照',
              success(r) {
                if (!r.confirm) wx.navigateBack()
              }
            })
          }
        } catch (e) {
          wx.showToast({ title: '验证异常', icon: 'none' })
          that.setData({ capturing: false })
        }
      },
      fail() {
        wx.hideLoading()
        that.setData({ capturing: false })
        wx.showToast({ title: '网络异常', icon: 'none' })
      }
    })
  }
})
