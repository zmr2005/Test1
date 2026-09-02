import request from './request'
import type { PageResult, Task } from './types'

export interface TaskQuery {
  page?: number
  size?: number
  type?: string
  ownerId?: number
}

export function listTasks(params: TaskQuery) {
  return request.get('/tasks', { params }) as Promise<PageResult<Task>>
}

export function createTask(data: Partial<Task>) {
  return request.post('/tasks', data) as Promise<Task>
}

/** 完成状态切换 */
export function toggleDone(id: number, done: boolean) {
  return request.put(`/tasks/${id}/done`, null, { params: { done } }) as Promise<Task>
}
