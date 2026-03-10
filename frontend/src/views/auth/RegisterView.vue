<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = reactive({
  username: '',
  realName: '',
  phone: '',
  password: ''
})

function handleRegister() {
  if (!form.username || !form.realName || !form.phone || !form.password) {
    ElMessage.warning('请补全注册信息')
    return
  }

  ElMessage.success('注册表单已就绪，后续接入 /api/auth/user/register 即可')
  router.push('/login')
}
</script>

<template>
  <div class="auth-screen auth-screen--light">
    <el-card shadow="never" class="auth-card auth-card--wide">
      <div class="auth-card__header">
        <p>用户注册</p>
        <h1>创建你的停车账户</h1>
      </div>
      <el-form label-position="top" class="auth-form auth-form--grid">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" show-password placeholder="请输入密码" />
        </el-form-item>
      </el-form>
      <div class="auth-actions">
        <el-button type="primary" @click="handleRegister">提交注册</el-button>
        <el-button @click="router.push('/login')">返回登录</el-button>
      </div>
    </el-card>
  </div>
</template>
