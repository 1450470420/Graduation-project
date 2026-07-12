const { authApi } = require('../../utils/api')
Page({
  data:{username:'',password:'',loading:false},
  onUsernameInput(e){this.setData({username:e.detail.value})},
  onPasswordInput(e){this.setData({password:e.detail.value})},
  async handleLogin(){
    if(!this.data.username||!this.data.password){wx.showToast({title:'请填写账号密码',icon:'none'});return}
    this.setData({loading:true})
    try{
      const res=await authApi.login({username:this.data.username,password:this.data.password})
      if(res.data.userType!==2){wx.showToast({title:'请使用跑腿员账号登录',icon:'none'});return}
      const app=getApp();app.globalData.userInfo=res.data;app.globalData.isLogin=true
      wx.setStorageSync('courierInfo',res.data)
      wx.switchTab({url:'/pages/index/index'})
    }catch(e){}finally{this.setData({loading:false})}
  },
  goRegister(){wx.navigateTo({url:'/pages/register/register'})}
})
