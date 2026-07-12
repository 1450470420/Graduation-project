<template>
  <div class="page-container">
    <div class="search-bar">
      <el-input v-model="query.keyword" placeholder="搜索姓名/用户名/手机号" clearable style="width:280px" @keyup.enter="loadData"/>
      <el-select v-model="query.courierStatus" placeholder="跑腿员状态" clearable style="width:160px">
        <el-option label="待审核" :value="1"/><el-option label="已通过" :value="2"/><el-option label="已驳回" :value="3"/>
      </el-select>
      <el-select v-model="query.status" placeholder="账号状态" clearable style="width:130px">
        <el-option label="正常" :value="1"/><el-option label="封禁" :value="2"/>
      </el-select>
      <el-button type="primary" @click="loadData">🔍 搜索</el-button>
      <el-button @click="resetQuery">重置</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe style="width:100%" :header-cell-style="headerStyle">
      <el-table-column label="头像" width="70" align="center">
        <template #default="{row}"><el-avatar :size="36" :src="row.avatar"><span>{{row.realName?.charAt(0)}}</span></el-avatar></template>
      </el-table-column>
      <el-table-column prop="username" label="用户名" min-width="110"/>
      <el-table-column prop="realName" label="姓名" width="100"><template #default="{row}">{{row.realName||'未填写'}}</template></el-table-column>
      <el-table-column prop="phone" label="手机号" width="130"/>
      <el-table-column prop="studentId" label="学号" width="130"/>
      <el-table-column prop="reputation" label="信誉分" width="90" align="center">
        <template #default="{row}"><el-tag :type="row.reputation>=80?'success':row.reputation>=60?'warning':'danger'">{{row.reputation}}</el-tag></template>
      </el-table-column>
      <el-table-column prop="balance" label="余额(元)" width="100" align="center"><template #default="{row}">¥{{row.balance?.toFixed(2)}}</template></el-table-column>
      <el-table-column prop="totalEarnings" label="总收益(元)" width="110" align="center"><template #default="{row}">¥{{row.totalEarnings?.toFixed(2)}}</template></el-table-column>
      <el-table-column label="审核状态" width="100" align="center">
        <template #default="{row}">
          <el-tag :type="row.courierStatus===2?'success':row.courierStatus===1?'warning':row.courierStatus===3?'danger':'info'" size="small">
            {{['未申请','待审核','已通过','已驳回'][row.courierStatus]||'--'}}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="账号状态" width="90" align="center">
        <template #default="{row}"><el-tag :type="row.status===1?'success':'danger'">{{row.status===1?'正常':'封禁'}}</el-tag></template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="170"/>
      <el-table-column label="操作" width="140" align="center" fixed="right">
        <template #default="{row}">
          <el-button v-if="row.status===1" type="danger" size="small" text @click="handleBan(row)">封禁</el-button>
          <el-button v-else type="success" size="small" text @click="handleUnban(row)">解封</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="query.current" v-model:page-size="query.size" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" @change="loadData"/>
    </div>
  </div>
</template>
<script setup>
import {ref,reactive,onMounted} from 'vue'
import {ElMessage,ElMessageBox} from 'element-plus'
import {userApi} from '@/api/index.js'
const loading=ref(false),tableData=ref([]),total=ref(0)
const query=reactive({current:1,size:10,keyword:'',status:null,courierStatus:null})
const headerStyle={background:'#fdf0f0',color:'#333',fontWeight:'600'}
onMounted(loadData)
async function loadData(){loading.value=true;try{const r=await userApi.getCouriers(query);tableData.value=r.data.records;total.value=r.data.total}finally{loading.value=false}}
function resetQuery(){query.keyword='';query.status=null;query.courierStatus=null;query.current=1;loadData()}
async function handleBan(row){await ElMessageBox.confirm(`确定封禁【${row.realName||row.username}】吗？`,'警告',{type:'warning'});await userApi.banUser(row.id);ElMessage.success('封禁成功');loadData()}
async function handleUnban(row){await userApi.unbanUser(row.id);ElMessage.success('解封成功');loadData()}
</script>
<style scoped>
.page-container{background:#fff;border-radius:12px;padding:24px;min-height:calc(100vh - 140px)}
.search-bar{display:flex;gap:12px;flex-wrap:wrap;margin-bottom:20px}
.pagination{display:flex;justify-content:flex-end;margin-top:20px}
</style>
