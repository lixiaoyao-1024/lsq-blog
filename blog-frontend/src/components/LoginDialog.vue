<script setup lang="ts">
import { ElDialog, ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'

import { useAuthStore } from '@/stores/auth'
import router, { consumeRedirectAfterLogin } from '@/router'

const authStore = useAuthStore()

const form = reactive({
  username: '',
  password: '',
})
const submitting = ref(false)

async function submit() {
  if (!form.username.trim() || !form.password) {
    ElMessage.error('请输入用户名和密码')
    return
  }
  submitting.value = true
  try {
    await authStore.login(form.username.trim(), form.password)
    form.password = ''
    ElMessage.success(`欢迎回来，${authStore.nickname || authStore.username}`)
    // 若因访问受保护页面（如 /admin）触发的登录，登录成功后跳转回原目标页
    const target = consumeRedirectAfterLogin()
    if (target && target !== router.currentRoute.value.fullPath) {
      void router.push(target)
    }
  } catch {
    ElMessage.error('用户名或密码错误')
  } finally {
    submitting.value = false
  }
}

// 与写作编辑器一致的输入框样式
const inputClass =
  'w-full rounded-xl border border-white/30 bg-white/40 px-4 py-2.5 text-sm text-slate-800 backdrop-blur-sm transition placeholder:text-slate-400 focus:border-violet-300/80 focus:outline-none focus:ring-2 focus:ring-violet-400/25 dark:border-white/10 dark:bg-white/5 dark:text-slate-100'
const labelClass = 'mb-1.5 block text-xs font-medium text-slate-500 dark:text-slate-400'
</script>

<template>
  <el-dialog
    v-model="authStore.loginDialogVisible"
    title="登录管理后台"
    width="min(92vw, 400px)"
    :append-to-body="true"
    destroy-on-close
  >
    <p class="-mt-1 mb-5 text-sm leading-6 text-slate-500 dark:text-slate-400">
      增删改操作需要管理员权限，请先登录。
    </p>
    <form @submit.prevent="submit">
      <div class="space-y-4">
        <div>
          <label :class="labelClass">用户名</label>
          <input
            v-model="form.username"
            type="text"
            autocomplete="username"
            placeholder="请输入管理员用户名"
            :class="inputClass"
          />
        </div>
        <div>
          <label :class="labelClass">密码</label>
          <input
            v-model="form.password"
            type="password"
            autocomplete="current-password"
            placeholder="请输入密码"
            :class="inputClass"
          />
        </div>
      </div>
      <div class="mt-6 flex justify-end gap-2.5">
        <button
          type="button"
          class="rounded-full border border-white/30 bg-white/40 px-4 py-2 text-sm text-slate-600 transition hover:border-violet-300/70 hover:text-violet-600 dark:border-white/10 dark:bg-white/5 dark:text-slate-300"
          @click="authStore.closeLogin()"
        >
          取消
        </button>
        <button
          type="submit"
          :disabled="submitting"
          class="rounded-full bg-gradient-to-r from-violet-500 to-fuchsia-500 px-5 py-2 text-sm font-medium text-white shadow-lg shadow-violet-500/30 transition hover:-translate-y-0.5 hover:shadow-violet-500/50 disabled:opacity-50"
        >
          {{ submitting ? '登录中…' : '登录' }}
        </button>
      </div>
    </form>
  </el-dialog>
</template>
