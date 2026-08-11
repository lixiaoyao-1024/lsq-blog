<script setup lang="ts">
import { MdEditor, type Themes, type ToolbarNames, type UploadImgEvent } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { createArticle, getAdminArticleDetail, updateArticle, type ContentPayload } from '@/api/article'
import { createNote, getAdminNoteDetail, updateNote } from '@/api/note'
import { createProject, getAdminProjectDetail, updateProject } from '@/api/project'
import { getCategories } from '@/api/category'
import { uploadFile } from '@/api/file'
import { useThemeStore } from '@/stores/theme'
import type { CategoryItem } from '@/types/content'

type ContentType = 'article' | 'note' | 'project'

const route = useRoute()
const router = useRouter()
const themeStore = useThemeStore()

const rawType = route.params.type as string
const type: ContentType = rawType === 'note' || rawType === 'project' ? rawType : 'article'
const id = route.params.id ? Number(route.params.id) : null

const typeLabel = computed(() => (type === 'article' ? '文章' : type === 'note' ? '杂谈' : '项目'))

const categories = ref<CategoryItem[]>([])
const saving = ref(false)
const notice = ref('')
const coverInput = ref<HTMLInputElement | null>(null)

const form = reactive({
  title: '',
  slug: '',
  summary: '',
  coverUrl: '',
  categoryId: null as number | null,
  tags: '',
  contentMd: '',
  status: 0,
  sortOrder: 0,
  pinned: 0,
  featured: 0,
  techStack: '',
  demoUrl: '',
  repoUrl: '',
})

const editorTheme = computed<Themes>(() => (themeStore.isDark ? 'dark' : 'light'))

const toolbars: ToolbarNames[] = [
  'bold',
  'italic',
  'strikeThrough',
  'title',
  'quote',
  'code',
  'codeRow',
  'link',
  'image',
  'table',
  'unorderedList',
  'orderedList',
  'task',
  'preview',
  'htmlPreview',
  'catalog',
  'fullscreen',
]

const inputClass =
  'w-full rounded-xl border border-white/30 bg-white/40 px-4 py-2.5 text-sm text-slate-800 backdrop-blur-sm transition placeholder:text-slate-400 focus:border-violet-300/80 focus:outline-none focus:ring-2 focus:ring-violet-400/25 dark:border-white/10 dark:bg-white/5 dark:text-slate-100'
const labelClass = 'mb-1.5 block text-xs font-medium text-slate-500 dark:text-slate-400'

function jsonArrayToString(value: string | null | undefined): string {
  if (!value) return ''
  try {
    const parsed: unknown = JSON.parse(value)
    return Array.isArray(parsed) ? parsed.join(', ') : value
  } catch {
    return value
  }
}

async function loadExisting() {
  if (id == null) return
  if (type === 'article') {
    const item = await getAdminArticleDetail(id)
    form.title = item.title
    form.slug = item.slug ?? ''
    form.summary = item.summary ?? ''
    form.coverUrl = item.coverUrl ?? ''
    form.categoryId = item.categoryId ?? null
    form.tags = jsonArrayToString(item.tags)
    form.contentMd = item.contentMd ?? ''
    form.status = item.status ?? 0
    form.sortOrder = item.sortOrder ?? 0
    form.pinned = item.pinned ?? 0
  } else if (type === 'note') {
    const item = await getAdminNoteDetail(id)
    form.title = item.title
    form.slug = item.slug ?? ''
    form.summary = item.summary ?? ''
    form.coverUrl = item.coverUrl ?? ''
    form.categoryId = item.categoryId ?? null
    form.tags = jsonArrayToString(item.tags)
    form.contentMd = item.contentMd ?? ''
    form.status = item.status ?? 0
    form.sortOrder = item.sortOrder ?? 0
  } else {
    const item = await getAdminProjectDetail(id)
    form.title = item.title
    form.slug = item.slug ?? ''
    form.summary = item.summary ?? ''
    form.coverUrl = item.coverUrl ?? ''
    form.categoryId = item.categoryId ?? null
    form.tags = jsonArrayToString(item.tags)
    form.techStack = jsonArrayToString(item.techStack)
    form.demoUrl = item.demoUrl ?? ''
    form.repoUrl = item.repoUrl ?? ''
    form.contentMd = item.contentMd ?? ''
    form.status = item.status ?? 0
    form.sortOrder = item.sortOrder ?? 0
    form.featured = item.featured ?? 0
  }
}

function splitList(value: string): string[] {
  return value
    .split(/[,，]/)
    .map((s) => s.trim())
    .filter(Boolean)
}

function tagsToJson(): string {
  return JSON.stringify(splitList(form.tags))
}

async function save(publish: boolean) {
  if (!form.title.trim()) {
    notice.value = '请先填写标题'
    return
  }
  saving.value = true
  notice.value = ''
  try {
    const payload: ContentPayload = {
      title: form.title.trim(),
      slug: form.slug.trim() || null,
      summary: form.summary.trim() || null,
      coverUrl: form.coverUrl.trim() || null,
      categoryId: form.categoryId,
      tags: tagsToJson(),
      contentMd: form.contentMd,
      status: publish ? 1 : 0,
      sortOrder: form.sortOrder,
      pinned: type === 'article' ? (form.pinned ? 1 : 0) : undefined,
      featured: type === 'project' ? (form.featured ? 1 : 0) : undefined,
      techStack:
        type === 'project' && form.techStack.trim()
          ? JSON.stringify(splitList(form.techStack))
          : null,
      demoUrl: type === 'project' && form.demoUrl.trim() ? form.demoUrl.trim() : null,
      repoUrl: type === 'project' && form.repoUrl.trim() ? form.repoUrl.trim() : null,
    }
    if (id != null) {
      if (type === 'article') await updateArticle(id, payload)
      else if (type === 'note') await updateNote(id, payload)
      else await updateProject(id, payload)
    } else {
      if (type === 'article') await createArticle(payload)
      else if (type === 'note') await createNote(payload)
      else await createProject(payload)
    }
    notice.value = publish ? '已发布，正在返回…' : '已保存草稿，正在返回…'
    window.setTimeout(() => void router.push('/admin'), 700)
  } catch {
    notice.value = '保存失败，请稍后重试'
  } finally {
    saving.value = false
  }
}

const handleUploadImg: UploadImgEvent = async (files, callback) => {
  const urls: string[] = []
  for (const file of files) {
    try {
      const asset = await uploadFile(file)
      urls.push(asset.url)
    } catch {
      // 单个文件失败跳过，其余继续
    }
  }
  callback(urls)
}

async function onCoverChange(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  try {
    const asset = await uploadFile(file)
    form.coverUrl = asset.url
  } catch {
    notice.value = '封面上传失败'
  } finally {
    input.value = ''
  }
}

onMounted(async () => {
  try {
    categories.value = await getCategories(type)
  } catch {
    categories.value = []
  }
  if (id != null) {
    try {
      await loadExisting()
    } catch {
      notice.value = '加载失败，请检查内容是否存在'
    }
  }
})
</script>

<template>
  <section class="py-6">
    <header class="mb-6 flex flex-wrap items-center justify-between gap-4">
      <div>
        <p class="text-sm font-semibold uppercase tracking-[0.2em] text-violet-500 dark:text-violet-300">
          {{ typeLabel }}
        </p>
        <h1 class="mt-1 text-2xl font-bold text-slate-900 dark:text-white sm:text-3xl">
          {{ id ? `编辑${typeLabel}` : `新建${typeLabel}` }}
        </h1>
      </div>
      <div class="flex items-center gap-2.5">
        <button
          type="button"
          class="rounded-full border border-white/30 bg-white/40 px-4 py-2 text-sm text-slate-600 transition hover:border-violet-300/70 hover:text-violet-600 dark:border-white/10 dark:bg-white/5 dark:text-slate-300"
          @click="router.push('/admin')"
        >
          返回
        </button>
        <button
          type="button"
          :disabled="saving"
          class="rounded-full border border-white/30 bg-white/40 px-4 py-2 text-sm text-slate-600 transition hover:border-violet-300/70 hover:text-violet-600 disabled:opacity-50 dark:border-white/10 dark:bg-white/5 dark:text-slate-300"
          @click="save(false)"
        >
          {{ saving ? '保存中…' : '保存草稿' }}
        </button>
        <button
          type="button"
          :disabled="saving"
          class="rounded-full bg-gradient-to-r from-violet-500 to-fuchsia-500 px-5 py-2 text-sm font-medium text-white shadow-lg shadow-violet-500/30 transition hover:-translate-y-0.5 hover:shadow-violet-500/50 disabled:opacity-50"
          @click="save(true)"
        >
          {{ saving ? '保存中…' : '发布' }}
        </button>
      </div>
    </header>

    <p
      v-if="notice"
      class="mb-4 rounded-full px-4 py-2 text-center text-sm"
      :class="
        notice.includes('失败') || notice.includes('填写')
          ? 'bg-rose-500/10 text-rose-600 dark:text-rose-300'
          : 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-300'
      "
    >
      {{ notice }}
    </p>

    <div class="glass-surface rounded-3xl p-4 sm:p-6">
      <div class="grid gap-4">
        <div>
          <label :class="labelClass" for="f-title">标题 *</label>
          <input id="f-title" v-model="form.title" type="text" placeholder="起一个吸引人的标题" :class="inputClass" />
        </div>

        <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          <div>
            <label :class="labelClass" for="f-category">分类</label>
            <select id="f-category" v-model="form.categoryId" :class="inputClass">
              <option :value="null">未分类</option>
              <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
          </div>
          <div>
            <label :class="labelClass" for="f-tags">标签（逗号分隔）</label>
            <input id="f-tags" v-model="form.tags" type="text" placeholder="Vue, Spring Boot" :class="inputClass" />
          </div>
          <div>
            <label :class="labelClass" for="f-sort">排序（越大越靠前）</label>
            <input id="f-sort" v-model.number="form.sortOrder" type="number" :class="inputClass" />
          </div>
        </div>

        <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          <div v-if="type === 'article'">
            <label class="flex items-center gap-2 pt-5 text-sm text-slate-600 dark:text-slate-300">
              <input v-model="form.pinned" type="checkbox" class="size-4 rounded accent-violet-500" />
              置顶文章
            </label>
          </div>
          <div v-if="type === 'project'">
            <label class="flex items-center gap-2 pt-5 text-sm text-slate-600 dark:text-slate-300">
              <input v-model="form.featured" type="checkbox" class="size-4 rounded accent-violet-500" />
              精选项目
            </label>
          </div>
          <div>
            <label :class="labelClass" for="f-slug">别名（slug，可选）</label>
            <input id="f-slug" v-model="form.slug" type="text" placeholder="url-friendly-slug" :class="inputClass" />
          </div>
        </div>

        <div v-if="type === 'project'" class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          <div>
            <label :class="labelClass" for="f-tech">技术栈（逗号分隔）</label>
            <input id="f-tech" v-model="form.techStack" type="text" placeholder="Vue 3, Spring Boot" :class="inputClass" />
          </div>
          <div>
            <label :class="labelClass" for="f-demo">演示地址</label>
            <input id="f-demo" v-model="form.demoUrl" type="url" placeholder="https://…" :class="inputClass" />
          </div>
          <div>
            <label :class="labelClass" for="f-repo">仓库地址</label>
            <input id="f-repo" v-model="form.repoUrl" type="url" placeholder="https://github.com/…" :class="inputClass" />
          </div>
        </div>

        <div>
          <label :class="labelClass" for="f-summary">摘要</label>
          <textarea
            id="f-summary"
            v-model="form.summary"
            rows="2"
            placeholder="一句话概括内容"
            :class="inputClass"
          ></textarea>
        </div>

        <div>
          <label :class="labelClass" for="f-cover">封面图</label>
          <div class="flex items-center gap-3">
            <input id="f-cover" v-model="form.coverUrl" type="text" placeholder="图片 URL 或上传本地图片" :class="inputClass" />
            <button
              type="button"
              class="shrink-0 rounded-full border border-white/30 bg-white/40 px-4 py-2.5 text-sm text-slate-600 transition hover:border-violet-300/70 hover:text-violet-600 dark:border-white/10 dark:bg-white/5 dark:text-slate-300"
              @click="coverInput?.click()"
            >
              上传
            </button>
            <input ref="coverInput" type="file" accept="image/*" class="hidden" @change="onCoverChange" />
          </div>
          <img
            v-if="form.coverUrl"
            :src="form.coverUrl"
            alt="封面预览"
            class="mt-3 h-32 w-56 rounded-xl border border-white/20 object-cover"
          />
        </div>
      </div>
    </div>

    <div class="mt-6 overflow-hidden rounded-3xl border border-white/15 shadow-2xl shadow-slate-900/20">
      <MdEditor
        v-model="form.contentMd"
        :theme="editorTheme"
        language="zh-CN"
        :toolbars="toolbars"
        style="height: 560px"
        @onUploadImg="handleUploadImg"
      />
    </div>
  </section>
</template>
