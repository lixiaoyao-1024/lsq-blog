import { http } from '@/api/http'
import type { CategoryItem } from '@/types/content'

export async function getCategories(contentType?: string) {
  const response = await http.get<CategoryItem[]>('/categories', {
    params: contentType ? { contentType } : undefined,
  })
  return response.data
}
