<template>
  <div class="page-container">
    <div class="search-bar">
      <el-input v-model="query.keyword" placeholder="搜索区域名称" clearable style="width:220px"/>
      <el-select v-model="query.type" placeholder="区域类型" clearable style="width:140px">
        <el-option label="校区" value="campus"/><el-option label="宿舍楼" value="dorm"/><el-option label="教学楼" value="teaching"/><el-option label="其他" value="other"/>
      </el-select>
      <el-button type="primary" @click="loadData">🔍 搜索</el-button>
      <el-button type="success" @click="openAdd">➕ 添加区域</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe style="width:100%" :header-cell-style="headerStyle">
      <el-table-column prop="id" label="ID" width="70" align="center"/>
      <el-table-column prop="name" label="区域名称" min-width="150"/>
      <el-table-column label="区域类型" width="110" align="center">
        <template #default="{row}">
          <el-tag :type="typeTag[row.type]" size="small">{{typeLabel[row.type]||row.type}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="parentId" label="父区域ID" width="100" align="center"><template #default="{row}">{{row.parentId===0?'顶级':row.parentId}}</template></el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="80" align="center"/>
      <el-table-column label="状态" width="80" align="center">
        <template #default="{row}"><el-tag :type="row.status===1?'success':'danger'" size="small">{{row.status===1?'启用':'禁用'}}</el-tag></template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170"/>
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template #default="{row}">
          <el-button type="primary" size="small" text @click="openEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" text @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="query.current" v-model:page-size="query.size" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" @change="loadData"/>
    </div>
    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑区域':'添加区域'" width="500px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="区域名称"><el-input v-model="form.name" placeholder="请输入区域名称"/></el-form-item>
        <el-form-item label="区域类型">
          <el-select v-model="form.type" style="width:100%">
            <el-option label="校区" value="campus"/><el-option label="宿舍楼" value="dorm"/><el-option label="教学楼" value="teaching"/><el-option label="其他" value="other"/>
          </el-select>
        </el-form-item>
        <el-form-item label="父区域ID"><el-input-number v-model="form.parentId" :min="0" placeholder="0=顶级"/></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0"/></el-form-item>
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
import {areaApi} from '@/api/index.js'
const loading=ref(false),tableData=ref([]),total=ref(0),dialogVisible=ref(false),isEdit=ref(false)
const query=reactive({current:1,size:10,keyword:'',type:''})
const form=reactive({id:null,name:'',type:'campus',parentId:0,sortOrder:0,status:1})
const headerStyle={background:'#fdf0f0',color:'#333',fontWeight:'600'}
const typeLabel={campus:'校区',dorm:'宿舍楼',teaching:'教学楼',other:'其他'}
const typeTag={campus:'',dorm:'success',teaching:'warning',other:'info'}
onMounted(loadData)
async function loadData(){loading.value=true;try{const r=await areaApi.getList(query);tableData.value=r.data.records;total.value=r.data.total}finally{loading.value=false}}
function openAdd(){isEdit.value=false;Object.assign(form,{id:null,name:'',type:'campus',parentId:0,sortOrder:0,status:1});dialogVisible.value=true}
function openEdit(row){isEdit.value=true;Object.assign(form,row);dialogVisible.value=true}
async function handleSave(){if(!form.name){ElMessage.warning('请填写区域名称');return}isEdit.value?await areaApi.update(form.id,form):await areaApi.add(form);ElMessage.success('保存成功');dialogVisible.value=false;loadData()}
async function handleDelete(row){await ElMessageBox.confirm(`确定删除区域【${row.name}】吗？`,'警告',{type:'warning'});await areaApi.delete(row.id);ElMessage.success('删除成功');loadData()}
</script>
<style scoped>
.page-container{background:#fff;border-radius:12px;padding:24px;min-height:calc(100vh - 140px)}
.search-bar{display:flex;gap:12px;flex-wrap:wrap;margin-bottom:20px}
.pagination{display:flex;justify-content:flex-end;margin-top:20px}
</style>
