<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { positiveNumberRule, requiredRule } from '@/utils/validators'

const formRef = ref()
const rule = reactive({
  freeMinutes: 30,
  pricePerHour: 5,
  dailyMaxAmount: 30
})

const rules = {
  freeMinutes: [requiredRule('请输入免费时长'), positiveNumberRule('免费时长不能小于 0')],
  pricePerHour: [requiredRule('请输入小时单价'), positiveNumberRule('小时单价不能小于 0')],
  dailyMaxAmount: [requiredRule('请输入日封顶金额'), positiveNumberRule('日封顶金额不能小于 0')]
}

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }
  if (Number(rule.dailyMaxAmount) < Number(rule.pricePerHour)) {
    ElMessage.warning('日封顶金额不能小于每小时单价')
    return
  }
  ElMessage.success('收费规则页面已就绪，后续接 /api/admin/fee-rule')
}
</script>

<template>
  <el-card shadow="never" class="panel-card">
    <template #header>
      <div class="panel-card__header">
        <div>
          <p>收费设置</p>
          <h3>固定计费规则</h3>
        </div>
      </div>
    </template>
    <el-form ref="formRef" :model="rule" :rules="rules" label-position="top" class="form-grid">
      <el-form-item label="免费时长（分钟）" prop="freeMinutes">
        <el-input-number v-model="rule.freeMinutes" :min="0" />
      </el-form-item>
      <el-form-item label="每小时单价（元）" prop="pricePerHour">
        <el-input-number v-model="rule.pricePerHour" :min="0" />
      </el-form-item>
      <el-form-item label="每日封顶（元）" prop="dailyMaxAmount">
        <el-input-number v-model="rule.dailyMaxAmount" :min="0" />
      </el-form-item>
    </el-form>
    <el-button type="primary" @click="handleSave">保存规则</el-button>
  </el-card>
</template>
