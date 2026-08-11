package com.zmr.blogbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zmr.blogbackend.entity.MusicConfig;
import com.zmr.blogbackend.mapper.MusicConfigMapper;
import com.zmr.blogbackend.service.MusicConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class MusicConfigServiceImpl implements MusicConfigService {

    private static final Set<String> SUPPORTED_PLATFORMS = Set.of("netease", "tencent");
    private static final Set<String> SUPPORTED_TYPES = Set.of("playlist", "song");

    private final MusicConfigMapper musicConfigMapper;

    public MusicConfigServiceImpl(MusicConfigMapper musicConfigMapper) {
        this.musicConfigMapper = musicConfigMapper;
    }

    @Override
    public MusicConfig getConfig() {
        MusicConfig config = activeConfig();
        if (config != null) {
            return config;
        }
        // 数据未初始化时返回默认配置，不落库（正常由 data.sql 种子保证存在）
        MusicConfig fallback = new MusicConfig();
        fallback.setPlatform("netease");
        fallback.setResourceType("playlist");
        fallback.setResourceId("18244892901");
        fallback.setFixedEnabled(1);
        fallback.setAutoplay(0);
        return fallback;
    }

    @Override
    public MusicConfig updateConfig(MusicConfig config) {
        validate(config);
        normalize(config);

        MusicConfig existing = activeConfig();
        // 保证全局只有一条生效配置：先把其它非删除行软删除
        List<MusicConfig> others = musicConfigMapper.selectList(
                new LambdaQueryWrapper<MusicConfig>().eq(MusicConfig::getDeleted, 0));
        for (MusicConfig other : others) {
            if (existing != null && other.getId().equals(existing.getId())) {
                continue;
            }
            other.setDeleted(1);
            musicConfigMapper.updateById(other);
        }

        if (existing != null) {
            existing.setPlatform(config.getPlatform());
            existing.setResourceType(config.getResourceType());
            existing.setResourceId(config.getResourceId());
            existing.setFixedEnabled(config.getFixedEnabled() == null ? 1 : config.getFixedEnabled());
            existing.setAutoplay(config.getAutoplay() == null ? 0 : config.getAutoplay());
            existing.setRemark(config.getRemark());
            musicConfigMapper.updateById(existing);
            return existing;
        }

        config.setId(null);
        config.setDeleted(0);
        if (config.getFixedEnabled() == null) {
            config.setFixedEnabled(1);
        }
        if (config.getAutoplay() == null) {
            config.setAutoplay(0);
        }
        musicConfigMapper.insert(config);
        return config;
    }

    private MusicConfig activeConfig() {
        return musicConfigMapper.selectOne(new LambdaQueryWrapper<MusicConfig>()
                .eq(MusicConfig::getDeleted, 0)
                .orderByAsc(MusicConfig::getId)
                .last("LIMIT 1"));
    }

    private void validate(MusicConfig config) {
        if (config == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "配置不能为空");
        }
        if (!StringUtils.hasText(config.getResourceId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "资源id不能为空");
        }
        if (!StringUtils.hasText(config.getPlatform()) || !SUPPORTED_PLATFORMS.contains(config.getPlatform().toLowerCase(Locale.ROOT))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不支持的平台，仅支持 netease / tencent");
        }
        if (!StringUtils.hasText(config.getResourceType()) || !SUPPORTED_TYPES.contains(config.getResourceType().toLowerCase(Locale.ROOT))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不支持的资源类型，仅支持 playlist / song");
        }
    }

    /** 平台与资源类型统一转小写，匹配 MetingJS 的 server / type 取值 */
    private void normalize(MusicConfig config) {
        config.setPlatform(config.getPlatform().toLowerCase(Locale.ROOT));
        config.setResourceType(config.getResourceType().toLowerCase(Locale.ROOT));
    }
}
