<template>
  <div>
    <el-card>
      <div class="toolbar">
        <el-input
          v-model="query.keyword"
          placeholder="姓名 / 公司搜索"
          clearable
          style="width: 220px"
        />
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 160px">
          <el-option label="待分配" value="pending" />
          <el-option label="已分配" value="assigned" />
          <el-option label="已转化" value="converted" />
          <el-option label="已作废" value="invalid" />
          <el-option label="回收站" value="recycled" />
        </el-select>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button @click="openCreate">新建线索</el-button>
      </div>

      <el-table v-loading="loading" :data="rows" border stripe>
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="company" label="公司" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="source" label="来源" />
        <el-table-column prop="status" label="状态" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button link type="primary" @click="convert(row)">转化</el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pager"
        layout="total, prev, pager, next"
        :total="total"
        :page-size="query.limit"
        :current-page="currentPage"
        @current-change="onPageChange"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" title="新建线索" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="公司"><el-input v-model="form.company" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="来源"><el-input v-model="form.source" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { convertLead, createLead, deleteLead, listLeads } from '@/api/lead'
import type { Lead } from '@/types'

const rows = ref<Lead[]>([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const currentPage = ref(1)

const query = reactive({ keyword: '', status: '', limit: 10 })
const form = reactive({ name: '', company: '', phone: '', source: '' })

async function load() {
  loading.value = true
  try {
    const data = await listLeads({
      ...query,
      skip: (currentPage.value - 1) * query.limit,
    })
    rows.value = data.items
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function onPageChange(page: number) {
  currentPage.value = page
  load()
}

function openCreate() {
  Object.assign(form, { name: '', company: '', phone: '', source: '' })
  dialogVisible.value = true
}

async function submit() {
  await createLead(form)
  ElMessage.success('已创建')
  dialogVisible.value = false
  load()
}

async function convert(row: Lead) {
  await convertLead(row.id)
  ElMessage.success('已转化为客户')
  load()
}

async function remove(row: Lead) {
  await deleteLead(row.id)
  ElMessage.success('已移入回收站')
  load()
}

onMounted(load)
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
