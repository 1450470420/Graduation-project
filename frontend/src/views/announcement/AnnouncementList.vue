<template>
  <div class="page-container">
    <div class="search-bar">
      <el-input v-model="query.keyword" placeholder="搜索公告标题" clearable style="width:220px"/>
      <el-select v-model="query.target" placeholder="发布对象" clearable style="width:130px">
        <el-option label="全部" value="all"/><el-option label="学生" value="student"/><el-option label="跑腿员" value="courier"/>
      </el-select>
      <el-select v-model="query.status" placeholder="发布状态" clearable style="width:120px">
        <el-option label="已发布" :value="1"/><el-option label="已下架" :value="0"/>
      </el-select>
      <el-button type="primary" @click="loadData">🔍 搜索</el-button>
      <el-button type="success" @click="openAdd">📢 发布公告</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe style="width:100%" :header-cell-style="headerStyle">
      <el-table-column prop="title" label="公告标题" min-width="200"/>
      <el-table-column label="发布对象" width="100" align="center">
        <template #default="{row}"><el-tag :type="row.target==='all'?'':row.target==='student'?'success':'warning'" size="small">{{{'all':'全部','student':'学生','courier':'跑腿员'}[row.target]}}</el-tag></template>
      </el-table-column>
      <el-table-column label="状态" width="90" align="center">
        <template #default="{row}"><el-tag :type="row.status===1?'success':'danger'">{{row.status===1?'已发布':'已下架'}}</el-tag></template>
      </el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="170"/>
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="{row}">
          <el-button type="primary" size="small" text @click="openEdit(row)">编辑</el-button>
          <el-button v-if="row.status===1" type="warning" size="small" text @click="handleOffline(row)">下架</el-button>
          <el-button type="danger" size="small" text @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="query.current" v-model:page-size="query.size" :total="total" layout="total,prev,pager,next" @change="loadData"/>
    </div>
    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑公告':'发布公告'" width="600px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="公告标题"><el-input v-model="form.title" placeholder="请输入公告标题"/></el-form-item>
        <el-form-item label="发布对象">
          <el-select v-model="form.target" style="width:100%">
            <el-option label="全部用户" value="all"/><el-option label="仅学生" value="student"/><el-option label="仅跑腿员" value="courier"/>
          </el-select>
        </el-form-item>
        <el-form-item label="公告内容"><el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入公告内容"/></el-form-item>
        <el-form-item label="发布状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="立即发布" inactive-text="暂不发布"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSave">发布</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import {ref,reactive,onMounted} from 'vue'
import {ElMessage,ElMessageBox} from 'element-plus'
import {announcementApi} from '@/api/index.js'
const loading=ref(false),tableData=ref([]),total=ref(0),dialogVisible=ref(false),isEdit=ref(false)
const query=reactive({current:1,size:10,keyword:'',target:'',status:null})
const form=reactive({id:null,title:'',content:'',target:'all',status:1})
const headerStyle={background:'#fdf0f0',color:'#333',fontWeight:'600'}
onMounted(loadData)
async function loadData(){loading.value=true;try{const r=await announcementApi.getList(query);tableData.value=r.data.records;total.value=r.data.total}finally{loading.value=false}}
function openAdd(){isEdit.value=false;Object.assign(form,{id:null,title:'',content:'',target:'all',status:1});dialogVisible.value=true}
function openEdit(row){isEdit.value=true;Object.assign(form,{id:row.id,title:row.title,content:row.content,target:row.target,status:row.status});dialogVisible.value=true}
async function handleSave(){if(!form.title){ElMessage.warning('请填写标题');return}isEdit.value?await announcementApi.update(form.id,form):await announcementApi.add(form);ElMessage.success('保存成功');dialogVisible.value=false;loadData()}
async function handleOffline(row){await announcementApi.offline(row.id);ElMessage.success('已下架');loadData()}
async function handleDelete(row){await ElMessageBox.confirm(`确定删除公告【${row.title}】吗？`,'警告',{type:'warning'});await announcementApi.delete(row.id);ElMessage.success('删除成功');loadData()}
</script>
<style scoped>
.page-container{background:#fff;border-radius:12px;padding:24px;min-height:calc(100vh - 140px)}
.search-bar{display:flex;gap:12px;flex-wrap:wrap;margin-bottom:20px}
.pagination{display:flex;justify-content:flex-end;margin-top:20px}
</style>
