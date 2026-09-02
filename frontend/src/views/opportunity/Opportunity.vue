<template>
  <div>
    <el-card>
      <div class="toolbar">
        <el-radio-group v-model="view">
          <el-radio-button value="list">列表</el-radio-button>
          <el-radio-button value="board">看板</el-radio-button>
        </el-radio-group>

        <el-select v-if="view === 'list'" v-model="filterStage" placeholder="阶段筛选" clearable style="width: 140px">
          <el-option v-for="(label, val) in OPPORTUNITY_STAGE" :key="val" :label="label" :value="val" />
        </el-select>

        <div class="spacer" />
        <el-button type="primary" @click="openCreate">新建商机</el-button>
      </div>

      <!-- 列表视图 -->
      <el-table v-if="view === 'list'" :data="filteredList" border stripe v-loading="loading">
        <el-table-column prop="name" label="商机名称" min-width="160" />
        <el-table-column prop="product" label="意向产品" width="140" />
        <el-table-column prop="budget" label="预算" width="120" />
        <el-table-column label="阶段" width="110">
          <template #default="{ row }">
            <el-tag size="small" :type="stageType(row.stage)">{{ OPPORTUNITY_STAGE[row.stage] || row.stage }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expectedCloseDate" label="预计成交" width="120" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button v-if="nextStage(row.stage)" link type="primary" @click="advance(row)">推进</el-button>
            <template v-if="row.stage === 'negotiation'">
              <el-button link type="success" @click="change(row, 'won')">成交</el-button>
              <el-button link type="danger" @click="change(row, 'lost')">输单</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <!-- 看板视图 -->
      <div v-else class="board" v-loading="loading">
        <div v-for="stage in stages" :key="stage" class="board-col">
          <div class="board-col-title">{{ OPPORTUNITY_STAGE[stage] }}（{{ boardData[stage]?.length || 0 }}）</div>
          <div class="board-col-body">
            <div v-for="opp in boardData[stage] || []" :key="opp.id" class="board-card">
              <div class="board-card-name">{{ opp.name }}</div>
              <div class="board-card-meta">预算：{{ opp.budget ?? '-' }}</div>
              <div class="board-card-meta">预计：{{ opp.expectedCloseDate ?? '-' }}</div>
              <div class="board-card-actions">
                <el-button v-if="nextStage(opp.stage)" link type="primary" @click="advance(opp)">推进</el-button>
                <template v-if="opp.stage === 'negotiation'">
                  <el-button link type="success" @click="change(opp, 'won')">成交</el-button>
                  <el-button link type="danger" @click="change(opp, 'lost')">输单</el-button>
                </template>
              </div>
            </div>
            <el-empty v-if="!(boardData[stage] || []).length" description="暂无" :image-size="50" />
          </div>
        </div>
      </div>
    </el-card>

    <!-- 新建商机对话框 -->
    <el-dialog v-model="dialogVisible" title="新建商机" width="520px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="客户ID" required><el-input-number v-model="form.customerId" :min="1" style="width: 100%" /></el-form-item>
        <el-form-item label="商机名称" required><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="意向产品"><el-input v-model="form.product" /></el-form-item>
        <el-form-item label="预算"><el-input-number v-model="form.budget" :min="0" style="width: 100%" /></el-form-item>
        <el-form-item label="预计成交"><el-date-picker v-model="form.expectedCloseDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { changeStage, createOpportunity, listOpportunities } from '@/api/opportunity'
import { OPPORTUNITY_STAGE, type Opportunity } from '@/api/types'

const stages = ['contact', 'quotation', 'negotiation', 'won', 'lost']
const flow = ['contact', 'quotation', 'negotiation']

const view = ref('list')
const filterStage = ref('')
const rows = ref<Opportunity[]>([])
const loading = ref(false)

const dialogVisible = ref(false)
const form = reactive<any>({ customerId: 1, name: '', product: '', budget: undefined, expectedCloseDate: '', remark: '' })

const filteredList = computed(() =>
  filterStage.value ? rows.value.filter((r) => r.stage === filterStage.value) : rows.value,
)

const boardData = computed(() => {
  const map: Record<string, Opportunity[]> = {}
  for (const s of stages) map[s] = []
  for (const opp of rows.value) {
    if (map[opp.stage]) map[opp.stage].push(opp)
  }
  return map
})

function stageType(stage: string) {
  const map: Record<string, string> = {
    contact: 'info',
    quotation: 'warning',
    negotiation: 'primary',
    won: 'success',
    lost: 'danger',
  }
  return map[stage] || 'info'
}

function nextStage(stage: string) {
  const i = flow.indexOf(stage)
  return i >= 0 && i < flow.length - 1 ? flow[i + 1] : null
}

async function load() {
  loading.value = true
  try {
    const data = await listOpportunities({ page: 1, size: 500 })
    rows.value = data.records
  } finally {
    loading.value = false
  }
}

function openCreate() {
  Object.assign(form, { customerId: 1, name: '', product: '', budget: undefined, expectedCloseDate: '', remark: '' })
  dialogVisible.value = true
}

async function submit() {
  if (!form.customerId || !form.name) {
    ElMessage.warning('请填写客户ID和商机名称')
    return
  }
  await createOpportunity(form)
  ElMessage.success('已创建')
  dialogVisible.value = false
  load()
}

async function advance(opp: Opportunity) {
  const target = nextStage(opp.stage)
  if (target) await change(opp, target)
}

async function change(opp: Opportunity, stage: string) {
  await changeStage(opp.id, { stage })
  ElMessage.success(`已变更为「${OPPORTUNITY_STAGE[stage]}」`)
  load()
}

onMounted(load)
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  align-items: center;
}
.spacer {
  flex: 1;
}
.board {
  display: flex;
  gap: 12px;
  overflow-x: auto;
}
.board-col {
  flex: 1;
  min-width: 200px;
  background: var(--el-fill-color-lighter);
  border-radius: 8px;
  padding: 8px;
}
.board-col-title {
  font-weight: 600;
  padding: 8px;
  border-bottom: 1px solid var(--el-border-color-light);
  margin-bottom: 8px;
}
.board-col-body {
  min-height: 200px;
}
.board-card {
  background: var(--el-bg-color);
  border-radius: 6px;
  padding: 10px;
  margin-bottom: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}
.board-card-name {
  font-weight: 500;
  margin-bottom: 4px;
}
.board-card-meta {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.board-card-actions {
  margin-top: 8px;
}
</style>
