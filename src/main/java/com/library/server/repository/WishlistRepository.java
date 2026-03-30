package com.library.server.repository;

import com.library.server.entity.Wishlist;
import com.library.server.entity.WishlistId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, WishlistId> {
    List<Wishlist> findByUserId(Integer userId);
    List<Wishlist> findByBookId(Integer bookId);
}

