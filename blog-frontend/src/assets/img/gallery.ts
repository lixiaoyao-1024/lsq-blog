// 照片墙本地图片清单（DM_*.webp），由 Vite 打包为哈希 URL
// 通过 glob 自动收集，新增图片无需改动代码
const modules = import.meta.glob('./DM_*.webp', {
  eager: true,
  import: 'default',
}) as Record<string, string>

export const photos: string[] = Object.keys(modules)
  .sort((a, b) => a.localeCompare(b, undefined, { numeric: true }))
  .map((key) => modules[key])
  .filter((value): value is string => Boolean(value))

export const photoCount = photos.length
