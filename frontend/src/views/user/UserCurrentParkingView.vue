<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getCurrentParking } from '@/api/user'

const currentParking = ref(null)

async function loadCurrentParking() {
  try {
    const { data } = await getCurrentParking()
    currentParking.value = data.data?.active === false ? null : data.data
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '当前停车信息获取失败')
  }
}

onMounted(loadCurrentParking)
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
    <div v-if="currentParking" class="info-grid">
      <div class="info-pill"><span>车牌号</span><strong>{{ currentParking.plateNumber }}</strong></div>
      <div class="info-pill"><span>车位</span><strong>{{ currentParking.spaceCode }}</strong></div>
      <div class="info-pill"><span>入场时间</span><strong>{{ currentParking.entryTime }}</strong></div>
      <div class="info-pill"><span>停车时长</span><strong>{{ currentParking.durationMinutes }} 分钟</strong></div>
      <div class="info-pill"><span>原始费用</span><strong>{{ currentParking.originalAmount }} 元</strong></div>
      <div class="info-pill"><span>最终费用</span><strong>{{ currentParking.finalAmount }} 元</strong></div>
    </div>
    <el-empty v-else description="当前没有在场车辆" />
  </el-card>
</template>
