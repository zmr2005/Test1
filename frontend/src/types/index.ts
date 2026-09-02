// 与后端模型枚举保持一致
export const LEAD_STATUS = ['pending', 'assigned', 'converted', 'invalid', 'recycled'] as const
export const CUSTOMER_STATUS = ['private', 'public', 'locked'] as const
export const OPPORTUNITY_STAGE = ['contact', 'quotation', 'negotiation', 'won', 'lost'] as const

export interface Lead {
  id: number
  name: string
  company?: string | null
  phone?: string | null
  email?: string | null
  source?: string | null
  tags?: string | null
  status: string
  owner_id?: number | null
  remark?: string | null
  created_at: string
  updated_at: string
}

export interface Page<T> {
  items: T[]
  total: number
}
