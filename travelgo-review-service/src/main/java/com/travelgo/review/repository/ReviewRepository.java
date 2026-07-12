package com.travelgo.review.repository;

import com.travelgo.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByPackageId(Long packageId);

    boolean existsByUserIdAndPackageId(Long userId, Long packageId);
}
