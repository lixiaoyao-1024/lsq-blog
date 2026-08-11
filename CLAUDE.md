# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目简介

personal-blog 是一个个人博客全栈项目，前后端分离，包含两个独立子项目：

- `blog-backend/` — 后端 REST API 服务
- `blog-frontend/` — 前端单页应用（SPA）

主要功能：

- **内容浏览**：文章、项目、杂谈（笔记）三个内容模块，均支持列表分页与详情查看
- **全局音乐播放器**：播放/暂停、上一曲/下一曲、进度与音量调节、静音、列表循环/单曲循环/随机播放三种模式
- **主题切换**：亮色 / 暗色双主题，跟随系统偏好并记忆用户选择
- **接口文档**：后端内置 Swagger UI，路径为 `/swagger-ui.html`

界面文案为中文，采用玻璃拟态（glassmorphism）设计风格。目前后端接口均为公开只读接口（仅 GET），暂无登录与后台管理。

## 技术栈

### 后端 `blog-backend/`

| 分类 | 技术 |
|---|---|
| 语言 | Java 21 |
| 框架 | Spring Boot 4.1.0 |
| ORM | MyBatis-Plus 3.5.5（Mappers 继承 `BaseMapper<T>`） |
| 数据库 | MySQL（默认 `localhost:3306/personal_blog`，账号 `root`） |
| 接口文档 | SpringDoc OpenAPI 2.x（Swagger UI） |
| 构建工具 | Maven（使用 `mvnw` wrapper） |
| 其他 | Lombok |

### 前端 `blog-frontend/`

| 分类 | 技术 |
|---|---|
| 框架 | Vue 3（Composition API + `<script setup>`） |
| 构建工具 | Vite 8 |
| 语言 | TypeScript |
| 样式 | Tailwind CSS 4（通过 `@tailwindcss/vite` 插件，无配置文件） |
| 状态管理 | Pinia |
| 路由 | Vue Router |
| HTTP | Axios |
| Markdown 渲染 | markdown-it（前端渲染，`html: false`） |
| 音频播放 | APlayer + MetingJS（第三方 CDN，`<head>` 引入；音频不落本地） |
| 图标 | lucide-vue |
| 弹窗/消息 | element-plus（`ElMessageBox` / `ElMessage`，按需引入） |

## 开发命令

### 后端（在 `blog-backend/` 目录下执行）

```sh
./mvnw spring-boot:run   # 启动后端（Windows：mvnw.cmd spring-boot:run）
./mvnw test              # 运行测试
./mvnw clean package     # 打包
```

### 前端（在 `blog-frontend/` 目录下执行）

```sh
npm install           # 安装依赖（需要 Node ^22.18 或 >=24.12）
npm run dev           # 启动开发服务器（默认 5173 端口，/api 代理到 http://localhost:8080）
npm run type-check    # 仅类型检查（vue-tsc）
npm run build         # 类型检查 + 构建
npm run preview       # 预览构建产物
```

## 目录结构

```
personal-blog/
├── blog-backend/                 # Spring Boot 后端
│   └── src/main/java/com/zmr/blogbackend/
│       ├── controller/           # REST 控制器（articles / projects / notes / music 配置 / files）
│       ├── service/              # 服务接口
│       │   └── impl/             # 服务实现
│       ├── mapper/               # MyBatis-Plus Mapper
│       ├── entity/               # 数据库实体
│       ├── config/               # 配置（分页插件、数据库引导）
│       └── common/               # 通用类（PageResponse 分页封装）
└── blog-frontend/                # Vue 3 前端
    └── src/
        ├── api/                  # 后端接口调用封装
        ├── components/           # 通用组件（导航栏、播放器、内容卡片等）
        ├── router/               # 路由配置
        ├── stores/               # Pinia 状态（theme）
        ├── types/                # TypeScript 类型定义
        └── views/                # 页面视图（文章/项目/杂谈列表与详情、首页）
```

## 关键约定与注意事项

- **数据库自动初始化**：后端启动时 `DatabaseBootstrapper` 会按 `application.yaml` 中的 JDBC 地址自动创建数据库（如不存在）；表结构由 `src/main/resources/db/schema.sql` 在每次启动时幂等执行（`CREATE TABLE IF NOT EXISTS`）。
- **公共查询约定**：后端各 Service 实现通过 `publicWrapper()` 过滤 `status = 1 AND deleted = 0`（已发布且未删除）。
- **软删除**：所有 `blog_*` 表都带有 `deleted` 字段（0 正常 / 1 删除），不做物理删除。
- **分页返回结构**：后端 `PageResponse<T>` 返回 `{ records, total, size, current }`，前端 `src/types/content.ts` 有对应类型。
- **数据库字段命名**：JSON 列（如 `tags`、`extra_json`）在实体中直接映射为 `String`，未配置 JSON 类型处理器。
- **前后端字段命名**：后端实体为驼峰命名（`coverUrl`、`publishedTime` 等），前端类型与之对应。
