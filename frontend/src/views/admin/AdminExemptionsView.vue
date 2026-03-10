<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { exemptions } from '@/mock/data'
import { patternRule, positiveNumberRule, recordIdPattern, requiredRule } from '@/utils/validators'

const formRef = ref()
const form = reactive({
  recordId: '',
  exemptionType: 'FULL',
  exemptionAmount: 0,
  reason: ''
})

const rules = {
  recordId: [
    requiredRule('请输入停车记录编号'),
    patternRule(recordIdPattern, '停车记录编号格式应为 R1001 这类形式')
  ],
  exemptionType: [requiredRule('请选择豁免类型')],
  exemptionAmount: [
    {
      validator: (_, value, callback) => {
        if (form.exemptionType === 'PARTIAL' && Number(value) <= 0) {
          callback(new Error('部分减免时，减免金额必须大于 0'))
          return
        }
        if (Number(value) < 0) {
          callback(new Error('减免金额不能小于 0'))
          return
        }
        callback()
      },
      trigger: ['blur', 'change']
    }
  ],
  reason: [
    requiredRule('请输入豁免原因'),
    {
      min: 4,
      max: 50,
      message: '豁免原因长度需在 4-50 个字符之间',
      trigger: ['blur', 'change']
    }
  ]
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }
  ElMessage.success(`已提交豁免申请：${form.recordId || '未填写记录号'}`)
}
</script>

<template>
  <div class="dual-grid">
    <el-card shadow="never" class="panel-card">
      <template #header><h3>新增费用豁免</h3></template>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="停车记录编号" prop="recordId">
          <el-input v-model.trim="form.recordId" maxlength="12" placeholder="例如 R1001" />
        </el-form-item>
        <el-form-item label="豁免类型" prop="exemptionType">
          <el-radio-group v-model="form.exemptionType">
            <el-radio-button label="FULL">全额豁免</el-radio-button>
            <el-radio-button label="PARTIAL">部分减免</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="豁免金额" prop="exemptionAmount">
          <el-input-number v-model="form.exemptionAmount" :min="0" />
        </el-form-item>
        <el-form-item label="原因" prop="reason">
          <el-input v-model.trim="form.reason" maxlength="50" show-word-limit type="textarea" :rows="3" />
        </el-form-item>
        <el-button type="primary" @click="handleSubmit">提交处理</el-button>
      </el-form>
    </el-card>

    <el-card shadow="never" class="panel-card">
      <template #header><h3>豁免记录</h3></template>
      <el-table :data="exemptions" stripe>
        <el-table-column prop="recordId" label="记录编号" />
        <el-table-column prop="plateNumber" label="车牌号" />
        <el-table-column prop="type" label="类型" />
        <el-table-column prop="amount" label="金额" />
        <el-table-column prop="reason" label="原因" />
      </el-table>
    </el-card>
  </div>
</template>
