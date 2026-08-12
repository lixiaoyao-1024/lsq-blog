<script setup lang="ts">
import type { CSSProperties } from 'vue'
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { RouterView } from 'vue-router'

import { getMusicConfig } from '@/api/musicConfig'
import bgImage from '@/assets/img/bg.webp'
import LoginDialog from '@/components/LoginDialog.vue'
import NavBar from '@/components/NavBar.vue'
import { METING_API_SERVER } from '@/config/music'
import { useAuthStore } from '@/stores/auth'
import { useThemeStore } from '@/stores/theme'

const themeStore = useThemeStore()
const authStore = useAuthStore()

// APlayer + MetingJS 悬浮播放器的挂载点（只在 App 根组件渲染一次，路由切换不重建，音乐不中断）
const musicHost = ref<HTMLElement | null>(null)

onMounted(() => {
  themeStore.initTheme()
  void loadMusicPlayer()
  // 接口 401（未登录 / 令牌过期）时全局弹出登录框
  window.addEventListener('auth:required', onAuthRequired)
})

onBeforeUnmount(() => {
  window.removeEventListener('auth:required', onAuthRequired)
})

function onAuthRequired() {
  authStore.openLogin()
}

/** 根据后端全局配置动态创建 <meting-js>，由 MetingJS 渲染固定右下角的 APlayer */
async function loadMusicPlayer() {
  const host = musicHost.value
  if (!host) return

  let config
  try {
    config = await getMusicConfig()
  } catch {
    return // 配置接口异常时不渲染播放器，不阻塞页面
  }
  // fixedEnabled=0 表示关闭右下角悬浮播放器
  if (!config?.resourceId || config.fixedEnabled === 0) return
  if (!(window as unknown as Record<string, unknown>).APlayer) {
    console.warn('[music] APlayer/MetingJS 未从 CDN 加载成功，播放器已跳过')
    return
  }

  // APlayer fixed 模式把播放器 DOM 挂到 document.body 上，host.innerHTML='' 清不掉残留；
  // 若此前配置过其它歌单（HMR/重复挂载），旧实例会叠加在右下角显示旧歌单。这里先销毁全部旧实例再重建
  const aplayers = (window as unknown as { APlayer?: { aplayers?: Array<{ destroy: () => void }> } }).APlayer?.aplayers
  aplayers?.forEach((player) => {
    try {
      player.destroy()
    } catch {
      // 忽略个别实例的销毁异常
    }
  })
  document.querySelectorAll('meting-js').forEach((node) => node.remove())
  host.innerHTML = ''
  const el = document.createElement('meting-js')
  el.setAttribute('server', config.platform || 'netease')
  el.setAttribute('type', config.resourceType || 'playlist')
  el.setAttribute('id', String(config.resourceId))
  el.setAttribute('api', METING_API_SERVER)
  el.setAttribute('fixed', 'true')
  el.setAttribute('autoplay', config.autoplay ? 'true' : 'false')
  el.setAttribute('theme', '#8b5cf6')
  el.setAttribute('loop', 'all')
  el.setAttribute('order', 'list')
  el.setAttribute('preload', 'auto')
  el.setAttribute('volume', '0.7')
  el.setAttribute('mutex', 'true')
  el.setAttribute('list-folded', 'false')
  el.setAttribute('lrc-type', '1')
  host.appendChild(el)
}

// 背景星点（固定位置，轻微闪烁）
const starColors = ['rgba(139, 92, 246, 0.55)', 'rgba(125, 211, 252, 0.5)', 'rgba(240, 171, 252, 0.5)']
const stars = Array.from({ length: 26 }, (_, i) => ({
  id: i,
  style: {
    top: `${Math.random() * 100}%`,
    left: `${Math.random() * 100}%`,
    width: `${1 + Math.random() * 2}px`,
    height: `${1 + Math.random() * 2}px`,
    background: starColors[i % starColors.length],
    boxShadow: `0 0 8px ${starColors[i % starColors.length]}`,
    animationDelay: `${Math.random() * 4}s`,
  } as CSSProperties,
}))

// 缓慢漂浮的微光粒子（白 / 淡蓝 / 淡紫，非常轻微）
const particles = Array.from({ length: 16 }, (_, i) => {
  const color =
    i % 3 === 0
      ? ['rgba(255, 255, 255, 0.8)', 'rgba(124, 58, 237, 0.55)']
      : i % 3 === 1
        ? ['rgba(224, 242, 254, 0.8)', 'rgba(56, 189, 248, 0.5)']
        : ['rgba(237, 233, 254, 0.8)', 'rgba(168, 85, 247, 0.5)']
  return {
    id: i,
    style: {
      top: `${Math.random() * 100}%`,
      left: `${Math.random() * 100}%`,
      width: `${2 + Math.random() * 3}px`,
      height: `${2 + Math.random() * 3}px`,
      '--p-color-light': color[1],
      '--p-color': color[0],
      '--p-dur': `${8 + Math.random() * 10}s`,
      '--p-delay': `${-Math.random() * 10}s`,
      '--p-dx': `${-12 + Math.random() * 24}px`,
      '--p-dy': `${-56 - Math.random() * 32}px`,
      '--p-o-min': '0.12',
      '--p-o-max': `${0.35 + Math.random() * 0.4}`,
    } as CSSProperties,
  }
})
</script>

<template>
  <div class="relative min-h-screen bg-transparent text-slate-800 transition-colors duration-300 dark:text-slate-100">
    <!-- 全局背景：照片底图 + 梦幻渐变蒙版 + 光晕 + 星点 + 漂浮粒子 -->
    <div class="bg-scene" aria-hidden="true">
      <img
        :src="bgImage"
        alt=""
        class="absolute inset-0 h-full w-full object-cover opacity-60 transition-opacity duration-700 dark:opacity-75"
      />
      <div class="absolute inset-0 bg-gradient-to-b from-[#f1f2fb]/30 via-[#e9e8f8]/15 to-[#f3effb]/40 dark:from-[#0a0e24]/42 dark:via-[#141138]/24 dark:to-[#090a18]/48"></div>
      <div class="halo left-[-14rem] top-[-12rem] h-[34rem] w-[34rem] bg-violet-300/25 dark:bg-indigo-500/12"></div>
      <div class="halo right-[-12rem] top-[16rem] h-[36rem] w-[36rem] bg-sky-300/20 dark:bg-fuchsia-500/9"></div>
      <div class="halo bottom-[-14rem] left-[30%] h-[32rem] w-[32rem] bg-fuchsia-200/20 dark:bg-cyan-400/8"></div>
      <span v-for="s in stars" :key="`star-${s.id}`" class="star-dot" :style="s.style"></span>
      <span v-for="p in particles" :key="`particle-${p.id}`" class="particle" :style="p.style"></span>
    </div>

    <NavBar />

    <main class="relative z-10 mx-auto w-full max-w-7xl px-4 pb-40 pt-28 sm:px-6 lg:px-8">
      <RouterView />
    </main>

    <!-- 悬浮播放器：meting-js 在 fixed 模式下定位右下角，此处仅是挂载容器 -->
    <div ref="musicHost" aria-hidden="true"></div>

    <!-- 登录弹窗：增删改操作 / 访问管理页未登录时触发 -->
    <LoginDialog />
  </div>
</template>
