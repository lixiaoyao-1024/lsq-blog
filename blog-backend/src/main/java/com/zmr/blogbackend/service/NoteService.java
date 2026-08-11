package com.zmr.blogbackend.service;

import com.zmr.blogbackend.common.PageResponse;
import com.zmr.blogbackend.entity.Note;

public interface NoteService {

    PageResponse<Note> page(long current, long size, Long categoryId);

    Note detail(Long id);

    /** 管理端：全量列表（含草稿），按更新时间倒序，可按标题/摘要模糊搜索 */
    PageResponse<Note> adminPage(long current, long size, String keyword);

    /** 管理端：按 id 读取，草稿也可读取，不存在时抛 404 */
    Note adminDetail(Long id);

    Note create(Note note);

    Note update(Long id, Note note);

    void delete(Long id);
}
