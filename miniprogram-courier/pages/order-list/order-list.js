const { orderApi } = require('../../utils/api')
Page({
  data:{orders:[],activeTab:0,tabs:['进行中','已完成','全部'],loading:false},
  onShow(){this.loadOrders()},
  changeTab(e){this.setData({activeTab:e.currentTarget.dataset.index});this.loadOrders()},
  async loadOrders(){
    // statusMap: 进行中=[1,2,3] 已完成=[4] 全部=null
    const statusMap=[[1,2,3],[4],null]
    const s=statusMap[this.data.activeTab]
    this.setData({loading:true})
    try{
      // 当需要多个状态时（如进行中=[1,2,3]），传null获取全部再客户端过滤
      const fetchStatus = (Array.isArray(s) && s.length > 1) ? null : (s ? s[0] : null)
      const r = await orderApi.getMy(fetchStatus)
      let orders = r.data || []
      // 客户端过滤多状态
      if(Array.isArray(s)) orders = orders.filter(o => s.includes(o.status))
      this.setData({orders})
    }catch(e){}finally{this.setData({loading:false})}
  },
  goDetail(e){wx.navigateTo({url:`/pages/order-detail/order-detail?id=${e.currentTarget.dataset.id}`})}
})
