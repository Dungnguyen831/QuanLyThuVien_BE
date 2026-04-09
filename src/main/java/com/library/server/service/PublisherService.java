package com.library.server.service;

import com.library.server.entity.Publisher;
import com.library.server.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PublisherService {

    @Autowired
    private PublisherRepository publisherRepository;
//    Tìm kiếm hoặc tất cả
    public List<Publisher> getAll() {
        return publisherRepository.findAll();
    }

    public Optional<Publisher> getById(Integer id) {
        return publisherRepository.findById(id);
    }
    public List<Publisher> search(String name){
        return publisherRepository.findByNameContainingIgnoreCase(name);
    }
//  Cập nhật tác giả
    public Publisher save(Publisher publisher) {
        return publisherRepository.save(publisher);
    }
//  Xoá tác giả
    public void delete(Integer id) {
        publisherRepository.deleteById(id);
    }
}