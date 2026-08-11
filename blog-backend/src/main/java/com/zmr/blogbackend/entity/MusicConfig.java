package com.zmr.blogbackend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 博客音乐播放器全局配置（全局仅一条生效）。
 * 播放器采用 APlayer + MetingJS 加载第三方音乐 CDN，本表只记录平台/资源信息，不存音频。
 */
@TableName("blog_music_config")
public class MusicConfig {

    @TableId
    private Long id;
    /** 音乐平台：netease网易云 / tencentQQ音乐 */
    private String platform;
    /** 资源类型：playlist歌单 / song单曲 */
    private String resourceType;
    /** 资源id（歌单id或单曲id） */
    private String resourceId;
    /** 是否开启右下角悬浮播放器：1开 0关 */
    private Integer fixedEnabled;
    /** 自动播放开关：1开 0关 */
    private Integer autoplay;
    /** 备注 */
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public Integer getFixedEnabled() { return fixedEnabled; }
    public void setFixedEnabled(Integer fixedEnabled) { this.fixedEnabled = fixedEnabled; }
    public Integer getAutoplay() { return autoplay; }
    public void setAutoplay(Integer autoplay) { this.autoplay = autoplay; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
