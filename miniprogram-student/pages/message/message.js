const { messageApi } = require('../../utils/api')
Page({
  data: { messages:[] },
  onShow(){ this.loadList() },
  async loadList(){ try{ const r=await messageApi.getList(); this.setData({messages:r.data}) }catch(e){} },
  async readMsg(e){ const id=e.currentTarget.dataset.id; await messageApi.markRead(id); this.loadList() }
})
