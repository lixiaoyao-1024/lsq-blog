# blog-frontend

个人博客前端，Vue 3 + Vite + TypeScript + Tailwind CSS 4 构建的单页应用。

## 功能

- 文章 / 项目 / 杂谈列表与详情，支持按分类筛选
- 全局音乐播放器：列表 / 单曲 / 随机三种播放模式，支持 LRC 歌词与沉浸式（全屏）模式
- 亮色 / 暗色主题切换（跟随系统偏好并记忆选择）

## 开发

需要 Node `^22.18.0 || >=24.12.0`。

```sh
npm install        # 安装依赖
npm run dev        # 开发服务器（默认 5173，/api 代理到 http://localhost:8080）
npm run type-check # 类型检查（vue-tsc）
npm run build      # 类型检查 + 生产构建
npm run preview    # 预览构建产物
```

## 目录

- `src/api/` — 后端接口调用封装（按领域拆分）
- `src/components/` — 通用组件（导航栏、播放器、内容外壳、分类筛选等）
- `src/router/` — 路由配置
- `src/stores/` — Pinia 状态（主题、音乐播放器）
- `src/views/` — 页面视图
