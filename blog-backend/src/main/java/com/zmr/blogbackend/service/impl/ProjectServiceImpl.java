package com.zmr.blogbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zmr.blogbackend.common.PageResponse;
import com.zmr.blogbackend.entity.Project;
import com.zmr.blogbackend.mapper.ProjectMapper;
import com.zmr.blogbackend.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    @Override
    public PageResponse<Project> page(long current, long size, Long categoryId) {
        LambdaQueryWrapper<Project> wrapper = publicWrapper()
                .eq(categoryId != null, Project::getCategoryId, categoryId)
                .orderByDesc(Project::getFeatured)
                .orderByDesc(Project::getPublishedTime)
                .orderByDesc(Project::getCreateTime);
        Page<Project> page = projectMapper.selectPage(new Page<>(current, size), wrapper);
        return new PageResponse<>(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
    }

    @Override
    public Project detail(Long id) {
        return projectMapper.selectOne(publicWrapper().eq(Project::getId, id));
    }

    @Override
    public PageResponse<Project> adminPage(long current, long size, String keyword) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<Project>()
                .eq(Project::getDeleted, 0)
                .and(StringUtils.hasText(keyword), w -> w
                        .like(Project::getTitle, keyword)
                        .or()
                        .like(Project::getSummary, keyword))
                .orderByDesc(Project::getUpdateTime);
        Page<Project> page = projectMapper.selectPage(new Page<>(current, size), wrapper);
        return new PageResponse<>(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
    }

    @Override
    public Project adminDetail(Long id) {
        Project project = projectMapper.selectOne(new LambdaQueryWrapper<Project>()
                .eq(Project::getId, id)
                .eq(Project::getDeleted, 0));
        if (project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "项目不存在");
        }
        return project;
    }

    @Override
    public Project create(Project project) {
        requireTitle(project);
        project.setId(null);
        project.setViewCount(0L);
        if (project.getStatus() == null) {
            project.setStatus(0);
        }
        if (project.getFeatured() == null) {
            project.setFeatured(0);
        }
        if (project.getSortOrder() == null) {
            project.setSortOrder(0);
        }
        project.setDeleted(0);
        applyPublishTime(project, false);
        projectMapper.insert(project);
        return project;
    }

    @Override
    public Project update(Long id, Project patch) {
        Project existing = adminDetail(id);
        requireTitle(patch);
        existing.setTitle(patch.getTitle());
        existing.setSlug(patch.getSlug());
        existing.setSummary(patch.getSummary());
        existing.setCoverUrl(patch.getCoverUrl());
        existing.setCategoryId(patch.getCategoryId());
        existing.setTags(patch.getTags());
        existing.setTechStack(patch.getTechStack());
        existing.setScreenshots(patch.getScreenshots());
        existing.setDemoUrl(patch.getDemoUrl());
        existing.setRepoUrl(patch.getRepoUrl());
        existing.setContentMd(patch.getContentMd());
        existing.setStatus(patch.getStatus() == null ? existing.getStatus() : patch.getStatus());
        existing.setFeatured(patch.getFeatured() == null ? existing.getFeatured() : patch.getFeatured());
        existing.setSortOrder(patch.getSortOrder() == null ? existing.getSortOrder() : patch.getSortOrder());
        existing.setExtraJson(patch.getExtraJson());
        applyPublishTime(existing, true);
        projectMapper.updateById(existing);
        return existing;
    }

    @Override
    public void delete(Long id) {
        Project existing = adminDetail(id);
        existing.setDeleted(1);
        projectMapper.updateById(existing);
    }

    /**
     * 发布时间约定：状态为已发布(1)时若未设置发布时间则补齐为当前时间；
     * 新建且非发布状态时清空发布时间；更新时保留已发布的原始时间。
     */
    private void applyPublishTime(Project project, boolean updating) {
        boolean published = project.getStatus() != null && project.getStatus() == 1;
        if (published) {
            if (project.getPublishedTime() == null) {
                project.setPublishedTime(LocalDateTime.now());
            }
        } else if (!updating) {
            project.setPublishedTime(null);
        }
    }

    private void requireTitle(Project project) {
        if (project.getTitle() == null || project.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "标题不能为空");
        }
    }

    private LambdaQueryWrapper<Project> publicWrapper() {
        return new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, 1)
                .eq(Project::getDeleted, 0);
    }
}
