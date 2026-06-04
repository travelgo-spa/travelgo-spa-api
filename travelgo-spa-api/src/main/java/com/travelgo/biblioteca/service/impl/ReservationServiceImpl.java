package com.travelgo.biblioteca.service.impl;

import com.travelgo.biblioteca.exception.BusinessException;
import com.travelgo.biblioteca.exception.NotFoundException;
import com.travelgo.biblioteca.model.Reservation;
import com.travelgo.biblioteca.repository.ReservationRepository;
import com.travelgo.biblioteca.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Lógica de negocio para Reservas.
 *
 * Comunicación inter-servicio:
 *   Antes de crear una reserva, consulta al travelgo-user-service
 *   en GET /api/users/check/{userId} para verificar que el usuario existe.
 *   Si el servicio no responde, se propaga ResourceAccessException
 *   que el GlobalExceptionHandler convierte en 503.
 */
@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final ReservationRepository repo;
    private final RestTemplate restTemplate;
    private final String userServiceBaseUrl;

    public ReservationServiceImpl(
            ReservationRepository repo,
            RestTemplate restTemplate,
            @Value("${userservice.base-url}") String userServiceBaseUrl) {
        this.repo = repo;
        this.restTemplate = restTemplate;
        this.userServiceBaseUrl = userServiceBaseUrl;
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

        // -------------------------------------------------------
        // Comunicación inter-servicio: verificar usuario en user-service
        // ResourceAccessException si el servicio está caído → 503 vía GlobalExceptionHandler
        // -------------------------------------------------------
        String checkUrl = userServiceBaseUrl + "/api/users/check/" + reservation.getUserId();
        log.debug("Consultando existencia de usuario en: {}", checkUrl);

        Boolean userExists = restTemplate.getForObject(checkUrl, Boolean.class);

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
