import { http } from '@/api/http'
import type { ContentItem, PageResponse } from '@/types/content'

export interface ArticlePageParams {
  page?: number
  size?: number
  categoryId?: number
}

/** 编辑器可提交的内容载荷（字段均可选，后端按需落库） */
export interface ContentPayload {
  title?: string
  slug?: string | null
  summary?: string | null
  coverUrl?: string | null
  categoryId?: number | null
  tags?: string | null
  contentMd?: string | null
  status?: number
  sortOrder?: number
  pinned?: number
  featured?: number
  techStack?: string | null
  screenshots?: string | null
  demoUrl?: string | null
  repoUrl?: string | null
}

export async function getArticles(params: ArticlePageParams = {}) {
  const response = await http.get<PageResponse<ContentItem>>('/articles', { params })
  return response.data
}

export async function getArticleDetail(id: string | number) {
  const response = await http.get<ContentItem>(`/articles/${id}`)
  return response.data
}

// ---------- 管理端（含草稿） ----------

export async function getAdminArticles(params: { page?: number; size?: number; keyword?: string } = {}) {
  const response = await http.get<PageResponse<ContentItem>>('/articles/admin', { params })
  return response.data
}

export async function getAdminArticleDetail(id: string | number) {
  const response = await http.get<ContentItem>(`/articles/admin/${id}`)
  return response.data
}

export async function createArticle(payload: ContentPayload) {
  const response = await http.post<ContentItem>('/articles', payload)
  return response.data
}

export async function updateArticle(id: string | number, payload: ContentPayload) {
  const response = await http.put<ContentItem>(`/articles/${id}`, payload)
  return response.data
}

export async function deleteArticle(id: string | number) {
  await http.delete(`/articles/${id}`)
}
