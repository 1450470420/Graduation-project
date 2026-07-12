<template>
  <div class="page-container">
    <div class="search-bar">
      <el-button type="primary" @click="adjustVisible=true">➕ 手动调整信誉分</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe style="width:100%" :header-cell-style="headerStyle">
      <el-table-column prop="username" label="用户名" width="130"/>
      <el-table-column prop="realName" label="姓名" width="100"/>
      <el-table-column label="变动分数" width="100" align="center">
        <template #default="{row}">
          <span :style="row.type===1?'color:#27AE60;font-weight:600':'color:#E74C3C;font-weight:600'">
            {{row.type===1?'+':''}}{{row.changeScore}}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="reason" label="变动原因" min-width="200"/>
      <el-table-column label="类型" width="80" align="center">
        <template #default="{row}"><el-tag :type="row.type===1?'success':'danger'" size="small">{{row.type===1?'加分':'扣分'}}</el-tag></template>
      </el-table-column>
      <el-table-column prop="operator" label="操作者" width="100"/>
      <el-table-column prop="createTime" label="时间" width="170"/>
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="query.current" v-model:page-size="query.size" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" @change="loadData"/>
    </div>

    <el-dialog v-model="adjustVisible" title="手动调整信誉分" width="480px">
      <el-form :model="adjustForm" label-width="100px">
        <el-form-item label="用户ID"><el-input-number v-model="adjustForm.userId" :min="1"/></el-form-item>
        <el-form-item label="调整分数"><el-input-number v-model="adjustForm.score" :min="-100" :max="100"/><span style="margin-left:8px;color:#999">正数加分，负数扣分</span></el-form-item>
        <el-form-item label="原因"><el-input v-model="adjustForm.reason" placeholder="请填写调整原因"/></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustVisible=false">取消</el-button>
        <el-button type="primary" @click="confirmAdjust">确认调整</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import {ref,reactive,onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {reputationApi} from '@/api/index.js'
const loading=ref(false),tableData=ref([]),total=ref(0),adjustVisible=ref(false)
const query=reactive({current:1,size:10})
const adjustForm=reactive({userId:null,score:0,reason:''})
const headerStyle={background:'#fdf0f0',color:'#333',fontWeight:'600'}
onMounted(loadData)
async function loadData(){loading.value=true;try{const r=await reputationApi.getRecords(query);tableData.value=r.data.records;total.value=r.data.total}finally{loading.value=false}}
async function confirmAdjust(){if(!adjustForm.userId||!adjustForm.reason){ElMessage.warning('请填写完整');return}await reputationApi.adjust(adjustForm);ElMessage.success('调整成功');adjustVisible.value=false;loadData()}
</script>
<style scoped>
.page-container{background:#fff;border-radius:12px;padding:24px;min-height:calc(100vh - 140px)}
.search-bar{display:flex;gap:12px;flex-wrap:wrap;margin-bottom:20px}
.pagination{display:flex;justify-content:flex-end;margin-top:20px}
</style>
