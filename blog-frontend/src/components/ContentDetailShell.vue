<script setup lang="ts">
import { ArrowLeft, ExternalLink, GitBranch, Loader2 } from '@lucide/vue'
import MarkdownIt from 'markdown-it'
import { computed } from 'vue'

import type { ContentItem, ProjectItem } from '@/types/content'

const props = defineProps<{
  item: ContentItem | ProjectItem | null
  loading: boolean
  error: string
  backTo: string
  backLabel: string
  fallbackTone: string
}>()

const markdown = new MarkdownIt({
  html: false,
  linkify: true,
  breaks: true,
})

const renderedContent = computed(() => {
  if (!props.item) return ''
  if (props.item.contentHtml) return props.item.contentHtml
  return markdown.render(props.item.contentMd || '暂无正文')
})

const project = computed(() => props.item as ProjectItem | null)
</script>

<template>
  <section class="mx-auto max-w-4xl py-6">
    <RouterLink
      :to="backTo"
      class="mb-6 inline-flex items-center gap-2 rounded-full border border-white/50 bg-white/60 px-4 py-2 text-sm font-semibold text-slate-700 backdrop-blur-xl transition hover:bg-white/80 dark:border-slate-700/50 dark:bg-slate-950/50 dark:text-slate-200"
    >
      <ArrowLeft class="size-4" />
      {{ backLabel }}
    </RouterLink>

    <div v-if="loading" class="glass-surface grid min-h-64 place-items-center rounded-2xl">
      <Loader2 class="size-7 animate-spin text-violet-400" />
    </div>

    <div v-else-if="error" class="glass-surface rounded-2xl p-8 text-sm text-rose-600 dark:text-rose-300">
      {{ error }}
    </div>

    <div v-else-if="!item" class="glass-surface rounded-2xl p-8">
      <p class="text-lg font-semibold text-slate-950 dark:text-white">内容不存在</p>
    </div>

    <article v-else class="overflow-hidden rounded-[1.5rem]">
      <div
        class="min-h-72 bg-cover bg-center"
        :class="fallbackTone"
        :style="item.coverUrl ? { backgroundImage: `url(${item.coverUrl})` } : undefined"
      ></div>
      <div class="glass-surface rounded-b-[1.5rem] p-6 sm:p-8">
        <h1 class="text-4xl font-bold leading-tight text-slate-950 dark:text-white sm:text-5xl">
          {{ item.title }}
        </h1>
        <p v-if="item.summary" class="mt-5 text-base leading-8 text-slate-600 dark:text-slate-300">
          {{ item.summary }}
        </p>

        <div v-if="project?.demoUrl || project?.repoUrl" class="mt-6 flex flex-wrap gap-3">
          <a
            v-if="project?.demoUrl"
            :href="project.demoUrl"
            target="_blank"
            rel="noreferrer"
            class="inline-flex items-center gap-2 rounded-full bg-slate-950 px-4 py-2 text-sm font-semibold text-white dark:bg-white dark:text-slate-950"
          >
            <ExternalLink class="size-4" />
            Demo
          </a>
          <a
            v-if="project?.repoUrl"
            :href="project.repoUrl"
            target="_blank"
            rel="noreferrer"
            class="inline-flex items-center gap-2 rounded-full border border-slate-300/70 bg-white/60 px-4 py-2 text-sm font-semibold text-slate-800 dark:border-slate-700 dark:bg-slate-950/50 dark:text-slate-100"
          >
            <GitBranch class="size-4" />
            Repository
          </a>
        </div>

        <div class="content-prose mt-8 max-w-none" v-html="renderedContent"></div>
      </div>
    </article>
  </section>
</template>
