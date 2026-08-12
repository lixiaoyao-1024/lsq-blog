# blog-frontend

个人博客前端，Vue 3 + Vite + TypeScript + Tailwind CSS 4 构建的单页应用，界面为中文玻璃拟态（glassmorphism）设计并支持亮 / 暗双主题。

## 功能

- **内容浏览**：文章 / 项目 / 杂谈列表与详情，支持分页与分类筛选
- **内容管理**（`/admin`）：文章、杂谈、项目的增删改查，草稿 / 发布 / 隐藏状态，标题搜索与分页
- **写作编辑器**（`/editor`）：基于 md-editor-v3 的 Markdown 编辑器，支持封面图上传、正文图片上传
- **照片墙**（`/photos`）：瀑布流布局 + 懒加载，图片上传与删除
- **全局音乐播放器**：APlayer + MetingJS（CDN 引入），右下角悬浮，歌单来自后端全局配置（网易云 / QQ 音乐），路由切换不中断
- **主题切换**：亮色 / 暗色（跟随系统偏好并记忆用户选择）
- **管理员鉴权**：增删改内容 / 文件上传需登录，游客可自由浏览；未登录触发写操作或访问 `/admin`、`/editor` 时弹出全局登录框，登录成功后自动回跳

## 开发

需要 Node `^22.18.0 || >=24.12.0`。

```sh
npm install        # 安装依赖
npm run dev        # 开发服务器（默认 5173，/api 与 /uploads 代理到 http://localhost:8080）
npm run type-check # 类型检查（vue-tsc）
npm run build      # 类型检查 + 生产构建
npm run preview    # 预览构建产物
```

## 目录

- `src/api/` — 后端接口调用封装（文章 / 项目 / 杂谈 / 分类 / 文件 / 音乐配置 / 鉴权）
- `src/components/` — 通用组件（导航栏、主题切换、内容外壳、首页卡片、照片墙卡片、登录弹窗等）
- `src/router/` — 路由配置（`requiresAuth` 守卫，登录后回跳）
- `src/stores/` — Pinia 状态（主题 / 鉴权）
- `src/types/` — TypeScript 类型定义
- `src/views/` — 页面视图（列表 / 详情 / 照片墙 / 管理后台 / 写作编辑器 / 首页 / 个人信息）
- `src/config/` — 全局配置（Meting API 服务地址等）

## 注意

- APlayer / MetingJS 通过 `index.html` 的 `<head>` 以第三方 CDN 引入（不在 `package.json` 中），音频来自网易云 / QQ 音乐，本地不存放任何 MP3。
- 全局音乐播放器在 `src/App.vue` 根组件初始化，根据后端 `/api/music/config` 返回的配置动态创建 `<meting-js>` 元素，路由切换不重建实例、音乐不中断。
- 登录令牌存于 `localStorage`（键 `personal-blog-auth-token`）；`src/api/http.ts` 的请求拦截器自动附加 `Authorization: Bearer` 头，响应拦截器在 401 时（登录接口除外）派发 `auth:required` 事件弹出全局登录框。
- 后端地址可用环境变量 `VITE_API_TARGET` 覆盖（默认 `http://localhost:8080`），例如 `VITE_API_TARGET=http://localhost:8081 npm run dev`。
