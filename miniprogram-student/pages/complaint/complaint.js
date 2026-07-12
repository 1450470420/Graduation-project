const { orderApi } = require('../../utils/api')
Page({
  data: { orderId:null, orderNo:'', serviceType:'', type:'超时', content:'', types:['超时','服务差','丢件','其他'], loading:false },
  onLoad(o){
    this.setData({orderId:o.orderId})
    if(o.orderId) orderApi.getOrderDetail(o.orderId).then(r=>
      this.setData({orderNo:r.data.orderNo||'',serviceType:r.data.serviceType||''})
    ).catch(()=>{})
  },
  changeType(e){ this.setData({type:this.data.types[e.detail.value]}) },
  async submit(){
    if(!this.data.content.trim()){wx.showToast({title:'请填写投诉内容',icon:'none'});return}
    this.setData({loading:true})
    try{
      await orderApi.submitComplaint(this.data.orderId,{type:this.data.type,content:this.data.content})
      wx.showToast({title:'投诉已提交'})
      setTimeout(()=>wx.navigateBack(),1500)
    }catch(e){}finally{this.setData({loading:false})}
  },
  // 通用输入处理（兼容所有微信基础库版本）
  onInput: function(e) { var d = {}; d[e.currentTarget.dataset.field] = e.detail.value; this.setData(d) },
})
