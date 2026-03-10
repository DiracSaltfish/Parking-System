<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { userVehicles } from '@/mock/data'

const vehicles = ref([...userVehicles])
const form = reactive({
  plateNumber: '',
  isPrimary: true
})

function addVehicle() {
  if (!form.plateNumber) {
    ElMessage.warning('请输入车牌号')
    return
  }

  vehicles.value.unshift({
    vehicleId: `V${Date.now()}`,
    plateNumber: form.plateNumber,
    isPrimary: form.isPrimary,
    bindTime: '待接入后端'
  })

  form.plateNumber = ''
  form.isPrimary = true
  ElMessage.success('车辆已加入本地页面状态')
}
</script>

<template>
  <div class="dual-grid">
    <el-card shadow="never" class="panel-card">
      <template #header><h3>已绑定车辆</h3></template>
      <el-table :data="vehicles" stripe>
        <el-table-column prop="plateNumber" label="车牌号" />
        <el-table-column label="主车牌">
          <template #default="{ row }">{{ row.isPrimary ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column prop="bindTime" label="绑定时间" />
      </el-table>
    </el-card>

    <el-card shadow="never" class="panel-card">
      <template #header><h3>绑定新车牌</h3></template>
      <el-form label-position="top">
        <el-form-item label="车牌号">
          <el-input v-model="form.plateNumber" placeholder="例如 粤B12345" />
        </el-form-item>
        <el-form-item label="是否主车牌">
          <el-switch v-model="form.isPrimary" />
        </el-form-item>
        <el-button type="primary" @click="addVehicle">提交绑定</el-button>
      </el-form>
    </el-card>
  </div>
</template>
