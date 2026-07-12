<template>
  <div class="page-container">
    <div class="search-bar">
      <el-select v-model="query.status" placeholder="处理状态" clearable style="width:140px">
        <el-option label="待处理" :value="0"/><el-option label="处理中" :value="1"/><el-option label="已处理" :value="2"/>
      </el-select>
      <el-button type="primary" @click="loadData">🔍 搜索</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe style="width:100%" :header-cell-style="headerStyle">
      <el-table-column prop="orderNo" label="订单编号" width="180"/>
      <el-table-column prop="studentName" label="投诉用户" width="110"/>
      <el-table-column prop="type" label="投诉类型" width="100" align="center"><template #default="{row}"><el-tag type="warning" size="small">{{row.type}}</el-tag></template></el-table-column>
      <el-table-column prop="content" label="投诉内容" min-width="200"/>
      <el-table-column label="凭证图片" width="100" align="center">
        <template #default="{row}">
          <el-image v-if="row.images" :src="row.images.split(',')[0]" :preview-src-list="row.images.split(',')" style="width:40px;height:40px;border-radius:4px"/>
          <span v-else style="color:#ccc">无</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90" align="center">
        <template #default="{row}"><el-tag :type="row.status===2?'success':row.status===1?'warning':'danger'">{{['待处理','处理中','已处理'][row.status]}}</el-tag></template>
      </el-table-column>
      <el-table-column prop="result" label="处理结果" min-width="150"/>
      <el-table-column prop="createTime" label="投诉时间" width="170"/>
      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template #default="{row}">
          <el-button v-if="row.status<2" type="primary" size="small" text @click="showHandle(row)">处理</el-button>
          <span v-else style="color:#999;font-size:13px">已处理</span>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="query.current" v-model:page-size="query.size" :total="total" layout="total,prev,pager,next" @change="loadData"/>
    </div>
    <el-dialog v-model="handleVisible" title="处理投诉" width="480px">
      <el-input v-model="handleResult" type="textarea" :rows="4" placeholder="请填写处理结果说明"/>
      <template #footer><el-button @click="handleVisible=false">取消</el-button><el-button type="primary" @click="confirmHandle">提交处理结果</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import {ref,reactive,onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {complaintApi} from '@/api/index.js'
const loading=ref(false),tableData=ref([]),total=ref(0),handleVisible=ref(false),handleResult=ref(''),currentId=ref(null)
const query=reactive({current:1,size:10,status:null})
const headerStyle={background:'#fdf0f0',color:'#333',fontWeight:'600'}
onMounted(loadData)
async function loadData(){loading.value=true;try{const r=await complaintApi.getList(query);tableData.value=r.data.records;total.value=r.data.total}finally{loading.value=false}}
function showHandle(row){currentId.value=row.id;handleResult.value='';handleVisible.value=true}
async function confirmHandle(){await complaintApi.handle(currentId.value,{result:handleResult.value});ElMessage.success('处理完成');handleVisible.value=false;loadData()}
</script>
<style scoped>
.page-container{background:#fff;border-radius:12px;padding:24px;min-height:calc(100vh - 140px)}
.search-bar{display:flex;gap:12px;margin-bottom:20px}
.pagination{display:flex;justify-content:flex-end;margin-top:20px}
</style>
