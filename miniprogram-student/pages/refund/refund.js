const { orderApi } = require('../../utils/api')
Page({
  data: { orderId:null, orderNo:'', serviceType:'', reason:'', loading:false },
  onLoad(o){
    this.setData({orderId:o.orderId})
    // 加载订单基本信息以供用户确认
    if(o.orderId) orderApi.getOrderDetail(o.orderId).then(r=>
      this.setData({orderNo:r.data.orderNo||'',serviceType:r.data.serviceType||''})
    ).catch(()=>{})
  },
  async submit(){
    if(!this.data.reason.trim()){wx.showToast({title:'请填写退款原因',icon:'none'});return}
    this.setData({loading:true})
    try{
      await orderApi.applyRefund(this.data.orderId,this.data.reason)
      wx.showToast({title:'退款申请已提交'})
      setTimeout(()=>wx.navigateBack(),1500)
    }catch(e){}finally{this.setData({loading:false})}
  },
  // 通用输入处理（兼容所有微信基础库版本）
  onInput: function(e) { var d = {}; d[e.currentTarget.dataset.field] = e.detail.value; this.setData(d) },
})
