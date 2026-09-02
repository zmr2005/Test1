import type { Lead, Page } from '@/types'
import request from './request'

export interface LeadQuery {
  skip?: number
  limit?: number
  status?: string
  source?: string
  keyword?: string
}

export function listLeads(params: LeadQuery): Promise<Page<Lead>> {
  return request.get('/leads', { params }) as Promise<Page<Lead>>
}

export function createLead(data: Partial<Lead>): Promise<Lead> {
  return request.post('/leads', data) as Promise<Lead>
}

export function updateLead(id: number, data: Partial<Lead>): Promise<Lead> {
  return request.put(`/leads/${id}`, data) as Promise<Lead>
}

export function deleteLead(id: number): Promise<void> {
  return request.delete(`/leads/${id}`) as Promise<void>
}

export function batchAssign(leadIds: number[], ownerId: number): Promise<{ updated: number }> {
  return request.post('/leads/batch-assign', {
    lead_ids: leadIds,
    owner_id: ownerId,
  }) as Promise<{ updated: number }>
}

export function convertLead(id: number): Promise<{ customer_id: number }> {
  return request.post(`/leads/${id}/convert`) as Promise<{ customer_id: number }>
}
