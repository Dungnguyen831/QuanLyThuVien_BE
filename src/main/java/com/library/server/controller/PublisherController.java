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

    // Lấy toàn bộ danh sách nhà xuất bản hoặc tìm kiếm (?name=...)
    @GetMapping
    public List<Publisher> list(@RequestParam(required = false)String name) {
        if (name != null && !name.isEmpty()){
        return publisherService.search(name);}
//        http://localhost:8080/api/v1/publishers
        return  publisherService.getAll();
//        http://localhost:8080/api/v1/publishers/1
    }
    @GetMapping("/{id}")
    public ResponseEntity<Publisher> getById(@PathVariable Integer id) {
        return publisherService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Thêm mới nhà xuất bản
    @PostMapping
    public Publisher create(@RequestBody Publisher publisher) {
        return publisherService.save(publisher);
//        http://localhost:8080/api/v1/publishers
    }

    // Cập nhật thông tin nhà xuất bản
    @PutMapping("/{id}")
    public ResponseEntity<Publisher> update(@PathVariable Integer id, @RequestBody Publisher details) {
        return publisherService.getById(id).map(publisher -> {
            publisher.setName(details.getName());
            publisher.setAddress(details.getAddress());
            publisher.setEmail(details.getEmail());
            return ResponseEntity.ok(publisherService.save(publisher));
        }).orElse(ResponseEntity.notFound().build());
//        http://localhost:8080/api/v1/publishers/1
    }

    // Xóa nhà xuất bản
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        publisherService.delete(id);
        return ResponseEntity.ok().build();
    }
}