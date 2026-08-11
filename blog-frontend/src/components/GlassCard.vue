<script setup lang="ts">
withDefaults(
  defineProps<{
    as?: string
    interactive?: boolean
  }>(),
  {
    as: 'article',
    interactive: false,
  },
)
</script>

<template>
  <component
    :is="as"
    class="glass-surface rounded-3xl transition-all duration-300"
    :class="interactive ? 'hover:-translate-y-1 hover:border-violet-300/40 hover:shadow-2xl hover:shadow-violet-500/15' : ''"
  >
    <slot />
  </component>
</template>
