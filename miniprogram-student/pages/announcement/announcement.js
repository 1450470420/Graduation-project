const { announcementApi } = require('../../utils/api')
Page({
  data: { announcements:[], selected:null },
  onLoad(){ this.loadList() },
  async loadList(){ try{ const r=await announcementApi.getList('student'); this.setData({announcements:r.data}) }catch(e){} },
  viewDetail(e){ this.setData({selected:e.currentTarget.dataset.item}) },
  closeDetail(){ this.setData({selected:null}) }
})
