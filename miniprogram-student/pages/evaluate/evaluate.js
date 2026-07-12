const { orderApi } = require('../../utils/api')
Page({
  data: { rating:5, content:'', orderId:null, loading:false },
  onLoad(o){ this.setData({orderId:o.id}) },
  changeRating(e){ this.setData({rating:e.currentTarget.dataset.star}) },
  async submit(){
    if(!this.data.content.trim()){wx.showToast({title:'请填写评价内容',icon:'none'});return}
    this.setData({loading:true})
    try{
      await orderApi.evaluate(this.data.orderId,{rating:this.data.rating,content:this.data.content})
      wx.showToast({title:'评价成功'})
      setTimeout(()=>wx.navigateBack(),1500)
    }catch(e){}finally{this.setData({loading:false})}
  },
  // 通用输入处理（兼容所有微信基础库版本）
  onInput: function(e) { var d = {}; d[e.currentTarget.dataset.field] = e.detail.value; this.setData(d) },
})
