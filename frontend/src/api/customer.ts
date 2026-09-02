import request from './request'
import type { Contact, Customer, FollowUpLog, PageResult } from './types'

export interface CustomerQuery {
  page?: number
  size?: number
  status?: string
  keyword?: string
}

export function listCustomers(params: CustomerQuery) {
  return request.get('/customers', { params }) as Promise<PageResult<Customer>>
}

export function getCustomer(id: number) {
  return request.get(`/customers/${id}`) as Promise<Customer>
}

/** 公海认领 */
export function reclaimCustomer(id: number) {
  return request.post(`/customers/${id}/reclaim`) as Promise<void>
}

/** 回收进公海 */
export function recycleCustomer(id: number) {
  return request.post(`/customers/${id}/recycle`) as Promise<void>
}

/** 移交 */
export function transferCustomer(id: number, data: { toOwnerId: number; remark?: string }) {
  return request.post(`/customers/${id}/transfer`, data) as Promise<void>
}

export function listFollowUps(customerId: number) {
  return request.get(`/customers/${customerId}/follow-ups`) as Promise<FollowUpLog[]>
}

export interface FollowUpLogDTO {
  recordType: string
  opportunityId?: number
  content?: string
  amount?: number
  attachment?: string
  nextFollowAt?: string
}

export function addFollowUp(customerId: number, data: FollowUpLogDTO) {
  return request.post(`/customers/${customerId}/follow-ups`, data) as Promise<FollowUpLog>
}

export function listContacts(customerId: number) {
  return request.get(`/customers/${customerId}/contacts`) as Promise<Contact[]>
}

export function addContact(customerId: number, data: Partial<Contact>) {
  return request.post(`/customers/${customerId}/contacts`, data) as Promise<Contact>
}
