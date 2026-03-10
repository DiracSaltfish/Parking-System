<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userRegister } from '@/api/auth'
import {
  passwordPattern,
  patternRule,
  phonePattern,
  requiredRule,
  usernamePattern
} from '@/utils/validators'

const router = useRouter()
const formRef = ref()
const form = reactive({
  username: '',
  realName: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const rules = {
  username: [
    requiredRule('请输入用户名'),
    patternRule(usernamePattern, '用户名仅支持 4-16 位字母、数字或下划线')
  ],
  realName: [
    requiredRule('请输入真实姓名'),
    {
      min: 2,
      max: 20,
      message: '真实姓名长度需在 2-20 个字符之间',
      trigger: ['blur', 'change']
    }
  ],
  phone: [
    requiredRule('请输入手机号'),
    patternRule(phonePattern, '请输入正确的 11 位手机号')
  ],
  password: [
    requiredRule('请输入密码'),
    patternRule(passwordPattern, '密码需为 6-20 位，且至少包含字母和数字')
  ],
  confirmPassword: [
    requiredRule('请再次输入密码'),
    {
      validator: (_, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次输入的密码不一致'))
          return
        }
        callback()
      },
      trigger: ['blur', 'change']
    }
  ]
}

async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }

  try {
    await userRegister({
      username: form.username.trim(),
      realName: form.realName.trim(),
      phone: form.phone.trim(),
      password: form.password
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '注册失败，请稍后重试')
  }
}
</script>

<template>
  <div class="auth-screen auth-screen--light">
    <el-card shadow="never" class="auth-card auth-card--wide">
      <div class="auth-card__header">
        <p>用户注册</p>
        <h1>创建你的停车账户</h1>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="auth-form auth-form--grid">
        <el-form-item label="用户名" prop="username">
          <el-input v-model.trim="form.username" maxlength="16" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model.trim="form.realName" maxlength="20" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model.trim="form.phone" maxlength="11" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model.trim="form.password" maxlength="20" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model.trim="form.confirmPassword" maxlength="20" show-password placeholder="请再次输入密码" />
        </el-form-item>
      </el-form>
      <div class="auth-actions">
        <el-button type="primary" @click="handleRegister">提交注册</el-button>
        <el-button @click="router.push('/login')">返回登录</el-button>
      </div>
    </el-card>
  </div>
</template>
