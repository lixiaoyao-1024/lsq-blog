<script setup lang="ts">
import { ChevronLeft, ChevronRight, Loader2 } from '@lucide/vue'
import { onMounted, ref } from 'vue'

import { getArticles } from '@/api/article'
import type { ContentItem } from '@/types/content'

const items = ref<ContentItem[]>([])
const loading = ref(true)
const trackRef = ref<HTMLElement | null>(null)

const coverFallback =
  'bg-[radial-gradient(circle_at_25%_20%,rgba(139,92,246,0.35),transparent_45%),radial-gradient(circle_at_80%_30%,rgba(103,232,249,0.28),transparent_50%),linear-gradient(150deg,#1e1b4b,#2e1065)]'

onMounted(async () => {
  try {
    const data = await getArticles({ page: 1, size: 6 })
    items.value = data.records
  } finally {
    loading.value = false
  }
})

function scrollBy(dir: 1 | -1) {
  const el = trackRef.value
  if (!el) return
  el.scrollBy({ left: dir * el.clientWidth * 0.82, behavior: 'smooth' })
}
</script>

<template>
  <section class="glass-surface flex h-full flex-col overflow-hidden rounded-3xl p-4 sm:p-5">
    <div class="mb-3 flex items-center justify-between gap-2">
      <div class="flex items-baseline gap-2">
        <h2 class="text-sm font-bold text-slate-900 dark:text-white">推荐文章</h2>
        <p class="hidden text-[11px] text-slate-500 dark:text-slate-400 sm:block">想再次分享的东西</p>
      </div>
      <div class="flex gap-1.5">
        <button
          type="button"
          class="grid size-8 place-items-center rounded-full border border-white/40 bg-white/40 text-slate-600 backdrop-blur-sm transition hover:border-violet-300/70 hover:text-violet-600 dark:border-white/10 dark:bg-white/5 dark:text-slate-300 dark:hover:border-violet-300/40 dark:hover:text-violet-200"
          aria-label="上一组"
          title="上一组"
          @click="scrollBy(-1)"
        >
          <ChevronLeft class="size-4" />
        </button>
        <button
          type="button"
          class="grid size-8 place-items-center rounded-full border border-white/40 bg-white/40 text-slate-600 backdrop-blur-sm transition hover:border-violet-300/70 hover:text-violet-600 dark:border-white/10 dark:bg-white/5 dark:text-slate-300 dark:hover:border-violet-300/40 dark:hover:text-violet-200"
          aria-label="下一组"
          title="下一组"
          @click="scrollBy(1)"
        >
          <ChevronRight class="size-4" />
        </button>
      </div>
    </div>

    <div ref="trackRef" class="scrollbar-none flex min-h-0 flex-1 snap-x snap-mandatory gap-3 overflow-x-auto pb-1">
      <div v-if="loading" class="grid min-h-32 flex-1 place-items-center">
        <Loader2 class="size-5 animate-spin text-violet-400" />
      </div>
      <div v-else-if="items.length === 0" class="grid min-h-32 flex-1 place-items-center text-xs text-slate-500 dark:text-slate-400">
        暂无推荐文章
      </div>
      <RouterLink
        v-for="article in items"
        v-else
        :key="article.id"
        :to="`/articles/${article.id}`"
        class="group w-40 shrink-0 snap-start overflow-hidden rounded-xl border border-white/15 bg-white/5 transition hover:-translate-y-0.5 hover:border-violet-300/40 hover:shadow-lg hover:shadow-violet-500/10 dark:bg-white/5"
      >
        <div
          class="aspect-[16/10] bg-cover bg-center transition-transform duration-500 group-hover:scale-105"
          :class="article.coverUrl ? '' : coverFallback"
          :style="article.coverUrl ? { backgroundImage: `url(${article.coverUrl})` } : undefined"
        ></div>
        <div class="p-2.5">
          <h3 class="line-clamp-2 text-xs font-semibold leading-snug text-slate-900 dark:text-white">
            {{ article.title }}
          </h3>
        </div>
      </RouterLink>
    </div>
  </section>
</template>
