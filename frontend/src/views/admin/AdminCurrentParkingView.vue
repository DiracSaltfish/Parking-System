<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createParkingEntry, getCurrentParkingList } from '@/api/admin'
import {
  normalizePlateNumber,
  patternRule,
  plateNumberPattern,
  requiredRule,
  spaceCodePattern
} from '@/utils/validators'

const keyword = ref('')
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const records = ref([])
const total = ref(0)
const formRef = ref()

const form = reactive({
  plateNumber: '',
  spaceId: ''
})

const rules = {
  plateNumber: [
    requiredRule('请输入车牌号'),
    patternRule(plateNumberPattern, '请输入正确的车牌号')
  ],
  spaceId: [
    {
      validator: (_, value, callback) => {
        if (!value) {
          callback()
          return
        }
        if (!spaceCodePattern.test(value.trim().toUpperCase())) {
          callback(new Error('车位编号格式示例：A-021'))
          return
        }
        callback()
      },
      trigger: ['blur', 'change']
    }
  ]
}

async function loadCurrentParking() {
  loading.value = true
  try {
    const { data } = await getCurrentParkingList({
      plateNumber: normalizePlateNumber(keyword.value)
    })
    records.value = data.data?.records || []
    total.value = data.data?.total || 0
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '当前车辆列表获取失败')
  } finally {
    loading.value = false
  }
}

function openDialog() {
  form.plateNumber = ''
  form.spaceId = ''
  dialogVisible.value = true
}

async function submitEntry() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }

  submitLoading.value = true
  try {
    await createParkingEntry({
      plateNumber: normalizePlateNumber(form.plateNumber),
      spaceId: form.spaceId?.trim().toUpperCase() || undefined
    })
    ElMessage.success('车辆已入场，计费已从当前时刻开始')
    dialogVisible.value = false
    await loadCurrentParking()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '入场登记失败')
  } finally {
    submitLoading.value = false
  }
}

function handleAction(action, row) {
  ElMessage.info(`${action}：${row.plateNumber}，该操作下一步接后端联调`)
}

onMounted(loadCurrentParking)
</script>

<template>
  <div class="page-stack">
    <el-card shadow="never" class="panel-card">
      <div class="toolbar-row">
        <el-input v-model="keyword" placeholder="按车牌号搜索" clearable @keyup.enter="loadCurrentParking" />
        <el-button type="primary" :loading="loading" @click="loadCurrentParking">查询</el-button>
        <el-button @click="loadCurrentParking">刷新</el-button>
        <el-button type="success" @click="openDialog">手动录入入场</el-button>
      </div>
      <div class="summary-line">当前在场车辆：{{ total }} 辆</div>
      <el-table v-loading="loading" :data="records" stripe>
        <el-table-column prop="recordId" label="记录编号" width="120" />
        <el-table-column prop="plateNumber" label="车牌号" />
        <el-table-column prop="ownerName" label="车主" />
        <el-table-column prop="spaceCode" label="车位" />
        <el-table-column prop="entryTime" label="入场时间" />
        <el-table-column prop="durationMinutes" label="停车时长" />
        <el-table-column prop="finalAmount" label="当前费用" />
        <el-table-column prop="payStatus" label="支付状态" />
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleAction('办理出场', row)">办理出场</el-button>
            <el-button link type="warning" @click="handleAction('费用豁免', row)">费用豁免</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="手动录入车辆入场" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="车牌号" prop="plateNumber">
          <el-input
            v-model.trim="form.plateNumber"
            maxlength="8"
            placeholder="例如：粤B12345"
            @blur="form.plateNumber = normalizePlateNumber(form.plateNumber)"
          />
        </el-form-item>
        <el-form-item label="车位编号" prop="spaceId">
          <el-input
            v-model.trim="form.spaceId"
            maxlength="5"
            placeholder="可选，例如：A-021；留空则自动分配"
            @blur="form.spaceId = form.spaceId.trim().toUpperCase()"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitEntry">确认入场</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.summary-line {
  margin-bottom: 16px;
  color: #606266;
}
</style>
