<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { exemptions } from '@/mock/data'

const form = reactive({
  recordId: '',
  exemptionType: 'FULL',
  exemptionAmount: 0,
  reason: ''
})

function handleSubmit() {
  ElMessage.success(`已提交豁免申请：${form.recordId || '未填写记录号'}`)
}
</script>

<template>
  <div class="dual-grid">
    <el-card shadow="never" class="panel-card">
      <template #header><h3>新增费用豁免</h3></template>
      <el-form label-position="top">
        <el-form-item label="停车记录编号">
          <el-input v-model="form.recordId" placeholder="例如 R1001" />
        </el-form-item>
        <el-form-item label="豁免类型">
          <el-radio-group v-model="form.exemptionType">
            <el-radio-button label="FULL">全额豁免</el-radio-button>
            <el-radio-button label="PARTIAL">部分减免</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="豁免金额">
          <el-input-number v-model="form.exemptionAmount" :min="0" />
        </el-form-item>
        <el-form-item label="原因">
          <el-input v-model="form.reason" type="textarea" :rows="3" />
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
