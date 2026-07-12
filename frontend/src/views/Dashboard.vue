<template>
  <div class="dashboard">
    <!-- 顶部统计卡片 -->
    <div class="stat-cards">
      <div v-for="card in statCards" :key="card.key" class="stat-card" :style="{ '--card-color': card.color }">
        <div class="card-icon">{{ card.icon }}</div>
        <div class="card-info">
          <div class="card-value">{{ overview[card.key] ?? '--' }}</div>
          <div class="card-label">{{ card.label }}</div>
        </div>
        <div class="card-trend">{{ card.desc }}</div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-row">
      <!-- 订单趋势折线图 -->
      <div class="chart-card chart-large">
        <div class="chart-title">📈 近30天订单趋势</div>
        <div ref="orderTrendChart" class="chart-container"></div>
      </div>

      <!-- 服务类型分布饼图 -->
      <div class="chart-card chart-small">
        <div class="chart-title">🎯 服务类型分布</div>
        <div ref="pieChart" class="chart-container"></div>
      </div>
    </div>

    <div class="charts-row">
      <!-- 用户增长柱状图 -->
      <div class="chart-card chart-medium">
        <div class="chart-title">👥 近6个月用户增长</div>
        <div ref="userGrowthChart" class="chart-container"></div>
      </div>

      <!-- 收益趋势折线图 -->
      <div class="chart-card chart-medium">
        <div class="chart-title">💰 近6个月收益统计</div>
        <div ref="revenueChart" class="chart-container"></div>
      </div>
    </div>

    <!-- 跑腿员排行榜 -->
    <div class="chart-card">
      <div class="chart-title">🏆 跑腿员业绩排行 TOP10</div>
      <el-table 
        :data="ranking" 
        stripe 
        style="width: 100%"
        :header-cell-style="{ background: '#fdf0f0', color: '#333' }"
      >
        <el-table-column label="排名" width="80" align="center">
          <template #default="{ $index }">
            <span :class="['rank-badge', $index < 3 ? 'rank-top' : '']">
              {{ $index < 3 ? ['🥇','🥈','🥉'][$index] : $index + 1 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="头像" width="80" align="center">
          <template #default="{ row }">
            <el-avatar :size="36" :src="row.avatar">
              <span>{{ row.courierName?.charAt(0) }}</span>
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="courierName" label="跑腿员" min-width="100" />
        <el-table-column prop="orderCount" label="接单量" width="100" align="center" />
        <el-table-column prop="completedCount" label="完成量" width="100" align="center" />
        <el-table-column label="完成率" width="120" align="center">
          <template #default="{ row }">
            <el-progress 
              :percentage="row.completeRate" 
              :color="row.completeRate >= 90 ? '#27AE60' : row.completeRate >= 70 ? '#F39C12' : '#E74C3C'"
              :stroke-width="8"
            />
          </template>
        </el-table-column>
        <el-table-column label="好评率" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.goodRate >= 90 ? 'success' : row.goodRate >= 70 ? 'warning' : 'danger'">
              {{ row.goodRate }}%
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalEarnings" label="总收益(元)" width="120" align="center">
          <template #default="{ row }">
            <span style="color: #E74C3C; font-weight: 600;">¥{{ row.totalEarnings?.toFixed(2) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { statisticsApi } from '@/api/index.js'

// 统计卡片配置
const statCards = [
  { key: 'totalOrders', icon: '📋', label: '总订单数', desc: '全部历史订单', color: '#E74C3C' },
  { key: 'completedOrders', icon: '✅', label: '已完成订单', desc: '成功交付数量', color: '#27AE60' },
  { key: 'ongoingOrders', icon: '🚀', label: '进行中订单', desc: '实时进行中', color: '#F39C12' },
  { key: 'totalStudents', icon: '🎓', label: '学生用户', desc: '注册学生总数', color: '#8E44AD' },
  { key: 'totalCouriers', icon: '🏃', label: '认证跑腿员', desc: '在职跑腿员', color: '#2980B9' },
  { key: 'todayOrders', icon: '📅', label: '今日新增订单', desc: '今日发布量', color: '#16A085' },
  { key: 'todayUsers', icon: '👤', label: '今日新增用户', desc: '今日注册量', color: '#D35400' },
  { key: 'totalCommission', icon: '💵', label: '平台总佣金', desc: '(5%佣金)', color: '#C0392B' }
]

const overview = ref({})
const ranking = ref([])

// ECharts图表引用（DOM容器）
const orderTrendChart = ref()
const pieChart = ref()
const userGrowthChart = ref()
const revenueChart = ref()
// ECharts实例引用，用于销毁
const chartInstances = []

// 通用颜色配置（非AI蓝色，使用暖色系）
const colors = ['#E74C3C', '#F39C12', '#27AE60', '#8E44AD', '#D35400', '#16A085']

onMounted(async () => {
  await loadData()
})

// 组件卸载时销毁所有ECharts实例，防止内存泄漏
onUnmounted(() => {
  chartInstances.forEach(c => c && c.dispose())
})

const loadData = async () => {
  try {
    // 加载概览数据
    const overviewRes = await statisticsApi.getOverview()
    overview.value = overviewRes.data

    // 加载排行榜
    const rankingRes = await statisticsApi.getCourierRanking()
    ranking.value = rankingRes.data

    // 初始化图表
    await nextTick()
    await initCharts()
  } catch (e) {
    console.error('加载数据失败', e)
  }
}

const initCharts = async () => {
  // 1. 订单趋势折线图
  try {
    const trendRes = await statisticsApi.getOrderTrend()
    const trendData = trendRes.data
    const chart1 = echarts.init(orderTrendChart.value)
    chartInstances.push(chart1)
    chart1.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 20, top: 30, bottom: 30 },
      xAxis: { type: 'category', data: trendData.dates, axisLine: { lineStyle: { color: '#ddd' } } },
      yAxis: { type: 'value', axisLine: { lineStyle: { color: '#ddd' } }, splitLine: { lineStyle: { color: '#f5f5f5' } } },
      series: [{
        data: trendData.counts,
        type: 'line',
        smooth: true,
        lineStyle: { color: '#E74C3C', width: 3 },
        itemStyle: { color: '#E74C3C' },
        areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(231,76,60,0.3)' }, { offset: 1, color: 'rgba(231,76,60,0.05)' }] } }
      }]
    })
  } catch (e) {}

  // 2. 服务类型饼图
  try {
    const pieRes = await statisticsApi.getServiceTypeDistribution()
    const chart2 = echarts.init(pieChart.value)
    chartInstances.push(chart2)
    chart2.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { bottom: 0, left: 'center' },
      series: [{
        type: 'pie',
        radius: ['40%', '65%'],
        center: ['50%', '45%'],
        data: pieRes.data.map((item, i) => ({ ...item, itemStyle: { color: colors[i] } })),
        label: { formatter: '{b}\n{d}%' }
      }]
    })
  } catch (e) {}

  // 3. 用户增长柱状图
  try {
    const growthRes = await statisticsApi.getUserGrowth()
    const growthData = growthRes.data
    const chart3 = echarts.init(userGrowthChart.value)
    chartInstances.push(chart3)
    chart3.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['学生', '跑腿员'], top: 0 },
      grid: { left: 40, right: 20, top: 40, bottom: 30 },
      xAxis: { type: 'category', data: growthData.months },
      yAxis: { type: 'value', splitLine: { lineStyle: { color: '#f5f5f5' } } },
      series: [
        { name: '学生', type: 'bar', data: growthData.students, itemStyle: { color: '#E74C3C', borderRadius: [4, 4, 0, 0] } },
        { name: '跑腿员', type: 'bar', data: growthData.couriers, itemStyle: { color: '#F39C12', borderRadius: [4, 4, 0, 0] } }
      ]
    })
  } catch (e) {}

  // 4. 收益趋势
  try {
    const revenueRes = await statisticsApi.getRevenueTrend()
    const revenueData = revenueRes.data
    const chart4 = echarts.init(revenueChart.value)
    chartInstances.push(chart4)
    chart4.setOption({
      tooltip: { trigger: 'axis', formatter: '{b}: ¥{c}' },
      grid: { left: 60, right: 20, top: 30, bottom: 30 },
      xAxis: { type: 'category', data: revenueData.months },
      yAxis: { type: 'value', axisLabel: { formatter: '¥{value}' }, splitLine: { lineStyle: { color: '#f5f5f5' } } },
      series: [{
        data: revenueData.revenues,
        type: 'line',
        smooth: true,
        lineStyle: { color: '#27AE60', width: 3 },
        itemStyle: { color: '#27AE60' },
        areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(39,174,96,0.3)' }, { offset: 1, color: 'rgba(39,174,96,0.05)' }] } }
      }]
    })
  } catch (e) {}
}
</script>

<style scoped>
.dashboard {
  max-width: 100%;
}

/* 统计卡片 */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  border-left: 4px solid var(--card-color);
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.12);
}

.card-icon {
  font-size: 36px;
  flex-shrink: 0;
}

.card-info {
  flex: 1;
}

.card-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--card-color);
  line-height: 1.2;
}

.card-label {
  font-size: 13px;
  color: #666;
  margin-top: 4px;
}

.card-trend {
  font-size: 11px;
  color: #999;
}

/* 图表区域 */
.charts-row {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.chart-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  flex: 1;
}

.chart-large {
  flex: 2;
}

.chart-small {
  flex: 1;
}

.chart-medium {
  flex: 1;
}

.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #fdf0f0;
}

.chart-container {
  height: 280px;
  width: 100%;
}

/* 排行榜 */
.rank-badge {
  font-size: 18px;
}

.rank-top {
  font-size: 22px;
}

@media (max-width: 1200px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
