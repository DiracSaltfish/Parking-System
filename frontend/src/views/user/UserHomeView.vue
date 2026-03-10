<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import SummaryCard from '@/components/SummaryCard.vue'
import { getCurrentParking, getMyPayments, getMyVehicles } from '@/api/user'

const vehicles = ref([])
const currentParking = ref(null)
const payments = ref([])

const summary = computed(() => [
  { title: '绑定车辆', value: `${vehicles.value.length} 辆`, tone: 'mint' },
  { title: '当前停车位', value: currentParking.value?.spaceCode || '暂无', tone: 'ocean' },
  { title: '待缴金额', value: `${currentParking.value?.finalAmount ?? 0} 元`, tone: 'ember' },
  { title: '历史支付', value: `${payments.value.length} 笔`, tone: 'sun' }
])

async function loadData() {
  try {
    const [vehicleRes, currentRes, paymentRes] = await Promise.all([
      getMyVehicles(),
      getCurrentParking(),
      getMyPayments()
    ])
    vehicles.value = vehicleRes.data.data || []
    currentParking.value = currentRes.data.data?.active === false ? null : currentRes.data.data
    payments.value = paymentRes.data.data?.records || []
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '用户首页数据加载失败')
  }
}

onMounted(loadData)
</script>

<template>
  <div class="page-stack">
    <div class="summary-grid">
      <SummaryCard
        v-for="item in summary"
        :key="item.title"
        :title="item.title"
        :value="item.value"
        :tone="item.tone"
      />
    </div>

    <el-card shadow="never" class="panel-card">
      <template #header>
        <div class="panel-card__header">
          <div>
            <p>我的停车状态</p>
            <h3>当前订单</h3>
          </div>
        </div>
      </template>
      <div v-if="currentParking" class="info-grid">
        <div class="info-pill"><span>车牌号</span><strong>{{ currentParking.plateNumber }}</strong></div>
        <div class="info-pill"><span>入场时间</span><strong>{{ currentParking.entryTime }}</strong></div>
        <div class="info-pill"><span>停车时长</span><strong>{{ currentParking.durationMinutes }} 分钟</strong></div>
        <div class="info-pill"><span>当前费用</span><strong>{{ currentParking.finalAmount }} 元</strong></div>
      </div>
      <el-empty v-else description="当前没有在场车辆" />
    </el-card>
  </div>
</template>
