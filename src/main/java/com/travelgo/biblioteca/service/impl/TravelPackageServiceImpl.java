package com.travelgo.biblioteca.service.impl;

import com.travelgo.biblioteca.model.TravelPackage;
import com.travelgo.biblioteca.repository.TravelPackageRepository;
import com.travelgo.biblioteca.service.TravelPackageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class TravelPackageServiceImpl implements TravelPackageService {

    private final TravelPackageRepository repo;

    public TravelPackageServiceImpl(TravelPackageRepository repo) {
        this.repo = repo;
    }

    @Override
    public TravelPackage create(TravelPackage travelPackage) {
        // Lógica de negocio mínima para la rúbrica: validar precio
        if (travelPackage.getPrice() != null && travelPackage.getPrice() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        return repo.save(travelPackage);
    }

    @Override
    public List<TravelPackage> findAll() {
        return repo.findAll();
    }

    @Override
    public TravelPackage findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Paquete no encontrado"));
    }

    @Override
    public TravelPackage update(Long id, TravelPackage data) {
        TravelPackage current = findById(id);
        current.setName(data.getName());
        current.setDescription(data.getDescription());
        current.setPrice(data.getPrice());
        current.setDurationDays(data.getDurationDays());
        current.setDestinations(data.getDestinations());
        return repo.save(current);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<TravelPackage> searchByName(String q) {
        return repo.findByNameContainingIgnoreCase(q);
    }
}