package com.library.server.controller;

import com.library.server.entity.Publisher;
import com.library.server.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/publishers")
@CrossOrigin("*")
public class PublisherController {

    @Autowired
    private PublisherService publisherService;

    // Lấy toàn bộ danh sách nhà xuất bản
    @GetMapping
    public List<Publisher> getAll() {
        return publisherService.getAllPublishers();
//        http://localhost:8080/api/v1/publishers
    }

    // Lấy thông tin chi tiết 1 nhà xuất bản
    @GetMapping("/{id}")
    public ResponseEntity<Publisher> getById(@PathVariable Integer id) {
        return publisherService.getPublisherById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
//        http://localhost:8080/api/v1/publishers/1
    }

    // Thêm mới nhà xuất bản
    @PostMapping
    public Publisher create(@RequestBody Publisher publisher) {
        return publisherService.savePublisher(publisher);
//        http://localhost:8080/api/v1/publishers
    }

    // Cập nhật thông tin nhà xuất bản
    @PutMapping("/{id}")
    public ResponseEntity<Publisher> update(@PathVariable Integer id, @RequestBody Publisher details) {
        return publisherService.getPublisherById(id).map(publisher -> {
            publisher.setName(details.getName());
            publisher.setAddress(details.getAddress());
            publisher.setEmail(details.getEmail());
            return ResponseEntity.ok(publisherService.savePublisher(publisher));
        }).orElse(ResponseEntity.notFound().build());
//        http://localhost:8080/api/v1/publishers/1
    }

    // Xóa nhà xuất bản
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.ok().build();
    }
}