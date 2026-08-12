<script setup lang="ts">
import { ElDialog, ElMessage, ElMessageBox } from 'element-plus'
import { FileText, Link2, Mail, Pencil, Plus, Trash2 } from '@lucide/vue'
import { onMounted, reactive, ref } from 'vue'

import {
  createPersonalInfo,
  deletePersonalInfo,
  getAdminPersonalInfo,
  updatePersonalInfo,
  type PersonalInfo,
  type PersonalInfoType,
} from '@/api/personalInfo'
import avatar from '@/assets/avatar.webp'

const items = ref<PersonalInfo[]>([])
const loading = ref(false)

/** 不同渲染类型对应的图标 */
const typeIcons = {
  text: FileText,
  link: Link2,
  email: Mail,
} as const

const typeLabels: Record<PersonalInfoType, string> = {
  text: '纯文本',
  link: '外链',
  email: '邮箱',
}

async function load() {
  loading.value = true
  try {
    items.value = await getAdminPersonalInfo()
  } catch {
    ElMessage.error('加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// ---------- 新增 / 编辑 ----------
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const form = reactive({
  label: '',
  value: '',
  valueType: 'text' as PersonalInfoType,
  sortOrder: 0,
  visible: true, // 对应后端 status：1 对外显示 / 0 隐藏
})

function openCreate() {
  editingId.value = null
  form.label = ''
  form.value = ''
  form.valueType = 'text'
  form.sortOrder = items.value.length + 1
  form.visible = true
  dialogVisible.value = true
}

function openEdit(item: PersonalInfo) {
  editingId.value = item.id
  form.label = item.label
  form.value = item.value
  form.valueType = (item.valueType as PersonalInfoType) ?? 'text'
  form.sortOrder = item.sortOrder ?? 0
  form.visible = item.status === 1
  dialogVisible.value = true
}

async function submit() {
  if (!form.label.trim() || !form.value.trim()) {
    ElMessage.error('请填写条目名称与内容')
    return
  }
  const payload = {
    label: form.label.trim(),
    value: form.value.trim(),
    valueType: form.valueType,
    sortOrder: form.sortOrder,
    status: form.visible ? 1 : 0,
  }
  try {
    if (editingId.value != null) {
      await updatePersonalInfo(editingId.value, payload)
    } else {
      await createPersonalInfo(payload)
    }
    dialogVisible.value = false
    ElMessage.success(editingId.value != null ? '已更新' : '已新增')
    void load()
  } catch {
    ElMessage.error('保存失败，请稍后重试')
  }
}

async function remove(item: PersonalInfo) {
  try {
    await ElMessageBox.confirm(
      `确定删除「${item.label}」这条信息吗？删除后不可恢复。`,
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
    await deletePersonalInfo(item.id)
    ElMessage.success('已删除')
    void load()
  } catch {
    ElMessage.error('删除失败，请稍后重试')
  }
}

onMounted(load)

// 表单控件样式，与写作编辑器保持一致
const inputClass =
  'w-full rounded-xl border border-white/30 bg-white/40 px-4 py-2.5 text-sm text-slate-800 backdrop-blur-sm transition placeholder:text-slate-400 focus:border-violet-300/80 focus:outline-none focus:ring-2 focus:ring-violet-400/25 dark:border-white/10 dark:bg-white/5 dark:text-slate-100'
const labelClass = 'mb-1.5 block text-xs font-medium text-slate-500 dark:text-slate-400'
</script>

<template>
  <section class="mx-auto max-w-3xl py-6">
    <header class="mb-8 flex flex-wrap items-end justify-between gap-4">
      <div>
        <p class="text-sm font-semibold uppercase tracking-[0.2em] text-violet-500 dark:text-violet-300">Profile</p>
        <h1 class="mt-2 text-4xl font-bold text-slate-900 dark:text-white sm:text-5xl">个人信息</h1>
        <p class="mt-4 text-base leading-8 text-slate-600 dark:text-slate-300">
          关于我的一些信息，可在此处增删改查并调整展示顺序。
        </p>
      </div>
      <button
        type="button"
        class="inline-flex items-center gap-2 rounded-full bg-gradient-to-r from-violet-500 to-fuchsia-500 px-5 py-2.5 text-sm font-medium text-white shadow-lg shadow-violet-500/30 transition hover:-translate-y-0.5 hover:shadow-violet-500/50"
        @click="openCreate"
      >
        <Plus class="size-4" />
        新增信息
      </button>
    </header>

    <!-- 头像名片：与首页 ProfileCard 视觉一致 -->
    <article class="glass-surface overflow-hidden rounded-3xl p-5 sm:p-6">
      <div class="flex items-center gap-4">
        <div class="relative shrink-0">
          <span class="absolute -inset-1.5 rounded-full bg-gradient-to-tr from-violet-500/60 via-fuchsia-400/40 to-sky-400/50 blur-md"></span>
          <div class="relative size-16 overflow-hidden rounded-full shadow-inner ring-2 ring-white/40">
            <img :src="avatar" alt="头像" class="size-full object-cover" />
          </div>
        </div>
        <div class="min-w-0">
          <h2 class="text-xl font-bold text-slate-900 dark:text-white">lsq</h2>
          <p class="mt-1 text-sm text-slate-500 dark:text-slate-400">Personal Profile · 保持简单，保持热爱</p>
        </div>
      </div>
    </article>

    <!-- 个人信息条目列表 -->
    <div class="mt-6 glass-surface rounded-3xl p-4 sm:p-6">
      <div v-if="loading" class="grid min-h-32 place-items-center py-8 text-sm text-slate-500 dark:text-slate-400">
        加载中…
      </div>

      <ul v-else-if="items.length === 0" class="grid min-h-32 place-items-center py-8 text-center text-sm text-slate-500 dark:text-slate-400">
        <li>暂无个人信息，点击右上角「新增信息」开始编辑</li>
      </ul>

      <ul v-else class="divide-y divide-white/10">
        <li
          v-for="item in items"
          :key="item.id"
          class="group flex items-center gap-4 py-3.5"
        >
          <span class="grid size-9 shrink-0 place-items-center rounded-full border border-white/30 bg-white/40 text-violet-600 dark:border-white/10 dark:bg-white/5 dark:text-violet-300">
            <component :is="typeIcons[item.valueType as PersonalInfoType] ?? FileText" class="size-4" />
          </span>

          <div class="min-w-0 flex-1">
            <div class="flex flex-wrap items-center gap-2">
              <span class="text-sm font-semibold text-slate-800 dark:text-slate-100">{{ item.label }}</span>
              <span
                v-if="item.status === 0"
                class="rounded-full bg-white/40 px-2 py-0.5 text-[11px] font-medium text-slate-500 dark:bg-white/10 dark:text-slate-400"
              >
                隐藏
              </span>
            </div>
            <div class="mt-1 truncate text-sm text-slate-500 dark:text-slate-400">
              <a
                v-if="item.valueType === 'email'"
                :href="`mailto:${item.value}`"
                class="transition hover:text-violet-600 dark:hover:text-violet-300"
              >{{ item.value }}</a>
              <a
                v-else-if="item.valueType === 'link'"
                :href="item.value"
                target="_blank"
                rel="noreferrer"
                class="transition hover:text-violet-600 dark:hover:text-violet-300"
              >{{ item.value }}</a>
              <span v-else>{{ item.value }}</span>
            </div>
          </div>

          <div class="flex shrink-0 gap-2">
            <button
              type="button"
              class="grid size-9 place-items-center rounded-full border border-white/30 bg-white/40 text-slate-600 transition hover:border-violet-300/70 hover:text-violet-600 dark:border-white/10 dark:bg-white/5 dark:text-slate-300 dark:hover:text-violet-200"
              aria-label="编辑"
              title="编辑"
              @click="openEdit(item)"
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
    </div>
  </section>

  <!-- 新增 / 编辑弹窗 -->
  <el-dialog
    v-model="dialogVisible"
    :title="editingId == null ? '新增信息' : '编辑信息'"
    width="min(92vw, 480px)"
    :append-to-body="true"
    destroy-on-close
  >
    <form @submit.prevent="submit">
      <div class="space-y-4">
        <div>
          <label :class="labelClass">名称 *</label>
          <input v-model="form.label" type="text" placeholder="如：邮箱 / GitHub / 所在城市" :class="inputClass" />
        </div>
        <div>
          <label :class="labelClass">内容 *</label>
          <input v-model="form.value" type="text" placeholder="条目对应的内容" :class="inputClass" />
        </div>
        <div class="grid gap-4 sm:grid-cols-2">
          <div>
            <label :class="labelClass">渲染类型</label>
            <select v-model="form.valueType" :class="inputClass">
              <option v-for="(text, key) in typeLabels" :key="key" :value="key">{{ text }}</option>
            </select>
          </div>
          <div>
            <label :class="labelClass">排序（越小越靠前）</label>
            <input v-model.number="form.sortOrder" type="number" :class="inputClass" />
          </div>
        </div>
        <label class="flex items-center gap-2 pt-1 text-sm text-slate-600 dark:text-slate-300">
          <input v-model="form.visible" type="checkbox" class="size-4 rounded accent-violet-500" />
          对外显示
        </label>
      </div>
      <div class="mt-6 flex justify-end gap-2.5">
        <button
          type="button"
          class="rounded-full border border-white/30 bg-white/40 px-4 py-2 text-sm text-slate-600 transition hover:border-violet-300/70 hover:text-violet-600 dark:border-white/10 dark:bg-white/5 dark:text-slate-300"
          @click="dialogVisible = false"
        >
          取消
        </button>
        <button
          type="submit"
          class="rounded-full bg-gradient-to-r from-violet-500 to-fuchsia-500 px-5 py-2 text-sm font-medium text-white shadow-lg shadow-violet-500/30 transition hover:-translate-y-0.5 hover:shadow-violet-500/50"
        >
          {{ editingId == null ? '新增' : '保存' }}
        </button>
      </div>
    </form>
  </el-dialog>
</template>
