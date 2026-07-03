package com.travelgo.biblioteca.service.impl;

import com.travelgo.biblioteca.Client.UserServiceClient;
import com.travelgo.biblioteca.exception.BusinessException;
import com.travelgo.biblioteca.exception.NotFoundException;
import com.travelgo.biblioteca.model.Reservation;
import com.travelgo.biblioteca.repository.ReservationRepository;
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

    public ReservationServiceImpl(ReservationRepository repo,
                                  UserServiceClient userServiceClient) {
        this.repo = repo;
        this.userServiceClient = userServiceClient;
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
        log.info("Intentando crear reserva para userId={}, packageId={}",
                reservation.getUserId(), reservation.getPackageId());


        log.debug("Verificando existencia de usuario id={} en user-service via Feign",
                reservation.getUserId());

        Boolean userExists;
        try {
            userExists = userServiceClient.checkUserExists(reservation.getUserId());
        } catch (Exception ex) {
            log.error("No se pudo validar el usuario id={} en user-service",
                    reservation.getUserId(), ex);
            throw new BusinessException(
                    "No se pudo validar el usuario en el microservicio de usuarios. Intenta más tarde.");
        }

        if (!Boolean.TRUE.equals(userExists)) {
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