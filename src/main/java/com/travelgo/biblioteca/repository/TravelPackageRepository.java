package com.travelgo.biblioteca.repository;

import com.travelgo.biblioteca.model.TravelPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelPackageRepository extends JpaRepository<TravelPackage, Long> {

    List<TravelPackage> findByNameContainingIgnoreCase(String name);
}
