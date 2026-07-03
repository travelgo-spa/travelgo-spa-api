package com.travelgo.biblioteca.repository;

import com.travelgo.biblioteca.model.TravelPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelPackageRepository extends JpaRepository<TravelPackage, Long> {

    List<TravelPackage> findByNameContainingIgnoreCase(String name);

    List<TravelPackage> findByPriceLessThanEqual(Double maxPrice);
}
