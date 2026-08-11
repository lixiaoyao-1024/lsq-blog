import { http } from '@/api/http'

export interface FileAsset {
  id: number
  originalName?: string | null
  url: string
  mimeType?: string | null
  fileSize?: number | null
  createTime?: string
}

export async function getFiles() {
  const response = await http.get<FileAsset[]>('/files')
  return response.data
}

/** 上传文件（表单，限制 20MB），返回带 /uploads/... 访问地址的文件资源 */
export async function uploadFile(file: File) {
  const form = new FormData()
  form.append('file', file)
  const response = await http.post<FileAsset>('/files/upload', form)
  return response.data
}

/** 软删除文件资源（同时移除磁盘文件） */
export async function deleteFile(id: number) {
  await http.delete(`/files/${id}`)
}
