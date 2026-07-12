const { courierApi } = require('../../utils/api')
Page({
  data:{earnings:null},
  onShow(){this.loadEarnings()},
  async loadEarnings(){
    try{
      const r=await courierApi.getEarnings()
      const d=r.data||{}
      // 统一格式化金额为两位小数
      this.setData({earnings:{
        ...d,
        balance: Number(d.balance||0).toFixed(2),
        totalEarnings: Number(d.totalEarnings||0).toFixed(2),
        records: (d.records||[]).map(rec=>({...rec, amount: Number(rec.amount||0).toFixed(2)}))
      }})
    }catch(e){}
  },
  goWithdraw(){wx.navigateTo({url:'/pages/withdrawal/withdrawal'})}
})
