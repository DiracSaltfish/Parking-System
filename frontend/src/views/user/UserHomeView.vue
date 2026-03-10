<script setup>
import SummaryCard from '@/components/SummaryCard.vue'
import { userCurrentParking, userVehicles, userPayments } from '@/mock/data'

const summary = [
  { title: '绑定车辆', value: `${userVehicles.length} 辆`, tone: 'mint' },
  { title: '当前停车位', value: userCurrentParking.spaceCode, tone: 'ocean' },
  { title: '待缴金额', value: `${userCurrentParking.finalAmount} 元`, tone: 'ember' },
  { title: '历史支付', value: `${userPayments.length} 笔`, tone: 'sun' }
]
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
      <div class="info-grid">
        <div class="info-pill"><span>车牌号</span><strong>{{ userCurrentParking.plateNumber }}</strong></div>
        <div class="info-pill"><span>入场时间</span><strong>{{ userCurrentParking.entryTime }}</strong></div>
        <div class="info-pill"><span>停车时长</span><strong>{{ userCurrentParking.durationMinutes }} 分钟</strong></div>
        <div class="info-pill"><span>当前费用</span><strong>{{ userCurrentParking.finalAmount }} 元</strong></div>
      </div>
    </el-card>
  </div>
</template>
