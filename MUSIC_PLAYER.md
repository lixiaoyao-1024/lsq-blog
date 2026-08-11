# 音乐播放器重构说明（APlayer + MetingJS）

将原先「本地存储 MP3 + howler」的播放器重构为 **APlayer + MetingJS** 方案：
音频全部来自第三方音乐服务商 CDN，本地不再存放任何 MP3，数据库不存音频二进制。

> 仅用于非商用个人博客；版权归原音乐平台所有。

---

## 一、架构总览

```
浏览器 App.vue（根组件，仅初始化一次）
   │  GET /api/music/config
   ▼
动态创建 <meting-js server=netease type=playlist id=18244892901 fixed api=...>
   │  MetingJS 通过 Meting API 服务器请求网易云/QQ 接口（自动刷新防盗链播放链接）
   ▼
渲染 APlayer（fixed 悬浮右下角：歌单 / 滚动 LRC / 音量 / 循环 / 上下一首）
```

- 播放器挂载在 **App.vue 根组件**，路由切换不销毁实例 → **页面跳转音乐不中断**
- 配置只存一条（`blog_music_config` 表），全局博客共用同一播放器

---

## 二、数据库（`blog-backend/src/main/resources/db/schema.sql`）

旧的 `blog_music` 表在启动时被丢弃；新建全局配置表：

```sql
DROP TABLE IF EXISTS blog_music;

CREATE TABLE IF NOT EXISTS blog_music_config (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    platform VARCHAR(20) NOT NULL DEFAULT 'netease' COMMENT '音乐平台：netease网易云 / tencentQQ音乐',
    resource_type VARCHAR(20) NOT NULL DEFAULT 'playlist' COMMENT '资源类型：playlist歌单 / song单曲',
    resource_id VARCHAR(64) NOT NULL COMMENT '资源id（歌单id或单曲id）',
    fixed_enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否开启右下角悬浮播放器：1开 0关',
    autoplay TINYINT NOT NULL DEFAULT 0 COMMENT '自动播放开关：1开 0关',
    remark VARCHAR(255) NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '0 normal, 1 deleted',
    PRIMARY KEY (id),
    KEY idx_music_config_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Global music player config (single row)';
```

种子数据（`db/data.sql`，幂等）：默认配置 = 网易云歌单 `18244892901`。

---

## 三、后端接口（`com.zmr.blogbackend.controller.MusicConfigController`）

| 接口 | 方法 | 说明 |
|---|---|---|
| `/api/music/config` | GET | 获取全局播放器配置（仅一条） |
| `/api/music/config/update` | POST | 更新配置（保证只保留一条生效） |

更新示例：

```bash
curl -X POST http://localhost:8080/api/music/config/update \
  -H "Content-Type: application/json" \
  -d '{"platform":"netease","resourceType":"playlist","resourceId":"18244892901","fixedEnabled":1,"autoplay":0,"remark":"我的歌单"}'
```

- `platform`：`netease`（网易云）/ `tencent`（QQ音乐），其余返回 400
- `resourceType`：`playlist`（歌单）/ `song`（单曲），其余返回 400
- 更新时后端会把平台/类型归一为小写并软删除其它多余行

相关代码：
- 实体：`entity/MusicConfig.java` / Mapper：`mapper/MusicConfigMapper.java`
- 服务：`service/MusicConfigService.java` + `service/impl/MusicConfigServiceImpl.java`
- 控制器：`controller/MusicConfigController.java`

---

## 四、前端

### 1. CDN 引入（`blog-frontend/index.html` `<head>`）

```html
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/aplayer/1.10.1/APlayer.min.css" />
<script src="https://cdn.bootcdn.net/ajax/libs/aplayer/1.10.1/APlayer.min.js" defer></script>
<script src="https://cdn.bootcdn.net/ajax/libs/meting/2.0.1/Meting.min.js" defer></script>
```

### 2. 动态渲染（`src/App.vue`）

App.vue 挂载后请求配置并创建 `<meting-js>`：

```ts
const el = document.createElement('meting-js')
el.setAttribute('server', config.platform)      // netease | tencent
el.setAttribute('type', config.resourceType)    // playlist | song
el.setAttribute('id', String(config.resourceId))
el.setAttribute('api', METING_API_SERVER)       // Meting API 服务
el.setAttribute('fixed', 'true')                // 固定右下角悬浮
el.setAttribute('autoplay', 'false')            // 默认不自动播放
el.setAttribute('theme', '#8b5cf6')
el.setAttribute('loop', 'all')
el.setAttribute('order', 'list')
el.setAttribute('volume', '0.7')
el.setAttribute('mutex', 'true')
el.setAttribute('lrc-type', '1')                // 滚动 LRC 歌词
```

播放器能力（APlayer 原生）：歌单列表、滚动 LRC 歌词、音量调节、循环模式（列表/单曲/随机）、上/下一首。

Meting API 地址在 `src/config/music.ts` 的 `METING_API_SERVER`，可随时替换。

---

## 五、部署步骤

1. 后端：改完配置后 **重启 Spring Boot**（`schema.sql` 会自动建表 + 种子默认歌单）。
2. 前端：`npm install && npm run dev`（或 `npm run build` 部署静态产物）。
3. 首次访问首页：右下角出现悬浮播放器，自动加载网易云歌单 `18244892901`。
4. 换歌单/切 QQ 音乐：调用 `POST /api/music/config/update` 后刷新页面即可。

---

## 六、坑点与注意事项

- **网易云防盗链**：MetingJS 不直接请求网易云，而是通过 **Meting API 服务器**中转获取播放链接并自动刷新。公网免费节点 `api.i-meto.com` 可能不稳定，可自建：[metowolf/MetingApi](https://github.com/metowolf/MetingApi)，然后在 `src/config/music.ts` 替换地址。
- **浏览器自动播放限制**：即使 `autoplay` 打开，浏览器也可能拦截未交互的音频自动播放，这是浏览器策略，属正常。
- **旧实例残留**：APlayer `fixed` 模式会把播放器 DOM 挂到 `document.body` 上，仅清空挂载容器（`innerHTML=''`）清不掉。开发时若反复切换过歌单配置（或 HMR 重复挂载），右下角可能叠加多个旧播放器、显示旧歌单。修复方式：重建 `<meting-js>` 前先销毁全部旧实例（遍历 `window.APlayer.aplayers` 调用 `destroy()`，并移除残留的 `meting-js` 节点），参见 `src/App.vue` 的 `loadMusicPlayer()`。
- **CDN 被墙/慢**：国内用户如 bootcdn 访问异常，可换 `cdn.jsdelivr.net` 或 unpkg 同版本路径。
- 本项目仅用于**非商用个人博客**。
