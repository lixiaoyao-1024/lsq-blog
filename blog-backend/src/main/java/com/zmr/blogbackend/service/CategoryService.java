package com.zmr.blogbackend.service;

import com.zmr.blogbackend.entity.Category;

import java.util.List;

public interface CategoryService {

    List<Category> listEnabled(String contentType);
}
