<script setup lang="ts">
import { ArrowUpRight } from '@lucide/vue'
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'

import { photos } from '@/assets/img/gallery'
import { fetchTotalPhotoCount, localPhotoCount } from '@/utils/photoCount'

// 取清单中间一张作为卡片背景，避免与网格首图重复
const cover = photos[Math.floor(photos.length / 2)] ?? photos[0]

// 与照片墙页保持一致：本地图片 + 后端已上传图片，先显示本地数量再异步刷新
const totalCount = ref(localPhotoCount)
onMounted(async () => {
  totalCount.value = await fetchTotalPhotoCount()
})
</script>

<template>
  <RouterLink
    to="/photos"
    class="group relative block h-full min-h-40 overflow-hidden rounded-3xl border border-white/15 shadow-2xl shadow-slate-900/30"
  >
    <img
      :src="cover"
      alt="照片墙"
      loading="lazy"
      class="absolute inset-0 h-full w-full object-cover transition-transform duration-700 group-hover:scale-105"
    />
    <div class="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-transparent"></div>
    <div class="absolute inset-0 rounded-3xl ring-1 ring-inset ring-white/10 transition group-hover:ring-violet-300/50"></div>

    <div class="relative flex h-full flex-col justify-end p-4 sm:p-5">
      <span class="inline-flex size-8 items-center justify-center rounded-full border border-white/20 bg-white/10 text-violet-100 backdrop-blur-md">
        <ArrowUpRight class="size-3.5" />
      </span>
      <p class="mt-2.5 text-[10px] font-medium uppercase tracking-[0.22em] text-violet-200/90">Gallery · {{ totalCount }} 张</p>
      <h2 class="mt-0.5 text-lg font-bold text-white">照片墙</h2>
      <p class="mt-1 text-xs leading-5 text-slate-200/90">光影与瞬间，被收集在这片夜空的角落。</p>
    </div>
  </RouterLink>
</template>
