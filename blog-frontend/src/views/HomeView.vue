<script setup lang="ts">
import ArticleCarousel from '@/components/ArticleCarousel.vue'
import PhotoWallCard from '@/components/PhotoWallCard.vue'
import ProfileCard from '@/components/ProfileCard.vue'
import SearchBox from '@/components/SearchBox.vue'
import TimeDateClock from '@/components/TimeDateClock.vue'
</script>

<template>
  <!-- 紧凑居中内容块：背景图为主角，元素轻量悬浮 -->
  <section
    class="mx-auto flex min-h-[76vh] w-full max-w-2xl flex-col items-center justify-center gap-5 py-10"
  >
    <TimeDateClock />

    <SearchBox class="mt-1 w-full max-w-md" />

    <ProfileCard class="mt-1 w-full" />

    <div class="grid w-full items-stretch gap-4 sm:grid-cols-2">
      <ArticleCarousel />
      <PhotoWallCard />
    </div>
  </section>
</template>
