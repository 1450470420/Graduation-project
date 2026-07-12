<template>
  <div class="page-container">
    <div class="search-bar">
      <el-select v-model="query.status" placeholder="工单状态" clearable style="width:140px">
        <el-option label="待处理" :value="0"/><el-option label="处理中" :value="1"/><el-option label="已关闭" :value="2"/>
      </el-select>
      <el-button type="primary" @click="loadData">🔍 搜索</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" stripe style="width:100%" :header-cell-style="headerStyle">
      <el-table-column prop="username" label="提交用户" width="120"/>
      <el-table-column prop="realName" label="姓名" width="100"/>
      <el-table-column prop="title" label="工单标题" min-width="180"/>
      <el-table-column prop="content" label="问题描述" min-width="200" show-overflow-tooltip/>
      <el-table-column label="状态" width="90" align="center">
        <template #default="{row}"><el-tag :type="row.status===2?'info':row.status===1?'warning':'danger'">{{['待处理','处理中','已关闭'][row.status]}}</el-tag></template>
      </el-table-column>
      <el-table-column prop="reply" label="客服回复" min-width="200" show-overflow-tooltip/>
      <el-table-column prop="createTime" label="提交时间" width="170"/>
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template #default="{row}">
          <el-button type="primary" size="small" text @click="showReply(row)" v-if="row.status<2">回复</el-button>
          <el-button type="info" size="small" text @click="handleClose(row)" v-if="row.status<2">关闭</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination v-model:current-page="query.current" v-model:page-size="query.size" :total="total" layout="total,prev,pager,next" @change="loadData"/>
    </div>
    <el-dialog v-model="replyVisible" title="回复工单" width="500px">
      <div class="ticket-content">
        <div class="ticket-q"><strong>问题描述：</strong>{{currentTicket?.content}}</div>
      </div>
      <el-input v-model="replyContent" type="textarea" :rows="5" placeholder="请输入回复内容" style="margin-top:12px"/>
      <template #footer><el-button @click="replyVisible=false">取消</el-button><el-button type="primary" @click="confirmReply">发送回复</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import {ref,reactive,onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {ticketApi} from '@/api/index.js'
const loading=ref(false),tableData=ref([]),total=ref(0),replyVisible=ref(false),replyContent=ref(''),currentTicket=ref(null)
const query=reactive({current:1,size:10,status:null})
const headerStyle={background:'#fdf0f0',color:'#333',fontWeight:'600'}
onMounted(loadData)
async function loadData(){loading.value=true;try{const r=await ticketApi.getList(query);tableData.value=r.data.records;total.value=r.data.total}finally{loading.value=false}}
function showReply(row){currentTicket.value=row;replyContent.value=row.reply||'';replyVisible.value=true}
async function confirmReply(){await ticketApi.reply(currentTicket.value.id,{reply:replyContent.value});ElMessage.success('回复成功');replyVisible.value=false;loadData()}
async function handleClose(row){await ticketApi.close(row.id);ElMessage.success('工单已关闭');loadData()}
</script>
<style scoped>
.page-container{background:#fff;border-radius:12px;padding:24px;min-height:calc(100vh - 140px)}
.search-bar{display:flex;gap:12px;margin-bottom:20px}
.pagination{display:flex;justify-content:flex-end;margin-top:20px}
.ticket-content{background:#fdf0f0;padding:12px;border-radius:8px;font-size:14px;color:#555}
</style>
