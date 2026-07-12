package com.travelgo.review.service;

import com.travelgo.review.model.Review;

import java.util.List;

public interface ReviewService {
    List<Review> findAll();
    List<Review> findByPackageId(Long packageId);
    Review create(Review review);
    void delete(Long id);
}
