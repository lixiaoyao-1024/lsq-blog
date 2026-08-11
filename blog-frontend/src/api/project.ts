import { http } from '@/api/http'
import type { PageResponse, ProjectItem } from '@/types/content'
import type { ContentPayload } from '@/api/article'

export interface ProjectPageParams {
  page?: number
  size?: number
  categoryId?: number
}

export async function getProjects(params: ProjectPageParams = {}) {
  const response = await http.get<PageResponse<ProjectItem>>('/projects', { params })
  return response.data
}

export async function getProjectDetail(id: string | number) {
  const response = await http.get<ProjectItem>(`/projects/${id}`)
  return response.data
}

// ---------- 管理端（含草稿） ----------

export async function getAdminProjects(params: { page?: number; size?: number; keyword?: string } = {}) {
  const response = await http.get<PageResponse<ProjectItem>>('/projects/admin', { params })
  return response.data
}

export async function getAdminProjectDetail(id: string | number) {
  const response = await http.get<ProjectItem>(`/projects/admin/${id}`)
  return response.data
}

export async function createProject(payload: ContentPayload) {
  const response = await http.post<ProjectItem>('/projects', payload)
  return response.data
}

export async function updateProject(id: string | number, payload: ContentPayload) {
  const response = await http.put<ProjectItem>(`/projects/${id}`, payload)
  return response.data
}

export async function deleteProject(id: string | number) {
  await http.delete(`/projects/${id}`)
}
