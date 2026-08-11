<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'

import { getArticles } from '@/api/article'
import { getCategories } from '@/api/category'
import CategoryFilter from '@/components/CategoryFilter.vue'
import ContentListShell from '@/components/ContentListShell.vue'
import type { CategoryItem, ContentItem, PageResponse } from '@/types/content'

const pageData = ref<PageResponse<ContentItem> | null>(null)
const categories = ref<CategoryItem[]>([])
const activeCategoryId = ref(0)
const loading = ref(false)
const error = ref('')
const current = ref(1)
const size = 9

async function loadArticles() {
  loading.value = true
  error.value = ''
  try {
    pageData.value = await getArticles({
      page: current.value,
      size,
      categoryId: activeCategoryId.value || undefined,
    })
  } catch {
    error.value = '文章列表加载失败，请确认后端服务已启动。'
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  try {
    categories.value = await getCategories('article')
  } catch {
    categories.value = []
  }
}

watch(activeCategoryId, () => {
  current.value = 1
  void loadArticles()
})

function previous() {
  if (current.value <= 1) return
  current.value -= 1
  void loadArticles()
}

function next() {
  if (!pageData.value || current.value >= Math.ceil(pageData.value.total / pageData.value.size)) return
  current.value += 1
  void loadArticles()
}

onMounted(() => {
  void loadCategories()
  void loadArticles()
})
</script>

<template>
  <CategoryFilter v-model="activeCategoryId" :categories="categories" />
  <ContentListShell
    title="文章"
    description="完整记录技术实践、问题复盘和长期思考。"
    :page-data="pageData"
    :loading="loading"
    :error="error"
    detail-base="/articles"
    fallback-tone="bg-[radial-gradient(circle_at_25%_20%,rgba(14,165,233,0.35),transparent_36%),linear-gradient(135deg,#e0f2fe,#f8fafc)] dark:bg-[radial-gradient(circle_at_25%_20%,rgba(14,165,233,0.28),transparent_36%),linear-gradient(135deg,#0f172a,#111827)]"
    @previous="previous"
    @next="next"
  />
</template>
