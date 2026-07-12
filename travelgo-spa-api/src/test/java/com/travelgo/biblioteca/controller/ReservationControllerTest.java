package com.travelgo.biblioteca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelgo.biblioteca.exception.BusinessException;
import com.travelgo.biblioteca.exception.GlobalExceptionHandler;
import com.travelgo.biblioteca.exception.NotFoundException;
import com.travelgo.biblioteca.model.Reservation;
import com.travelgo.biblioteca.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private ReservationService service;

    @InjectMocks
    private ReservationController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Reservation buildReservation() {
        Reservation r = new Reservation();
        r.setId(1L);
        r.setUserId(10L);
        r.setPackageId(20L);
        r.setDate("2026-08-01");
        return r;
    }

    @Test
    void getAll_retorna200ConLaLista() throws Exception {
        when(service.findAll()).thenReturn(List.of(buildReservation()));

        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(10));
    }

    @Test
    void getByUser_retorna200ConLasReservasDelUsuario() throws Exception {
        when(service.findByUserId(10L)).thenReturn(List.of(buildReservation()));

        mockMvc.perform(get("/api/reservations/user/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].packageId").value(20));
    }

    @Test
    void create_conDatosValidos_retorna201() throws Exception {
        Reservation reservation = buildReservation();
        when(service.create(any(Reservation.class))).thenReturn(reservation);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_conDatosInvalidos_retorna400() throws Exception {
        Reservation invalido = new Reservation();
        invalido.setDate("");

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_conUsuarioInexistente_retorna422() throws Exception {
        Reservation reservation = buildReservation();
        when(service.create(any(Reservation.class)))
                .thenThrow(new BusinessException("El usuario con id 10 no existe en el sistema."));

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void delete_conIdExistente_retorna204() throws Exception {
        mockMvc.perform(delete("/api/reservations/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_conIdInexistente_retorna404() throws Exception {
        org.mockito.Mockito.doThrow(new NotFoundException("Reserva no encontrada con id: 99"))
                .when(service).delete(anyLong());

        mockMvc.perform(delete("/api/reservations/99"))
                .andExpect(status().isNotFound());
    }
}