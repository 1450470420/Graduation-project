<template>
  <div class="page-container">
    <div class="search-bar">
      <el-button type="primary" @click="openAdd">➕ 新增配置</el-button>
      <el-alert type="info" title="系统配置会直接影响平台功能，请谨慎修改" show-icon :closable="false" style="margin-left:20px;flex:1"/>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe style="width:100%" :header-cell-style="headerStyle">
      <el-table-column prop="configKey" label="配置键名" width="220"/>
      <el-table-column prop="configValue" label="配置值" min-width="200"/>
      <el-table-column prop="configDesc" label="配置说明" min-width="200"/>
      <el-table-column prop="updateTime" label="最后修改时间" width="170"/>
      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template #default="{row}"><el-button type="primary" size="small" text @click="openEdit(row)">编辑</el-button></template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑配置':'新增配置'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="配置键名"><el-input v-model="form.configKey" :disabled="isEdit" placeholder="如：commission_rate"/></el-form-item>
        <el-form-item label="配置值"><el-input v-model="form.configValue" placeholder="请输入配置值"/></el-form-item>
        <el-form-item label="配置说明"><el-input v-model="form.configDesc" placeholder="请输入说明"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import {ref,reactive,onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {configApi} from '@/api/index.js'
const loading=ref(false),tableData=ref([]),dialogVisible=ref(false),isEdit=ref(false)
const form=reactive({id:null,configKey:'',configValue:'',configDesc:''})
const headerStyle={background:'#fdf0f0',color:'#333',fontWeight:'600'}
onMounted(loadData)
async function loadData(){loading.value=true;try{const r=await configApi.getList();tableData.value=r.data}finally{loading.value=false}}
function openAdd(){isEdit.value=false;Object.assign(form,{id:null,configKey:'',configValue:'',configDesc:''});dialogVisible.value=true}
function openEdit(row){isEdit.value=true;Object.assign(form,row);dialogVisible.value=true}
async function handleSave(){if(!form.configKey||!form.configValue){ElMessage.warning('请填写完整');return}isEdit.value?await configApi.update(form.id,form):await configApi.save(form);ElMessage.success('保存成功');dialogVisible.value=false;loadData()}
</script>
<style scoped>
.page-container{background:#fff;border-radius:12px;padding:24px;min-height:calc(100vh - 140px)}
.search-bar{display:flex;align-items:center;margin-bottom:20px}
</style>
