package com.zmr.blogbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zmr.blogbackend.entity.Category;
import com.zmr.blogbackend.mapper.CategoryMapper;
import com.zmr.blogbackend.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<Category> listEnabled(String contentType) {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .eq(Category::getDeleted, 0)
                .eq(contentType != null && !contentType.isBlank(), Category::getContentType, contentType)
                .orderByAsc(Category::getSortOrder)
                .orderByAsc(Category::getId));
    }
}
