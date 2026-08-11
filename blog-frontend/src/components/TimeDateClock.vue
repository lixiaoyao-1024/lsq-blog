<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

const now = ref(new Date())
let timer: number | undefined

const time = computed(() => {
  const d = now.value
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
})

const date = computed(() =>
  now.value.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long',
  }),
)

onMounted(() => {
  now.value = new Date()
  timer = window.setInterval(() => {
    now.value = new Date()
  }, 1000)
})

onBeforeUnmount(() => {
  if (timer !== undefined) window.clearInterval(timer)
})
</script>

<template>
  <div class="flex flex-col items-center gap-1.5 text-center">
    <p
      class="tabular-nums text-4xl font-extralight tracking-[0.1em] text-slate-800 dark:text-slate-100 [text-shadow:0_0_24px_rgba(139,92,246,0.35)] sm:text-5xl"
    >
      {{ time }}
    </p>
    <p class="text-xs text-slate-500 dark:text-slate-300 sm:text-sm">{{ date }}</p>
  </div>
</template>
