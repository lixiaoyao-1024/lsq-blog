# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目简介

personal-blog 是一个个人博客全栈项目，前后端分离，包含两个独立子项目：

- `blog-backend/` — 后端 REST API 服务
- `blog-frontend/` — 前端单页应用（SPA）

主要功能：

- **内容浏览**：文章、项目、杂谈（笔记）三个内容模块，均支持列表分页、分类筛选与详情查看
- **内容创作**：前端管理后台（`/admin`）与 Markdown 编辑器（`/editor`），支持草稿 / 发布 / 隐藏、封面图与正文图片上传；对应后端 admin 查询与增删改接口
- **照片墙**（`/photos`）：瀑布流布局 + 懒加载，图片上传与删除
- **全局音乐播放器**：APlayer + MetingJS（第三方 CDN，音频不落本地），右下角悬浮，歌单由后端全局配置控制（网易云 / QQ 音乐）
- **主题切换**：亮色 / 暗色双主题，跟随系统偏好并记忆用户选择
- **接口文档**：后端内置 Swagger UI，路径为 `/swagger-ui.html`
- **管理员鉴权**：内容增删改 / 文件上传需管理员登录；游客可自由浏览公开内容。登录后签发无状态签名令牌（HMAC-SHA256），前端全局登录弹窗 + 路由守卫拦截

界面文案为中文，采用玻璃拟态（glassmorphism）设计风格。**鉴权模型**：游客（默认）只能浏览公开 GET 接口；登录后获得管理员权限，可执行所有增删改操作。无角色细分，登录即管理员。

## 技术栈

### 后端 `blog-backend/`

| 分类 | 技术 |
|---|---|
| 语言 | Java 21 |
| 框架 | Spring Boot 4.1.0 |
| ORM | MyBatis-Plus 3.5.15（必须使用 `mybatis-plus-spring-boot4-starter` + `mybatis-plus-jsqlparser`） |
| 数据库 | MySQL（默认 `localhost:3306/personal_blog`，账号 `root` / 密码 `123456`） |
| 接口文档 | SpringDoc OpenAPI 2.3.0（Swagger UI） |
| 构建工具 | Maven（使用 `mvnw` wrapper） |
| 鉴权 | Argon2id 密码哈希（`de.mkammerer:argon2-jvm:2.11`）+ 无状态 HMAC-SHA256 签名令牌（Java 内置 `Mac`，无 JWT 库） |
| 其他 | Lombok |

### 前端 `blog-frontend/`

| 分类 | 技术 |
|---|---|
| 框架 | Vue 3（Composition API + `<script setup>`） |
| 构建工具 | Vite 8 |
| 语言 | TypeScript（`noUncheckedIndexedAccess`，数组索引取值可能为 `undefined`） |
| 样式 | Tailwind CSS 4（通过 `@tailwindcss/vite` 插件，无配置文件） |
| 状态管理 | Pinia |
| 路由 | Vue Router |
| HTTP | Axios |
| Markdown | markdown-it（前端渲染，`html: false`）+ md-editor-v3（写作编辑器，`language="zh-CN"` 传字符串） |
| 弹窗/消息 | element-plus（`ElMessageBox` / `ElMessage`，完整引入 CSS；暗色模式需引入 `theme-chalk/dark/css-vars.css`） |
| 音频播放 | APlayer + MetingJS（第三方 CDN，`index.html` `<head>` 引入；音频不落本地） |
| 图标 | lucide-vue |

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
npm run dev           # 启动开发服务器（默认 5173 端口，/api 与 /uploads 代理到 http://localhost:8080）
npm run type-check    # 仅类型检查（vue-tsc）
npm run build         # 类型检查 + 构建
npm run preview       # 预览构建产物
```

## 目录结构

```
personal-blog/
├── blog-backend/                 # Spring Boot 后端
│   └── src/main/java/com/zmr/blogbackend/
│       ├── controller/           # REST 控制器（articles / projects / notes / categories / files / music 配置 / personal-info / auth）
│       ├── service/              # 服务接口
│       │   └── impl/             # 服务实现
│       ├── mapper/               # MyBatis-Plus Mapper
│       ├── entity/               # 数据库实体
│       ├── dto/                  # 请求/响应 DTO（LoginRequest / LoginResult）
│       ├── config/               # 配置（分页插件、数据库引导、/uploads 静态映射、PasswordHasher、AuthTokenService、AuthInterceptor）
│       └── common/               # 通用类（PageResponse 分页封装）
├── blog-frontend/                # Vue 3 前端
│   └── src/
│       ├── api/                  # 后端接口调用封装（含 admin 接口、文件上传/删除、auth）
│       ├── components/           # 通用组件（导航栏、主题切换、内容外壳、首页卡片、照片墙卡片、登录弹窗等）
│       ├── config/               # 全局配置（Meting API 服务地址等）
│       ├── router/               # 路由配置（含 requiresAuth 守卫与登录后回跳）
│       ├── stores/               # Pinia 状态（theme / auth）
│       ├── types/                # TypeScript 类型定义
│       └── views/                # 页面视图（列表/详情/照片墙/管理后台/写作编辑器/首页/个人信息）
└── uploads/                      # 上传文件目录（后端 app.upload-dir 配置，默认 ./uploads）
```

## 关键约定与注意事项

- **数据库自动初始化**：后端通过 `ApplicationContextInitializer` 在容器初始化（HikariCP / sql.init）之前调用 `DatabaseBootstrapper` 确保数据库存在（不存在则创建）。它通过 Spring `Environment` 读取 JDBC 配置（兼容 `SPRING_DATASOURCE_*` 环境变量覆盖），并内置「指数退避重试」等待 MySQL 就绪（默认 120s，可用 `app.db.init-timeout-seconds` 调整），适配 Docker 中 MySQL 容器启动但内部服务未就绪的竞态。表结构由 `src/main/resources/db/schema.sql` 在每次启动时幂等执行（`CREATE TABLE IF NOT EXISTS`），示例数据由 `data.sql` 幂等写入（`ON DUPLICATE KEY UPDATE`）。
- **公共查询约定**：后端各 Service 实现通过 `publicWrapper()` 过滤 `status = 1 AND deleted = 0`（已发布且未删除）；admin 查询不受 `status` 限制但排除已删除。
- **软删除**：所有 `blog_*` 表都带有 `deleted` 字段（0 正常 / 1 删除），不做物理删除。**例外**：`blog_file_asset` 删除时除软删行外还会移除磁盘文件（`Files.deleteIfExists`）。
- **分页返回结构**：后端 `PageResponse<T>` 返回 `{ records, total, size, current }`，前端 `src/types/content.ts` 有对应类型。
- **文件上传**：`POST /api/files/upload`（表单字段 `file`，单文件最大 20MB），存储名 `UUID + 扩展名` 落在 `./uploads`，访问地址为相对路径 `/uploads/<storageName>`；`WebMvcConfig` 注册了 `/uploads/**` 静态映射。开发环境需在 `vite.config.ts` 中为 `/uploads` 配置代理。
- **音乐播放器配置**：全局仅一条生效配置（`blog_music_config` 表），由 `GET/POST /api/music/config(/update)` 提供；前端 `App.vue` 挂载时读取并动态创建 `<meting-js>`，根组件只初始化一次 → 路由切换音乐不中断。Meting 服务地址在 `src/config/music.ts`。
- **数据库字段命名**：JSON 列（如 `tags`、`extra_json`）在实体中直接映射为 `String`，未配置 JSON 类型处理器。
- **前后端字段命名**：后端实体为驼峰命名（`coverUrl`、`publishedTime` 等），前端类型与之对应。
- **鉴权规则**（`AuthInterceptor` 拦截 `/api/**`）：`POST /api/auth/login` 放行；其它 `/api/auth/**`（`/me`、`/logout`）需令牌；GET 且路径不含 `/admin` 视为公开浏览放行；其余所有写操作（POST/PUT/DELETE）与含 `/admin` 的读接口需携带有效令牌，否则返回 401 `{"message":"未登录或登录已过期"}`。`/uploads/**` 静态资源不在 `/api/**` 下不受影响。
- **登录令牌**：无状态签名令牌 `<base64url(username)>.<expirySeconds>.<signature>`（HMAC-SHA256），由 `AuthTokenService` 签发/校验，服务重启不失效，无需会话存储。密钥与有效期配置在 `app.auth.token-secret` / `app.auth.token-ttl-hours`（默认 7 天）；**生产部署务必更换默认密钥**。
- **管理员账号**：`blog_admin_user` 表，密码以 Argon2id 存储（`PasswordHasher`，参数 m=65536,t=3,p=4）。`data.sql` 幂等写入默认账号 `admin` / `asdf2318655412`（密码仅在种子 SQL 中，修改后请勿恢复默认值）。
- **前端鉴权流程**：`stores/auth.ts` 保存令牌（localStorage），`api/http.ts` 请求拦截器自动附加 `Authorization: Bearer <token>`，响应拦截器对 401（登录接口除外）派发 `auth:required` 事件 → `App.vue` 弹出全局 `LoginDialog`；写操作按钮（照片上传/删除、个人信息增改删）通过 `authStore.requireLogin()` 先行拦截，`/admin`、`/editor` 路由带 `meta.requiresAuth` 由路由守卫拦截（登录成功后自动回跳）。
- **个人信息页游客模式**：`PersonalInfoView` 按登录态区分加载——游客拉公开接口（仅 `status=1`），登录后拉 admin 接口（含隐藏项），并在登录态变化时自动刷新。
