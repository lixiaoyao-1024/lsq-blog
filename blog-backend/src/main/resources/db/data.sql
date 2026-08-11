-- ============================================================
-- 种子数据（幂等：所有语句使用显式主键 + ON DUPLICATE KEY UPDATE，
-- 每次启动重跑只会更新、不会重复插入）
-- ============================================================

-- ---------- 分类 ----------
INSERT INTO blog_category (id, content_type, name, slug, description, sort_order, status, create_time, update_time, deleted)
VALUES (1, 'article', '前端', 'frontend', '前端开发与工程化实践', 1, 1, '2026-07-01 00:00:00', '2026-07-01 00:00:00', 0)
ON DUPLICATE KEY UPDATE name = VALUES(name), slug = VALUES(slug), description = VALUES(description), sort_order = VALUES(sort_order), status = VALUES(status), deleted = VALUES(deleted);

INSERT INTO blog_category (id, content_type, name, slug, description, sort_order, status, create_time, update_time, deleted)
VALUES (2, 'article', '后端', 'backend', '服务端与数据库实践', 2, 1, '2026-07-01 00:00:00', '2026-07-01 00:00:00', 0)
ON DUPLICATE KEY UPDATE name = VALUES(name), slug = VALUES(slug), description = VALUES(description), sort_order = VALUES(sort_order), status = VALUES(status), deleted = VALUES(deleted);

INSERT INTO blog_category (id, content_type, name, slug, description, sort_order, status, create_time, update_time, deleted)
VALUES (3, 'article', '生活随笔', 'life', '生活记录与随想', 3, 1, '2026-07-01 00:00:00', '2026-07-01 00:00:00', 0)
ON DUPLICATE KEY UPDATE name = VALUES(name), slug = VALUES(slug), description = VALUES(description), sort_order = VALUES(sort_order), status = VALUES(status), deleted = VALUES(deleted);

INSERT INTO blog_category (id, content_type, name, slug, description, sort_order, status, create_time, update_time, deleted)
VALUES (4, 'project', '网站', 'website', '网站与全栈项目', 1, 1, '2026-07-01 00:00:00', '2026-07-01 00:00:00', 0)
ON DUPLICATE KEY UPDATE name = VALUES(name), slug = VALUES(slug), description = VALUES(description), sort_order = VALUES(sort_order), status = VALUES(status), deleted = VALUES(deleted);

INSERT INTO blog_category (id, content_type, name, slug, description, sort_order, status, create_time, update_time, deleted)
VALUES (5, 'project', '工具', 'tool', '命令行与效率工具', 2, 1, '2026-07-01 00:00:00', '2026-07-01 00:00:00', 0)
ON DUPLICATE KEY UPDATE name = VALUES(name), slug = VALUES(slug), description = VALUES(description), sort_order = VALUES(sort_order), status = VALUES(status), deleted = VALUES(deleted);

INSERT INTO blog_category (id, content_type, name, slug, description, sort_order, status, create_time, update_time, deleted)
VALUES (6, 'note', '随想', 'thoughts', '零散的临时想法', 1, 1, '2026-07-01 00:00:00', '2026-07-01 00:00:00', 0)
ON DUPLICATE KEY UPDATE name = VALUES(name), slug = VALUES(slug), description = VALUES(description), sort_order = VALUES(sort_order), status = VALUES(status), deleted = VALUES(deleted);

INSERT INTO blog_category (id, content_type, name, slug, description, sort_order, status, create_time, update_time, deleted)
VALUES (7, 'note', '阅读', 'reading', '阅读摘记与书评', 2, 1, '2026-07-01 00:00:00', '2026-07-01 00:00:00', 0)
ON DUPLICATE KEY UPDATE name = VALUES(name), slug = VALUES(slug), description = VALUES(description), sort_order = VALUES(sort_order), status = VALUES(status), deleted = VALUES(deleted);

-- ---------- 文章 ----------
INSERT INTO blog_article (id, title, slug, summary, category_id, tags, content_md, status, pinned, view_count, sort_order, published_time, create_time, update_time, deleted)
VALUES (1, '从零搭建个人博客：架构与目录设计', 'blog-architecture', '记录这个博客从零搭建的架构选型、目录设计以及关键约定，作为全站的技术总览。', 2, '["架构","Spring Boot","Vue"]',
'# 从零搭建个人博客：架构与目录设计

这是一篇记录博客搭建过程的文章，聊聊前后端分离的整体设计。

## 整体架构

博客采用前后端分离：

- **后端**：Spring Boot 4 + Java 21 + MyBatis-Plus，提供只读 REST 接口
- **前端**：Vue 3 + Vite + TypeScript + Tailwind CSS 4 的单页应用

## 后端约定

- 每个内容域（文章 / 项目 / 杂谈 / 音乐）都是 controller → service → mapper → entity 的分层结构
- 所有公开查询统一过滤 `status = 1 AND deleted = 0`
- 数据库表都带软删除字段 `deleted`，不做物理删除
- 分页返回 `{ records, total, size, current }`，与前端类型一一对应

## 前端约定

- 列表页与详情页复用 `ContentListShell` 与 `ContentDetailShell` 两个外壳组件
- 接口请求统一走 `src/api` 下按领域拆分的模块
- 主题与播放器状态由 Pinia 管理

## 后续计划

- 接入内容管理后台
- 完善沉浸式播放器体验', 1, 1, 128, 0, '2026-07-20 10:00:00', '2026-07-20 10:00:00', '2026-07-20 10:00:00', 0)
ON DUPLICATE KEY UPDATE title = VALUES(title), slug = VALUES(slug), summary = VALUES(summary), category_id = VALUES(category_id), tags = VALUES(tags), content_md = VALUES(content_md), status = VALUES(status), pinned = VALUES(pinned), view_count = VALUES(view_count), published_time = VALUES(published_time), deleted = VALUES(deleted);

INSERT INTO blog_article (id, title, slug, summary, category_id, tags, content_md, status, pinned, view_count, sort_order, published_time, create_time, update_time, deleted)
VALUES (2, 'Vue 3 + Vite 8 + Tailwind CSS 4 工程实践', 'vue-vite-tailwind', '分享前端工程化的一些细节：Vite 代理、Tailwind 暗色模式、vue-tsc 类型检查与构建流程。', 1, '["Vue","Vite","Tailwind"]',
'# Vue 3 + Vite 8 + Tailwind CSS 4 工程实践

## Vite 开发代理

开发环境下前端通过 Vite 代理把 `/api` 转发到后端 8080 端口，避免跨域：

\`\`\`ts
server: {
  proxy: {
    "/api": { target: "http://localhost:8080", changeOrigin: true },
  },
}
\`\`\`

## Tailwind CSS 4 暗色模式

Tailwind 4 不需要配置文件，通过插件接入。暗色模式采用 class 策略：

\`\`\`css
@import "tailwindcss";
@custom-variant dark (&:where(.dark, .dark *));
\`\`\`

## 构建流程

`npm run build` 会并行执行 type-check 与 vite build，任何 TS 类型错误都会阻断发布，保证产物质量。', 1, 0, 96, 0, '2026-07-18 09:30:00', '2026-07-18 09:30:00', '2026-07-18 09:30:00', 0)
ON DUPLICATE KEY UPDATE title = VALUES(title), slug = VALUES(slug), summary = VALUES(summary), category_id = VALUES(category_id), tags = VALUES(tags), content_md = VALUES(content_md), status = VALUES(status), pinned = VALUES(pinned), view_count = VALUES(view_count), published_time = VALUES(published_time), deleted = VALUES(deleted);

INSERT INTO blog_article (id, title, slug, summary, category_id, tags, content_md, status, pinned, view_count, sort_order, published_time, create_time, update_time, deleted)
VALUES (3, '写在七月：慢下来的生活', 'life-in-july', '关于节奏、专注与整理的七月随笔。', 3, '["随笔"]',
'# 写在七月：慢下来的生活

七月的关键词是「慢」。

这个月刻意减少了同时推进的任务数量，把精力集中在一两件真正重要的事情上。结果是：

1. 代码的提交质量明显上升
2. 睡眠变规律了
3. 重新捡起了读完一本书的习惯

慢并不意味着低效。很多时候，快是被碎片化逼出来的假象。', 1, 0, 45, 0, '2026-07-25 20:00:00', '2026-07-25 20:00:00', '2026-07-25 20:00:00', 0)
ON DUPLICATE KEY UPDATE title = VALUES(title), slug = VALUES(slug), summary = VALUES(summary), category_id = VALUES(category_id), tags = VALUES(tags), content_md = VALUES(content_md), status = VALUES(status), pinned = VALUES(pinned), view_count = VALUES(view_count), published_time = VALUES(published_time), deleted = VALUES(deleted);

-- ---------- 项目 ----------
INSERT INTO blog_project (id, title, slug, summary, category_id, tags, tech_stack, demo_url, repo_url, content_md, status, featured, view_count, sort_order, published_time, create_time, update_time, deleted)
VALUES (1, 'personal-blog 个人博客', 'personal-blog', '本文档对应的前后端分离博客系统：文章、项目、杂谈与音乐播放一应俱全。', 4, '["全栈"]', '["Vue 3","Spring Boot","MySQL","Tailwind CSS"]', 'https://example.com', 'https://github.com/example/personal-blog',
'# personal-blog 个人博客

这个项目就是你现在正在浏览的站点。

- 前端：Vue 3 + Vite + TypeScript + Tailwind CSS 4
- 后端：Spring Boot 4 + MyBatis-Plus + MySQL
- 亮点：分类筛选、玻璃拟态 UI、全局音乐播放器与沉浸式歌词', 1, 1, 210, 0, '2026-07-22 12:00:00', '2026-07-22 12:00:00', '2026-07-22 12:00:00', 0)
ON DUPLICATE KEY UPDATE title = VALUES(title), slug = VALUES(slug), summary = VALUES(summary), category_id = VALUES(category_id), tags = VALUES(tags), tech_stack = VALUES(tech_stack), demo_url = VALUES(demo_url), repo_url = VALUES(repo_url), content_md = VALUES(content_md), status = VALUES(status), featured = VALUES(featured), view_count = VALUES(view_count), published_time = VALUES(published_time), deleted = VALUES(deleted);

INSERT INTO blog_project (id, title, slug, summary, category_id, tags, tech_stack, demo_url, repo_url, content_md, status, featured, view_count, sort_order, published_time, create_time, update_time, deleted)
VALUES (2, 'Toolbox 命令行工具集', 'toolbox', '一组用 Java 编写的日常命令行小工具，聚合常用脚本。', 5, '["CLI"]', '["Java","Maven"]', NULL, 'https://github.com/example/toolbox',
'# Toolbox 命令行工具集

聚合常用命令行脚本，例如：

- 文件批量重命名
- 二维码生成
- 时间戳与日期互转

通过一个入口命令分派到各个子命令。', 1, 0, 32, 0, '2026-06-15 15:00:00', '2026-06-15 15:00:00', '2026-06-15 15:00:00', 0)
ON DUPLICATE KEY UPDATE title = VALUES(title), slug = VALUES(slug), summary = VALUES(summary), category_id = VALUES(category_id), tags = VALUES(tags), tech_stack = VALUES(tech_stack), demo_url = VALUES(demo_url), repo_url = VALUES(repo_url), content_md = VALUES(content_md), status = VALUES(status), featured = VALUES(featured), view_count = VALUES(view_count), published_time = VALUES(published_time), deleted = VALUES(deleted);

-- ---------- 杂谈 ----------
INSERT INTO blog_note (id, title, slug, summary, category_id, tags, content_md, status, view_count, sort_order, published_time, create_time, update_time, deleted)
VALUES (1, '读《代码整洁之道》的几条笔记', 'clean-code-notes', '关于命名、函数长度与依赖方向的几条摘记。', 7, '["阅读"]',
'**命名要诚实**：能精确描述行为的名字，本身就是文档。

**函数宜短**：短函数更接近「一个函数只做一件事」的边界。

**依赖方向**：让上层依赖抽象而非实现细节。', 1, 18, 0, '2026-07-28 08:00:00', '2026-07-28 08:00:00', '2026-07-28 08:00:00', 0)
ON DUPLICATE KEY UPDATE title = VALUES(title), slug = VALUES(slug), summary = VALUES(summary), category_id = VALUES(category_id), tags = VALUES(tags), content_md = VALUES(content_md), status = VALUES(status), view_count = VALUES(view_count), published_time = VALUES(published_time), deleted = VALUES(deleted);

INSERT INTO blog_note (id, title, slug, summary, category_id, tags, content_md, status, view_count, sort_order, published_time, create_time, update_time, deleted)
VALUES (2, '关于分类筛选的一次小重构', 'category-refactor', '把分类下拉改成胶囊按钮，顺手把过滤逻辑下沉到了后端。', 6, '["随想"]',
'给文章、项目、杂谈统一加上分类筛选：

- 后端新增 `/api/categories` 与 categoryId 过滤
- 前端抽出可复用的 `CategoryFilter` 组件
- 切换分类时回到第一页

整体改动不大，但三个列表页的行为终于一致了。', 1, 26, 0, '2026-08-01 21:00:00', '2026-08-01 21:00:00', '2026-08-01 21:00:00', 0)
ON DUPLICATE KEY UPDATE title = VALUES(title), slug = VALUES(slug), summary = VALUES(summary), category_id = VALUES(category_id), tags = VALUES(tags), content_md = VALUES(content_md), status = VALUES(status), view_count = VALUES(view_count), published_time = VALUES(published_time), deleted = VALUES(deleted);

-- ---------- 音乐播放器全局配置（默认：网易云歌单 18244892901） ----------
INSERT INTO blog_music_config (id, platform, resource_type, resource_id, fixed_enabled, autoplay, remark, create_time, update_time, deleted)
VALUES (1, 'netease', 'playlist', '18244892901', 1, 0, '默认歌单', '2026-08-11 00:00:00', '2026-08-11 00:00:00', 0)
ON DUPLICATE KEY UPDATE platform = VALUES(platform), resource_type = VALUES(resource_type), resource_id = VALUES(resource_id), fixed_enabled = VALUES(fixed_enabled), autoplay = VALUES(autoplay), remark = VALUES(remark), deleted = VALUES(deleted);
