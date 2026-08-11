package com.zmr.blogbackend.service;

import com.zmr.blogbackend.entity.MusicConfig;

public interface MusicConfigService {

    /** 获取全局唯一的播放器配置（未初始化时返回默认配置） */
    MusicConfig getConfig();

    /** 更新全局唯一的播放器配置，保证只有一条生效 */
    MusicConfig updateConfig(MusicConfig config);
}
