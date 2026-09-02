<template>
  <div>
    <el-page-header content="客户档案" @back="router.back()" style="margin-bottom: 16px" />

    <el-row :gutter="16">
      <!-- 左：基本信息 + 联系人 -->
      <el-col :span="9">
        <el-card header="基本信息" v-loading="loading">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="客户名称">{{ customer?.name || '-' }}</el-descriptions-item>
            <el-descriptions-item label="行业">{{ customer?.industry || '-' }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ customer?.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ customer?.email || '-' }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag size="small">{{ CUSTOMER_STATUS[customer?.status || ''] || '-' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="最近跟进">{{ formatTime(customer?.lastFollowUpAt) }}</el-descriptions-item>
            <el-descriptions-item label="备注">{{ customer?.remark || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card header="联系人" style="margin-top: 16px">
          <div class="contact-head">
            <el-button link type="primary" @click="openContact">新增联系人</el-button>
          </div>
          <div v-for="c in contacts" :key="c.id" class="contact-item">
            <div class="contact-name">
              {{ c.name }}
              <el-tag v-if="c.isDecisionMaker" size="small" type="warning">决策人</el-tag>
            </div>
            <div class="contact-meta">{{ c.position || '-' }} · {{ c.phone || '-' }} · {{ c.email || '-' }}</div>
          </div>
          <el-empty v-if="!contacts.length" description="暂无联系人" :image-size="50" />
        </el-card>
      </el-col>

      <!-- 右：跟进记录 -->
      <el-col :span="15">
        <el-card header="跟进记录">
          <div class="add-log">
            <el-select v-model="logForm.recordType" placeholder="类型" style="width: 130px">
              <el-option v-for="(label, val) in RECORD_TYPE" :key="val" :label="label" :value="val" />
            </el-select>
            <el-input v-model="logForm.content" placeholder="跟进内容 / 备注" @keyup.enter="submitLog" />
            <el-button type="primary" @click="submitLog">添加</el-button>
          </div>

          <el-timeline v-if="logs.length" style="margin-top: 16px">
            <el-timeline-item
              v-for="log in logs"
              :key="log.id"
              :timestamp="formatTime(log.createdAt)"
              placement="top"
            >
              <div class="log-head">
                <el-tag size="small" type="info">{{ RECORD_TYPE[log.recordType] || log.recordType }}</el-tag>
                <span v-if="log.amount != null" class="amount">金额：{{ log.amount }}</span>
              </div>
              <div class="log-content">{{ log.content || '-' }}</div>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无跟进记录" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 关联商机 -->
    <el-card header="关联商机" style="margin-top: 16px">
      <el-table :data="opportunities" border stripe>
        <el-table-column prop="name" label="商机名称" />
        <el-table-column prop="product" label="意向产品" />
        <el-table-column prop="budget" label="预算" />
        <el-table-column label="阶段" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ OPPORTUNITY_STAGE[row.stage] || row.stage }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expectedCloseDate" label="预计成交" width="120" />
      </el-table>
    </el-card>

    <!-- 新增联系人对话框 -->
    <el-dialog v-model="contactVisible" title="新增联系人" width="440px">
      <el-form :model="contactForm" label-width="80px">
        <el-form-item label="姓名" required><el-input v-model="contactForm.name" /></el-form-item>
        <el-form-item label="职位"><el-input v-model="contactForm.position" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="contactForm.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="contactForm.email" /></el-form-item>
        <el-form-item label="决策人"><el-switch v-model="contactForm.isDecisionMaker" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="contactVisible = false">取消</el-button>
        <el-button type="primary" @click="submitContact">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { addContact, addFollowUp, getCustomer, listContacts, listFollowUps } from '@/api/customer'
import { listOpportunities } from '@/api/opportunity'
import { CUSTOMER_STATUS, OPPORTUNITY_STAGE, RECORD_TYPE, type Contact, type Customer, type FollowUpLog, type Opportunity } from '@/api/types'

const route = useRoute()
const router = useRouter()
const customerId = Number(route.params.id)

const customer = ref<Customer | null>(null)
const logs = ref<FollowUpLog[]>([])
const opportunities = ref<Opportunity[]>([])
const contacts = ref<Contact[]>([])
const loading = ref(false)

const logForm = reactive({ recordType: 'log', content: '' })
const contactVisible = ref(false)
const contactForm = reactive({ name: '', position: '', phone: '', email: '', isDecisionMaker: false })

async function loadCustomer() {
  customer.value = await getCustomer(customerId)
}

async function loadLogs() {
  logs.value = await listFollowUps(customerId)
}

async function loadOpportunities() {
  const data = await listOpportunities({ page: 1, size: 100, customerId })
  opportunities.value = data.records
}

async function loadContacts() {
  contacts.value = await listContacts(customerId)
}

async function submitLog() {
  if (!logForm.recordType || !logForm.content) {
    ElMessage.warning('请填写类型和内容')
    return
  }
  await addFollowUp(customerId, { recordType: logForm.recordType, content: logForm.content })
  ElMessage.success('已添加跟进记录')
  logForm.content = ''
  await Promise.all([loadLogs(), loadCustomer()])
}

function openContact() {
  Object.assign(contactForm, { name: '', position: '', phone: '', email: '', isDecisionMaker: false })
  contactVisible.value = true
}

async function submitContact() {
  if (!contactForm.name) {
    ElMessage.warning('请填写姓名')
    return
  }
  await addContact(customerId, { ...contactForm })
  ElMessage.success('已添加联系人')
  contactVisible.value = false
  loadContacts()
}

function formatTime(t?: string | null) {
  return t ? t.replace('T', ' ').slice(0, 16) : '-'
}

onMounted(async () => {
  loading.value = true
  try {
    await Promise.all([loadCustomer(), loadLogs(), loadOpportunities(), loadContacts()])
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.add-log {
  display: flex;
  gap: 12px;
}
.log-head {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 4px;
}
.amount {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
.log-content {
  white-space: pre-wrap;
}
.contact-head {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 8px;
}
.contact-item {
  padding: 8px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}
.contact-name {
  font-weight: 500;
  display: flex;
  gap: 8px;
  align-items: center;
}
.contact-meta {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 2px;
}
</style>
