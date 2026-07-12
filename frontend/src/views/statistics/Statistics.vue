<template>
  <div class="page-container">
    <div class="charts-section">
      <div class="section-title">📊 订单统计</div>
      <div class="charts-row">
        <div class="chart-box">
          <div class="chart-label">近30天订单趋势</div>
          <div ref="orderChart" style="height:300px"></div>
        </div>
        <div class="chart-box">
          <div class="chart-label">服务类型分布</div>
          <div ref="pieChart" style="height:300px"></div>
        </div>
      </div>
    </div>
    <div class="charts-section">
      <div class="section-title">👥 用户与收益统计</div>
      <div class="charts-row">
        <div class="chart-box">
          <div class="chart-label">近6个月用户增长</div>
          <div ref="growthChart" style="height:300px"></div>
        </div>
        <div class="chart-box">
          <div class="chart-label">近6个月收益趋势</div>
          <div ref="revenueChart" style="height:300px"></div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import {ref,onMounted,onUnmounted,nextTick} from 'vue'
import * as echarts from 'echarts'
import {statisticsApi} from '@/api/index.js'
const orderChart=ref(),pieChart=ref(),growthChart=ref(),revenueChart=ref()
const colors=['#E74C3C','#F39C12','#27AE60','#8E44AD','#D35400','#16A085']
// 存储实例用于组件卸载时销毁
const chartInstances = []
onMounted(async()=>{
  await nextTick()
  try{const r=await statisticsApi.getOrderTrend();const c=echarts.init(orderChart.value);chartInstances.push(c);c.setOption({tooltip:{trigger:'axis'},grid:{left:40,right:20,top:20,bottom:30},xAxis:{type:'category',data:r.data.dates},yAxis:{type:'value'},series:[{type:'line',smooth:true,data:r.data.counts,lineStyle:{color:'#E74C3C',width:3},itemStyle:{color:'#E74C3C'},areaStyle:{color:{type:'linear',x:0,y:0,x2:0,y2:1,colorStops:[{offset:0,color:'rgba(231,76,60,0.3)'},{offset:1,color:'rgba(231,76,60,0.05)'}]}}}]})}catch(e){}
  try{const r=await statisticsApi.getServiceTypeDistribution();const c=echarts.init(pieChart.value);chartInstances.push(c);c.setOption({tooltip:{trigger:'item'},series:[{type:'pie',radius:['40%','65%'],data:r.data.map((item,i)=>({...item,itemStyle:{color:colors[i]}})),label:{formatter:'{b}\n{d}%'}}]})}catch(e){}
  try{const r=await statisticsApi.getUserGrowth();const c=echarts.init(growthChart.value);chartInstances.push(c);c.setOption({tooltip:{trigger:'axis'},legend:{data:['学生','跑腿员'],top:0},grid:{left:40,right:20,top:40,bottom:30},xAxis:{type:'category',data:r.data.months},yAxis:{type:'value'},series:[{name:'学生',type:'bar',data:r.data.students,itemStyle:{color:'#E74C3C',borderRadius:[4,4,0,0]}},{name:'跑腿员',type:'bar',data:r.data.couriers,itemStyle:{color:'#F39C12',borderRadius:[4,4,0,0]}}]})}catch(e){}
  try{const r=await statisticsApi.getRevenueTrend();const c=echarts.init(revenueChart.value);chartInstances.push(c);c.setOption({tooltip:{trigger:'axis'},grid:{left:60,right:20,top:20,bottom:30},xAxis:{type:'category',data:r.data.months},yAxis:{type:'value',axisLabel:{formatter:'¥{value}'}},series:[{type:'line',smooth:true,data:r.data.revenues,lineStyle:{color:'#27AE60',width:3},itemStyle:{color:'#27AE60'}}]})}catch(e){}
})
// 页面卸载时销毁所有ECharts实例
onUnmounted(()=>{ chartInstances.forEach(c=>c&&c.dispose()) })
</script>
<style scoped>
.page-container{background:#fff;border-radius:12px;padding:24px}
.charts-section{margin-bottom:30px}
.section-title{font-size:16px;font-weight:700;color:#333;padding:10px 0;border-bottom:2px solid #fdf0f0;margin-bottom:20px}
.charts-row{display:flex;gap:20px}
.chart-box{flex:1;background:#fafafa;border-radius:8px;padding:16px}
.chart-label{font-size:14px;color:#666;margin-bottom:10px}
</style>
