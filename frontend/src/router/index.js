import { createRouter, createWebHistory } from 'vue-router'

// 路由懒加载：只有访问对应页面时才加载对应组件，提高首屏速度
const routes = [
  // 登录页（无需布局）
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '管理员登录' }
  },
  // 管理后台（需要登录认证）
  {
    path: '/',
    component: () => import('@/components/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/dashboard' },
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue'), meta: { title: '数据总览' } },
      // 用户管理
      { path: 'user/students', name: 'StudentList', component: () => import('@/views/user/StudentList.vue'), meta: { title: '学生管理' } },
      { path: 'user/couriers', name: 'CourierList', component: () => import('@/views/user/CourierList.vue'), meta: { title: '跑腿员管理' } },
      { path: 'user/applications', name: 'ApplicationList', component: () => import('@/views/user/ApplicationList.vue'), meta: { title: '入驻申请审核' } },
      { path: 'user/reputation', name: 'ReputationList', component: () => import('@/views/user/ReputationList.vue'), meta: { title: '信誉分管理' } },
      // 区域管理
      { path: 'area/areas', name: 'AreaList', component: () => import('@/views/area/AreaList.vue'), meta: { title: '区域管理' } },
      { path: 'area/pickup-points', name: 'PickupPointList', component: () => import('@/views/area/PickupPointList.vue'), meta: { title: '取件点管理' } },
      // 订单管理
      { path: 'order/list', name: 'OrderList', component: () => import('@/views/order/OrderList.vue'), meta: { title: '订单管理' } },
      { path: 'order/refunds', name: 'RefundList', component: () => import('@/views/order/RefundList.vue'), meta: { title: '退款管理' } },
      { path: 'order/complaints', name: 'ComplaintList', component: () => import('@/views/order/ComplaintList.vue'), meta: { title: '投诉管理' } },
      // 提现管理
      { path: 'withdrawal/list', name: 'WithdrawalList', component: () => import('@/views/withdrawal/WithdrawalList.vue'), meta: { title: '提现管理' } },
      // 公告管理
      { path: 'announcement/list', name: 'AnnouncementList', component: () => import('@/views/announcement/AnnouncementList.vue'), meta: { title: '公告管理' } },
      // 客服工单
      { path: 'ticket/list', name: 'TicketList', component: () => import('@/views/ticket/TicketList.vue'), meta: { title: '客服工单' } },
      // 数据统计
      { path: 'statistics/index', name: 'Statistics', component: () => import('@/views/statistics/Statistics.vue'), meta: { title: '数据统计' } },
      // 系统管理
      { path: 'system/configs', name: 'ConfigList', component: () => import('@/views/system/ConfigList.vue'), meta: { title: '系统配置' } },
      { path: 'system/logs', name: 'LogList', component: () => import('@/views/system/LogList.vue'), meta: { title: '操作日志' } }
    ]
  },
  // 默认重定向到登录页
  { path: '/:pathMatch(.*)*', redirect: '/login' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局路由守卫：验证登录状态
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 校园跑腿管理系统`
  }
  
  // 需要认证的页面检查登录状态
  if (to.meta.requiresAuth) {
    const userInfo = sessionStorage.getItem('adminInfo')
    if (!userInfo) {
      next('/login')
      return
    }
  }
  next()
})

export default router
