<script setup lang="ts">
import { Moon, Sun } from '@lucide/vue'

import { useThemeStore } from '@/stores/theme'

const themeStore = useThemeStore()
</script>

<template>
  <button
    type="button"
    class="inline-flex size-10 items-center justify-center rounded-full border border-white/40 bg-white/60 text-slate-700 shadow-sm backdrop-blur-xl transition hover:-translate-y-0.5 hover:bg-white/80 dark:border-slate-700/50 dark:bg-slate-950/55 dark:text-slate-100 dark:hover:bg-slate-900/80"
    :aria-label="themeStore.isDark ? '切换到亮色模式' : '切换到暗色模式'"
    :title="themeStore.isDark ? '切换到亮色模式' : '切换到暗色模式'"
    @click="themeStore.toggle()"
  >
    <Sun v-if="themeStore.isDark" class="size-4" />
    <Moon v-else class="size-4" />
  </button>
</template>
