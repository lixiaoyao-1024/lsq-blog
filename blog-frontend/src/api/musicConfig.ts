import { http } from '@/api/http'

/** 全局音乐播放器配置（对应后端 GET /api/music/config） */
export interface MusicConfig {
  id?: number
  /** 音乐平台：netease网易云 / tencentQQ音乐 */
  platform: 'netease' | 'tencent'
  /** 资源类型：playlist歌单 / song单曲 */
  resourceType: 'playlist' | 'song'
  /** 资源id（歌单id或单曲id） */
  resourceId: string
  /** 是否开启右下角悬浮播放器：1开 0关 */
  fixedEnabled?: number
  /** 自动播放开关：1开 0关 */
  autoplay?: number
  remark?: string | null
  createTime?: string
  updateTime?: string
}

export async function getMusicConfig() {
  const response = await http.get<MusicConfig>('/music/config')
  return response.data
}

export async function updateMusicConfig(config: MusicConfig) {
  const response = await http.post<MusicConfig>('/music/config/update', config)
  return response.data
}
