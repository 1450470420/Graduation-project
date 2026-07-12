<template>
  <div class="page-container">
    <div class="search-bar">
      <el-input v-model="query.keyword" placeholder="搜索姓名/手机号/学号" clearable style="width:250px" @keyup.enter="loadData"/>
      <el-select v-model="query.status" placeholder="审核状态" clearable style="width:140px">
        <el-option label="待审核" :value="0"/><el-option label="已通过" :value="1"/><el-option label="已驳回" :value="2"/>
      </el-select>
      <el-button type="primary" @click="loadData">🔍 搜索</el-button>
      <el-button @click="resetQuery">重置</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe style="width:100%" :header-cell-style="headerStyle">
      <el-table-column prop="username" label="用户名" width="120"/>
      <el-table-column prop="realName" label="姓名" width="100"/>
      <el-table-column prop="studentId" label="学号" width="130"/>
      <el-table-column prop="phone" label="手机号" width="130"/>
      <el-table-column prop="idCardNo" label="身份证号" width="160"/>
      <el-table-column prop="remark" label="申请备注" min-width="150"/>
      <el-table-column label="状态" width="90" align="center">
        <template #default="{row}">
          <el-tag :type="row.status===1?'success':row.status===0?'warning':'danger'">
            {{['待审核','已通过','已驳回'][row.status]}}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="rejectReason" label="驳回原因" min-width="150"/>
      <el-table-column prop="createTime" label="申请时间" width="170"/>
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template #default="{row}">
          <template v-if="row.status===0">
            <el-button type="success" size="small" text @click="handleApprove(row)">✅ 通过</el-button>
            <el-button type="danger" size="small" text @click="handleReject(row)">❌ 驳回</el-button>
          </template>
          <span v-else class="handled-text">已处理</span>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="query.current" v-model:page-size="query.size" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" @change="loadData"/>
    </div>

    <!-- 驳回对话框 -->
    <el-dialog v-model="rejectVisible" title="驳回申请" width="480px">
      <el-input v-model="rejectReason" type="textarea" :rows="4" placeholder="请填写驳回原因（必填）"/>
      <template #footer>
        <el-button @click="rejectVisible=false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import {ref,reactive,onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {applicationApi} from '@/api/index.js'
const loading=ref(false),tableData=ref([]),total=ref(0)
const rejectVisible=ref(false),rejectReason=ref(''),currentId=ref(null)
const query=reactive({current:1,size:10,keyword:'',status:null})
const headerStyle={background:'#fdf0f0',color:'#333',fontWeight:'600'}
onMounted(loadData)
async function loadData(){loading.value=true;try{const r=await applicationApi.getList(query);tableData.value=r.data.records;total.value=r.data.total}finally{loading.value=false}}
function resetQuery(){query.keyword='';query.status=null;query.current=1;loadData()}
async function handleApprove(row){await applicationApi.approve(row.id);ElMessage.success('已审核通过');loadData()}
function handleReject(row){currentId.value=row.id;rejectReason.value='';rejectVisible.value=true}
async function confirmReject(){if(!rejectReason.value.trim()){ElMessage.warning('请填写驳回原因');return}await applicationApi.reject(currentId.value,{reason:rejectReason.value});ElMessage.success('已驳回');rejectVisible.value=false;loadData()}
</script>
<style scoped>
.page-container{background:#fff;border-radius:12px;padding:24px;min-height:calc(100vh - 140px)}
.search-bar{display:flex;gap:12px;flex-wrap:wrap;margin-bottom:20px}
.pagination{display:flex;justify-content:flex-end;margin-top:20px}
.handled-text{color:#999;font-size:13px}
</style>
