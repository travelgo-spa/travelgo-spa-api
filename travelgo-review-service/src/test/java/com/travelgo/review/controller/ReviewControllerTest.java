package com.travelgo.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelgo.review.exception.BusinessException;
import com.travelgo.review.exception.GlobalExceptionHandler;
import com.travelgo.review.exception.NotFoundException;
import com.travelgo.review.model.Review;
import com.travelgo.review.service.ReviewService;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService service;

    @InjectMocks
    private ReviewController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Review buildReview() {
        Review r = new Review();
        r.setId(1L);
        r.setPackageId(1L);
        r.setUserId(10L);
        r.setRating(5);
        r.setComment("Excelente viaje");
        r.setCreatedAt("2026-07-11");
        return r;
    }

    @Test
    void findAll_retorna200ConLaLista() throws Exception {
        when(service.findAll()).thenReturn(List.of(buildReview()));

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].packageId").value(1))
                .andExpect(jsonPath("$[0].rating").value(5));
    }

    @Test
    void findByPackageId_retorna200ConLasResenasDelPaquete() throws Exception {
        when(service.findByPackageId(1L)).thenReturn(List.of(buildReview()));

        mockMvc.perform(get("/api/reviews/package/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(10));
    }

    @Test
    void create_conDatosValidos_retorna201() throws Exception {
        Review review = buildReview();
        when(service.create(any(Review.class))).thenReturn(review);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_conDatosInvalidos_retorna400() throws Exception {
        // Objeto vacio: dispara @NotNull en packageId, userId y rating,
        // y @NotBlank en createdAt (definidos en la entidad Review).
        Review invalida = new Review();

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalida)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_conPaqueteInexistente_retorna404() throws Exception {
        Review review = buildReview();
        when(service.create(any(Review.class)))
                .thenThrow(new NotFoundException("El paquete de viaje con id 1 no existe."));

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_conResenaDuplicada_retorna422() throws Exception {
        Review review = buildReview();
        when(service.create(any(Review.class)))
                .thenThrow(new BusinessException("El usuario ya ha resenado este paquete de viaje anteriormente."));

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void delete_conIdExistente_retorna204() throws Exception {
        mockMvc.perform(delete("/api/reviews/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_conIdInexistente_retorna404() throws Exception {
        doThrow(new NotFoundException("Resena no encontrada con id: 99"))
                .when(service).delete(anyLong());

        mockMvc.perform(delete("/api/reviews/99"))
                .andExpect(status().isNotFound());
    }
}