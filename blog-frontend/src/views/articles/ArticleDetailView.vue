<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

import { getArticleDetail } from '@/api/article'
import ContentDetailShell from '@/components/ContentDetailShell.vue'
import type { ContentItem } from '@/types/content'

const route = useRoute()
const item = ref<ContentItem | null>(null)
const loading = ref(false)
const error = ref('')

onMounted(async () => {
  loading.value = true
  try {
    item.value = await getArticleDetail(String(route.params.id))
  } catch {
    error.value = '文章详情加载失败，请确认内容存在且后端服务已启动。'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <ContentDetailShell
    :item="item"
    :loading="loading"
    :error="error"
    back-to="/articles"
    back-label="返回文章"
    fallback-tone="bg-[radial-gradient(circle_at_25%_20%,rgba(14,165,233,0.35),transparent_36%),linear-gradient(135deg,#e0f2fe,#f8fafc)] dark:bg-[radial-gradient(circle_at_25%_20%,rgba(14,165,233,0.28),transparent_36%),linear-gradient(135deg,#0f172a,#111827)]"
  />
</template>
