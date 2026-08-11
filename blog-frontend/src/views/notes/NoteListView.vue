<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'

import { getCategories } from '@/api/category'
import { getNotes } from '@/api/note'
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

async function loadNotes() {
  loading.value = true
  error.value = ''
  try {
    pageData.value = await getNotes({
      page: current.value,
      size,
      categoryId: activeCategoryId.value || undefined,
    })
  } catch {
    error.value = '杂谈列表加载失败，请确认后端服务已启动。'
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  try {
    categories.value = await getCategories('note')
  } catch {
    categories.value = []
  }
}

watch(activeCategoryId, () => {
  current.value = 1
  void loadNotes()
})

function previous() {
  if (current.value <= 1) return
  current.value -= 1
  void loadNotes()
}

function next() {
  if (!pageData.value || current.value >= Math.ceil(pageData.value.total / pageData.value.size)) return
  current.value += 1
  void loadNotes()
}

onMounted(() => {
  void loadCategories()
  void loadNotes()
})
</script>

<template>
  <CategoryFilter v-model="activeCategoryId" :categories="categories" />
  <ContentListShell
    title="杂谈"
    description="更轻量的日常记录、阅读摘记和临时想法。"
    :page-data="pageData"
    :loading="loading"
    :error="error"
    detail-base="/notes"
    fallback-tone="bg-[radial-gradient(circle_at_25%_20%,rgba(244,114,182,0.35),transparent_36%),linear-gradient(135deg,#fce7f3,#f8fafc)] dark:bg-[radial-gradient(circle_at_25%_20%,rgba(244,114,182,0.22),transparent_36%),linear-gradient(135deg,#0f172a,#3b1125)]"
    @previous="previous"
    @next="next"
  />
</template>
