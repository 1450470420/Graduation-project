const { profileApi, authApi, faceApi } = require('../../utils/api')
var getImgUrl = require('../../utils/image').getImgUrl
Page({
  data: {
    userInfo: null,
    hasFace: false,
    showEdit: false,
    editForm: { realName: '', phone: '', studentId: '', dormAddress: '' },
    showCertify: false,
    certifyForm: { realName: '', studentId: '' },
    showChangePwd: false,
    pwdForm: { oldPwd: '' , newPwd: '' , confirmPwd: '' }
  },
  onShow() { this.loadProfile(); this.loadFaceStatus() },

  async loadProfile() {
    try {
      const r = await profileApi.getProfile()
      const user = r.data || {}
      user.avatar = getImgUrl(user.avatar)
      this.setData({ userInfo: user })
    } catch(e) {}
  },

  async loadFaceStatus() {
    try {
      const r = await faceApi.getStatus()
      this.setData({ hasFace: r.data.hasFace })
    } catch(e) {}
  },

  // 跳转到独立的编辑资料页
  openEdit() {
    wx.navigateTo({ url: '/pages/profile-edit/profile-edit' })
    return
  },
  // 旧的弹窗逆辑已弃用，保留不削除以备兴趣
  _openEditModal() {
    const u = this.data.userInfo || {}
    this.setData({
      showEdit: true,
      editForm: {
        realName: u.realName || '',
        phone: u.phone || '',
        studentId: u.studentId || '',
        dormAddress: u.dormAddress || ''
      }
    })
  },

  closeEdit() { this.setData({ showEdit: false }) },

  // 提交编辑资料
  async saveEdit() {
    const form = this.data.editForm
    if (!form.realName) { wx.showToast({ title: '请填写姓名', icon: 'none' }); return }
    try {
      await profileApi.updateProfile(form)
      wx.showToast({ title: '修改成功' })
      this.setData({ showEdit: false })
      this.loadProfile()
    } catch(e) {}
  },

  // 实名认证弹窗
  openCertify(){
    const u = this.data.userInfo || {}
    this.setData({
      showCertify: true,
      certifyForm: { realName: u.realName || '', studentId: u.studentId || '' }
    })
  },
  closeCertify(){ this.setData({ showCertify: false }) },
  async saveCertify(){
    const { realName, studentId } = this.data.certifyForm
    if(!realName||!studentId){ wx.showToast({title:'请填写完整',icon:'none'}); return }
    try{
      await profileApi.certify({ realName, studentId })
      wx.showToast({ title:'认证成功' })
      this.setData({ showCertify: false })
      this.loadProfile()
    }catch(e){}
  },

  // 修改密码弹窗
  openChangePwd(){
    this.setData({ showChangePwd: true, pwdForm: { oldPwd: '', newPwd: '', confirmPwd: '' } })
  },
  closeChangePwd(){ this.setData({ showChangePwd: false }) },
  async saveChangePwd(){
    const { oldPwd, newPwd, confirmPwd } = this.data.pwdForm
    if (!oldPwd || !newPwd) { wx.showToast({ title: '请填写完整', icon: 'none' }); return }
    if (newPwd.length < 6) { wx.showToast({ title: '新密码至少6位', icon: 'none' }); return }
    if (newPwd !== confirmPwd) { wx.showToast({ title: '两次新密码不一致', icon: 'none' }); return }
    try {
      await authApi.changePassword({ oldPassword: oldPwd, newPassword: newPwd })
      wx.showToast({ title: '密码修改成功' })
      this.setData({ showChangePwd: false })
    } catch(e) {}
  },

  // 更换头像：选图 → wx.uploadFile 上传 → updateProfile
  changeAvatar: function() {
    var that = this
    var app = getApp()
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: function(res) {
        var filePath = res.tempFiles[0].tempFilePath
        wx.showLoading({ title: '上传中...' })
        wx.uploadFile({
          url: app.globalData.baseUrl + '/upload/avatar',
          filePath: filePath,
          name: 'file',
          withCredentials: true,
          success: function(uploadRes) {
            wx.hideLoading()
            try {
              var result = JSON.parse(uploadRes.data)
              if (result.code === 200) {
                // 拼接完整可访问 URL
                var serverRoot = app.globalData.baseUrl.replace('/api', '')
                var avatarUrl = serverRoot + result.data
                // 即时刷新页面头像
                that.setData({ 'userInfo.avatar': avatarUrl })
                // 同步保存到数据库
                wx.request({
                  url: app.globalData.baseUrl + '/app/student/profile',
                  method: 'PUT',
                  data: { avatar: avatarUrl },
                  header: { 'Content-Type': 'application/json' },
                  withCredentials: true,
                  success: function() {
                    wx.showToast({ title: '头像更新成功' })
                    if (app.globalData.userInfo) app.globalData.userInfo.avatar = avatarUrl
                    wx.setStorageSync('userInfo', app.globalData.userInfo)
                  }
                })
              } else {
                wx.showToast({ title: result.message || '上传失败', icon: 'none' })
              }
            } catch(e) { wx.showToast({ title: '上传失败', icon: 'none' }) }
          },
          fail: function() {
            wx.hideLoading()
            wx.showToast({ title: '上传失败', icon: 'none' })
          }
        })
      }
    })
  },

  // 导航到各功能页（已修复 URL）
  goFace(){ wx.navigateTo({url:'/pages/face/face'}) },
  goBalance(){ wx.navigateTo({url:'/pages/balance/balance'}) },
  goReputation(){ wx.navigateTo({url:'/pages/reputation/reputation'}) },
  goAddress(){ wx.navigateTo({url:'/pages/address/address'}) },
  goAnnouncement(){ wx.navigateTo({url:'/pages/announcement/announcement'}) },
  goTicket(){ wx.navigateTo({url:'/pages/ticket/ticket'}) },

  async logout(){
    wx.showModal({title:'退出登录',content:'确定退出登录吗？',success:async(r)=>{
      if(r.confirm){
        try{ await authApi.logout() }catch(e){}
        const app=getApp(); app.globalData.userInfo=null; app.globalData.isLogin=false
        wx.removeStorageSync('userInfo')
        wx.redirectTo({url:'/pages/login/login'})
      }
    }})
  },
  // 通用输入处理（兼容所有微信基础库版本）
  onInput: function(e) { var d = {}; d[e.currentTarget.dataset.field] = e.detail.value; this.setData(d) },
})
