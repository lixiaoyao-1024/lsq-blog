package com.zmr.blogbackend.controller;

import com.zmr.blogbackend.common.PageResponse;
import com.zmr.blogbackend.entity.Article;
import com.zmr.blogbackend.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public PageResponse<Article> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Long categoryId
    ) {
        return articleService.page(page, size, categoryId);
    }

    @GetMapping("/{id}")
    public Article detail(@PathVariable Long id) {
        return articleService.detail(id);
    }

    /** 管理端：全量列表（含草稿），按更新时间倒序，可按标题/摘要搜索 */
    @GetMapping("/admin")
    public PageResponse<Article> adminPage(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword
    ) {
        return articleService.adminPage(page, size, keyword);
    }

    /** 管理端：详情（草稿也可读取） */
    @GetMapping("/admin/{id}")
    public Article adminDetail(@PathVariable Long id) {
        return articleService.adminDetail(id);
    }

    /** 新建文章（status=0 草稿 / 1 已发布） */
    @PostMapping
    public Article create(@RequestBody Article article) {
        return articleService.create(article);
    }

    /** 更新文章 */
    @PutMapping("/{id}")
    public Article update(@PathVariable Long id, @RequestBody Article article) {
        return articleService.update(id, article);
    }

    /** 软删除 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
