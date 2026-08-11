import { defineStore } from 'pinia'

const THEME_STORAGE_KEY = 'personal-blog-theme'

type ThemePreference = 'light' | 'dark'

function getPreferredTheme(): ThemePreference {
  const savedTheme = localStorage.getItem(THEME_STORAGE_KEY)
  if (savedTheme === 'light' || savedTheme === 'dark') {
    return savedTheme
  }

  // 默认深色梦幻氛围；亮色为「晨雾」变体，由用户手动切换
  return 'dark'
}

function applyTheme(theme: ThemePreference) {
  document.documentElement.classList.toggle('dark', theme === 'dark')
}

export const useThemeStore = defineStore('theme', {
  state: () => ({
    isDark: false,
    initialized: false,
  }),
  actions: {
    initTheme() {
      const theme = getPreferredTheme()
      this.isDark = theme === 'dark'
      this.initialized = true
      applyTheme(theme)
    },
    toggle() {
      this.isDark = !this.isDark
      const theme: ThemePreference = this.isDark ? 'dark' : 'light'
      applyTheme(theme)
      localStorage.setItem(THEME_STORAGE_KEY, theme)
    },
  },
})
