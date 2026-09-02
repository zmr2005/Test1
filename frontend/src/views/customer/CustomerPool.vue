<template>
  <div>
    <el-card>
      <el-tabs v-model="activeTab" @tab-change="load">
        <el-tab-pane label="私海" name="private" />
        <el-tab-pane label="公海" name="public" />
      </el-tabs>

      <div class="toolbar">
        <el-input v-model="keyword" placeholder="客户名称 / 电话搜索" clearable style="width: 240px" @keyup.enter="load" />
        <el-button type="primary" @click="load">查询</el-button>
      </div>

      <el-table :data="rows" border stripe v-loading="loading">
        <el-table-column prop="name" label="客户名称" min-width="160" />
        <el-table-column prop="industry" label="行业" width="120" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="160" />
        <el-table-column label="最近跟进" width="160">
          <template #default="{ row }">{{ formatTime(row.lastFollowUpAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="goDetail(row)">详情</el-button>
            <template v-if="activeTab === 'private'">
              <el-button link @click="openTransfer(row)">移交</el-button>
              <el-button link type="warning" @click="recycle(row)">回收公海</el-button>
            </template>
            <template v-else>
              <el-button link type="primary" @click="reclaim(row)">认领</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pager"
        layout="total, prev, pager, next"
        :total="total"
        :page-size="query.size"
        :current-page="query.page"
        @current-change="(p) => { query.page = p; load() }"
      />
    </el-card>

    <!-- 移交对话框 -->
    <el-dialog v-model="transferVisible" title="客户移交" width="420px">
      <p style="margin-bottom: 12px">将「{{ current?.name }}」移交给：</p>
      <el-input-number v-model="toOwnerId" :min="1" placeholder="目标负责人ID" style="width: 100%" />
      <el-input v-model="transferRemark" placeholder="备注（可选）" style="margin-top: 12px" />
      <template #footer>
        <el-button @click="transferVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTransfer">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listCustomers, reclaimCustomer, recycleCustomer, transferCustomer } from '@/api/customer'
import type { Customer } from '@/api/types'

const router = useRouter()
const activeTab = ref('private')
const keyword = ref('')
const rows = ref<Customer[]>([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 10 })

const transferVisible = ref(false)
const current = ref<Customer | null>(null)
const toOwnerId = ref(1)
const transferRemark = ref('')

async function load() {
  loading.value = true
  try {
    const data = await listCustomers({
      page: query.page,
      size: query.size,
      status: activeTab.value,
      keyword: keyword.value || undefined,
    })
    rows.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function formatTime(t?: string | null) {
  return t ? t.replace('T', ' ').slice(0, 16) : '-'
}

function goDetail(row: Customer) {
  router.push(`/customers/${row.id}`)
}

async function reclaim(row: Customer) {
  await ElMessageBox.confirm(`确认认领客户「${row.name}」？`, '提示', { type: 'info' })
  await reclaimCustomer(row.id)
  ElMessage.success('认领成功')
  load()
}

async function recycle(row: Customer) {
  await ElMessageBox.confirm(`确认将「${row.name}」回收进公海？`, '提示', { type: 'warning' })
  await recycleCustomer(row.id)
  ElMessage.success('已回收进公海')
  load()
}

function openTransfer(row: Customer) {
  current.value = row
  toOwnerId.value = 1
  transferRemark.value = ''
  transferVisible.value = true
}

async function submitTransfer() {
  if (!current.value) return
  await transferCustomer(current.value.id, { toOwnerId: toOwnerId.value, remark: transferRemark.value })
  ElMessage.success('移交成功')
  transferVisible.value = false
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
