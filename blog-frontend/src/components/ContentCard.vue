<script setup lang="ts">
import { CalendarDays, Eye } from '@lucide/vue'
import { computed } from 'vue'

import GlassCard from '@/components/GlassCard.vue'
import type { ContentItem } from '@/types/content'

const props = defineProps<{
  item: ContentItem
  detailBase: string
  fallbackTone: string
}>()

const displayTime = computed(() => {
  const value = props.item.publishedTime || props.item.createTime
  if (!value) return '未发布'
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(new Date(value))
})

const parsedTags = computed(() => {
  if (!props.item.tags) return []
  try {
    const value = JSON.parse(props.item.tags)
    return Array.isArray(value) ? value.slice(0, 3).map(String) : []
  } catch {
    return props.item.tags
      .split(',')
      .map((tag) => tag.trim())
      .filter(Boolean)
      .slice(0, 3)
  }
})
</script>

<template>
  <RouterLink :to="`${detailBase}/${item.id}`" class="block h-full">
    <GlassCard interactive class="h-full overflow-hidden">
      <div
        class="aspect-[16/9] bg-cover bg-center"
        :class="fallbackTone"
        :style="item.coverUrl ? { backgroundImage: `url(${item.coverUrl})` } : undefined"
      ></div>
      <div class="p-5">
        <div class="mb-3 flex flex-wrap gap-2">
          <span
            v-for="tag in parsedTags"
            :key="tag"
            class="rounded-full bg-white/70 px-2.5 py-1 text-xs font-medium text-slate-600 dark:bg-white/10 dark:text-slate-300"
          >
            {{ tag }}
          </span>
        </div>
        <h2 class="line-clamp-2 text-xl font-bold leading-snug text-slate-950 dark:text-white">
          {{ item.title }}
        </h2>
        <p class="mt-3 line-clamp-3 text-sm leading-7 text-slate-600 dark:text-slate-300">
          {{ item.summary || '暂无摘要' }}
        </p>
        <div class="mt-5 flex items-center justify-between text-xs text-slate-500 dark:text-slate-400">
          <span class="inline-flex items-center gap-1.5">
            <CalendarDays class="size-3.5" />
            {{ displayTime }}
          </span>
          <span class="inline-flex items-center gap-1.5">
            <Eye class="size-3.5" />
            {{ item.viewCount || 0 }}
          </span>
        </div>
      </div>
    </GlassCard>
  </RouterLink>
</template>
