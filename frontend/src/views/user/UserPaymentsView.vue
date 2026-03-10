<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { userCurrentParking, userPayments } from '@/mock/data'

const payMethod = ref('WECHAT')

function handlePay() {
  ElMessage.success(`已模拟支付 ${userCurrentParking.finalAmount} 元，方式：${payMethod.value}`)
}
</script>

<template>
  <div class="dual-grid">
    <el-card shadow="never" class="panel-card">
      <template #header><h3>当前待缴订单</h3></template>
      <div class="info-grid">
        <div class="info-pill"><span>记录编号</span><strong>{{ userCurrentParking.recordId }}</strong></div>
        <div class="info-pill"><span>车牌号</span><strong>{{ userCurrentParking.plateNumber }}</strong></div>
        <div class="info-pill"><span>待缴金额</span><strong>{{ userCurrentParking.finalAmount }} 元</strong></div>
      </div>
      <el-divider />
      <el-radio-group v-model="payMethod">
        <el-radio-button label="WECHAT">微信</el-radio-button>
        <el-radio-button label="ALIPAY">支付宝</el-radio-button>
        <el-radio-button label="BALANCE">余额</el-radio-button>
      </el-radio-group>
      <div class="pay-actions">
        <el-button type="primary" @click="handlePay">立即缴费</el-button>
      </div>
    </el-card>

    <el-card shadow="never" class="panel-card">
      <template #header><h3>历史缴费记录</h3></template>
      <el-table :data="userPayments" stripe>
        <el-table-column prop="paymentId" label="支付编号" />
        <el-table-column prop="recordId" label="记录编号" />
        <el-table-column prop="payMethod" label="支付方式" />
        <el-table-column prop="payAmount" label="支付金额" />
        <el-table-column prop="payStatus" label="支付状态" />
        <el-table-column prop="payTime" label="支付时间" />
      </el-table>
    </el-card>
  </div>
</template>
