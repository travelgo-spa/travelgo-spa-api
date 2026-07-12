package com.travelgo.destination.service;

import com.travelgo.destination.model.Destination;

import java.util.List;

public interface DestinationService {
    List<Destination> findAll();
    Destination findById(Long id);
    Destination create(Destination destination);
    void delete(Long id);
}