package com.library.server.service;

import com.library.server.entity.Shelf;
import com.library.server.repository.ShelfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ShelfService {

    @Autowired
    private ShelfRepository shelfRepository;

    public List<Shelf> getAll() {
        return shelfRepository.findAll();
    }

    public Optional<Shelf> getById(Integer id) {
        return shelfRepository.findById(id);
    }

    public List<Shelf> search(String name){
        return shelfRepository.findByNameContainingIgnoreCase(name);
    }

    public Shelf saveShelf(Shelf shelf) {
        return shelfRepository.save(shelf);
    }

    public void deleteShelf(Integer id) {
        shelfRepository.deleteById(id);
    }
}