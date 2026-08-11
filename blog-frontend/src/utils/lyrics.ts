/**
 * LRC 歌词解析工具。
 *
 * 支持两种输入：
 * - 标准 LRC：带 [mm:ss.xx] 时间标签，返回按时间排序、可用于同步高亮的行
 * - 纯文本：不含任何时间标签，逐行返回，time 均为 -1（表示非同步模式）
 */

export interface LyricLine {
  /** 时间戳（秒）。-1 表示纯文本行（无时间标签） */
  time: number
  text: string
}

const TIME_TAG = /\[(\d{1,2}):(\d{1,2})(?:[.:](\d{1,3}))?\]/g

export function parseLyrics(raw: string | null | undefined): LyricLine[] {
  const text = (raw ?? '').replace(/\r\n/g, '\n')
  if (!text.trim()) return []

  const result: LyricLine[] = []

  for (const line of text.split('\n')) {
    const trimmed = line.trim()
    if (!trimmed) continue

    const tags = Array.from(trimmed.matchAll(TIME_TAG))
    if (tags.length === 0) {
      result.push({ time: -1, text: trimmed })
      continue
    }

    const content = trimmed.replace(TIME_TAG, '').trim()
    for (const tag of tags) {
      const minutes = Number(tag[1])
      const seconds = Number(tag[2])
      const fraction = tag[3] ? Number(tag[3]) : 0
      const divisor = tag[3] ? 10 ** tag[3].length : 100
      const time = minutes * 60 + seconds + fraction / divisor
      result.push({ time, text: content || tag[0] })
    }
  }

  // 时间行按时间升序，纯文本行（time === -1）排到最后
  result.sort((a, b) => {
    if (a.time === -1 && b.time === -1) return 0
    if (a.time === -1) return 1
    if (b.time === -1) return -1
    return a.time - b.time
  })

  return result
}

/** 是否为带时间标签的同步歌词 */
export function isSyncedLyrics(lines: LyricLine[]): boolean {
  return lines.some((line) => line.time >= 0)
}
