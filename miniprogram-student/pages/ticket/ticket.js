const { ticketApi } = require('../../utils/api')
Page({
  data: { tickets:[], showAdd:false, form:{title:'',content:''} },
  onLoad(){ this.loadList() },
  async loadList(){ try{ const r=await ticketApi.getList(); this.setData({tickets:r.data}) }catch(e){} },
  openAdd(){ this.setData({showAdd:true,form:{title:'',content:''}}) },
  closeAdd(){ this.setData({showAdd:false}) },
  async submit(){
    if(!this.data.form.title||!this.data.form.content){wx.showToast({title:'请填写完整',icon:'none'});return}
    try{ await ticketApi.submit(this.data.form); wx.showToast({title:'提交成功'}); this.setData({showAdd:false}); this.loadList() }catch(e){}
  },
  // 通用输入处理（兼容所有微信基础库版本）
  onInput: function(e) { var d = {}; d[e.currentTarget.dataset.field] = e.detail.value; this.setData(d) },
})
