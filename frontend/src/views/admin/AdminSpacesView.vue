<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { spaceList } from '@/mock/data'
import { patternRule, requiredRule, spaceCodePattern } from '@/utils/validators'

const formRef = ref()
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

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }
  ElMessage.success(`车位 ${form.spaceCode || '新建项'} 已保存，后续接入车位接口`)
}
</script>

<template>
  <div class="dual-grid">
    <el-card shadow="never" class="panel-card">
      <template #header><h3>车位列表</h3></template>
      <el-table :data="spaceList" stripe>
        <el-table-column prop="spaceCode" label="车位编号" />
        <el-table-column prop="type" label="类型" />
        <el-table-column prop="status" label="状态" />
        <el-table-column prop="floor" label="楼层" />
      </el-table>
    </el-card>

    <el-card shadow="never" class="panel-card">
      <template #header><h3>维护车位</h3></template>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="车位编号" prop="spaceCode">
          <el-input v-model.trim="form.spaceCode" maxlength="5" placeholder="例如 A-021" />
        </el-form-item>
        <el-form-item label="车位类型" prop="type">
          <el-select v-model="form.type">
            <el-option label="普通车位" value="NORMAL" />
            <el-option label="新能源" value="NEW_ENERGY" />
            <el-option label="VIP" value="VIP" />
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
