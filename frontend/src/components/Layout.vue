<template>
  <div class="layout-container">
    <!-- 左侧菜单栏（独立滚动） -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <!-- Logo区域 -->
      <div class="sidebar-logo">
        <span class="logo-icon">🏃</span>
        <span class="logo-text" v-show="!isCollapsed">校园跑腿</span>
      </div>

      <!-- 导航菜单 -->
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :collapse-transition="false"
        router
        class="sidebar-menu"
        active-text-color="#fff"
        background-color="transparent"
        text-color="rgba(255,255,255,0.75)"
      >
        <!-- 数据总览 -->
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <template #title>数据总览</template>
        </el-menu-item>

        <!-- 用户管理 -->
        <el-sub-menu index="user">
          <template #title>
            <el-icon><UserFilled /></el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/user/students">
            <el-icon><Avatar /></el-icon>学生管理
          </el-menu-item>
          <el-menu-item index="/user/couriers">
            <el-icon><Van /></el-icon>跑腿员管理
          </el-menu-item>
          <el-menu-item index="/user/applications">
            <el-icon><DocumentChecked /></el-icon>入驻申请审核
          </el-menu-item>
          <el-menu-item index="/user/reputation">
            <el-icon><Star /></el-icon>信誉分管理
          </el-menu-item>
        </el-sub-menu>

        <!-- 区域管理 -->
        <el-sub-menu index="area">
          <template #title>
            <el-icon><MapLocation /></el-icon>
            <span>区域管理</span>
          </template>
          <el-menu-item index="/area/areas">
            <el-icon><Location /></el-icon>校园区域
          </el-menu-item>
          <el-menu-item index="/area/pickup-points">
            <el-icon><Box /></el-icon>取件点管理
          </el-menu-item>
        </el-sub-menu>

        <!-- 订单管理 -->
        <el-sub-menu index="order">
          <template #title>
            <el-icon><List /></el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/order/list">
            <el-icon><Document /></el-icon>全部订单
          </el-menu-item>
          <el-menu-item index="/order/refunds">
            <el-icon><RefreshRight /></el-icon>退款管理
          </el-menu-item>
          <el-menu-item index="/order/complaints">
            <el-icon><Warning /></el-icon>投诉管理
          </el-menu-item>
        </el-sub-menu>

        <!-- 提现管理 -->
        <el-menu-item index="/withdrawal/list">
          <el-icon><Wallet /></el-icon>
          <template #title>提现管理</template>
        </el-menu-item>

        <!-- 公告管理 -->
        <el-menu-item index="/announcement/list">
          <el-icon><Bell /></el-icon>
          <template #title>公告管理</template>
        </el-menu-item>

        <!-- 客服工单 -->
        <el-menu-item index="/ticket/list">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>客服工单</template>
        </el-menu-item>

        <!-- 数据统计 -->
        <el-menu-item index="/statistics/index">
          <el-icon><Histogram /></el-icon>
          <template #title>数据统计</template>
        </el-menu-item>

        <!-- 系统管理 -->
        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/configs">
            <el-icon><Tools /></el-icon>系统配置
          </el-menu-item>
          <el-menu-item index="/system/logs">
            <el-icon><Memo /></el-icon>操作日志
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </aside>

    <!-- 右侧内容区 -->
    <div class="main-container">
      <!-- 顶部导航栏 -->
      <header class="main-header">
        <!-- 左侧折叠按钮 -->
        <div class="header-left">
          <el-button 
            class="collapse-btn" 
            text 
            @click="isCollapsed = !isCollapsed"
          >
            <el-icon :size="20">
              <Expand v-if="isCollapsed" />
              <Fold v-else />
            </el-icon>
          </el-button>
          <!-- 面包屑导航 -->
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!-- 右侧用户信息 -->
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" class="user-avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="user-name">{{ adminInfo?.realName || adminInfo?.username || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区域（独立滚动，不影响侧边栏） -->
      <main class="main-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '@/api/index.js'

const route = useRoute()
const router = useRouter()

// 侧边栏折叠状态
const isCollapsed = ref(false)

// 当前激活的菜单项
const activeMenu = computed(() => route.path)

// 当前页面标题
const currentTitle = computed(() => route.meta.title || '')

// 管理员信息
const adminInfo = computed(() => {
  const info = sessionStorage.getItem('adminInfo')
  return info ? JSON.parse(info) : null
})

// 处理下拉菜单命令
const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await adminApi.logout()
      sessionStorage.removeItem('adminInfo')
      ElMessage.success('已退出登录')
      router.push('/login')
    } catch (e) {
      // 用户取消
    }
  }
}
</script>

<style scoped>
/* 整体布局：左右结构，各自独立滚动 */
.layout-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background: #f0f2f5;
}

/* 左侧菜单栏：固定宽度，独立滚动 */
.sidebar {
  width: 220px;
  min-height: 100vh;
  background: linear-gradient(180deg, #2c1a1a 0%, #3d2020 50%, #4a2424 100%);
  overflow-y: auto;
  overflow-x: hidden;
  transition: width 0.3s ease;
  flex-shrink: 0;
}

.sidebar.collapsed {
  width: 64px;
}

/* Logo区域 */
.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
}

.logo-icon {
  font-size: 28px;
  flex-shrink: 0;
}

.logo-text {
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 1px;
}

/* 侧边栏菜单 */
.sidebar-menu {
  border-right: none;
  padding: 8px 0;
}

.sidebar-menu :deep(.el-menu-item) {
  color: rgba(255,255,255,0.75) !important;
  height: 48px;
  line-height: 48px;
  margin: 2px 8px;
  border-radius: 8px;
  transition: all 0.2s;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(255,255,255,0.1) !important;
  color: #fff !important;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #C0392B, #E74C3C) !important;
  color: #fff !important;
}

.sidebar-menu :deep(.el-sub-menu__title) {
  color: rgba(255,255,255,0.75) !important;
  height: 48px;
  line-height: 48px;
  margin: 2px 8px;
  border-radius: 8px;
}

.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255,255,255,0.1) !important;
  color: #fff !important;
}

.sidebar-menu :deep(.el-menu--inline) {
  background: rgba(0,0,0,0.15) !important;
}

/* 右侧容器 */
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  overflow: hidden;
}

/* 顶部导航栏：固定高度，不参与滚动 */
.main-header {
  height: 56px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
  flex-shrink: 0;
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  color: #666;
  padding: 4px;
}

.breadcrumb {
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background 0.2s;
}

.user-info:hover {
  background: #f5f5f5;
}

.user-avatar {
  background: linear-gradient(135deg, #C0392B, #E74C3C);
}

.user-name {
  font-size: 14px;
  color: #333;
}

/* 内容区：独立滚动 */
.main-content {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 20px;
}
</style>
