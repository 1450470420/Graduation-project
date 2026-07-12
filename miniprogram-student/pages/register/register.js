const { authApi } = require('../../utils/api')
Page({
  data: { username:'',password:'',confirmPwd:'',phone:'',loading:false },
  // 返回登录页
  goLogin(){ wx.navigateBack() },
  async handleRegister() {
    const {username,password,confirmPwd,phone} = this.data
    if(!username||!password||!phone){wx.showToast({title:'请填写完整信息',icon:'none'});return}
    if(password.length<6){wx.showToast({title:'密码至少6位',icon:'none'});return}
    if(password!==confirmPwd){wx.showToast({title:'两次密码不一致',icon:'none'});return}
    this.setData({loading:true})
    try{
      await authApi.register({username,password,phone,userType:1})
      wx.showToast({title:'注册成功，请登录'})
      setTimeout(()=>wx.navigateBack(),1500)
    }catch(e){}finally{this.setData({loading:false})}
  },
  // 通用输入处理（兼容所有微信基础库版本）
  onInput: function(e) { var d = {}; d[e.currentTarget.dataset.field] = e.detail.value; this.setData(d) },
})
