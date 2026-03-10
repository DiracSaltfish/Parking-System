<script setup>
import { parkingRecords } from '@/mock/data'
</script>

<template>
  <el-card shadow="never" class="panel-card">
    <template #header><h3>我的停车记录</h3></template>
    <el-table :data="parkingRecords" stripe>
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
