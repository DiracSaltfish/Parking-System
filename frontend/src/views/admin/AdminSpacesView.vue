<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createSpace, deleteSpace, getSpaces } from '@/api/admin'
import { patternRule, requiredRule, spaceCodePattern } from '@/utils/validators'

const formRef = ref()
const loading = ref(false)
const spaces = ref([])
const form = reactive({
  spaceCode: '',
  type: 'NORMAL',
  floor: 'B1',
  remark: ''
})

const rules = {
  spaceCode: [
    requiredRule('请输入车位编号'),
    patternRule(spaceCodePattern, '车位编号格式应为 A-021 这类形式')
  ],
  type: [requiredRule('请选择车位类型')],
  floor: [requiredRule('请输入楼层')]
}

const typeOptions = [
  { label: '普通车位', value: 'NORMAL' },
  { label: '新能源车位', value: 'NEW_ENERGY' },
  { label: 'VIP 车位', value: 'VIP' }
]

async function loadSpaces() {
  loading.value = true
  try {
    const { data } = await getSpaces({ pageNum: 1, pageSize: 100 })
    spaces.value = data.data?.records || []
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '车位列表获取失败')
  } finally {
    loading.value = false
  }
}

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }

  try {
    await createSpace({
      spaceCode: form.spaceCode.trim().toUpperCase(),
      type: form.type,
      floor: form.floor.trim().toUpperCase(),
      remark: form.remark.trim()
    })
    ElMessage.success(`车位 ${form.spaceCode} 已保存`)
    form.spaceCode = ''
    form.type = 'NORMAL'
    form.floor = 'B1'
    form.remark = ''
    await loadSpaces()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '车位保存失败')
  }
}

async function handleDelete(row) {
  try {
    await deleteSpace(row.spaceId)
    ElMessage.success(`车位 ${row.spaceCode} 已删除`)
    await loadSpaces()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '车位删除失败')
  }
}

function typeLabel(type) {
  return typeOptions.find((item) => item.value === type)?.label || type
}

function statusLabel(status) {
  const mapping = {
    FREE: '空闲',
    OCCUPIED: '占用'
  }
  return mapping[status] || status
}

onMounted(loadSpaces)
</script>

<template>
  <div class="dual-grid">
    <el-card shadow="never" class="panel-card">
      <template #header>
        <div class="panel-card__header">
          <h3>车位列表</h3>
          <el-button @click="loadSpaces">刷新</el-button>
        </div>
      </template>
      <el-table v-loading="loading" :data="spaces" stripe>
        <el-table-column prop="spaceCode" label="车位编号" />
        <el-table-column label="类型">
          <template #default="{ row }">{{ typeLabel(row.type) }}</template>
        </el-table-column>
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'OCCUPIED' ? 'danger' : 'success'">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="floor" label="楼层" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card shadow="never" class="panel-card">
      <template #header><h3>新增车位</h3></template>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="车位编号" prop="spaceCode">
          <el-input v-model.trim="form.spaceCode" maxlength="5" placeholder="例如 A-021" />
        </el-form-item>
        <el-form-item label="车位类型" prop="type">
          <el-select v-model="form.type">
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼层" prop="floor">
          <el-input v-model.trim="form.floor" maxlength="10" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model.trim="form.remark" maxlength="100" show-word-limit type="textarea" :rows="3" />
        </el-form-item>
        <el-button type="primary" @click="handleSave">保存车位</el-button>
      </el-form>
    </el-card>
  </div>
</template>
