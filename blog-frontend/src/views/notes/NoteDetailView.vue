<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

import { getNoteDetail } from '@/api/note'
import ContentDetailShell from '@/components/ContentDetailShell.vue'
import type { ContentItem } from '@/types/content'

const route = useRoute()
const item = ref<ContentItem | null>(null)
const loading = ref(false)
const error = ref('')

onMounted(async () => {
  loading.value = true
  try {
    item.value = await getNoteDetail(String(route.params.id))
  } catch {
    error.value = '杂谈详情加载失败，请确认内容存在且后端服务已启动。'
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
    back-to="/notes"
    back-label="返回杂谈"
    fallback-tone="bg-[radial-gradient(circle_at_25%_20%,rgba(244,114,182,0.35),transparent_36%),linear-gradient(135deg,#fce7f3,#f8fafc)] dark:bg-[radial-gradient(circle_at_25%_20%,rgba(244,114,182,0.22),transparent_36%),linear-gradient(135deg,#0f172a,#3b1125)]"
  />
</template>
