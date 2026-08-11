package com.zmr.blogbackend.controller;

import com.zmr.blogbackend.entity.Category;
import com.zmr.blogbackend.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> list(
            @RequestParam(required = false) String contentType
    ) {
        return categoryService.listEnabled(contentType);
    }
}
