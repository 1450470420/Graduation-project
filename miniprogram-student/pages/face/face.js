const { faceApi } = require('../../utils/api')
var getImgUrl = require('../../utils/image').getImgUrl

Page({
  data: {
    hasFace: false,
    facePhoto: '',
    uploading: false,
    showCamera: false,
    cameraReady: false
  },

  onShow() {
    this.loadFaceStatus()
  },

  onHide() {
    this.setData({ showCamera: false, cameraReady: false })
  },

  async loadFaceStatus() {
    try {
      const r = await faceApi.getStatus()
      this.setData({
        hasFace: r.data.hasFace,
        facePhoto: r.data.facePhoto ? getImgUrl(r.data.facePhoto) : ''
      })
    } catch (e) {}
  },

  isDevtools() {
    try {
      return wx.getSystemInfoSync().platform === 'devtools'
    } catch (e) {
      return false
    }
  },

  openCamera() {
    if (this.isDevtools()) {
      this.usePhotoInDevtools()
      return
    }
    this.ensureCameraPermission().then((granted) => {
      if (!granted) {
        wx.showToast({ title: '未获得摄像头权限', icon: 'none' })
        return
      }
      this.setData({ showCamera: true, cameraReady: false })
    })
  },

  closeCamera() {
    this.setData({ showCamera: false, cameraReady: false })
  },

  onCameraInitDone() {
    this.setData({ cameraReady: true })
  },

  onCameraError(e) {
    wx.showToast({ title: '摄像头启动失败，请检查权限', icon: 'none' })
    this.closeCamera()
  },

  capturePhoto() {
    if (!this.data.cameraReady) {
      wx.showToast({ title: '摄像头初始化中，请稍后', icon: 'none' })
      return
    }

    const that = this
    const ctx = wx.createCameraContext()
    this.setData({ uploading: true })
    ctx.takePhoto({
      quality: 'normal',
      success(res) {
        that.closeCamera()
        that.uploadFace(res.tempImagePath)
      },
      fail() {
        that.setData({ uploading: false })
        wx.showToast({ title: '拍照失败，请重试', icon: 'none' })
      }
    })
  },

  usePhotoInDevtools() {
    wx.showModal({
      title: '开发者工具限制',
      content: '模拟器无法稳定调用摄像头，建议真机调试。是否从相册选择一张人脸照片继续调试？',
      confirmText: '选照片',
      cancelText: '真机调试',
      success: (r) => {
        if (r.confirm) this.chooseDebugPhoto()
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
        this.uploadFace(file)
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
      content: '请允许使用摄像头，用于人脸采集与验证',
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

  uploadFace(filePath) {
    const app = getApp()
    const that = this
    this.setData({ uploading: true })
    wx.showLoading({ title: '正在注册人脸...' })

    const cookie = wx.getStorageSync('jsessionid') || ''
    wx.uploadFile({
      url: app.globalData.baseUrl + '/app/face/register',
      filePath: filePath,
      name: 'file',
      header: cookie ? { Cookie: cookie } : {},
      success(uploadRes) {
        wx.hideLoading()
        that.setData({ uploading: false })
        try {
          const result = JSON.parse(uploadRes.data)
          if (result.code === 200) {
            wx.showToast({ title: '人脸注册成功' })
            that.loadFaceStatus()
          } else {
            wx.showToast({ title: result.message || '注册失败', icon: 'none' })
          }
        } catch (e) {
          wx.showToast({ title: '注册失败', icon: 'none' })
        }
      },
      fail() {
        wx.hideLoading()
        that.setData({ uploading: false })
        wx.showToast({ title: '网络异常', icon: 'none' })
      }
    })
  },

  reRegister() {
    wx.showModal({
      title: '重新采集人脸',
      content: '确定要重新采集人脸照片吗？旧照片将被替换。',
      success: (r) => {
        if (r.confirm) this.openCamera()
      }
    })
  }
})
