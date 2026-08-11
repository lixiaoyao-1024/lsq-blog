package com.zmr.blogbackend.controller;

import com.zmr.blogbackend.entity.MusicConfig;
import com.zmr.blogbackend.service.MusicConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/music")
public class MusicConfigController {

    private final MusicConfigService musicConfigService;

    public MusicConfigController(MusicConfigService musicConfigService) {
        this.musicConfigService = musicConfigService;
    }

    /** 获取全局播放器配置（仅一条） */
    @GetMapping("/config")
    public MusicConfig config() {
        return musicConfigService.getConfig();
    }

    /** 更新全局播放器配置（只允许一条生效，多余配置自动软删除） */
    @PostMapping("/config/update")
    public MusicConfig update(@RequestBody MusicConfig config) {
        return musicConfigService.updateConfig(config);
    }
}
