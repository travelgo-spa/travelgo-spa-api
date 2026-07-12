package com.travelgo.wishlist.repository;

import com.travelgo.wishlist.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    List<WishlistItem> findByUserId(Long userId);

    boolean existsByUserIdAndPackageId(Long userId, Long packageId);
}