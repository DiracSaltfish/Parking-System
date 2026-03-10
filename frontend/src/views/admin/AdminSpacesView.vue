<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { spaceList } from '@/mock/data'

const form = reactive({
  spaceCode: '',
  type: 'NORMAL',
  floor: 'B1',
  remark: ''
})

function handleSave() {
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
      <el-form label-position="top">
        <el-form-item label="车位编号">
          <el-input v-model="form.spaceCode" placeholder="例如 A-021" />
        </el-form-item>
        <el-form-item label="车位类型">
          <el-select v-model="form.type">
            <el-option label="普通车位" value="NORMAL" />
            <el-option label="新能源" value="NEW_ENERGY" />
            <el-option label="VIP" value="VIP" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼层">
          <el-input v-model="form.floor" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>
        <el-button type="primary" @click="handleSave">保存车位</el-button>
      </el-form>
    </el-card>
  </div>
</template>
