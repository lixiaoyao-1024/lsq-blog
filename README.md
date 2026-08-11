# personal-blog

前后端分离的个人博客。

- `blog-backend/` — Spring Boot 4 + Java 21 + MyBatis-Plus + MySQL 后端
- `blog-frontend/` — Vue 3 + Vite + TypeScript + Tailwind CSS 4 前端

## 功能

- 文章、项目、杂谈三大内容模块的列表与详情，支持分类筛选
- 全局音乐播放器（列表 / 单曲 / 随机），LRC 歌词同步与沉浸式全屏模式
- 亮色 / 暗色主题切换
- 后端文件上传接口（图片 / 音频 / 视频 / 文档）与本地静态服务
- Swagger UI 接口文档：`/swagger-ui.html`

## 快速开始

需要 Java 21、Node `^22.18.0 || >=24.12.0`、MySQL（默认 `localhost:3306`，账号 `root` / 密码 `123456`，可修改 `blog-backend/src/main/resources/application.yaml`）。

后端（自动建库建表并写入示例数据）：

```sh
cd blog-backend
./mvnw spring-boot:run
```

前端：

```sh
cd blog-frontend
npm install
npm run dev
```

访问 http://localhost:5173 ，后端 API 由 Vite 代理到 http://localhost:8080 。
