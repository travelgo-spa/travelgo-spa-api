package com.travelgo.destination.repository;

import com.travelgo.destination.model.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {

    boolean existsByNameIgnoreCase(String name);
}