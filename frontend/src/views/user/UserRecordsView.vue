<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyParkingRecords } from '@/api/user'

const records = ref([])

async function loadRecords() {
  try {
    const { data } = await getMyParkingRecords()
    records.value = data.data?.records || []
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '停车记录获取失败')
  }
}

onMounted(loadRecords)
</script>

<template>
  <el-card shadow="never" class="panel-card">
    <template #header><h3>我的停车记录</h3></template>
    <el-table :data="records" stripe>
      <el-table-column prop="recordId" label="记录编号" />
      <el-table-column prop="plateNumber" label="车牌号" />
      <el-table-column prop="entryTime" label="入场时间" />
      <el-table-column prop="exitTime" label="出场时间" />
      <el-table-column prop="durationMinutes" label="停车时长" />
      <el-table-column prop="finalAmount" label="实际支付" />
      <el-table-column prop="payStatus" label="状态" />
    </el-table>
  </el-card>
</template>
