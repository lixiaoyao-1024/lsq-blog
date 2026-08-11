<script setup lang="ts">
import { AtSign, Code2, Mail, Rss } from '@lucide/vue'
import { onMounted, ref } from 'vue'

import avatar from '@/assets/avatar.webp'
import { photoCount as localPhotoCount } from '@/assets/img/gallery'
import { getArticles } from '@/api/article'
import { getNotes } from '@/api/note'

const articleCount = ref(0)
const noteCount = ref(0)

onMounted(async () => {
  try {
    const [articles, notes] = await Promise.all([
      getArticles({ page: 1, size: 1 }),
      getNotes({ page: 1, size: 1 }),
    ])
    articleCount.value = articles.total
    noteCount.value = notes.total
  } catch {
    // 统计失败时保留 0，不打断页面
  }
})

const socials = [
  { icon: Code2, label: 'GitHub', href: 'https://github.com' },
  { icon: AtSign, label: 'Twitter / X', href: '#' },
  { icon: Mail, label: '邮箱', href: '#' },
  { icon: Rss, label: 'RSS', href: '#' },
]
</script>

<template>
  <article class="glass-surface relative h-full overflow-hidden rounded-3xl p-5 sm:p-6">
    <div class="flex items-center gap-3.5">
      <div class="relative shrink-0">
        <span class="absolute -inset-1.5 rounded-full bg-gradient-to-tr from-violet-500/60 via-fuchsia-400/40 to-sky-400/50 blur-md"></span>
        <div class="relative size-14 overflow-hidden rounded-full shadow-inner ring-2 ring-white/40">
          <img :src="avatar" alt="头像" class="size-full object-cover" />
        </div>
      </div>
      <div class="min-w-0">
        <h2 class="text-lg font-bold text-slate-900 dark:text-white">lsq</h2>
        <p class="mt-0.5 truncate text-xs text-slate-500 dark:text-slate-400">前端工程师 · 热爱代码、音乐与秩序感</p>
      </div>
    </div>

    <div class="mt-4 grid grid-cols-3 gap-2.5">
      <div class="rounded-xl bg-white/40 p-2.5 text-center backdrop-blur-sm dark:bg-white/5">
        <p class="text-base font-bold text-slate-900 dark:text-white">{{ articleCount }}</p>
        <p class="mt-0.5 text-[11px] text-slate-500 dark:text-slate-400">文章</p>
      </div>
      <div class="rounded-xl bg-white/40 p-2.5 text-center backdrop-blur-sm dark:bg-white/5">
        <p class="text-base font-bold text-slate-900 dark:text-white">{{ noteCount }}</p>
        <p class="mt-0.5 text-[11px] text-slate-500 dark:text-slate-400">杂谈</p>
      </div>
      <div class="rounded-xl bg-white/40 p-2.5 text-center backdrop-blur-sm dark:bg-white/5">
        <p class="text-base font-bold text-slate-900 dark:text-white">{{ localPhotoCount }}</p>
        <p class="mt-0.5 text-[11px] text-slate-500 dark:text-slate-400">照片</p>
      </div>
    </div>

    <div class="mt-4 flex items-center justify-center gap-2.5">
      <a
        v-for="s in socials"
        :key="s.label"
        :href="s.href"
        target="_blank"
        rel="noreferrer"
        class="grid size-8 place-items-center rounded-full border border-white/40 bg-white/40 text-slate-600 backdrop-blur-sm transition hover:-translate-y-0.5 hover:border-violet-300/70 hover:text-violet-600 dark:border-white/10 dark:bg-white/5 dark:text-slate-300 dark:hover:border-violet-300/40 dark:hover:text-violet-200"
        :aria-label="s.label"
        :title="s.label"
      >
        <component :is="s.icon" class="size-3.5" />
      </a>
    </div>
  </article>
</template>
