package com.zmr.blogbackend.controller;

import com.zmr.blogbackend.common.PageResponse;
import com.zmr.blogbackend.entity.Note;
import com.zmr.blogbackend.service.NoteService;
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
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public PageResponse<Note> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Long categoryId
    ) {
        return noteService.page(page, size, categoryId);
    }

    @GetMapping("/{id}")
    public Note detail(@PathVariable Long id) {
        return noteService.detail(id);
    }

    /** 管理端：全量列表（含草稿），按更新时间倒序，可按标题/摘要搜索 */
    @GetMapping("/admin")
    public PageResponse<Note> adminPage(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword
    ) {
        return noteService.adminPage(page, size, keyword);
    }

    /** 管理端：详情（草稿也可读取） */
    @GetMapping("/admin/{id}")
    public Note adminDetail(@PathVariable Long id) {
        return noteService.adminDetail(id);
    }

    /** 新建杂谈（status=0 草稿 / 1 已发布） */
    @PostMapping
    public Note create(@RequestBody Note note) {
        return noteService.create(note);
    }

    /** 更新杂谈 */
    @PutMapping("/{id}")
    public Note update(@PathVariable Long id, @RequestBody Note note) {
        return noteService.update(id, note);
    }

    /** 软删除 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
