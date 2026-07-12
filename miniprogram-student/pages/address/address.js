const { addressApi } = require('../../utils/api')
Page({
  data: { addresses:[], showAdd:false, showEdit:false, editId:null, form:{label:'宿舍',address:'',isDefault:0} },
  onLoad(){ this.loadList() },
  async loadList(){ try{ const r=await addressApi.getList(); this.setData({addresses:r.data||[]}) }catch(e){} },
  // 新增地址
  openAdd(){ this.setData({showAdd:true,showEdit:false,editId:null,form:{label:'宿舍',address:'',isDefault:0}}) },
  closeAdd(){ this.setData({showAdd:false,showEdit:false}) },
  async submitAdd(){
    if(!this.data.form.address.trim()){wx.showToast({title:'请填写地址',icon:'none'});return}
    try{ await addressApi.add(this.data.form); wx.showToast({title:'添加成功'}); this.setData({showAdd:false}); this.loadList() }catch(e){}
  },
  // 编辑地址
  openEdit(e){
    // dataset中的id是string，数据库返回的id是number，用Number()转换后比较
    const targetId = Number(e.currentTarget.dataset.id)
    const addr = this.data.addresses.find(a=>a.id===targetId)
    if(addr) this.setData({showEdit:true,showAdd:false,editId:addr.id,form:{label:addr.label,address:addr.address,isDefault:addr.isDefault}})
  },
  async submitEdit(){
    if(!this.data.form.address.trim()){wx.showToast({title:'请填写地址',icon:'none'});return}
    try{ await addressApi.update(this.data.editId,this.data.form); wx.showToast({title:'修改成功'}); this.setData({showEdit:false}); this.loadList() }catch(e){}
  },
  // 设为默认地址
  async setDefault(e){
    const id=Number(e.currentTarget.dataset.id)
    const addr=this.data.addresses.find(a=>a.id===id)
    if(addr){ await addressApi.update(id,{...addr,isDefault:1}); this.loadList() }
  },
  async deleteAddr(e){
    const id=Number(e.currentTarget.dataset.id)
    wx.showModal({title:'确认删除',content:'确定删除该地址吗？',success:async(r)=>{
      if(r.confirm){ await addressApi.delete(id); this.loadList() }
    }})
  },
  // 通用输入处理（兼容所有微信基础库版本）
  onInput: function(e) { var d = {}; d[e.currentTarget.dataset.field] = e.detail.value; this.setData(d) },
})
