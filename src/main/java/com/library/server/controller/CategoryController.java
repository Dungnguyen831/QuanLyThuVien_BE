package com.library.server.controller;

import com.library.server.entity.Category;
import com.library.server.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    //GET http://localhost:8080/api/categories
    @GetMapping
    public List<Category> getAll() {
        return categoryService.getAllCategories();
    }
}