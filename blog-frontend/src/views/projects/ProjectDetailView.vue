<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

import { getProjectDetail } from '@/api/project'
import ContentDetailShell from '@/components/ContentDetailShell.vue'
import type { ProjectItem } from '@/types/content'

const route = useRoute()
const item = ref<ProjectItem | null>(null)
const loading = ref(false)
const error = ref('')

onMounted(async () => {
  loading.value = true
  try {
    item.value = await getProjectDetail(String(route.params.id))
  } catch {
    error.value = '项目详情加载失败，请确认内容存在且后端服务已启动。'
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
    back-to="/projects"
    back-label="返回项目"
    fallback-tone="bg-[radial-gradient(circle_at_25%_20%,rgba(16,185,129,0.35),transparent_36%),linear-gradient(135deg,#dcfce7,#f8fafc)] dark:bg-[radial-gradient(circle_at_25%_20%,rgba(16,185,129,0.22),transparent_36%),linear-gradient(135deg,#0f172a,#052e2b)]"
  />
</template>
