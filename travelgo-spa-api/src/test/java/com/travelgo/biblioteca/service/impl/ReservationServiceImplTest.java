package com.travelgo.biblioteca.service.impl;

import com.travelgo.biblioteca.Client.UserServiceClient;
import com.travelgo.biblioteca.exception.BusinessException;
import com.travelgo.biblioteca.exception.NotFoundException;
import com.travelgo.biblioteca.model.Reservation;
import com.travelgo.biblioteca.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository repo;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private ReservationServiceImpl service;

    private Reservation buildReservation() {
        return new Reservation(1L, 10L, 20L, "2026-08-01");
    }

    @Test
    void findAll_retornaTodasLasReservas() {
        when(repo.findAll()).thenReturn(List.of(buildReservation()));

        List<Reservation> result = service.findAll();

        assertThat(result).hasSize(1);
        verify(repo).findAll();
    }

    @Test
    void findByUserId_retornaReservasDelUsuario() {
        when(repo.findByUserId(10L)).thenReturn(List.of(buildReservation()));

        List<Reservation> result = service.findByUserId(10L);

        assertThat(result).hasSize(1);
        verify(repo).findByUserId(10L);
    }

    @Test
    void create_conUsuarioExistente_creaLaReserva() {
        Reservation reservation = buildReservation();
        when(userServiceClient.checkUserExists(10L)).thenReturn(true);
        when(repo.save(reservation)).thenReturn(reservation);

        Reservation created = service.create(reservation);

        assertThat(created.getId()).isEqualTo(1L);
        verify(repo).save(reservation);
    }

    @Test
    void create_conUsuarioInexistente_lanzaBusinessException() {
        Reservation reservation = buildReservation();
        when(userServiceClient.checkUserExists(10L)).thenReturn(false);

        assertThatThrownBy(() -> service.create(reservation))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no existe en el sistema");

        verify(repo, never()).save(any());
    }

    @Test
    void delete_conIdExistente_eliminaLaReserva() {
        when(repo.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repo).deleteById(1L);
    }

    @Test
    void delete_conIdInexistente_lanzaNotFoundException() {
        when(repo.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");

        verify(repo, never()).deleteById(anyLong());
    }
}
