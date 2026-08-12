<script setup lang="ts">
import { LogOut, Menu, X } from '@lucide/vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ref } from 'vue'
import { RouterLink, useRoute } from 'vue-router'

import avatar from '@/assets/avatar.webp'
import ThemeToggle from '@/components/ThemeToggle.vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const menuOpen = ref(false)
const route = useRoute()

const navItems = [
  { label: '首页', to: '/' },
  { label: '文章', to: '/articles' },
  { label: '项目', to: '/projects' },
  { label: '杂谈', to: '/notes' },
  { label: '照片', to: '/photos' },
  { label: '写作', to: '/admin' },
  { label: '个人信息', to: '/profile' },
]

function isActive(to: string) {
  if (to === '/') return route.path === '/'
  return route.path.startsWith(to)
}

async function onLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '退出登录', {
      confirmButtonText: '退出',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return // 用户取消
  }
  await authStore.logout()
  ElMessage.success('已退出登录')
}
</script>

<template>
  <header class="fixed inset-x-0 top-0 z-40 px-3 py-2 sm:px-6">
    <div class="glass-surface mx-auto flex max-w-7xl items-center justify-between rounded-full px-4 py-2 transition-all duration-300">
      <RouterLink to="/" class="group flex items-center gap-2.5" @click="menuOpen = false">
        <span class="block size-8 overflow-hidden rounded-lg shadow-md shadow-violet-500/30 ring-1 ring-white/30 transition group-hover:-translate-y-0.5 group-hover:shadow-violet-500/45">
          <img :src="avatar" alt="头像" class="size-full object-cover" />
        </span>
        <span class="text-sm font-semibold tracking-wide text-slate-900 dark:text-white">原神 Blog</span>
      </RouterLink>

      <nav class="hidden items-center gap-0.5 md:flex">
        <RouterLink
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="group relative flex flex-col items-center gap-1 px-3 py-1 text-[13px] font-medium transition-colors"
          :class="
            isActive(item.to)
              ? 'text-violet-700 dark:text-violet-200'
              : 'text-slate-600 hover:text-slate-950 dark:text-slate-300 dark:hover:text-white'
          "
        >
          {{ item.label }}
          <span
            class="size-1.5 rounded-full transition-all duration-300"
            :class="
              isActive(item.to)
                ? 'scale-100 bg-violet-500 shadow-[0_0_10px_2px_rgba(167,139,250,0.75)] dark:bg-violet-300'
                : 'scale-0 bg-transparent'
            "
          ></span>
        </RouterLink>
      </nav>

      <div class="flex items-center gap-2">
        <!-- 已登录：显示当前用户并提供退出登录 -->
        <div v-if="authStore.isLoggedIn" class="hidden items-center gap-1 sm:flex">
          <span class="max-w-24 truncate text-[13px] font-medium text-slate-600 dark:text-slate-300">
            {{ authStore.nickname || authStore.username }}
          </span>
          <button
            type="button"
            class="inline-flex size-8 items-center justify-center rounded-full border border-white/40 bg-white/50 text-slate-600 shadow-sm backdrop-blur-xl transition hover:border-rose-300/60 hover:text-rose-500 dark:border-slate-700/50 dark:bg-slate-950/50 dark:text-slate-300 dark:hover:text-rose-300"
            aria-label="退出登录"
            title="退出登录"
            @click="onLogout"
          >
            <LogOut class="size-3.5" />
          </button>
        </div>
        <ThemeToggle />
        <button
          type="button"
          class="inline-flex size-9 items-center justify-center rounded-full border border-white/40 bg-white/50 text-slate-700 shadow-sm backdrop-blur-xl transition hover:bg-white/80 dark:border-slate-700/50 dark:bg-slate-950/50 dark:text-slate-100 md:hidden"
          aria-label="打开导航菜单"
          title="导航菜单"
          @click="menuOpen = !menuOpen"
        >
          <X v-if="menuOpen" class="size-4" />
          <Menu v-else class="size-4" />
        </button>
      </div>
    </div>

    <div
      v-if="menuOpen"
      class="glass-surface mx-auto mt-2 grid max-w-7xl gap-1 rounded-2xl p-2 md:hidden"
    >
      <RouterLink
        v-for="item in navItems"
        :key="item.to"
        :to="item.to"
        class="flex items-center gap-2 rounded-xl px-4 py-3 text-sm font-medium transition"
        :class="
          isActive(item.to)
            ? 'bg-violet-500/15 text-violet-700 dark:text-violet-200'
            : 'text-slate-700 hover:bg-white/70 dark:text-slate-200 dark:hover:bg-white/10'
        "
        @click="menuOpen = false"
      >
        <span
          class="size-1.5 rounded-full"
          :class="isActive(item.to) ? 'bg-violet-500' : 'bg-transparent'"
        ></span>
        {{ item.label }}
      </RouterLink>
    </div>
  </header>
</template>
