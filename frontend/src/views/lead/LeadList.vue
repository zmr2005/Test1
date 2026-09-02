<template>
  <div>
    <el-card>
      <!-- 筛选与操作栏 -->
      <div class="toolbar">
        <el-input
          v-model="query.keyword"
          placeholder="姓名 / 公司搜索"
          clearable
          style="width: 200px"
          @keyup.enter="load"
        />
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 140px">
          <el-option v-for="(label, val) in LEAD_STATUS" :key="val" :label="label" :value="val" />
        </el-select>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>

        <div class="spacer" />

        <el-button type="primary" @click="openCreate">新建线索</el-button>
        <el-button :disabled="!selected.length" @click="openBatchAssign">批量分配</el-button>
        <el-button :disabled="!rows.length" @click="exportCsv">导出</el-button>
        <el-button @click="openRecycle">回收站</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="rows" border stripe v-loading="loading" @selection-change="onSelect">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="company" label="公司" min-width="140" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="160" />
        <el-table-column label="标签" width="160">
          <template #default="{ row }">
            <el-tag v-for="t in splitTags(row.tags)" :key="t" size="small" class="tag">{{ t }}</el-tag>
            <span v-if="!splitTags(row.tags).length">-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ LEAD_STATUS[row.status] || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pager"
        layout="total, prev, pager, next, sizes"
        :total="total"
        :page-size="query.size"
        :current-page="query.page"
        @current-change="(p) => { query.page = p; load() }"
        @size-change="(s) => { query.size = s; load() }"
      />
    </el-card>

    <!-- 新建 / 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑线索' : '新建线索'" width="520px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名" required><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="公司"><el-input v-model="form.company" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="标签">
          <el-select v-model="form.tags" multiple filterable allow-create default-first-option placeholder="输入或选择标签" style="width: 100%">
            <el-option v-for="t in tagOptions" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 批量分配对话框 -->
    <el-dialog v-model="assignVisible" title="批量分配" width="420px">
      <p>已选择 {{ selected.length }} 条线索，分配给：</p>
      <el-input-number v-model="assignOwnerId" :min="1" placeholder="负责人ID" style="width: 100%" />
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign">确定</el-button>
      </template>
    </el-dialog>

    <!-- 回收站抽屉 -->
    <el-drawer v-model="recycleVisible" title="线索回收站" size="70%">
      <el-table :data="recycleRows" border stripe v-loading="recycleLoading">
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="company" label="公司" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button link type="primary" @click="restore(row)">恢复</el-button>
            <el-button link type="danger" @click="purge(row)">彻底删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { batchAssign, createLead, deleteLead, listLeads, purgeLead, restoreLead, updateLead } from '@/api/lead'
import { LEAD_STATUS, type Lead } from '@/api/types'

const rows = ref<Lead[]>([])
const total = ref(0)
const loading = ref(false)
const selected = ref<Lead[]>([])

const query = reactive({ page: 1, size: 10, keyword: '', status: '' })

const dialogVisible = ref(false)
const form = reactive<any>({})
const tagOptions = ref(['高意向', '价格敏感', 'B2B', 'B2C', '重点客户'])

const assignVisible = ref(false)
const assignOwnerId = ref(1)

const recycleVisible = ref(false)
const recycleRows = ref<Lead[]>([])
const recycleLoading = ref(false)

function splitTags(tags?: string | null): string[] {
  return tags ? tags.split(',').filter(Boolean) : []
}

function statusType(status: string) {
  const map: Record<string, string> = {
    pending: 'info',
    assigned: 'primary',
    converted: 'success',
    invalid: 'warning',
    recycled: 'danger',
  }
  return map[status] || 'info'
}

async function load() {
  loading.value = true
  try {
    const data = await listLeads({ ...query, keyword: query.keyword || undefined, status: query.status || undefined })
    rows.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.keyword = ''
  query.status = ''
  query.page = 1
  load()
}

function onSelect(val: Lead[]) {
  selected.value = val
}

function openCreate() {
  Object.assign(form, { id: null, name: '', company: '', phone: '', email: '', tags: [], remark: '' })
  dialogVisible.value = true
}

function openEdit(row: Lead) {
  Object.assign(form, { ...row, tags: splitTags(row.tags) })
  dialogVisible.value = true
}

async function submit() {
  const payload = { ...form, tags: form.tags?.length ? form.tags.join(',') : undefined }
  if (form.id) {
    await updateLead(form.id, payload)
    ElMessage.success('已更新')
  } else {
    await createLead(payload)
    ElMessage.success('已创建')
  }
  dialogVisible.value = false
  load()
}

async function remove(row: Lead) {
  await ElMessageBox.confirm('确认移入回收站？', '提示', { type: 'warning' })
  await deleteLead(row.id)
  ElMessage.success('已移入回收站')
  load()
}

function openBatchAssign() {
  assignVisible.value = true
}

async function submitAssign() {
  await batchAssign({ leadIds: selected.value.map((l) => l.id), ownerId: assignOwnerId.value })
  ElMessage.success('批量分配成功')
  assignVisible.value = false
  load()
}

function exportCsv() {
  const headers = ['姓名', '公司', '电话', '邮箱', '状态', '标签']
  const lines = rows.value.map((r) =>
    [r.name, r.company, r.phone, r.email, LEAD_STATUS[r.status] || r.status, r.tags || ''].join(','),
  )
  const csv = '\ufeff' + headers.join(',') + '\n' + lines.join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '线索导出.csv'
  a.click()
  URL.revokeObjectURL(url)
}

async function openRecycle() {
  recycleVisible.value = true
  recycleLoading.value = true
  try {
    const data = await listLeads({ page: 1, size: 100, status: 'recycled' })
    recycleRows.value = data.records
  } finally {
    recycleLoading.value = false
  }
}

async function restore(row: Lead) {
  await restoreLead(row.id)
  ElMessage.success('已恢复')
  openRecycle()
  load()
}

async function purge(row: Lead) {
  await ElMessageBox.confirm('彻底删除后不可恢复，确认？', '警告', { type: 'error' })
  await purgeLead(row.id)
  ElMessage.success('已彻底删除')
  openRecycle()
}

onMounted(load)
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  align-items: center;
  flex-wrap: wrap;
}
.spacer {
  flex: 1;
}
.tag {
  margin-right: 4px;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
