<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getCurrentParking, getMyPayments, submitPayment } from '@/api/user'

const payMethod = ref('WECHAT')
const currentParking = ref(null)
const payments = ref([])

async function loadPayments() {
  try {
    const [currentRes, paymentRes] = await Promise.all([
      getCurrentParking(),
      getMyPayments()
    ])
    currentParking.value = currentRes.data.data?.active === false ? null : currentRes.data.data
    payments.value = paymentRes.data.data?.records || []
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '支付信息获取失败')
  }
}

async function handlePay() {
  if (!currentParking.value?.recordId) {
    ElMessage.warning('当前没有可支付订单')
    return
  }

  try {
    await submitPayment({
      recordId: currentParking.value.recordId,
      payMethod: payMethod.value
    })
    ElMessage.success(`已完成支付，方式：${payMethod.value}`)
    await loadPayments()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '支付失败')
  }
}

onMounted(loadPayments)
</script>

<template>
  <div class="dual-grid">
    <el-card shadow="never" class="panel-card">
      <template #header><h3>当前待缴订单</h3></template>
      <template v-if="currentParking">
        <div class="info-grid">
          <div class="info-pill"><span>记录编号</span><strong>{{ currentParking.recordId }}</strong></div>
          <div class="info-pill"><span>车牌号</span><strong>{{ currentParking.plateNumber }}</strong></div>
          <div class="info-pill"><span>待缴金额</span><strong>{{ currentParking.finalAmount }} 元</strong></div>
        </div>
        <el-divider />
        <el-radio-group v-model="payMethod">
          <el-radio-button label="WECHAT">微信</el-radio-button>
          <el-radio-button label="ALIPAY">支付宝</el-radio-button>
          <el-radio-button label="BALANCE">余额</el-radio-button>
        </el-radio-group>
        <div class="pay-actions">
          <el-button type="primary" :disabled="currentParking.payStatus !== 'UNPAID'" @click="handlePay">
            {{ currentParking.payStatus === 'UNPAID' ? '立即缴费' : '当前订单已支付/豁免' }}
          </el-button>
        </div>
      </template>
      <el-empty v-else description="当前没有待缴订单" />
    </el-card>

    <el-card shadow="never" class="panel-card">
      <template #header><h3>历史缴费记录</h3></template>
      <el-table :data="payments" stripe>
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
