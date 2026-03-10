<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { bindVehicle, deleteVehicle, getMyVehicles } from '@/api/user'
import { normalizePlateNumber, patternRule, plateNumberPattern, requiredRule } from '@/utils/validators'

const vehicles = ref([])
const formRef = ref()
const form = reactive({
  plateNumber: '',
  isPrimary: true
})

const rules = {
  plateNumber: [
    requiredRule('请输入车牌号'),
    patternRule(plateNumberPattern, '请输入正确格式的车牌号，例如 粤B12345')
  ]
}

async function loadVehicles() {
  try {
    const { data } = await getMyVehicles()
    vehicles.value = data.data || []
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '获取车辆失败')
  }
}

async function addVehicle() {
  form.plateNumber = normalizePlateNumber(form.plateNumber)
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }

  try {
    const { data } = await bindVehicle({ ...form })
    vehicles.value.unshift(data.data)
    form.plateNumber = ''
    form.isPrimary = true
    ElMessage.success('车辆绑定成功')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '车辆绑定失败')
  }
}

async function removeVehicle(vehicleId) {
  try {
    await deleteVehicle(vehicleId)
    vehicles.value = vehicles.value.filter((item) => item.vehicleId !== vehicleId)
    ElMessage.success('车辆已删除')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '删除失败')
  }
}

onMounted(loadVehicles)
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
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button link type="danger" @click="removeVehicle(row.vehicleId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card shadow="never" class="panel-card">
      <template #header><h3>绑定新车牌</h3></template>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="车牌号" prop="plateNumber">
          <el-input
            v-model.trim="form.plateNumber"
            maxlength="8"
            placeholder="例如 粤B12345"
            @blur="form.plateNumber = normalizePlateNumber(form.plateNumber)"
          />
        </el-form-item>
        <el-form-item label="是否主车牌">
          <el-switch v-model="form.isPrimary" />
        </el-form-item>
        <el-button type="primary" @click="addVehicle">提交绑定</el-button>
      </el-form>
    </el-card>
  </div>
</template>
