import { http } from '@/api/http'

export type PersonalInfoType = 'text' | 'link' | 'email'

export interface PersonalInfo {
  id: number
  label: string
  value: string
  valueType?: PersonalInfoType | null
  sortOrder?: number
  status?: number
  createTime?: string
  updateTime?: string
}

/** 公开列表：仅展示 status=1 的条目 */
export async function getPersonalInfo() {
  const response = await http.get<PersonalInfo[]>('/personal-info')
  return response.data
}

/** 管理端列表：含隐藏条目 */
export async function getAdminPersonalInfo() {
  const response = await http.get<PersonalInfo[]>('/personal-info/admin')
  return response.data
}

export async function createPersonalInfo(payload: Partial<PersonalInfo>) {
  const response = await http.post<PersonalInfo>('/personal-info', payload)
  return response.data
}

export async function updatePersonalInfo(id: number, payload: Partial<PersonalInfo>) {
  const response = await http.put<PersonalInfo>(`/personal-info/${id}`, payload)
  return response.data
}

export async function deletePersonalInfo(id: number) {
  await http.delete(`/personal-info/${id}`)
}
