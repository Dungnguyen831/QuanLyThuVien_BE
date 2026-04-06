package com.library.server.controller;

import com.library.server.entity.Category;
import com.library.server.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@CrossOrigin("*") // Cho phép Front-end gọi vào
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 1. Lấy tất cả HOẶC Tìm kiếm theo tên (Nếu có truyền ?name=...)
    @GetMapping
    public List<Category> getAll(@RequestParam(required = false) String name) {
        if (name != null && !name.isEmpty()) {
            return categoryService.searchCategories(name);
        }
        return categoryService.getAllCategories();
    }

    // 2. Lấy chi tiết 1 danh mục theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(@PathVariable Integer id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Thêm mới
    @PostMapping
    public Category create(@RequestBody Category category) {
        return categoryService.saveCategory(category);
    }

    // 4. Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@PathVariable Integer id, @RequestBody Category details) {
        return categoryService.getCategoryById(id).map(category -> {
            category.setName(details.getName());
            category.setDescription(details.getDescription());
            return ResponseEntity.ok(categoryService.saveCategory(category));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (categoryService.getCategoryById(id).isPresent()) {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}