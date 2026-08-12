import { createRouter, createWebHistory } from 'vue-router'
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
    },
    {
      path: '/editor/:type/:id?',
      name: 'editor',
      component: () => import('../views/editor/ContentEditorView.vue'),
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/PersonalInfoView.vue'),
    },
  ],
})

export default router
