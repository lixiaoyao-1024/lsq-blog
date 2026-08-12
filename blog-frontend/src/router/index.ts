import { createRouter, createWebHistory } from 'vue-router'

import { useAuthStore } from '@/stores/auth'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/articles',
      name: 'articles',
      component: () => import('../views/articles/ArticleListView.vue'),
    },
    {
      path: '/articles/:id',
      name: 'article-detail',
      component: () => import('../views/articles/ArticleDetailView.vue'),
    },
    {
      path: '/projects',
      name: 'projects',
      component: () => import('../views/projects/ProjectListView.vue'),
    },
    {
      path: '/projects/:id',
      name: 'project-detail',
      component: () => import('../views/projects/ProjectDetailView.vue'),
    },
    {
      path: '/notes',
      name: 'notes',
      component: () => import('../views/notes/NoteListView.vue'),
    },
    {
      path: '/notes/:id',
      name: 'note-detail',
      component: () => import('../views/notes/NoteDetailView.vue'),
    },
    {
      path: '/photos',
      name: 'photos',
      component: () => import('../views/PhotosView.vue'),
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/admin/AdminContentView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/editor/:type/:id?',
      name: 'editor',
      component: () => import('../views/editor/ContentEditorView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/PersonalInfoView.vue'),
    },
  ],
})

/** 因未登录被路由守卫拦下的目标地址，登录成功后跳转回去（由 LoginDialog 消费） */
let redirectAfterLogin: string | null = null

// 管理类页面需登录：游客访问时弹出登录框并回到首页，登录成功后自动跳回原目标
router.beforeEach((to) => {
  if (to.meta.requiresAuth) {
    const authStore = useAuthStore()
    if (!authStore.isLoggedIn) {
      redirectAfterLogin = to.fullPath
      authStore.openLogin()
      return '/'
    }
  }
  return true
})

/** 取出登录成功后应跳转的目标地址并清空；无则返回 null */
export function consumeRedirectAfterLogin(): string | null {
  const target = redirectAfterLogin
  redirectAfterLogin = null
  return target
}

export default router
