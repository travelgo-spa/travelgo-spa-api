package com.travelgo.biblioteca.service;

import com.travelgo.biblioteca.model.Reservation;

import java.util.List;

public interface ReservationService {
    List<Reservation> findAll();
    List<Reservation> findByUserId(Long userId);
    Reservation create(Reservation reservation);
    void delete(Long id);
}
