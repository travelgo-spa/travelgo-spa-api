package com.travelgo.support.service.impl;

import com.travelgo.support.exception.BusinessException;
import com.travelgo.support.exception.NotFoundException;
import com.travelgo.support.model.SupportTicket;
import com.travelgo.support.repository.SupportTicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportTicketServiceImplTest {

    @Mock
    private SupportTicketRepository repo;

    @InjectMocks
    private SupportTicketServiceImpl service;

    private SupportTicket buildTicket(String status) {
        SupportTicket t = new SupportTicket();
        t.setId(1L);
        t.setUserId(10L);
        t.setSubject("Problema con mi reserva");
        t.setDescription("No me llego la confirmacion");
        t.setStatus(status);
        t.setCreatedAt("2026-07-11");
        return t;
    }

    @Test
    void findByUserId_retornaTicketsDelUsuario() {
        when(repo.findByUserId(10L)).thenReturn(List.of(buildTicket("ABIERTO")));
        List<SupportTicket> result = service.findByUserId(10L);
        assertThat(result).hasSize(1);
        verify(repo).findByUserId(10L);
    }

    @Test
    void create_creaElTicketComoAbierto() {
        SupportTicket ticket = buildTicket("ABIERTO");
        when(repo.save(any(SupportTicket.class))).thenReturn(ticket);
        SupportTicket created = service.create(ticket);
        assertThat(created.getStatus()).isEqualTo("ABIERTO");
        verify(repo).save(ticket);
    }

    @Test
    void updateStatus_avanzandoDeAbiertoAEnProgreso_actualizaCorrectamente() {
        SupportTicket ticket = buildTicket("ABIERTO");
        when(repo.findById(1L)).thenReturn(Optional.of(ticket));
        when(repo.save(any(SupportTicket.class))).thenAnswer(inv -> inv.getArgument(0));
        SupportTicket updated = service.updateStatus(1L, "EN_PROGRESO");
        assertThat(updated.getStatus()).isEqualTo("EN_PROGRESO");
    }

    @Test
    void updateStatus_intentandoRetroceder_lanzaBusinessException() {
        SupportTicket ticket = buildTicket("EN_PROGRESO");
        when(repo.findById(1L)).thenReturn(Optional.of(ticket));
        assertThatThrownBy(() -> service.updateStatus(1L, "ABIERTO")).isInstanceOf(BusinessException.class);
        verify(repo, never()).save(any());
    }

    @Test
    void updateStatus_conEstadoInvalido_lanzaBusinessException() {
        SupportTicket ticket = buildTicket("ABIERTO");
        when(repo.findById(1L)).thenReturn(Optional.of(ticket));
        assertThatThrownBy(() -> service.updateStatus(1L, "INEXISTENTE")).isInstanceOf(BusinessException.class);
    }

    @Test
    void updateStatus_conTicketInexistente_lanzaNotFoundException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.updateStatus(99L, "EN_PROGRESO")).isInstanceOf(NotFoundException.class);
    }
}