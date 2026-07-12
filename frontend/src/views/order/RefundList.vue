<template>
  <div class="page-container">
    <div class="search-bar">
      <el-select v-model="query.status" placeholder="审核状态" clearable style="width:140px">
        <el-option label="待审核" :value="0"/><el-option label="已通过" :value="1"/><el-option label="已驳回" :value="2"/>
      </el-select>
      <el-button type="primary" @click="loadData">🔍 搜索</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe style="width:100%" :header-cell-style="headerStyle">
      <el-table-column prop="orderNo" label="订单编号" width="180"/>
      <el-table-column prop="studentName" label="申请学生" width="110"/>
      <el-table-column prop="reward" label="订单金额" width="100" align="center"><template #default="{row}">¥{{row.reward?.toFixed(2)}}</template></el-table-column>
      <el-table-column prop="reason" label="退款原因" min-width="200"/>
      <el-table-column label="状态" width="90" align="center">
        <template #default="{row}"><el-tag :type="row.status===1?'success':row.status===0?'warning':'danger'">{{['待审核','已通过','已驳回'][row.status]}}</el-tag></template>
      </el-table-column>
      <el-table-column prop="adminRemark" label="处理备注" min-width="150"/>
      <el-table-column prop="createTime" label="申请时间" width="170"/>
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template #default="{row}">
          <template v-if="row.status===0">
            <el-button type="success" size="small" text @click="handleApprove(row)">✅ 通过</el-button>
            <el-button type="danger" size="small" text @click="showReject(row)">❌ 驳回</el-button>
          </template>
          <span v-else style="color:#999;font-size:13px">已处理</span>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="query.current" v-model:page-size="query.size" :total="total" layout="total,prev,pager,next" @change="loadData"/>
    </div>
    <el-dialog v-model="rejectVisible" title="驳回退款" width="450px">
      <el-input v-model="rejectRemark" type="textarea" :rows="3" placeholder="请填写驳回理由"/>
      <template #footer><el-button @click="rejectVisible=false">取消</el-button><el-button type="danger" @click="confirmReject">确认驳回</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import {ref,reactive,onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {refundApi} from '@/api/index.js'
const loading=ref(false),tableData=ref([]),total=ref(0),rejectVisible=ref(false),rejectRemark=ref(''),currentId=ref(null)
const query=reactive({current:1,size:10,status:null})
const headerStyle={background:'#fdf0f0',color:'#333',fontWeight:'600'}
onMounted(loadData)
async function loadData(){loading.value=true;try{const r=await refundApi.getList(query);tableData.value=r.data.records;total.value=r.data.total}finally{loading.value=false}}
async function handleApprove(row){await refundApi.approve(row.id,{remark:'审核通过，退款将原路退回'});ElMessage.success('已通过');loadData()}
function showReject(row){currentId.value=row.id;rejectRemark.value='';rejectVisible.value=true}
async function confirmReject(){await refundApi.reject(currentId.value,{remark:rejectRemark.value});ElMessage.success('已驳回');rejectVisible.value=false;loadData()}
</script>
<style scoped>
.page-container{background:#fff;border-radius:12px;padding:24px;min-height:calc(100vh - 140px)}
.search-bar{display:flex;gap:12px;margin-bottom:20px}
.pagination{display:flex;justify-content:flex-end;margin-top:20px}
</style>
