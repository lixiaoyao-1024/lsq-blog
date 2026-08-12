import { getFiles, type FileAsset } from '@/api/file'
import { photoCount as localPhotoCount } from '@/assets/img/gallery'

export { localPhotoCount }

/** 判断是否为图片：优先看 mimeType，兜底看 URL 扩展名（兼容 content-type 缺失的上传） */
export function isImageAsset(asset: FileAsset): boolean {
  if (asset.mimeType?.startsWith('image/')) return true
  return /\.(jpe?g|png|gif|webp|avif|svg|bmp|ico)$/i.test(asset.url)
}

/**
 * 照片墙总数量 = 本地打包图片 + 后端已上传图片（按 URL 去重）。
 * 首页卡片与照片墙页共用，保证两处显示一致；拉取失败时退回本地数量。
 */
export async function fetchTotalPhotoCount(): Promise<number> {
  try {
    const assets = await getFiles()
    const seen = new Set<string>()
    let uploaded = 0
    for (const asset of assets) {
      if (!asset.url || seen.has(asset.url)) continue
      seen.add(asset.url)
      if (isImageAsset(asset)) uploaded++
    }
    return localPhotoCount + uploaded
  } catch {
    return localPhotoCount
  }
}
