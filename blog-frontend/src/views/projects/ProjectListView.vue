<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'

import { getCategories } from '@/api/category'
import { getProjects } from '@/api/project'
import CategoryFilter from '@/components/CategoryFilter.vue'
import ContentListShell from '@/components/ContentListShell.vue'
import type { CategoryItem, PageResponse, ProjectItem } from '@/types/content'

const pageData = ref<PageResponse<ProjectItem> | null>(null)
const categories = ref<CategoryItem[]>([])
const activeCategoryId = ref(0)
const loading = ref(false)
const error = ref('')
const current = ref(1)
const size = 9

async function loadProjects() {
  loading.value = true
  error.value = ''
  try {
    pageData.value = await getProjects({
      page: current.value,
      size,
      categoryId: activeCategoryId.value || undefined,
    })
  } catch {
    error.value = '项目列表加载失败，请确认后端服务已启动。'
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  try {
    categories.value = await getCategories('project')
  } catch {
    categories.value = []
  }
}

watch(activeCategoryId, () => {
  current.value = 1
  void loadProjects()
})

function previous() {
  if (current.value <= 1) return
  current.value -= 1
  void loadProjects()
}

function next() {
  if (!pageData.value || current.value >= Math.ceil(pageData.value.total / pageData.value.size)) return
  current.value += 1
  void loadProjects()
}

onMounted(() => {
  void loadCategories()
  void loadProjects()
})
</script>

<template>
  <CategoryFilter v-model="activeCategoryId" :categories="categories" />
  <ContentListShell
    title="项目"
    description="沉淀已经完成或正在推进的作品，包含截图、技术栈和外部链接。"
    :page-data="pageData"
    :loading="loading"
    :error="error"
    detail-base="/projects"
    fallback-tone="bg-[radial-gradient(circle_at_25%_20%,rgba(16,185,129,0.35),transparent_36%),linear-gradient(135deg,#dcfce7,#f8fafc)] dark:bg-[radial-gradient(circle_at_25%_20%,rgba(16,185,129,0.22),transparent_36%),linear-gradient(135deg,#0f172a,#052e2b)]"
    @previous="previous"
    @next="next"
  />
</template>
