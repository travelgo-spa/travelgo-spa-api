package com.travelgo.biblioteca.service;

import com.travelgo.biblioteca.model.TravelPackage;
import java.util.List;

public interface TravelPackageService {
    List<TravelPackage> findAll();
    TravelPackage findById(Long id);
    List<TravelPackage> searchByName(String q);
    TravelPackage create(TravelPackage travelPackage);
    TravelPackage update(Long id, TravelPackage travelPackage);
    void delete(Long id);
}