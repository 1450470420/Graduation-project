<template>
  <div class="page-container">
    <div class="search-bar">
      <el-select v-model="query.status" placeholder="审核状态" clearable style="width:140px">
        <el-option label="待审核" :value="0"/><el-option label="已通过" :value="1"/><el-option label="已驳回" :value="2"/>
      </el-select>
      <el-button type="primary" @click="loadData">🔍 搜索</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe style="width:100%" :header-cell-style="headerStyle">
      <el-table-column prop="courierName" label="跑腿员" width="120"/>
      <el-table-column prop="courierUsername" label="用户名" width="120"/>
      <el-table-column prop="amount" label="提现金额" width="110" align="center"><template #default="{row}"><span style="color:#E74C3C;font-weight:700;font-size:16px">¥{{row.amount?.toFixed(2)}}</span></template></el-table-column>
      <el-table-column prop="paymentMethod" label="收款方式" width="100" align="center"><template #default="{row}"><el-tag size="small">{{row.paymentMethod}}</el-tag></template></el-table-column>
      <el-table-column prop="accountInfo" label="收款账号" min-width="200"/>
      <el-table-column label="状态" width="90" align="center">
        <template #default="{row}"><el-tag :type="row.status===1?'success':row.status===0?'warning':'danger'">{{['待审核','已通过','已驳回'][row.status]}}</el-tag></template>
      </el-table-column>
      <el-table-column prop="adminRemark" label="处理备注" min-width="150"/>
      <el-table-column prop="createTime" label="申请时间" width="170"/>
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template #default="{row}">
          <template v-if="row.status===0">
            <el-button type="success" size="small" text @click="handleApprove(row)">✅ 打款</el-button>
            <el-button type="danger" size="small" text @click="showReject(row)">❌ 驳回</el-button>
          </template>
          <span v-else style="color:#999;font-size:13px">已处理</span>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="query.current" v-model:page-size="query.size" :total="total" layout="total,prev,pager,next" @change="loadData"/>
    </div>
    <el-dialog v-model="rejectVisible" title="驳回提现" width="450px">
      <el-input v-model="rejectRemark" type="textarea" :rows="3" placeholder="请填写驳回原因"/>
      <template #footer><el-button @click="rejectVisible=false">取消</el-button><el-button type="danger" @click="confirmReject">确认驳回</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import {ref,reactive,onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {withdrawalApi} from '@/api/index.js'
const loading=ref(false),tableData=ref([]),total=ref(0),rejectVisible=ref(false),rejectRemark=ref(''),currentId=ref(null)
const query=reactive({current:1,size:10,status:null})
const headerStyle={background:'#fdf0f0',color:'#333',fontWeight:'600'}
onMounted(loadData)
async function loadData(){loading.value=true;try{const r=await withdrawalApi.getList(query);tableData.value=r.data.records;total.value=r.data.total}finally{loading.value=false}}
async function handleApprove(row){await withdrawalApi.approve(row.id,{remark:'已打款，请注意查收'});ElMessage.success('审核通过，打款成功');loadData()}
function showReject(row){currentId.value=row.id;rejectRemark.value='';rejectVisible.value=true}
async function confirmReject(){await withdrawalApi.reject(currentId.value,{remark:rejectRemark.value});ElMessage.success('已驳回');rejectVisible.value=false;loadData()}
</script>
<style scoped>
.page-container{background:#fff;border-radius:12px;padding:24px;min-height:calc(100vh - 140px)}
.search-bar{display:flex;gap:12px;margin-bottom:20px}
.pagination{display:flex;justify-content:flex-end;margin-top:20px}
</style>
