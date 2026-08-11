package com.zmr.blogbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zmr.blogbackend.common.PageResponse;
import com.zmr.blogbackend.entity.Article;
import com.zmr.blogbackend.mapper.ArticleMapper;
import com.zmr.blogbackend.service.ArticleService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;

    public ArticleServiceImpl(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Override
    public PageResponse<Article> page(long current, long size, Long categoryId) {
        LambdaQueryWrapper<Article> wrapper = publicWrapper()
                .eq(categoryId != null, Article::getCategoryId, categoryId)
                .orderByDesc(Article::getPinned)
                .orderByDesc(Article::getPublishedTime)
                .orderByDesc(Article::getCreateTime);
        Page<Article> page = articleMapper.selectPage(new Page<>(current, size), wrapper);
        return new PageResponse<>(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
    }

    @Override
    public Article detail(Long id) {
        return articleMapper.selectOne(publicWrapper().eq(Article::getId, id));
    }

    @Override
    public PageResponse<Article> adminPage(long current, long size, String keyword) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .eq(Article::getDeleted, 0)
                .and(StringUtils.hasText(keyword), w -> w
                        .like(Article::getTitle, keyword)
                        .or()
                        .like(Article::getSummary, keyword))
                .orderByDesc(Article::getUpdateTime);
        Page<Article> page = articleMapper.selectPage(new Page<>(current, size), wrapper);
        return new PageResponse<>(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
    }

    @Override
    public Article adminDetail(Long id) {
        Article article = articleMapper.selectOne(new LambdaQueryWrapper<Article>()
                .eq(Article::getId, id)
                .eq(Article::getDeleted, 0));
        if (article == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "文章不存在");
        }
        return article;
    }

    @Override
    public Article create(Article article) {
        requireTitle(article);
        article.setId(null);
        article.setViewCount(0L);
        if (article.getStatus() == null) {
            article.setStatus(0);
        }
        if (article.getPinned() == null) {
            article.setPinned(0);
        }
        if (article.getSortOrder() == null) {
            article.setSortOrder(0);
        }
        article.setDeleted(0);
        applyPublishTime(article, false);
        articleMapper.insert(article);
        return article;
    }

    @Override
    public Article update(Long id, Article patch) {
        Article existing = adminDetail(id);
        requireTitle(patch);
        existing.setTitle(patch.getTitle());
        existing.setSlug(patch.getSlug());
        existing.setSummary(patch.getSummary());
        existing.setCoverUrl(patch.getCoverUrl());
        existing.setCategoryId(patch.getCategoryId());
        existing.setTags(patch.getTags());
        existing.setContentMd(patch.getContentMd());
        existing.setStatus(patch.getStatus() == null ? existing.getStatus() : patch.getStatus());
        existing.setPinned(patch.getPinned() == null ? existing.getPinned() : patch.getPinned());
        existing.setSortOrder(patch.getSortOrder() == null ? existing.getSortOrder() : patch.getSortOrder());
        existing.setExtraJson(patch.getExtraJson());
        applyPublishTime(existing, true);
        articleMapper.updateById(existing);
        return existing;
    }

    @Override
    public void delete(Long id) {
        Article existing = adminDetail(id);
        existing.setDeleted(1);
        articleMapper.updateById(existing);
    }

    /**
     * 发布时间约定：状态为已发布(1)时若未设置发布时间则补齐为当前时间；
     * 新建且非发布状态时清空发布时间；更新时保留已发布的原始时间。
     */
    private void applyPublishTime(Article article, boolean updating) {
        boolean published = article.getStatus() != null && article.getStatus() == 1;
        if (published) {
            if (article.getPublishedTime() == null) {
                article.setPublishedTime(LocalDateTime.now());
            }
        } else if (!updating) {
            article.setPublishedTime(null);
        }
    }

    private void requireTitle(Article article) {
        if (article.getTitle() == null || article.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "标题不能为空");
        }
    }

    private LambdaQueryWrapper<Article> publicWrapper() {
        return new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, 1)
                .eq(Article::getDeleted, 0);
    }
}
