<template>
  <div class="page-container">
    <div class="search-bar">
      <el-input v-model="query.operator" placeholder="搜索操作者" clearable style="width:160px"/>
      <el-select v-model="query.status" placeholder="操作状态" clearable style="width:130px">
        <el-option label="成功" :value="1"/><el-option label="失败" :value="0"/>
      </el-select>
      <el-button type="primary" @click="loadData">🔍 搜索</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe style="width:100%" :header-cell-style="headerStyle">
      <el-table-column prop="operator" label="操作者" width="120"/>
      <el-table-column prop="operation" label="操作描述" min-width="150"/>
      <el-table-column prop="method" label="请求方法" min-width="200" show-overflow-tooltip/>
      <el-table-column prop="ip" label="IP地址" width="130"/>
      <el-table-column prop="costTime" label="耗时(ms)" width="100" align="center"/>
      <el-table-column label="状态" width="80" align="center">
        <template #default="{row}"><el-tag :type="row.status===1?'success':'danger'" size="small">{{row.status===1?'成功':'失败'}}</el-tag></template>
      </el-table-column>
      <el-table-column prop="errorMsg" label="错误信息" min-width="150" show-overflow-tooltip/>
      <el-table-column prop="createTime" label="操作时间" width="170"/>
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="query.current" v-model:page-size="query.size" :total="total" :page-sizes="[10,20,50,100]" layout="total,sizes,prev,pager,next,jumper" @change="loadData"/>
    </div>
  </div>
</template>
<script setup>
import {ref,reactive,onMounted} from 'vue'
import {logApi} from '@/api/index.js'
const loading=ref(false),tableData=ref([]),total=ref(0)
const query=reactive({current:1,size:20,operator:'',status:null})
const headerStyle={background:'#fdf0f0',color:'#333',fontWeight:'600'}
onMounted(loadData)
async function loadData(){loading.value=true;try{const r=await logApi.getList(query);tableData.value=r.data.records;total.value=r.data.total}finally{loading.value=false}}
</script>
<style scoped>
.page-container{background:#fff;border-radius:12px;padding:24px;min-height:calc(100vh - 140px)}
.search-bar{display:flex;gap:12px;margin-bottom:20px}
.pagination{display:flex;justify-content:flex-end;margin-top:20px}
</style>
