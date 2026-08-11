export interface PageResponse<T> {
  records: T[]
  total: number
  size: number
  current: number
}

export interface ContentItem {
  id: number
  title: string
  slug?: string | null
  summary?: string | null
  coverUrl?: string | null
  categoryId?: number | null
  tags?: string | null
  contentMd?: string | null
  contentHtml?: string | null
  status?: number
  viewCount?: number
  sortOrder?: number
  pinned?: number
  publishedTime?: string | null
  createTime?: string
  updateTime?: string
}

export interface ProjectItem extends ContentItem {
  techStack?: string | null
  screenshots?: string | null
  demoUrl?: string | null
  repoUrl?: string | null
  featured?: number
}

export interface CategoryItem {
  id: number
  contentType?: string | null
  name: string
  slug?: string | null
  description?: string | null
  sortOrder?: number
}
