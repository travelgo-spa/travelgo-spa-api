package com.travelgo.biblioteca.service.impl;

import com.travelgo.biblioteca.Client.UserServiceClient;
import com.travelgo.biblioteca.exception.BusinessException;
import com.travelgo.biblioteca.exception.NotFoundException;
import com.travelgo.biblioteca.model.Reservation;
import com.travelgo.biblioteca.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository repository;

    @Mock
    private UserServiceClient userServiceClient;

    private ReservationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ReservationServiceImpl(repository, userServiceClient);
    }

    @Test
    void findByUserIdReturnsReservations() {
        when(repository.findByUserId(1L)).thenReturn(List.of(validReservation()));

        List<Reservation> result = service.findByUserId(1L);

        assertEquals(1, result.size());
        verify(repository).findByUserId(1L);
    }

    @Test
    void createSavesReservationWhenUserExists() {
        Reservation reservation = validReservation();
        when(userServiceClient.checkUserExists(1L)).thenReturn(true);
        when(repository.save(reservation)).thenReturn(reservation);

        Reservation saved = service.create(reservation);

        assertEquals(1L, saved.getUserId());
        verify(repository).save(reservation);
    }

    @Test
    void createRejectsReservationWhenUserDoesNotExist() {
        Reservation reservation = validReservation();
        when(userServiceClient.checkUserExists(1L)).thenReturn(false);

        assertThrows(BusinessException.class, () -> service.create(reservation));
        verify(repository, never()).save(any());
    }

    @Test
    void createRejectsReservationWhenUserServiceFails() {
        Reservation reservation = validReservation();
        when(userServiceClient.checkUserExists(1L)).thenThrow(new RuntimeException("connection error"));

        assertThrows(BusinessException.class, () -> service.create(reservation));
        verify(repository, never()).save(any());
    }

    @Test
    void deleteThrowsWhenReservationDoesNotExist() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.delete(99L));
    }

    @Test
    void deleteRemovesExistingReservation() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    private Reservation validReservation() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUserId(1L);
        reservation.setPackageId(2L);
        reservation.setDate("2026-07-03");
        return reservation;
    }
}
