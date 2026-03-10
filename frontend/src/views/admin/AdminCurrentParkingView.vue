<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { currentParkingList } from '@/mock/data'

const keyword = ref('')

function handleAction(action, row) {
  ElMessage.success(`${action}：${row.plateNumber}，后续接后端接口即可`)
}
</script>

<template>
  <div class="page-stack">
    <el-card shadow="never" class="panel-card">
      <div class="toolbar-row">
        <el-input v-model="keyword" placeholder="按车牌号搜索" clearable />
        <el-button type="primary">查询</el-button>
        <el-button>新增入场</el-button>
      </div>
      <el-table :data="currentParkingList" stripe>
        <el-table-column prop="plateNumber" label="车牌号" />
        <el-table-column prop="ownerName" label="车主" />
        <el-table-column prop="spaceCode" label="车位" />
        <el-table-column prop="entryTime" label="入场时间" />
        <el-table-column prop="durationMinutes" label="停车时长" />
        <el-table-column prop="payStatus" label="支付状态" />
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleAction('办理出场', row)">办理出场</el-button>
            <el-button link type="warning" @click="handleAction('费用豁免', row)">费用豁免</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
