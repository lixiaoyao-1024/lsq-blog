<script setup lang="ts">
import type { CategoryItem } from '@/types/content'

defineProps<{
  categories: CategoryItem[]
  modelValue: number
}>()

defineEmits<{
  'update:modelValue': [value: number]
}>()
</script>

<template>
  <div v-if="categories.length > 0" class="mb-6 flex flex-wrap gap-2">
    <button
      type="button"
      class="rounded-full px-4 py-2 text-sm font-medium transition"
      :class="
        modelValue === 0
          ? 'bg-gradient-to-r from-violet-500 to-fuchsia-500 text-white shadow-lg shadow-violet-500/30'
          : 'border border-white/40 bg-white/55 text-slate-600 backdrop-blur-xl transition hover:border-violet-300/60 hover:text-violet-600 dark:border-white/10 dark:bg-white/5 dark:text-slate-300 dark:hover:border-violet-300/40 dark:hover:text-violet-200'
      "
      @click="$emit('update:modelValue', 0)"
    >
      全部
    </button>
    <button
      v-for="category in categories"
      :key="category.id"
      type="button"
      class="rounded-full px-4 py-2 text-sm font-medium transition"
      :class="
        modelValue === category.id
          ? 'bg-gradient-to-r from-violet-500 to-fuchsia-500 text-white shadow-lg shadow-violet-500/30'
          : 'border border-white/40 bg-white/55 text-slate-600 backdrop-blur-xl transition hover:border-violet-300/60 hover:text-violet-600 dark:border-white/10 dark:bg-white/5 dark:text-slate-300 dark:hover:border-violet-300/40 dark:hover:text-violet-200'
      "
      @click="$emit('update:modelValue', category.id)"
    >
      {{ category.name }}
    </button>
  </div>
</template>
