import request from './request'
import type { Lead, PageResult } from './types'

export interface LeadQuery {
  page?: number
  size?: number
  status?: string
  sourceId?: number
  keyword?: string
}

export function listLeads(params: LeadQuery) {
  return request.get('/leads', { params }) as Promise<PageResult<Lead>>
}

export function createLead(data: Partial<Lead>) {
  return request.post('/leads', data) as Promise<Lead>
}

export function updateLead(id: number, data: Partial<Lead>) {
  return request.put(`/leads/${id}`, data) as Promise<Lead>
}

/** 移入回收站 */
export function deleteLead(id: number) {
  return request.delete(`/leads/${id}`) as Promise<void>
}

/** 从回收站恢复 */
export function restoreLead(id: number) {
  return request.post(`/leads/${id}/restore`) as Promise<void>
}

/** 彻底删除 */
export function purgeLead(id: number) {
  return request.delete(`/leads/${id}/purge`) as Promise<void>
}

export function batchAssign(data: { leadIds: number[]; ownerId: number }) {
  return request.post('/leads/batch-assign', data) as Promise<number>
}

export function duplicateCheck(data: { phone?: string; email?: string; company?: string }) {
  return request.post('/leads/duplicate-check', data) as Promise<Lead[]>
}
