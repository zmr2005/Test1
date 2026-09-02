<template>
  <div>
    <el-card>
      <el-tabs v-model="activeTab" @tab-change="load">
        <el-tab-pane label="待办日历" name="todo" />
        <el-tab-pane label="工作日报" name="daily" />
      </el-tabs>

      <div class="toolbar">
        <el-button type="primary" @click="openCreate(activeTab)">{{ activeTab === 'todo' ? '新建待办' : '写日报' }}</el-button>
      </div>

      <!-- 待办：日历 + 列表 -->
      <template v-if="activeTab === 'todo'">
        <el-calendar v-model="calendarDate">
          <template #date-cell="{ data }">
            <div class="cal-cell">
              <span>{{ Number(data.day.split('-')[2]) }}</span>
              <span v-if="todoByDate[data.day]" class="cal-badge">{{ todoByDate[data.day].length }}</span>
            </div>
          </template>
        </el-calendar>

        <el-table :data="todoList" border stripe style="margin-top: 16px">
          <el-table-column label="完成" width="60">
            <template #default="{ row }">
              <el-checkbox :model-value="row.done" @change="(v) => toggle(row, v)" />
            </template>
          </el-table-column>
          <el-table-column prop="title" label="待办事项" min-width="200" />
          <el-table-column label="截止时间" width="160">
            <template #default="{ row }">{{ formatTime(row.dueAt) }}</template>
          </el-table-column>
        </el-table>
      </template>

      <!-- 日报列表 -->
      <template v-else>
        <el-table :data="dailyList" border stripe>
          <el-table-column prop="title" label="标题" min-width="160" />
          <el-table-column prop="content" label="日报内容" min-width="240" show-overflow-tooltip />
          <el-table-column label="日期" width="160">
            <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
          </el-table-column>
        </el-table>
      </template>
    </el-card>

    <!-- 新建对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.type === 'daily' ? '写日报' : '新建待办'" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item v-if="form.type === 'daily'" label="标题">
          <el-input v-model="form.title" :placeholder="`日报 ${today}`" />
        </el-form-item>
        <el-form-item v-else label="待办事项" required>
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item v-if="form.type === 'todo'" label="截止时间">
          <el-date-picker v-model="form.dueAt" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="4" />
        </el-form-item>
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
import { createTask, listTasks, toggleDone } from '@/api/task'
import type { Task } from '@/api/types'

const activeTab = ref('todo')
const calendarDate = ref(new Date())
const rows = ref<Task[]>([])
const dialogVisible = ref(false)

const today = new Date().toISOString().slice(0, 10)
const form = reactive<any>({ type: 'todo', title: '', content: '', dueAt: '' })

const todoList = computed(() => rows.value.filter((t) => t.type === 'todo'))
const dailyList = computed(() => rows.value.filter((t) => t.type === 'daily'))

const todoByDate = computed(() => {
  const map: Record<string, Task[]> = {}
  for (const t of todoList.value) {
    const key = (t.dueAt || '').slice(0, 10)
    if (!key) continue
    ;(map[key] ||= []).push(t)
  }
  return map
})

async function load() {
  const data = await listTasks({ page: 1, size: 500, type: activeTab.value })
  rows.value = data.records
}

function openCreate(type: string) {
  form.type = type
  form.title = ''
  form.content = ''
  form.dueAt = ''
  dialogVisible.value = true
}

async function submit() {
  if (!form.title) {
    ElMessage.warning('请填写标题')
    return
  }
  const payload: any = { title: form.title || `${form.type === 'daily' ? '日报' : '待办'} ${today}`, type: form.type, content: form.content }
  if (form.type === 'todo') payload.dueAt = form.dueAt || undefined
  await createTask(payload)
  ElMessage.success('已保存')
  dialogVisible.value = false
  load()
}

async function toggle(row: Task, done: boolean) {
  await toggleDone(row.id, done)
  row.done = done
}

function formatTime(t?: string | null) {
  return t ? t.replace('T', ' ').slice(0, 16) : '-'
}

onMounted(load)
</script>

<style scoped>
.toolbar {
  margin-bottom: 16px;
}
.cal-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
}
.cal-badge {
  margin-top: 2px;
  background: var(--el-color-primary);
  color: #fff;
  border-radius: 999px;
  font-size: 12px;
  padding: 0 6px;
}
</style>
