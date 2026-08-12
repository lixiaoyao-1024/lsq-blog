CREATE TABLE IF NOT EXISTS blog_category (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    content_type VARCHAR(32) NOT NULL COMMENT 'article, project, note, music or custom type',
    name VARCHAR(64) NOT NULL COMMENT 'Display name',
    slug VARCHAR(96) NULL COMMENT 'URL friendly key',
    description VARCHAR(255) NULL COMMENT 'Short description',
    sort_order INT NOT NULL DEFAULT 0 COMMENT 'Sort weight',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '0 disabled, 1 enabled',
    extra_json JSON NULL COMMENT 'Extension data for future fields',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '0 normal, 1 deleted',
    PRIMARY KEY (id),
    KEY idx_category_type_status (content_type, status, deleted),
    KEY idx_category_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Content categories';

CREATE TABLE IF NOT EXISTS blog_article (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    title VARCHAR(160) NOT NULL COMMENT 'Article title',
    slug VARCHAR(180) NULL COMMENT 'URL friendly key',
    summary VARCHAR(500) NULL COMMENT 'Article summary',
    cover_url VARCHAR(500) NULL COMMENT 'Cover image URL',
    category_id BIGINT UNSIGNED NULL COMMENT 'Category id, no hard foreign key for flexibility',
    tags JSON NULL COMMENT 'Tag list',
    content_md LONGTEXT NULL COMMENT 'Markdown content',
    content_html LONGTEXT NULL COMMENT 'Rendered HTML cache',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0 draft, 1 published, 2 hidden',
    pinned TINYINT NOT NULL DEFAULT 0 COMMENT '0 normal, 1 pinned',
    view_count BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'View count',
    sort_order INT NOT NULL DEFAULT 0 COMMENT 'Sort weight',
    published_time DATETIME NULL COMMENT 'Publish time',
    extra_json JSON NULL COMMENT 'Extension data for future fields',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '0 normal, 1 deleted',
    PRIMARY KEY (id),
    KEY idx_article_status_time (status, deleted, published_time),
    KEY idx_article_category (category_id, status, deleted),
    KEY idx_article_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Blog articles';

CREATE TABLE IF NOT EXISTS blog_project (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    title VARCHAR(160) NOT NULL COMMENT 'Project title',
    slug VARCHAR(180) NULL COMMENT 'URL friendly key',
    summary VARCHAR(500) NULL COMMENT 'Project summary',
    cover_url VARCHAR(500) NULL COMMENT 'Cover image URL',
    category_id BIGINT UNSIGNED NULL COMMENT 'Category id, no hard foreign key for flexibility',
    tags JSON NULL COMMENT 'Tag list',
    tech_stack JSON NULL COMMENT 'Technology stack list',
    screenshots JSON NULL COMMENT 'Screenshot URL list',
    demo_url VARCHAR(500) NULL COMMENT 'Online demo URL',
    repo_url VARCHAR(500) NULL COMMENT 'Repository URL',
    content_md LONGTEXT NULL COMMENT 'Markdown content',
    content_html LONGTEXT NULL COMMENT 'Rendered HTML cache',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0 draft, 1 published, 2 hidden',
    featured TINYINT NOT NULL DEFAULT 0 COMMENT '0 normal, 1 featured',
    view_count BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'View count',
    sort_order INT NOT NULL DEFAULT 0 COMMENT 'Sort weight',
    published_time DATETIME NULL COMMENT 'Publish time',
    extra_json JSON NULL COMMENT 'Extension data for future fields',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '0 normal, 1 deleted',
    PRIMARY KEY (id),
    KEY idx_project_status_time (status, deleted, published_time),
    KEY idx_project_category (category_id, status, deleted),
    KEY idx_project_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Portfolio projects';

CREATE TABLE IF NOT EXISTS blog_note (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    title VARCHAR(160) NOT NULL COMMENT 'Note title',
    slug VARCHAR(180) NULL COMMENT 'URL friendly key',
    summary VARCHAR(500) NULL COMMENT 'Note summary',
    cover_url VARCHAR(500) NULL COMMENT 'Cover image URL',
    category_id BIGINT UNSIGNED NULL COMMENT 'Category id, no hard foreign key for flexibility',
    tags JSON NULL COMMENT 'Tag list',
    content_md LONGTEXT NULL COMMENT 'Markdown content',
    content_html LONGTEXT NULL COMMENT 'Rendered HTML cache',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0 draft, 1 published, 2 hidden',
    view_count BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'View count',
    sort_order INT NOT NULL DEFAULT 0 COMMENT 'Sort weight',
    published_time DATETIME NULL COMMENT 'Publish time',
    extra_json JSON NULL COMMENT 'Extension data for future fields',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '0 normal, 1 deleted',
    PRIMARY KEY (id),
    KEY idx_note_status_time (status, deleted, published_time),
    KEY idx_note_category (category_id, status, deleted),
    KEY idx_note_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Short notes';

-- 旧的本地音频表已废弃（播放器改用 APlayer + MetingJS 加载第三方 CDN），丢弃
DROP TABLE IF EXISTS blog_music;

-- 博客音乐播放器全局配置表（全局仅一条生效配置）
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

-- 后台管理员表（鉴权用，密码为 Argon2id 哈希）
CREATE TABLE IF NOT EXISTS blog_admin_user (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    username VARCHAR(64) NOT NULL COMMENT 'Login username',
    password_hash VARCHAR(255) NOT NULL COMMENT 'Argon2id password hash',
    nickname VARCHAR(64) NULL COMMENT 'Display nickname',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1 enabled, 0 disabled',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '0 normal, 1 deleted',
    PRIMARY KEY (id),
    UNIQUE KEY uk_admin_user_username (username),
    KEY idx_admin_user_status (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Blog admin users';

-- 个人信息条目表（"关于"页展示，可增删改查）
CREATE TABLE IF NOT EXISTS blog_personal_info (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    label VARCHAR(64) NOT NULL COMMENT '条目名称，如：邮箱 / GitHub / 所在城市',
    value VARCHAR(500) NOT NULL COMMENT '条目内容',
    value_type VARCHAR(20) NOT NULL DEFAULT 'text' COMMENT '渲染类型：text纯文本 / link外链 / email邮箱',
    sort_order INT NOT NULL DEFAULT 0 COMMENT 'Sort weight (ascending)',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1 display, 0 hidden',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '0 normal, 1 deleted',
    PRIMARY KEY (id),
    KEY idx_personal_info_sort (status, deleted, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Personal profile info items';

CREATE TABLE IF NOT EXISTS blog_file_asset (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    original_name VARCHAR(255) NOT NULL COMMENT 'Original file name',
    storage_name VARCHAR(255) NOT NULL COMMENT 'Stored file name',
    url VARCHAR(500) NOT NULL COMMENT 'Public access URL',
    mime_type VARCHAR(120) NULL COMMENT 'MIME type',
    file_size BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'File size in bytes',
    storage_type VARCHAR(32) NOT NULL DEFAULT 'local' COMMENT 'local, oss or custom storage',
    bucket VARCHAR(128) NULL COMMENT 'Storage bucket',
    object_key VARCHAR(500) NULL COMMENT 'Storage object key',
    sha256 VARCHAR(64) NULL COMMENT 'File checksum',
    metadata_json JSON NULL COMMENT 'Extension metadata',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '0 normal, 1 deleted',
    PRIMARY KEY (id),
    KEY idx_file_storage (storage_type, deleted),
    KEY idx_file_sha256 (sha256)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Uploaded file assets';
