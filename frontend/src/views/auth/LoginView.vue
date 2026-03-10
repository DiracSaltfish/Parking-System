<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useSessionStore } from '@/stores/session'
import { adminLogin, userLogin } from '@/api/auth'
import { passwordPattern, patternRule, requiredRule, usernamePattern } from '@/utils/validators'

const router = useRouter()
const sessionStore = useSessionStore()
const formRef = ref()

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    requiredRule('请输入账号'),
    patternRule(usernamePattern, '账号仅支持 4-16 位字母、数字或下划线')
  ],
  password: [
    requiredRule('请输入密码'),
    patternRule(passwordPattern, '密码需为 6-20 位，且至少包含字母和数字')
  ]
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }

  try {
    const api = form.username === 'admin' ? adminLogin : userLogin
    const { data } = await api({ ...form })
    sessionStore.login(data.data)
    ElMessage.success('登录成功')
    router.push(data.data?.role === 'ADMIN' ? '/admin/dashboard' : '/user/home')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '登录失败，请确认后端是否启动')
  }
}
</script>

<template>
  <div class="auth-screen">
    <div class="auth-panel">
      <div class="auth-panel__intro">
        <p class="auth-panel__eyebrow">Vue 3 + Spring Boot</p>
        <h1>停车场管理系统</h1>
        <p>当前版本优先打通用户主流程，管理员与用户登录都已切到真实接口。</p>
      </div>

      <el-card shadow="never" class="auth-card">
        <el-form ref="formRef" :model="form" :rules="rules" class="auth-form" label-position="top">
          <el-alert
            title="登录规则：账号为 admin 时进入管理员端，其他账号进入用户端"
            type="info"
            :closable="false"
            show-icon
          />
          <el-form-item label="账号" prop="username">
            <el-input v-model.trim="form.username" maxlength="16" placeholder="请输入账号" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model.trim="form.password" maxlength="20" show-password placeholder="请输入密码" />
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
