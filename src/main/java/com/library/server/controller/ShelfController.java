package com.library.server.controller;

import com.library.server.entity.Shelf;
import com.library.server.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelves")
@CrossOrigin("*")
public class ShelfController {

    @Autowired
    private ShelfService shelfService;

    // Lấy toàn bộ danh sách kệ sách
    @GetMapping
    public List<Shelf> list() {
        return shelfService.getAllShelves();
//        http://localhost:8080/api/v1/shelves
    }

    // Lấy thông tin 1 kệ sách theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Shelf> getById(@PathVariable Integer id) {
        return shelfService.getShelfById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
//        http://localhost:8080/api/v1/shelves/1
    }

    // Thêm kệ sách mới
    @PostMapping
    public Shelf create(@RequestBody Shelf shelf) {
        return shelfService.saveShelf(shelf);
//        http://localhost:8080/api/v1/shelves
    }

    // Cập nhật kệ sách
    @PutMapping("/{id}")
    public ResponseEntity<Shelf> update(@PathVariable Integer id, @RequestBody Shelf details) {
        return shelfService.getShelfById(id).map(shelf -> {
            shelf.setName(details.getName());
            shelf.setFloor(details.getFloor());
            return ResponseEntity.ok(shelfService.saveShelf(shelf));
        }).orElse(ResponseEntity.notFound().build());
//        http://localhost:8080/api/v1/shelves/1
    }

    // Xóa kệ sách
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        shelfService.deleteShelf(id);
        return ResponseEntity.ok().build();
//        http://localhost:8080/api/v1/shelves/1
    }
}