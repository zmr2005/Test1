import request from './request'
import type { Opportunity, PageResult } from './types'

export interface OpportunityQuery {
  page?: number
  size?: number
  stage?: string
  customerId?: number
}

export function listOpportunities(params: OpportunityQuery) {
  return request.get('/opportunities', { params }) as Promise<PageResult<Opportunity>>
}

export function createOpportunity(data: Partial<Opportunity>) {
  return request.post('/opportunities', data) as Promise<Opportunity>
}

export function getOpportunity(id: number) {
  return request.get(`/opportunities/${id}`) as Promise<Opportunity>
}

/** 商机阶段流转 */
export function changeStage(id: number, data: { stage: string; remark?: string }) {
  return request.put(`/opportunities/${id}/stage`, data) as Promise<void>
}
