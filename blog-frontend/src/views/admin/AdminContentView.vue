<script setup lang="ts">
import { ChevronLeft, ChevronRight, Pencil, Plus, Search, Trash2 } from '@lucide/vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { deleteArticle, getAdminArticles } from '@/api/article'
import { deleteNote, getAdminNotes } from '@/api/note'
import { deleteProject, getAdminProjects } from '@/api/project'
import type { ContentItem, ProjectItem } from '@/types/content'

type ContentType = 'article' | 'note' | 'project'

const router = useRouter()

const contentType = ref<ContentType>('article')
const keyword = ref('')
const page = ref(1)
const size = 10
const loading = ref(false)
const records = ref<Array<ContentItem | ProjectItem>>([])
const total = ref(0)

const typeList: { key: ContentType; label: string; url: string }[] = [
  { key: 'article', label: '文章', url: '/articles' },
  { key: 'note', label: '杂谈', url: '/notes' },
  { key: 'project', label: '项目', url: '/projects' },
]

const typeLabels: Record<ContentType, string> = {
  article: '文章',
  note: '杂谈',
  project: '项目',
}

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / size)))

async function load() {
  loading.value = true
  try {
    const params = { page: page.value, size, keyword: keyword.value.trim() || undefined }
    if (contentType.value === 'article') {
      const data = await getAdminArticles(params)
      records.value = data.records
      total.value = data.total
    } else if (contentType.value === 'note') {
      const data = await getAdminNotes(params)
      records.value = data.records
      total.value = data.total
    } else {
      const data = await getAdminProjects(params)
      records.value = data.records
      total.value = data.total
    }
  } catch {
    ElMessage.error('加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

function switchType(type: ContentType) {
  contentType.value = type
  page.value = 1
  void load()
}

function search() {
  page.value = 1
  void load()
}

function createNew() {
  void router.push(`/editor/${contentType.value}`)
}

function edit(item: ContentItem) {
  void router.push(`/editor/${contentType.value}/${item.id}`)
}

async function remove(item: ContentItem) {
  try {
    await ElMessageBox.confirm(
      `确定删除「${item.title}」吗？删除后不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
      },
    )
  } catch {
    return // 用户取消
  }
  try {
    if (contentType.value === 'article') await deleteArticle(item.id)
    else if (contentType.value === 'note') await deleteNote(item.id)
    else await deleteProject(item.id)
    ElMessage.success('已删除')
    void load()
  } catch {
    ElMessage.error('删除失败，请稍后重试')
  }
}

function formatTime(value?: string | null) {
  if (!value) return '—'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

function statusLabel(status?: number) {
  if (status === 1) return '已发布'
  if (status === 2) return '隐藏'
  return '草稿'
}

onMounted(load)
</script>

<template>
  <section class="py-6">
    <header class="mb-6 flex flex-wrap items-end justify-between gap-4">
      <div>
        <p class="text-sm font-semibold uppercase tracking-[0.2em] text-violet-500 dark:text-violet-300">Writer</p>
        <h1 class="mt-1 text-2xl font-bold text-slate-900 dark:text-white sm:text-3xl">内容管理</h1>
        <p class="mt-2 text-sm text-slate-500 dark:text-slate-400">管理文章、杂谈与项目，支持草稿与发布。</p>
      </div>
      <button
        type="button"
        class="inline-flex items-center gap-2 rounded-full bg-gradient-to-r from-violet-500 to-fuchsia-500 px-5 py-2.5 text-sm font-medium text-white shadow-lg shadow-violet-500/30 transition hover:-translate-y-0.5 hover:shadow-violet-500/50"
        @click="createNew"
      >
        <Plus class="size-4" />
        新建{{ typeLabels[contentType] }}
      </button>
    </header>

    <div class="glass-surface rounded-3xl p-4 sm:p-6">
      <div class="flex flex-wrap items-center justify-between gap-3">
        <div class="flex gap-1.5">
          <button
            v-for="t in typeList"
            :key="t.key"
            type="button"
            class="rounded-full px-4 py-1.5 text-sm font-medium transition"
            :class="
              contentType === t.key
                ? 'bg-gradient-to-r from-violet-500 to-fuchsia-500 text-white shadow-lg shadow-violet-500/30'
                : 'border border-white/25 text-slate-600 hover:border-violet-300/70 hover:text-violet-600 dark:border-white/10 dark:text-slate-300 dark:hover:text-violet-200'
            "
            @click="switchType(t.key)"
          >
            {{ t.label }}
          </button>
        </div>

        <div class="relative">
          <Search class="pointer-events-none absolute left-4 top-1/2 size-3.5 -translate-y-1/2 text-slate-400" />
          <input
            v-model="keyword"
            type="text"
            placeholder="搜索标题…"
            class="w-full rounded-full border border-white/40 bg-white/35 py-2 pl-10 pr-4 text-sm text-slate-800 backdrop-blur-xl transition placeholder:text-slate-400 focus:border-violet-300/80 focus:outline-none focus:ring-2 focus:ring-violet-400/30 dark:border-white/12 dark:bg-white/5 dark:text-slate-100 sm:w-56"
            @keydown.enter="search"
          />
        </div>
      </div>

      <div v-if="loading" class="grid min-h-40 place-items-center py-8 text-sm text-slate-500 dark:text-slate-400">
        加载中…
      </div>

      <ul v-else-if="records.length === 0" class="grid min-h-40 place-items-center py-8 text-sm text-slate-500 dark:text-slate-400">
        暂无内容，点击右上角「新建{{ typeLabels[contentType] }}」开始创作
      </ul>

      <ul v-else class="mt-4 divide-y divide-white/10">
        <li v-for="item in records" :key="item.id" class="flex items-center gap-4 py-3.5">
          <div class="min-w-0 flex-1">
            <div class="flex flex-wrap items-center gap-2">
              <span class="truncate text-sm font-medium text-slate-800 dark:text-slate-100">{{ item.title }}</span>
              <span
                class="shrink-0 rounded-full px-2 py-0.5 text-[11px] font-medium"
                :class="
                  item.status === 1
                    ? 'bg-violet-500/15 text-violet-600 dark:text-violet-300'
                    : 'bg-white/40 text-slate-500 dark:bg-white/10 dark:text-slate-400'
                "
              >
                {{ statusLabel(item.status) }}
              </span>
            </div>
            <p class="mt-1 truncate text-xs text-slate-500 dark:text-slate-400">
              {{ item.summary || '暂无摘要' }} · 更新于 {{ formatTime(item.updateTime) }}
            </p>
          </div>
          <div class="flex shrink-0 gap-2">
            <a
              :href="item.status === 1 ? typeList.find((t) => t.key === contentType)?.url + `/${item.id}` : undefined"
              target="_blank"
              rel="noreferrer"
              class="hidden items-center rounded-full px-2 py-1.5 text-xs text-slate-500 transition hover:text-violet-600 dark:text-slate-400 dark:hover:text-violet-200 lg:inline-flex"
            >
              查看
            </a>
            <button
              type="button"
              class="grid size-9 place-items-center rounded-full border border-white/30 bg-white/40 text-slate-600 transition hover:border-violet-300/70 hover:text-violet-600 dark:border-white/10 dark:bg-white/5 dark:text-slate-300 dark:hover:text-violet-200"
              aria-label="编辑"
              title="编辑"
              @click="edit(item)"
            >
              <Pencil class="size-4" />
            </button>
            <button
              type="button"
              class="grid size-9 place-items-center rounded-full border border-white/30 bg-white/40 text-slate-600 transition hover:border-rose-300/70 hover:text-rose-500 dark:border-white/10 dark:bg-white/5 dark:text-slate-300 dark:hover:text-rose-300"
              aria-label="删除"
              title="删除"
              @click="remove(item)"
            >
              <Trash2 class="size-4" />
            </button>
          </div>
        </li>
      </ul>

      <div v-if="total > size" class="mt-5 flex items-center justify-between border-t border-white/10 pt-4">
        <p class="text-xs text-slate-500 dark:text-slate-400">共 {{ total }} 条 · 第 {{ page }} / {{ totalPages }} 页</p>
        <div class="flex gap-2">
          <button
            type="button"
            class="grid size-9 place-items-center rounded-full border border-white/30 bg-white/40 text-slate-600 transition hover:border-violet-300/70 hover:text-violet-600 disabled:opacity-40 dark:border-white/10 dark:bg-white/5 dark:text-slate-300"
            :disabled="page <= 1"
            aria-label="上一页"
            @click="page--; load()"
          >
            <ChevronLeft class="size-4" />
          </button>
          <button
            type="button"
            class="grid size-9 place-items-center rounded-full border border-white/30 bg-white/40 text-slate-600 transition hover:border-violet-300/70 hover:text-violet-600 disabled:opacity-40 dark:border-white/10 dark:bg-white/5 dark:text-slate-300"
            :disabled="page >= totalPages"
            aria-label="下一页"
            @click="page++; load()"
          >
            <ChevronRight class="size-4" />
          </button>
        </div>
      </div>
    </div>
  </section>
</template>
