<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useSessionStore } from '@/stores/session'

const route = useRoute()
const router = useRouter()
const sessionStore = useSessionStore()

const adminNav = [
  { label: '仪表盘', path: '/admin/dashboard' },
  { label: '当前车辆', path: '/admin/parking/current' },
  { label: '历史记录', path: '/admin/parking/records' },
  { label: '车位管理', path: '/admin/spaces' },
  { label: '收费规则', path: '/admin/fee-rule' },
  { label: '费用豁免', path: '/admin/exemptions' }
]

const userNav = [
  { label: '用户首页', path: '/user/home' },
  { label: '我的车辆', path: '/user/vehicles' },
  { label: '当前停车', path: '/user/parking/current' },
  { label: '停车记录', path: '/user/parking/records' },
  { label: '在线缴费', path: '/user/payments' }
]

const navItems = computed(() => (sessionStore.profile?.role === 'ADMIN' ? adminNav : userNav))
const displayName = computed(() => sessionStore.profile?.displayName || '未登录')

function handleLogout() {
  sessionStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="app-shell">
    <aside class="app-shell__sidebar">
      <div class="brand-panel">
        <p class="brand-panel__eyebrow">Parking Suite</p>
        <h1>停车场管理系统</h1>
        <span>{{ sessionStore.profile?.role === 'ADMIN' ? '管理员端' : '用户端' }}</span>
      </div>
      <el-menu
        class="app-shell__menu"
        :default-active="route.path"
        router
        background-color="transparent"
        text-color="#d7e4db"
        active-text-color="#fff7d1"
      >
        <el-menu-item v-for="item in navItems" :key="item.path" :index="item.path">
          {{ item.label }}
        </el-menu-item>
      </el-menu>
    </aside>

    <main class="app-shell__main">
      <header class="top-bar">
        <div>
          <p class="top-bar__hint">当前页面</p>
          <h2>{{ route.meta.title || '控制台' }}</h2>
        </div>
        <div class="top-bar__actions">
          <el-tag round type="success">{{ displayName }}</el-tag>
          <el-button plain @click="handleLogout">退出登录</el-button>
        </div>
      </header>

      <section class="page-body">
        <router-view />
      </section>
    </main>
  </div>
</template>
