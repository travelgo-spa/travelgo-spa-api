package com.travelgo.biblioteca.service.impl;

import com.travelgo.biblioteca.exception.BusinessException;
import com.travelgo.biblioteca.exception.NotFoundException;
import com.travelgo.biblioteca.model.TravelPackage;
import com.travelgo.biblioteca.repository.TravelPackageRepository;
import com.travelgo.biblioteca.service.TravelPackageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TravelPackageServiceImpl implements TravelPackageService {

    private static final Logger log = LoggerFactory.getLogger(TravelPackageServiceImpl.class);

    private final TravelPackageRepository repo;

    public TravelPackageServiceImpl(TravelPackageRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TravelPackage> findAll() {
        List<TravelPackage> packages = repo.findAll();
        log.info("Se encontraron {} paquetes de viaje", packages.size());
        return packages;
    }

    @Override
    @Transactional(readOnly = true)
    public TravelPackage findById(Long id) {
        log.debug("Buscando paquete con id: {}", id);
        return repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Paquete no encontrado con id: {}", id);
                    return new NotFoundException("Paquete de viaje no encontrado con id: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<TravelPackage> searchByName(String q) {
        log.debug("Buscando paquetes con nombre que contiene: '{}'", q);
        List<TravelPackage> results = repo.findByNameContainingIgnoreCase(q);
        log.info("Búsqueda por '{}' retornó {} resultados", q, results.size());
        return results;
    }

    @Override
    public TravelPackage create(TravelPackage travelPackage) {
        log.info("Creando paquete de viaje: '{}'", travelPackage.getName());

        // Regla de negocio: no puede haber paquetes con precio mayor a 10.000.000
        if (travelPackage.getPrice() != null && travelPackage.getPrice() > 10_000_000.0) {
            throw new BusinessException("El precio del paquete no puede superar los $10.000.000");
        }

        TravelPackage saved = repo.save(travelPackage);
        log.info("Paquete creado con id: {}", saved.getId());
        return saved;
    }

    @Override
    public TravelPackage update(Long id, TravelPackage data) {
        log.info("Actualizando paquete con id: {}", id);
        TravelPackage current = findById(id);

        // Regla de negocio: misma validación de precio en actualización
        if (data.getPrice() != null && data.getPrice() > 10_000_000.0) {
            throw new BusinessException("El precio del paquete no puede superar los $10.000.000");
        }

        current.setName(data.getName());
        current.setDescription(data.getDescription());
        current.setPrice(data.getPrice());
        current.setDurationDays(data.getDurationDays());
        current.setDestinations(data.getDestinations());

        TravelPackage updated = repo.save(current);
        log.info("Paquete id: {} actualizado correctamente", id);
        return updated;
    }

    @Override
    public void delete(Long id) {
        log.info("Eliminando paquete con id: {}", id);
        TravelPackage pkg = findById(id); // Verifica que existe antes de borrar
        repo.deleteById(pkg.getId());
        log.info("Paquete id: {} eliminado correctamente", id);
    }
}
