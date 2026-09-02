// 与后端实体、枚举对齐的类型定义

export interface Lead {
  id: number
  name: string
  company?: string | null
  phone?: string | null
  email?: string | null
  sourceId?: number | null
  status: string
  ownerId?: number | null
  remark?: string | null
  createdAt?: string
  updatedAt?: string
}

export interface Customer {
  id: number
  name: string
  industry?: string | null
  phone?: string | null
  email?: string | null
  status: string
  ownerId?: number | null
  leadId?: number | null
  lastFollowUpAt?: string | null
  remark?: string | null
  createdAt?: string
  updatedAt?: string
}

export interface Contact {
  id: number
  customerId: number
  name: string
  position?: string | null
  phone?: string | null
  email?: string | null
  isDecisionMaker?: boolean
  createdAt?: string
  updatedAt?: string
}

export interface FollowUpLog {
  id: number
  customerId: number
  opportunityId?: number | null
  recordType: string
  content?: string | null
  amount?: number | null
  attachment?: string | null
  nextFollowAt?: string | null
  operatorId?: number | null
  createdAt?: string
}

export interface Opportunity {
  id: number
  customerId: number
  name: string
  inquiry?: string | null
  product?: string | null
  budget?: number | null
  stage: string
  expectedCloseDate?: string | null
  ownerId?: number | null
  remark?: string | null
  createdAt?: string
}

export interface Task {
  id: number
  title: string
  type: string
  content?: string | null
  ownerId?: number | null
  relatedType?: string | null
  relatedId?: number | null
  dueAt?: string | null
  remindAt?: string | null
  done?: boolean
  createdAt?: string
}

export interface PageResult<T> {
  records: T[]
  total: number
}

export const LEAD_STATUS: Record<string, string> = {
  pending: '待分配',
  assigned: '已分配',
  converted: '已转化',
  invalid: '已作废',
  recycled: '回收站',
}

export const CUSTOMER_STATUS: Record<string, string> = {
  private: '私海',
  public: '公海',
  locked: '锁定',
}

export const OPPORTUNITY_STAGE: Record<string, string> = {
  contact: '初步接触',
  quotation: '报价',
  negotiation: '谈判',
  won: '成交',
  lost: '失败',
}

export const TASK_TYPE: Record<string, string> = {
  daily: '日报',
  todo: '待办',
  prospect: '拓客',
}

export const RECORD_TYPE: Record<string, string> = {
  email: '邮件',
  social: '社媒',
  quote: '报价',
  phone: '电话',
  visit: '面访',
  log: '日志',
}
