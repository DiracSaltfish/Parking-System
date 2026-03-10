<script setup>
import { userCurrentParking } from '@/mock/data'
</script>

<template>
  <el-card shadow="never" class="panel-card">
    <template #header>
      <div class="panel-card__header">
        <div>
          <p>停车中</p>
          <h3>当前停车信息</h3>
        </div>
      </div>
    </template>
    <div class="info-grid">
      <div class="info-pill"><span>车牌号</span><strong>{{ userCurrentParking.plateNumber }}</strong></div>
      <div class="info-pill"><span>车位</span><strong>{{ userCurrentParking.spaceCode }}</strong></div>
      <div class="info-pill"><span>入场时间</span><strong>{{ userCurrentParking.entryTime }}</strong></div>
      <div class="info-pill"><span>停车时长</span><strong>{{ userCurrentParking.durationMinutes }} 分钟</strong></div>
      <div class="info-pill"><span>原始费用</span><strong>{{ userCurrentParking.originalAmount }} 元</strong></div>
      <div class="info-pill"><span>最终费用</span><strong>{{ userCurrentParking.finalAmount }} 元</strong></div>
    </div>
  </el-card>
</template>
