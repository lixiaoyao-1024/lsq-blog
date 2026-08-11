import { http } from '@/api/http'
import type { ContentItem, PageResponse } from '@/types/content'
import type { ContentPayload } from '@/api/article'

export interface NotePageParams {
  page?: number
  size?: number
  categoryId?: number
}

export async function getNotes(params: NotePageParams = {}) {
  const response = await http.get<PageResponse<ContentItem>>('/notes', { params })
  return response.data
}

export async function getNoteDetail(id: string | number) {
  const response = await http.get<ContentItem>(`/notes/${id}`)
  return response.data
}

// ---------- 管理端（含草稿） ----------

export async function getAdminNotes(params: { page?: number; size?: number; keyword?: string } = {}) {
  const response = await http.get<PageResponse<ContentItem>>('/notes/admin', { params })
  return response.data
}

export async function getAdminNoteDetail(id: string | number) {
  const response = await http.get<ContentItem>(`/notes/admin/${id}`)
  return response.data
}

export async function createNote(payload: ContentPayload) {
  const response = await http.post<ContentItem>('/notes', payload)
  return response.data
}

export async function updateNote(id: string | number, payload: ContentPayload) {
  const response = await http.put<ContentItem>(`/notes/${id}`, payload)
  return response.data
}

export async function deleteNote(id: string | number) {
  await http.delete(`/notes/${id}`)
}
