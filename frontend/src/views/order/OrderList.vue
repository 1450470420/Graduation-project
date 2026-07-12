<template>
  <div class="page-container">
    <div class="search-bar">
      <el-input v-model="query.keyword" placeholder="搜索订单号/物品信息" clearable style="width:220px"/>
      <el-select v-model="query.status" placeholder="订单状态" clearable style="width:130px">
        <el-option label="待接单" :value="0"/><el-option label="待取件" :value="1"/><el-option label="配送中" :value="2"/>
        <el-option label="已送达" :value="3"/><el-option label="已完成" :value="4"/><el-option label="已取消" :value="5"/>
      </el-select>
      <el-select v-model="query.serviceType" placeholder="服务类型" clearable style="width:120px">
        <el-option label="代取快递" value="代取快递"/><el-option label="代买" value="代买"/><el-option label="代送" value="代送"/>
      </el-select>
      <!-- 日期范围筛选 -->
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        style="width:240px"
        clearable
        @change="onDateChange"
      />
      <el-button type="primary" @click="loadData">🔍 搜索</el-button>
      <el-button @click="resetQuery">重置</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe style="width:100%" :header-cell-style="headerStyle">
      <el-table-column prop="orderNo" label="订单编号" width="180"/>
      <el-table-column prop="studentName" label="学生" width="100"/>
      <el-table-column prop="courierName" label="跑腿员" width="100"><template #default="{row}">{{row.courierName||'待接单'}}</template></el-table-column>
      <el-table-column prop="serviceType" label="服务类型" width="100" align="center"><template #default="{row}"><el-tag size="small">{{row.serviceType}}</el-tag></template></el-table-column>
      <el-table-column prop="pickupAddress" label="取件地址" min-width="150"/>
      <el-table-column prop="deliveryAddress" label="送件地址" min-width="150"/>
      <el-table-column prop="itemInfo" label="物品信息" min-width="150"/>
      <el-table-column prop="reward" label="赏金(元)" width="90" align="center"><template #default="{row}"><span style="color:#E74C3C;font-weight:600">¥{{row.reward?.toFixed(2)}}</span></template></el-table-column>
      <el-table-column label="状态" width="90" align="center">
        <template #default="{row}">
          <el-tag :type="['warning','','primary','success','success','danger'][row.status]" size="small">
            {{['待接单','待取件','配送中','已送达','已完成','已取消'][row.status]}}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="170"/>
      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template #default="{row}">
          <el-button type="primary" size="small" text @click="viewDetail(row)">详情</el-button>
          <el-button v-if="row.status<4&&row.status!==5" type="danger" size="small" text @click="handleCancel(row)">强制取消</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="query.current" v-model:page-size="query.size" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" @change="loadData"/>
    </div>

    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
      <el-descriptions :column="2" border v-if="detail">
        <el-descriptions-item label="订单编号" :span="2">{{detail.orderNo}}</el-descriptions-item>
        <el-descriptions-item label="服务类型">{{detail.serviceType}}</el-descriptions-item>
        <el-descriptions-item label="赏金">¥{{detail.reward?.toFixed(2)}}</el-descriptions-item>
        <el-descriptions-item label="取件地址">{{detail.pickupAddress}}</el-descriptions-item>
        <el-descriptions-item label="送件地址">{{detail.deliveryAddress}}</el-descriptions-item>
        <el-descriptions-item label="物品信息" :span="2">{{detail.itemInfo}}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{detail.note||'无'}}</el-descriptions-item>
        <el-descriptions-item label="学生">{{detail.studentName}} ({{detail.studentPhone}})</el-descriptions-item>
        <el-descriptions-item label="跑腿员">{{detail.courierName||'暂无'}} {{detail.courierPhone?`(${detail.courierPhone})`:''}}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{detail.createTime}}</el-descriptions-item>
        <el-descriptions-item label="接单时间">{{detail.grabTime||'--'}}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{detail.completeTime||'--'}}</el-descriptions-item>
        <el-descriptions-item label="取消原因">{{detail.cancelReason||'--'}}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>
<script setup>
import {ref,reactive,onMounted} from 'vue'
import {ElMessage,ElMessageBox} from 'element-plus'
import {orderApi} from '@/api/index.js'
const loading=ref(false),tableData=ref([]),total=ref(0),detailVisible=ref(false),detail=ref(null)
const dateRange=ref([])
const query=reactive({current:1,size:10,keyword:'',status:null,serviceType:'',startDate:'',endDate:''})
function onDateChange(val){
  query.startDate = val ? val[0] : ''
  query.endDate = val ? val[1] : ''
}
const headerStyle={background:'#fdf0f0',color:'#333',fontWeight:'600'}
onMounted(loadData)
async function loadData(){loading.value=true;try{const r=await orderApi.getList(query);tableData.value=r.data.records;total.value=r.data.total}finally{loading.value=false}}
function resetQuery(){query.keyword='';query.status=null;query.serviceType='';query.startDate='';query.endDate='';query.current=1;dateRange.value=[];loadData()}
async function viewDetail(row){const r=await orderApi.getDetail(row.id);detail.value=r.data;detailVisible.value=true}
async function handleCancel(row){
  try{
    const {value}=await ElMessageBox.prompt('请填写强制取消原因','强制取消订单',{confirmButtonText:'确定',cancelButtonText:'取消',inputPlaceholder:'原因...'})
    await orderApi.cancel(row.id,{reason:value})
    ElMessage.success('已取消')
    loadData()
  }catch(e){}
}
</script>
<style scoped>
.page-container{background:#fff;border-radius:12px;padding:24px;min-height:calc(100vh - 140px)}
.search-bar{display:flex;gap:12px;flex-wrap:wrap;margin-bottom:20px}
.pagination{display:flex;justify-content:flex-end;margin-top:20px}
</style>
