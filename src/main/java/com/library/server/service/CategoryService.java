package com.library.server.service;

import com.library.server.entity.Category;
import com.library.server.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        // Trả về trực tiếp List<Category> từ Database
        return categoryRepository.findAll();
    }
}