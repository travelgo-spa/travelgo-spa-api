package com.travelgo.wishlist.service;

import com.travelgo.wishlist.model.WishlistItem;

import java.util.List;

public interface WishlistService {
    List<WishlistItem> findByUserId(Long userId);
    WishlistItem create(WishlistItem item);
    void delete(Long id);
}