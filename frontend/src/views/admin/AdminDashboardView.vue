<script setup>
import SummaryCard from '@/components/SummaryCard.vue'
import { adminSummary, currentParkingList } from '@/mock/data'
</script>

<template>
  <div class="page-stack">
    <div class="summary-grid">
      <SummaryCard
        v-for="item in adminSummary"
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
            <p>实时概览</p>
            <h3>当前停放车辆</h3>
          </div>
          <el-tag type="warning">骨架数据</el-tag>
        </div>
      </template>
      <el-table :data="currentParkingList" stripe>
        <el-table-column prop="plateNumber" label="车牌号" />
        <el-table-column prop="ownerName" label="车主" />
        <el-table-column prop="spaceCode" label="车位" />
        <el-table-column prop="entryTime" label="入场时间" />
        <el-table-column prop="durationMinutes" label="停车时长(分钟)" />
        <el-table-column prop="finalAmount" label="当前费用(元)" />
      </el-table>
    </el-card>
  </div>
</template>
