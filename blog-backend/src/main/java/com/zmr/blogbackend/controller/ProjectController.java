package com.zmr.blogbackend.controller;

import com.zmr.blogbackend.common.PageResponse;
import com.zmr.blogbackend.entity.Project;
import com.zmr.blogbackend.service.ProjectService;
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
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public PageResponse<Project> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Long categoryId
    ) {
        return projectService.page(page, size, categoryId);
    }

    @GetMapping("/{id}")
    public Project detail(@PathVariable Long id) {
        return projectService.detail(id);
    }

    /** 管理端：全量列表（含草稿），按更新时间倒序，可按标题/摘要搜索 */
    @GetMapping("/admin")
    public PageResponse<Project> adminPage(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword
    ) {
        return projectService.adminPage(page, size, keyword);
    }

    /** 管理端：详情（草稿也可读取） */
    @GetMapping("/admin/{id}")
    public Project adminDetail(@PathVariable Long id) {
        return projectService.adminDetail(id);
    }

    /** 新建项目（status=0 草稿 / 1 已发布） */
    @PostMapping
    public Project create(@RequestBody Project project) {
        return projectService.create(project);
    }

    /** 更新项目 */
    @PutMapping("/{id}")
    public Project update(@PathVariable Long id, @RequestBody Project project) {
        return projectService.update(id, project);
    }

    /** 软删除 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
