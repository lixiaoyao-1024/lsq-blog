package com.zmr.blogbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zmr.blogbackend.common.PageResponse;
import com.zmr.blogbackend.entity.Note;
import com.zmr.blogbackend.mapper.NoteMapper;
import com.zmr.blogbackend.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteMapper noteMapper;

    public NoteServiceImpl(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    @Override
    public PageResponse<Note> page(long current, long size, Long categoryId) {
        LambdaQueryWrapper<Note> wrapper = publicWrapper()
                .eq(categoryId != null, Note::getCategoryId, categoryId)
                .orderByDesc(Note::getPublishedTime)
                .orderByDesc(Note::getCreateTime);
        Page<Note> page = noteMapper.selectPage(new Page<>(current, size), wrapper);
        return new PageResponse<>(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
    }

    @Override
    public Note detail(Long id) {
        return noteMapper.selectOne(publicWrapper().eq(Note::getId, id));
    }

    @Override
    public PageResponse<Note> adminPage(long current, long size, String keyword) {
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<Note>()
                .eq(Note::getDeleted, 0)
                .and(StringUtils.hasText(keyword), w -> w
                        .like(Note::getTitle, keyword)
                        .or()
                        .like(Note::getSummary, keyword))
                .orderByDesc(Note::getUpdateTime);
        Page<Note> page = noteMapper.selectPage(new Page<>(current, size), wrapper);
        return new PageResponse<>(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
    }

    @Override
    public Note adminDetail(Long id) {
        Note note = noteMapper.selectOne(new LambdaQueryWrapper<Note>()
                .eq(Note::getId, id)
                .eq(Note::getDeleted, 0));
        if (note == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "杂谈不存在");
        }
        return note;
    }

    @Override
    public Note create(Note note) {
        requireTitle(note);
        note.setId(null);
        note.setViewCount(0L);
        if (note.getStatus() == null) {
            note.setStatus(0);
        }
        if (note.getSortOrder() == null) {
            note.setSortOrder(0);
        }
        note.setDeleted(0);
        applyPublishTime(note, false);
        noteMapper.insert(note);
        return note;
    }

    @Override
    public Note update(Long id, Note patch) {
        Note existing = adminDetail(id);
        requireTitle(patch);
        existing.setTitle(patch.getTitle());
        existing.setSlug(patch.getSlug());
        existing.setSummary(patch.getSummary());
        existing.setCoverUrl(patch.getCoverUrl());
        existing.setCategoryId(patch.getCategoryId());
        existing.setTags(patch.getTags());
        existing.setContentMd(patch.getContentMd());
        existing.setStatus(patch.getStatus() == null ? existing.getStatus() : patch.getStatus());
        existing.setSortOrder(patch.getSortOrder() == null ? existing.getSortOrder() : patch.getSortOrder());
        existing.setExtraJson(patch.getExtraJson());
        applyPublishTime(existing, true);
        noteMapper.updateById(existing);
        return existing;
    }

    @Override
    public void delete(Long id) {
        Note existing = adminDetail(id);
        existing.setDeleted(1);
        noteMapper.updateById(existing);
    }

    /**
     * 发布时间约定：状态为已发布(1)时若未设置发布时间则补齐为当前时间；
     * 新建且非发布状态时清空发布时间；更新时保留已发布的原始时间。
     */
    private void applyPublishTime(Note note, boolean updating) {
        boolean published = note.getStatus() != null && note.getStatus() == 1;
        if (published) {
            if (note.getPublishedTime() == null) {
                note.setPublishedTime(LocalDateTime.now());
            }
        } else if (!updating) {
            note.setPublishedTime(null);
        }
    }

    private void requireTitle(Note note) {
        if (note.getTitle() == null || note.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "标题不能为空");
        }
    }

    private LambdaQueryWrapper<Note> publicWrapper() {
        return new LambdaQueryWrapper<Note>()
                .eq(Note::getStatus, 1)
                .eq(Note::getDeleted, 0);
    }
}
