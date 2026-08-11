<script setup lang="ts">
import { ChevronLeft, ChevronRight, Loader2 } from '@lucide/vue'

import ContentCard from '@/components/ContentCard.vue'
import type { ContentItem, PageResponse } from '@/types/content'

defineProps<{
  title: string
  description: string
  pageData: PageResponse<ContentItem> | null
  loading: boolean
  error: string
  detailBase: string
  fallbackTone: string
}>()

defineEmits<{
  previous: []
  next: []
}>()
</script>

<template>
  <section class="py-6">
    <div class="mb-8 max-w-3xl">
      <h1 class="text-4xl font-bold text-slate-950 dark:text-white sm:text-5xl">{{ title }}</h1>
      <p class="mt-4 text-base leading-8 text-slate-600 dark:text-slate-300">{{ description }}</p>
    </div>

    <div v-if="loading" class="glass-surface grid min-h-64 place-items-center rounded-2xl">
      <Loader2 class="size-7 animate-spin text-violet-400" />
    </div>

    <div v-else-if="error" class="glass-surface rounded-2xl p-8 text-sm text-rose-600 dark:text-rose-300">
      {{ error }}
    </div>

    <div v-else-if="!pageData || pageData.records.length === 0" class="glass-surface rounded-2xl p-8">
      <p class="text-lg font-semibold text-slate-950 dark:text-white">暂无内容</p>
      <p class="mt-2 text-sm text-slate-500 dark:text-slate-400">后端接口已经就绪，写入发布数据后这里会自动展示。</p>
    </div>

    <template v-else>
      <div class="grid gap-5 md:grid-cols-2 lg:grid-cols-3">
        <ContentCard
          v-for="item in pageData.records"
          :key="item.id"
          :item="item"
          :detail-base="detailBase"
          :fallback-tone="fallbackTone"
        />
      </div>

      <div class="mt-8 flex items-center justify-between">
        <button
          type="button"
          class="inline-flex items-center gap-2 rounded-full border border-white/50 bg-white/60 px-4 py-2 text-sm font-semibold text-slate-700 backdrop-blur-xl transition hover:bg-white/80 disabled:cursor-not-allowed disabled:opacity-40 dark:border-slate-700/50 dark:bg-slate-950/50 dark:text-slate-200"
          :disabled="pageData.current <= 1"
          @click="$emit('previous')"
        >
          <ChevronLeft class="size-4" />
          上一页
        </button>
        <span class="text-sm text-slate-500 dark:text-slate-400">
          第 {{ pageData.current }} 页 / 共 {{ Math.max(1, Math.ceil(pageData.total / pageData.size)) }} 页
        </span>
        <button
          type="button"
          class="inline-flex items-center gap-2 rounded-full border border-white/50 bg-white/60 px-4 py-2 text-sm font-semibold text-slate-700 backdrop-blur-xl transition hover:bg-white/80 disabled:cursor-not-allowed disabled:opacity-40 dark:border-slate-700/50 dark:bg-slate-950/50 dark:text-slate-200"
          :disabled="pageData.current >= Math.ceil(pageData.total / pageData.size)"
          @click="$emit('next')"
        >
          下一页
          <ChevronRight class="size-4" />
        </button>
      </div>
    </template>
  </section>
</template>
