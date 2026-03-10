<script setup>
import { parkingRecords } from '@/mock/data'
</script>

<template>
  <el-card shadow="never" class="panel-card">
    <template #header>
      <div class="panel-card__header">
        <div>
          <p>订单查询</p>
          <h3>历史停车记录</h3>
        </div>
      </div>
    </template>
    <el-table :data="parkingRecords" stripe>
      <el-table-column prop="recordId" label="记录编号" />
      <el-table-column prop="plateNumber" label="车牌号" />
      <el-table-column prop="entryTime" label="入场时间" />
      <el-table-column prop="exitTime" label="出场时间" />
      <el-table-column prop="originalAmount" label="原始费用" />
      <el-table-column prop="discountAmount" label="减免金额" />
      <el-table-column prop="finalAmount" label="最终费用" />
      <el-table-column prop="payStatus" label="支付状态" />
    </el-table>
  </el-card>
</template>
