<script setup lang="ts">
import { Loader2, Search } from '@lucide/vue'
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { getArticles } from '@/api/article'
import type { ContentItem } from '@/types/content'

const router = useRouter()

const keyword = ref('')
const open = ref(false)
const loading = ref(true)
const articles = ref<ContentItem[]>([])

async function fetchArticles() {
  try {
    const data = await getArticles({ page: 1, size: 100 })
    articles.value = data.records
  } finally {
    loading.value = false
  }
}

function onKeydown(event: KeyboardEvent) {
  if (event.key === 'Escape') open.value = false
}

onMounted(() => {
  void fetchArticles()
  window.addEventListener('keydown', onKeydown)
})

onBeforeUnmount(() => {
  window.removeEventListener('keydown', onKeydown)
})

const results = computed(() => {
  const query = keyword.value.trim().toLowerCase()
  if (!query) return []
  return articles.value.filter((article) => article.title.toLowerCase().includes(query)).slice(0, 6)
})

function go(item: ContentItem) {
  open.value = false
  keyword.value = ''
  void router.push(`/articles/${item.id}`)
}

function onEnter() {
  if (results.value.length > 0) go(results.value[0] as ContentItem)
}

function onBlur() {
  // 延迟关闭，让点击结果先触发
  window.setTimeout(() => {
    open.value = false
  }, 150)
}
</script>

<template>
  <div class="relative">
    <div class="relative">
      <Search class="pointer-events-none absolute left-4 top-1/2 size-3.5 -translate-y-1/2 text-slate-400 dark:text-slate-400" />
      <input
        v-model="keyword"
        type="text"
        placeholder="搜索文章标题…"
        autocomplete="off"
        class="w-full rounded-full border border-white/40 bg-white/35 py-2.5 pl-10 pr-10 text-sm text-slate-800 shadow-[inset_0_2px_10px_rgba(2,6,40,0.06),0_12px_30px_rgba(2,4,30,0.18)] backdrop-blur-2xl transition placeholder:text-slate-400 focus:border-violet-300/80 focus:outline-none focus:ring-2 focus:ring-violet-400/30 dark:border-white/12 dark:bg-white/5 dark:text-slate-100 dark:shadow-[inset_0_2px_10px_rgba(0,0,0,0.3),0_12px_30px_rgba(2,4,30,0.3)]"
        @focus="open = true"
        @blur="onBlur"
        @keydown.enter="onEnter"
      />
      <Loader2
        v-if="loading"
        class="absolute right-4 top-1/2 size-3.5 -translate-y-1/2 animate-spin text-violet-400"
      />
    </div>

    <Transition name="search-pop">
      <div
        v-if="open && keyword.trim() && results.length > 0"
        class="glass-surface absolute inset-x-0 top-full z-30 mt-3 overflow-hidden rounded-3xl p-2"
      >
        <RouterLink
          v-for="result in results"
          :key="result.id"
          :to="`/articles/${result.id}`"
          class="flex items-center gap-3 rounded-2xl px-4 py-3 transition hover:bg-white/60 dark:hover:bg-white/10"
          @click="go(result)"
        >
          <span class="grid size-9 shrink-0 place-items-center rounded-xl bg-gradient-to-br from-indigo-500 to-fuchsia-500 text-sm font-bold text-white">
            {{ result.title.slice(0, 1) }}
          </span>
          <span class="min-w-0">
            <span class="block truncate text-sm font-medium text-slate-800 dark:text-slate-100">{{ result.title }}</span>
            <span class="block truncate text-xs text-slate-500 dark:text-slate-400">{{ result.summary || '暂无摘要' }}</span>
          </span>
        </RouterLink>
      </div>
      <div
        v-else-if="open && keyword.trim() && !loading && results.length === 0"
        class="glass-surface absolute inset-x-0 top-full z-30 mt-3 rounded-3xl p-5 text-center text-sm text-slate-500 dark:text-slate-400"
      >
        未找到相关文章
      </div>
    </Transition>
  </div>
</template>

<style scoped>
.search-pop-enter-active,
.search-pop-leave-active {
  transition:
    opacity 0.18s ease,
    transform 0.18s ease;
}
.search-pop-enter-from,
.search-pop-leave-to {
  opacity: 0;
  transform: translateY(-6px) scale(0.98);
}
</style>
