const { orderApi } = require('../../utils/api')
const { getImgUrl } = require('../../utils/image')
Page({
  data:{orders:[],userInfo:null,filterType:'',loading:false},
  onLoad(){const app=getApp();if(!app.globalData.isLogin){wx.redirectTo({url:'/pages/login/login'});return}const u=Object.assign({},app.globalData.userInfo);u.avatarUrl=getImgUrl(u.avatar);this.setData({userInfo:u});this.loadOrders()},
  onShow(){this.loadOrders()},
  changeType(e){const types=['','代取快递','代买','代送'];this.setData({filterType:types[e.detail.value]});this.loadOrders()},
  async loadOrders(){
    this.setData({loading:true})
    try{const r=await orderApi.getAvailable(this.data.filterType);this.setData({orders:r.data||[]})}catch(e){}finally{this.setData({loading:false})}
  },
  async grabOrder(e){
    const id=e.currentTarget.dataset.id
    wx.showModal({title:'接单确认',content:'确定接取这个订单吗？',success:async(r)=>{
      if(r.confirm){try{await orderApi.grab(id);wx.showToast({title:'接单成功🎉'});this.loadOrders()}catch(e){}}
    }})
  },
  goApply(){wx.navigateTo({url:'/pages/application/application'})}
})
