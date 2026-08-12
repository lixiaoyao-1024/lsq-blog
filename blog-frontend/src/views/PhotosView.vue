<script setup lang="ts">
import { ChevronLeft, ChevronRight, ImagePlus, Trash2, X } from '@lucide/vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'

import { deleteFile, getFiles, uploadFile, type FileAsset } from '@/api/file'
import { photos } from '@/assets/img/gallery'
import { useAuthStore } from '@/stores/auth'
import { isImageAsset } from '@/utils/photoCount'

const authStore = useAuthStore()

const activeIndex = ref<number | null>(null)
const uploadedAssets = ref<FileAsset[]>([])
const uploading = ref(false)
const photoInput = ref<HTMLInputElement | null>(null)

/** 已上传图片（新上传的在前）+ 本地打包的照片，组成完整的照片墙 */
const allPhotos = computed(() => [...uploadedAssets.value.map((asset) => asset.url), ...photos])
const photoCount = computed(() => allPhotos.value.length)

/** 照片墙中下标 index 对应的是否为可删除的上传图片，是则返回其资源记录 */
function uploadedAssetAt(index: number): FileAsset | null {
  if (index >= uploadedAssets.value.length) return null
  return uploadedAssets.value[index] ?? null
}

const activePhoto = computed(() => (activeIndex.value === null ? null : allPhotos.value[activeIndex.value]))

function openPhoto(index: number) {
  activeIndex.value = index
}

function close() {
  activeIndex.value = null
}

function step(dir: 1 | -1) {
  if (activeIndex.value === null) return
  activeIndex.value = (activeIndex.value + dir + photoCount.value) % photoCount.value
}

function onKeydown(event: KeyboardEvent) {
  if (activeIndex.value === null) return
  if (event.key === 'Escape') close()
  if (event.key === 'ArrowLeft') step(-1)
  if (event.key === 'ArrowRight') step(1)
}

function openUpload() {
  // 上传是写操作：游客点击先弹登录框
  if (!authStore.requireLogin()) return
  photoInput.value?.click()
}

async function onUpload(event: Event) {
  const input = event.target as HTMLInputElement
  const files = Array.from(input.files ?? [])
  if (files.length === 0) return
  uploading.value = true
  try {
    const uploaded: FileAsset[] = []
    for (const file of files) {
      try {
        const asset = await uploadFile(file)
        uploaded.push(asset)
      } catch {
        // 单个文件上传失败跳过，其余继续
      }
    }
    if (uploaded.length > 0) {
      uploadedAssets.value.unshift(...uploaded)
      ElMessage.success(`成功上传 ${uploaded.length} 张照片`)
    } else {
      ElMessage.error('上传失败，请检查文件格式与大小')
    }
  } finally {
    uploading.value = false
    input.value = ''
  }
}

async function removePhoto(asset: FileAsset, index: number) {
  // 删除是写操作：游客点击先弹登录框
  if (!authStore.requireLogin()) return
  try {
    await ElMessageBox.confirm(
      '删除后该照片会从服务器移除，且不可恢复。确定删除吗？',
      '删除照片',
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
    await deleteFile(asset.id)
    uploadedAssets.value = uploadedAssets.value.filter((item) => item.id !== asset.id)
    if (activeIndex.value !== null) {
      if (activeIndex.value === index) {
        activeIndex.value = null // 删除的正是当前预览的照片
      } else if (activeIndex.value > index) {
        activeIndex.value-- // 前方照片被删，预览下标前移
      }
    }
    ElMessage.success('已删除')
  } catch {
    ElMessage.error('删除失败，请稍后重试')
  }
}

onMounted(async () => {
  window.addEventListener('keydown', onKeydown)
  try {
    const assets = await getFiles()
    const seen = new Set<string>()
    const images = assets.filter((asset) => {
      if (!asset.url || seen.has(asset.url)) return false
      seen.add(asset.url)
      return isImageAsset(asset)
    })
    if (images.length > 0) uploadedAssets.value.unshift(...images)
  } catch {
    // 拉取历史上传失败不影响本地照片墙
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('keydown', onKeydown)
  document.body.style.overflow = ''
})

watch(activeIndex, (open) => {
  document.body.style.overflow = open === null ? '' : 'hidden'
})
</script>

<template>
  <section class="py-6">
    <header class="mb-8 flex max-w-3xl flex-wrap items-end justify-between gap-4">
      <div>
        <p class="text-sm font-semibold uppercase tracking-[0.2em] text-violet-500 dark:text-violet-300">Gallery</p>
        <h1 class="mt-2 text-4xl font-bold text-slate-900 dark:text-white sm:text-5xl">照片墙</h1>
        <p class="mt-4 text-base leading-8 text-slate-600 dark:text-slate-300">
          光影与瞬间，被收集在这片夜空的角落。点击任意照片可放大浏览，共 {{ photoCount }} 张。
        </p>
      </div>
      <div class="flex flex-col items-end gap-2">
        <button
          type="button"
          :disabled="uploading"
          class="inline-flex items-center gap-2 rounded-full bg-gradient-to-r from-violet-500 to-fuchsia-500 px-5 py-2.5 text-sm font-medium text-white shadow-lg shadow-violet-500/30 transition hover:-translate-y-0.5 hover:shadow-violet-500/50 disabled:opacity-50"
          @click="openUpload"
        >
          <ImagePlus class="size-4" />
          {{ uploading ? '上传中…' : '上传照片' }}
        </button>
        <input
          ref="photoInput"
          type="file"
          accept="image/*"
          multiple
          class="hidden"
          @change="onUpload"
        />
      </div>
    </header>

    <div class="columns-2 gap-4 sm:columns-3 lg:columns-4 [column-fill:_balance]">
      <button
        v-for="(photo, index) in allPhotos"
        :key="photo"
        type="button"
        class="group relative mb-4 block w-full break-inside-avoid overflow-hidden rounded-2xl border border-white/15 bg-white/5 shadow-lg shadow-slate-900/10 transition hover:-translate-y-1 hover:border-violet-300/50 hover:shadow-xl hover:shadow-violet-500/15"
        :aria-label="`查看第 ${index + 1} 张照片`"
        @click="openPhoto(index)"
      >
        <img
          :src="photo"
          :alt="`照片 ${index + 1}`"
          loading="lazy"
          decoding="async"
          class="h-auto w-full object-cover transition-transform duration-500 group-hover:scale-105"
        />
        <span class="pointer-events-none absolute inset-0 rounded-2xl ring-1 ring-inset ring-white/10 transition group-hover:ring-violet-300/40"></span>
        <button
          v-if="uploadedAssetAt(index)"
          type="button"
          class="absolute right-2 top-2 grid size-8 place-items-center rounded-full border border-white/20 bg-black/45 text-white/90 opacity-0 backdrop-blur-sm transition hover:bg-rose-500/80 hover:text-white focus-visible:opacity-100 group-hover:opacity-100"
          aria-label="删除照片"
          title="删除照片"
          @click.stop="removePhoto(uploadedAssetAt(index)!, index)"
        >
          <Trash2 class="size-4" />
        </button>
      </button>
    </div>
  </section>

  <Teleport to="body">
    <Transition name="lightbox">
      <div
        v-if="activeIndex !== null"
        class="fixed inset-0 z-[70] flex items-center justify-center bg-[#050510]/92 p-4 backdrop-blur-md"
        role="dialog"
        aria-modal="true"
        aria-label="照片预览"
        @click.self="close"
      >
        <button
          type="button"
          class="absolute right-4 top-4 grid size-11 place-items-center rounded-full border border-white/15 bg-white/5 text-slate-200 transition hover:bg-white/15 hover:text-white"
          aria-label="关闭预览"
          title="关闭预览"
          @click="close"
        >
          <X class="size-5" />
        </button>
        <button
          type="button"
          class="absolute left-3 top-1/2 grid size-11 -translate-y-1/2 place-items-center rounded-full border border-white/15 bg-white/5 text-slate-200 transition hover:bg-white/15 hover:text-white sm:left-6"
          aria-label="上一张"
          title="上一张"
          @click="step(-1)"
        >
          <ChevronLeft class="size-5" />
        </button>
        <img
          v-if="activePhoto"
          :src="activePhoto"
          :alt="`照片 ${(activeIndex ?? 0) + 1}`"
          class="max-h-[84vh] max-w-[88vw] rounded-2xl object-contain shadow-2xl shadow-black/60"
        />
        <button
          type="button"
          class="absolute right-3 top-1/2 grid size-11 -translate-y-1/2 place-items-center rounded-full border border-white/15 bg-white/5 text-slate-200 transition hover:bg-white/15 hover:text-white sm:right-6"
          aria-label="下一张"
          title="下一张"
          @click="step(1)"
        >
          <ChevronRight class="size-5" />
        </button>
        <p class="absolute bottom-5 rounded-full bg-black/45 px-4 py-1.5 text-xs font-medium text-slate-200 backdrop-blur-sm">
          {{ (activeIndex ?? 0) + 1 }} / {{ photoCount }}
        </p>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.lightbox-enter-active,
.lightbox-leave-active {
  transition:
    opacity 0.22s ease,
    transform 0.22s ease;
}
.lightbox-enter-from,
.lightbox-leave-to {
  opacity: 0;
  transform: scale(0.98);
}
</style>
