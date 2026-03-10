<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'

const rule = reactive({
  freeMinutes: 30,
  pricePerHour: 5,
  dailyMaxAmount: 30
})

function handleSave() {
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
    <el-form label-position="top" class="form-grid">
      <el-form-item label="免费时长（分钟）">
        <el-input-number v-model="rule.freeMinutes" :min="0" />
      </el-form-item>
      <el-form-item label="每小时单价（元）">
        <el-input-number v-model="rule.pricePerHour" :min="0" />
      </el-form-item>
      <el-form-item label="每日封顶（元）">
        <el-input-number v-model="rule.dailyMaxAmount" :min="0" />
      </el-form-item>
    </el-form>
    <el-button type="primary" @click="handleSave">保存规则</el-button>
  </el-card>
</template>
