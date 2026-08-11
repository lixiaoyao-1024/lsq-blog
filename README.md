# personal-blog

前后端分离的个人博客，界面为中文玻璃拟态（glassmorphism）设计，支持亮 / 暗双主题。

- `blog-backend/` — Spring Boot 4 + Java 21 + MyBatis-Plus + MySQL 后端
- `blog-frontend/` — Vue 3 + Vite + TypeScript + Tailwind CSS 4 前端

## 功能

- **内容浏览**：文章 / 项目 / 杂谈三类内容，列表分页 + 分类筛选 + 详情页
- **内容创作**：内置管理后台（`/admin`）与 Markdown 写作编辑器（`/editor`，基于 md-editor-v3），支持草稿 / 发布 / 隐藏、封面图上传、正文图片上传
- **照片墙**（`/photos`）：瀑布流布局 + 懒加载，支持图片上传与删除
- **全局音乐播放器**：基于 APlayer + MetingJS 的第三方音乐方案（音频不落本地、数据库不存二进制），从网易云 / QQ 音乐拉取歌单，右下角悬浮，路由切换不中断；歌单由后端全局配置控制
- **主题切换**：亮色 / 暗色，跟随系统偏好并记忆用户选择
- **文件服务**：后端文件上传接口（单文件最大 20MB）与 `/uploads` 静态访问
- **接口文档**：Swagger UI，路径 `/swagger-ui.html`

## 技术栈

### 后端 `blog-backend/`

| 分类 | 技术 |
|---|---|
| 语言 | Java 21 |
| 框架 | Spring Boot 4.1.0 |
| ORM | MyBatis-Plus 3.5.15（`mybatis-plus-spring-boot4-starter`） |
| 数据库 | MySQL（默认 `localhost:3306/personal_blog`，账号 `root` / 密码 `123456`） |
| 接口文档 | SpringDoc OpenAPI 2.3（Swagger UI） |
| 构建工具 | Maven（`mvnw` wrapper） |

### 前端 `blog-frontend/`

| 分类 | 技术 |
|---|---|
| 框架 | Vue 3.5（Composition API + `<script setup>`） |
| 构建工具 | Vite 8 |
| 语言 | TypeScript |
| 样式 | Tailwind CSS 4（通过 `@tailwindcss/vite` 插件，无配置文件） |
| 状态管理 | Pinia |
| 路由 | Vue Router 5 |
| HTTP | Axios |
| Markdown | markdown-it（渲染）+ md-editor-v3（写作编辑器） |
| 弹窗 / 消息 | element-plus（`ElMessageBox` / `ElMessage`） |
| 图标 | lucide-vue |
| 音频播放 | APlayer + MetingJS（第三方 CDN，`<head>` 引入） |

## 快速开始

需要 Java 21、Node `^22.18.0 || >=24.12.0`、MySQL（默认 `localhost:3306`，账号 `root` / 密码 `123456`，可修改 `blog-backend/src/main/resources/application.yaml`）。

后端（自动建库建表并写入示例数据）：

```sh
cd blog-backend
./mvnw spring-boot:run   # Windows：mvnw.cmd spring-boot:run
```

前端：

```sh
cd blog-frontend
npm install
npm run dev
```

访问 http://localhost:5173 ，后端 API 与 `/uploads` 静态资源均由 Vite 代理到 http://localhost:8080 。

## 目录

```
personal-blog/
├── blog-backend/     # Spring Boot 后端（controller / service / mapper / entity / config / common）
├── blog-frontend/    # Vue 3 前端（api / components / router / stores / types / views / config）
└── uploads/          # 上传文件目录（由后端 app.upload-dir 配置，默认 ./uploads）
```
