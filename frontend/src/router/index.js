import { createRouter, createWebHistory } from 'vue-router'
const AppShell = () => import('@/components/AppShell.vue')
const LoginView = () => import('@/views/auth/LoginView.vue')
const RegisterView = () => import('@/views/auth/RegisterView.vue')
const AdminDashboardView = () => import('@/views/admin/AdminDashboardView.vue')
const AdminCurrentParkingView = () => import('@/views/admin/AdminCurrentParkingView.vue')
const AdminRecordsView = () => import('@/views/admin/AdminRecordsView.vue')
const AdminSpacesView = () => import('@/views/admin/AdminSpacesView.vue')
const AdminFeeRuleView = () => import('@/views/admin/AdminFeeRuleView.vue')
const AdminExemptionsView = () => import('@/views/admin/AdminExemptionsView.vue')
const UserHomeView = () => import('@/views/user/UserHomeView.vue')
const UserVehiclesView = () => import('@/views/user/UserVehiclesView.vue')
const UserCurrentParkingView = () => import('@/views/user/UserCurrentParkingView.vue')
const UserRecordsView = () => import('@/views/user/UserRecordsView.vue')
const UserPaymentsView = () => import('@/views/user/UserPaymentsView.vue')

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    component: LoginView,
    meta: { public: true }
  },
  {
    path: '/register',
    component: RegisterView,
    meta: { public: true }
  },
  {
    path: '/admin',
    component: AppShell,
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', component: AdminDashboardView, meta: { title: '仪表盘' } },
      { path: 'parking/current', component: AdminCurrentParkingView, meta: { title: '当前车辆' } },
      { path: 'parking/records', component: AdminRecordsView, meta: { title: '历史记录' } },
      { path: 'spaces', component: AdminSpacesView, meta: { title: '车位管理' } },
      { path: 'fee-rule', component: AdminFeeRuleView, meta: { title: '收费规则' } },
      { path: 'exemptions', component: AdminExemptionsView, meta: { title: '费用豁免' } }
    ]
  },
  {
    path: '/user',
    component: AppShell,
    meta: { requiresAuth: true, role: 'USER' },
    children: [
      { path: '', redirect: '/user/home' },
      { path: 'home', component: UserHomeView, meta: { title: '用户首页' } },
      { path: 'vehicles', component: UserVehiclesView, meta: { title: '我的车辆' } },
      { path: 'parking/current', component: UserCurrentParkingView, meta: { title: '当前停车' } },
      { path: 'parking/records', component: UserRecordsView, meta: { title: '停车记录' } },
      { path: 'payments', component: UserPaymentsView, meta: { title: '在线缴费' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.meta.public) {
    return true
  }

  const session = JSON.parse(localStorage.getItem('parking-session') || 'null')

  if (!session) {
    return '/login'
  }

  if (to.meta.role && to.meta.role !== session.role) {
    return session.role === 'ADMIN' ? '/admin/dashboard' : '/user/home'
  }

  return true
})

export default router
