package com.travelgo.support.service.impl;

import com.travelgo.support.exception.BusinessException;
import com.travelgo.support.exception.NotFoundException;
import com.travelgo.support.model.SupportTicket;
import com.travelgo.support.repository.SupportTicketRepository;
import com.travelgo.support.service.SupportTicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SupportTicketServiceImpl implements SupportTicketService {

    private static final Logger log = LoggerFactory.getLogger(SupportTicketServiceImpl.class);

    // El orden define el avance permitido: no se puede retroceder de un
    // indice mayor a uno menor (ej. de EN_PROGRESO a ABIERTO).
    private static final List<String> ESTADOS_ORDENADOS =
            List.of("ABIERTO", "EN_PROGRESO", "CERRADO");

    private final SupportTicketRepository repo;

    public SupportTicketServiceImpl(SupportTicketRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportTicket> findByUserId(Long userId) {
        log.debug("Buscando tickets del usuario id: {}", userId);
        return repo.findByUserId(userId);
    }

    @Override
    public SupportTicket create(SupportTicket ticket) {
        log.info("Creando ticket de soporte para userId={}: '{}'",
                ticket.getUserId(), ticket.getSubject());
        ticket.setStatus("ABIERTO");
        SupportTicket saved = repo.save(ticket);
        log.info("Ticket creado con id: {}", saved.getId());
        return saved;
    }

    @Override
    public SupportTicket updateStatus(Long id, String newStatus) {
        log.info("Actualizando estado del ticket id={} a '{}'", id, newStatus);

        SupportTicket ticket = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket no encontrado con id: " + id));

        int estadoActual = ESTADOS_ORDENADOS.indexOf(ticket.getStatus());
        int estadoNuevo = ESTADOS_ORDENADOS.indexOf(newStatus);

        if (estadoNuevo == -1) {
            throw new BusinessException(
                    "Estado invalido: " + newStatus + ". Los estados validos son: " + ESTADOS_ORDENADOS);
        }

        if (estadoNuevo < estadoActual) {
            log.warn("Transicion rechazada: ticket id={} intento retroceder de '{}' a '{}'",
                    id, ticket.getStatus(), newStatus);
            throw new BusinessException(
                    "No se puede retroceder el estado de un ticket. Estado actual: " +
                    ticket.getStatus() + ", estado solicitado: " + newStatus);
        }

        ticket.setStatus(newStatus);
        SupportTicket updated = repo.save(ticket);
        log.info("Ticket id={} actualizado a estado '{}'", id, newStatus);
        return updated;
    }
}