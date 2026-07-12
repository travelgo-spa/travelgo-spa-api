package com.travelgo.destination.service.impl;

import com.travelgo.destination.exception.BusinessException;
import com.travelgo.destination.exception.NotFoundException;
import com.travelgo.destination.model.Destination;
import com.travelgo.destination.repository.DestinationRepository;
import com.travelgo.destination.service.DestinationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DestinationServiceImpl implements DestinationService {

    private static final Logger log = LoggerFactory.getLogger(DestinationServiceImpl.class);

    private final DestinationRepository repo;

    public DestinationServiceImpl(DestinationRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Destination> findAll() {
        List<Destination> destinations = repo.findAll();
        log.info("Se encontraron {} destinos", destinations.size());
        return destinations;
    }

    @Override
    @Transactional(readOnly = true)
    public Destination findById(Long id) {
        log.debug("Buscando destino con id: {}", id);
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Destino no encontrado con id: " + id));
    }

    @Override
    public Destination create(Destination destination) {
        log.info("Creando destino: '{}'", destination.getName());

        if (repo.existsByNameIgnoreCase(destination.getName())) {
            log.warn("Destino rechazado: ya existe uno llamado '{}'", destination.getName());
            throw new BusinessException(
                    "Ya existe un destino con el nombre: " + destination.getName());
        }

        Destination saved = repo.save(destination);
        log.info("Destino creado con id: {}", saved.getId());
        return saved;
    }

    @Override
    public void delete(Long id) {
        log.info("Eliminando destino con id: {}", id);
        if (!repo.existsById(id)) {
            throw new NotFoundException("Destino no encontrado con id: " + id);
        }
        repo.deleteById(id);
        log.info("Destino id: {} eliminado correctamente", id);
    }
}