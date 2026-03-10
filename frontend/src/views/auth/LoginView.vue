<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useSessionStore } from '@/stores/session'

const router = useRouter()
const sessionStore = useSessionStore()
const activeRole = ref('ADMIN')

const form = reactive({
  username: '',
  password: ''
})

function handleLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  const payload = activeRole.value === 'ADMIN'
    ? {
        token: 'admin-token-demo',
        role: 'ADMIN',
        username: form.username,
        displayName: '系统管理员'
      }
    : {
        token: 'user-token-demo',
        role: 'USER',
        username: form.username,
        displayName: form.username
      }

  sessionStore.login(payload)
  ElMessage.success('登录成功，当前为页面骨架演示')
  router.push(activeRole.value === 'ADMIN' ? '/admin/dashboard' : '/user/home')
}
</script>

<template>
  <div class="auth-screen">
    <div class="auth-panel">
      <div class="auth-panel__intro">
        <p class="auth-panel__eyebrow">Vue 3 + Spring Boot</p>
        <h1>停车场管理系统</h1>
        <p>当前版本已经完成双角色页面骨架，后续可直接接入后端接口和远程 Redis。</p>
      </div>

      <el-card shadow="never" class="auth-card">
        <el-segmented v-model="activeRole" :options="['ADMIN', 'USER']" block />
        <el-form class="auth-form" label-position="top">
          <el-form-item :label="activeRole === 'ADMIN' ? '管理员账号' : '用户账号'">
            <el-input v-model="form.username" placeholder="请输入账号" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" show-password placeholder="请输入密码" />
          </el-form-item>
          <el-button type="primary" size="large" class="block-button" @click="handleLogin">
            登录系统
          </el-button>
          <el-button link @click="router.push('/register')">没有账号？前往注册</el-button>
        </el-form>
      </el-card>
    </div>
  </div>
</template>
