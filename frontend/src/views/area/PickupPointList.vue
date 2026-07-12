<template>
  <div class="page-container">
    <div class="search-bar">
      <el-input v-model="query.keyword" placeholder="搜索取件点名称" clearable style="width:220px"/>
      <el-select v-model="query.type" placeholder="类型筛选" clearable style="width:140px">
        <el-option label="快递柜" value="快递柜"/><el-option label="菜鸟驿站" value="菜鸟驿站"/><el-option label="自提点" value="自提点"/>
      </el-select>
      <el-button type="primary" @click="loadData">🔍 搜索</el-button>
      <el-button type="success" @click="openAdd">➕ 添加取件点</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe style="width:100%" :header-cell-style="headerStyle">
      <el-table-column prop="name" label="取件点名称" min-width="160"/>
      <el-table-column prop="type" label="类型" width="110" align="center">
        <template #default="{row}"><el-tag size="small">{{row.type}}</el-tag></template>
      </el-table-column>
      <el-table-column prop="areaName" label="所属区域" width="150"/>
      <el-table-column prop="address" label="详细地址" min-width="200"/>
      <el-table-column label="状态" width="80" align="center">
        <template #default="{row}"><el-tag :type="row.status===1?'success':'danger'" size="small">{{row.status===1?'启用':'禁用'}}</el-tag></template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170"/>
      <el-table-column label="操作" width="140" align="center" fixed="right">
        <template #default="{row}">
          <el-button type="primary" size="small" text @click="openEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" text @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="query.current" v-model:page-size="query.size" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" @change="loadData"/>
    </div>
    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑取件点':'添加取件点'" width="500px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称"><el-input v-model="form.name"/></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" style="width:100%">
            <el-option label="快递柜" value="快递柜"/><el-option label="菜鸟驿站" value="菜鸟驿站"/><el-option label="自提点" value="自提点"/>
          </el-select>
        </el-form-item>
        <el-form-item label="所属区域">
          <el-select v-model="form.areaId" style="width:100%" filterable>
            <el-option v-for="a in areas" :key="a.id" :label="a.name" :value="a.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="详细地址"><el-input v-model="form.address"/></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用"/></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import {ref,reactive,onMounted} from 'vue'
import {ElMessage,ElMessageBox} from 'element-plus'
import {pickupPointApi,areaApi} from '@/api/index.js'
const loading=ref(false),tableData=ref([]),total=ref(0),dialogVisible=ref(false),isEdit=ref(false),areas=ref([])
const query=reactive({current:1,size:10,keyword:'',type:''})
const form=reactive({id:null,name:'',type:'快递柜',areaId:null,address:'',status:1})
const headerStyle={background:'#fdf0f0',color:'#333',fontWeight:'600'}
onMounted(async()=>{await loadData();const r=await areaApi.getAll();areas.value=r.data})
async function loadData(){loading.value=true;try{const r=await pickupPointApi.getList(query);tableData.value=r.data.records;total.value=r.data.total}finally{loading.value=false}}
function openAdd(){isEdit.value=false;Object.assign(form,{id:null,name:'',type:'快递柜',areaId:null,address:'',status:1});dialogVisible.value=true}
function openEdit(row){isEdit.value=true;Object.assign(form,{id:row.id,name:row.name,type:row.type,areaId:row.areaId,address:row.address,status:row.status});dialogVisible.value=true}
async function handleSave(){if(!form.name){ElMessage.warning('请填写名称');return}isEdit.value?await pickupPointApi.update(form.id,form):await pickupPointApi.add(form);ElMessage.success('保存成功');dialogVisible.value=false;loadData()}
async function handleDelete(row){await ElMessageBox.confirm(`确定删除【${row.name}】吗？`,'警告',{type:'warning'});await pickupPointApi.delete(row.id);ElMessage.success('删除成功');loadData()}
</script>
<style scoped>
.page-container{background:#fff;border-radius:12px;padding:24px;min-height:calc(100vh - 140px)}
.search-bar{display:flex;gap:12px;flex-wrap:wrap;margin-bottom:20px}
.pagination{display:flex;justify-content:flex-end;margin-top:20px}
</style>
