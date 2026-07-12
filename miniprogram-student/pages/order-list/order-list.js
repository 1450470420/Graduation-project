const { orderApi } = require('../../utils/api')
Page({
  data: { orders:[], activeTab:0, tabs:['全部','待接单','进行中','已完成','已取消'], loading:false },
  onLoad(){ this.loadOrders() },
  onShow(){ this.loadOrders() },
  changeTab(e){ this.setData({activeTab:e.currentTarget.dataset.index}); this.loadOrders() },
  async loadOrders() {
    const statusMap=[null,0,[1,2,3],4,5]
    const s = statusMap[this.data.activeTab]
    this.setData({loading:true})
    try{
      const res = await orderApi.getMyOrders(Array.isArray(s)?null:s)
      let orders = res.data||[]
      if(Array.isArray(s)) orders=orders.filter(o=>s.includes(o.status))
      this.setData({orders})
    }catch(e){}finally{this.setData({loading:false})}
  },
  goDetail(e){ wx.navigateTo({url:`/pages/order-detail/order-detail?id=${e.currentTarget.dataset.id}`}) }
})
