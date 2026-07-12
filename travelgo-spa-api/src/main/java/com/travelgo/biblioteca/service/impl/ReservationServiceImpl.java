package com.travelgo.biblioteca.service.impl;

import com.travelgo.biblioteca.Client.UserServiceClient;
import com.travelgo.biblioteca.exception.BusinessException;
import com.travelgo.biblioteca.exception.NotFoundException;
import com.travelgo.biblioteca.model.Reservation;
import com.travelgo.biblioteca.model.TravelPackage;
import com.travelgo.biblioteca.repository.ReservationRepository;
import com.travelgo.biblioteca.repository.TravelPackageRepository;
import com.travelgo.biblioteca.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final ReservationRepository repo;
    private final UserServiceClient userServiceClient;
    private final TravelPackageRepository travelPackageRepository;

    public ReservationServiceImpl(ReservationRepository repo,
                                  UserServiceClient userServiceClient,
                                  TravelPackageRepository travelPackageRepository) {
        this.repo = repo;
        this.userServiceClient = userServiceClient;
        this.travelPackageRepository = travelPackageRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        List<Reservation> reservations = repo.findAll();
        log.info("Se encontraron {} reservas", reservations.size());
        return reservations;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByUserId(Long userId) {
        log.debug("Buscando reservas del usuario id: {}", userId);
        List<Reservation> reservations = repo.findByUserId(userId);
        log.info("Usuario id: {} tiene {} reservas", userId, reservations.size());
        return reservations;
    }

    @Override
    public Reservation create(Reservation reservation) {
        Long packageId = reservation.getPackageId();
        log.info("Intentando crear reserva para userId={}, packageId={}",
                reservation.getUserId(), packageId);

        // Regla de negocio: el paquete de viaje debe existir de verdad.
        // Esto es lo que hace real la relación @ManyToOne -- en vez de
        // confiar ciegamente en el número que manda el cliente, se busca
        // el TravelPackage real en la base de datos y se reemplaza la
        // referencia "shell" que había armado el setPackageId().
        TravelPackage travelPackage = travelPackageRepository.findById(packageId)
                .orElseThrow(() -> {
                    log.warn("Reserva rechazada: el paquete id={} no existe", packageId);
                    return new NotFoundException(
                            "El paquete de viaje con id " + packageId + " no existe.");
                });
        reservation.setTravelPackage(travelPackage);

        log.debug("Verificando existencia de usuario id={} en user-service via Feign",
                reservation.getUserId());

        Boolean userExists = userServiceClient.checkUserExists(reservation.getUserId());

        if (Boolean.FALSE.equals(userExists)) {
            log.warn("Reserva rechazada: usuario id={} no existe en user-service",
                    reservation.getUserId());
            throw new BusinessException(
                    "El usuario con id " + reservation.getUserId() +
                    " no existe en el sistema. No se puede crear la reserva.");
        }

        Reservation saved = repo.save(reservation);
        log.info("Reserva creada con id: {} para userId={}", saved.getId(), saved.getUserId());
        return saved;
    }

    @Override
    public void delete(Long id) {
        log.info("Eliminando reserva con id: {}", id);
        if (!repo.existsById(id)) {
            throw new NotFoundException("Reserva no encontrada con id: " + id);
        }
        repo.deleteById(id);
        log.info("Reserva id: {} eliminada correctamente", id);
    }
}