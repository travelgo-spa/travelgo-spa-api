package com.travelgo.biblioteca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelgo.biblioteca.exception.BusinessException;
import com.travelgo.biblioteca.exception.GlobalExceptionHandler;
import com.travelgo.biblioteca.exception.NotFoundException;
import com.travelgo.biblioteca.model.TravelPackage;
import com.travelgo.biblioteca.service.TravelPackageService;
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
class TravelPackageControllerTest {

    @Mock
    private TravelPackageService service;

    @InjectMocks
    private TravelPackageController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private TravelPackage buildPackage() {
        return new TravelPackage(1L, "Cartagena Full", "Playa y ciudad amurallada",
                1500000.0, 5, List.of("Cartagena"));
    }

    @Test
    void findAll_retorna200ConLaLista() throws Exception {
        when(service.findAll()).thenReturn(List.of(buildPackage()));

        mockMvc.perform(get("/api/packages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Cartagena Full"));
    }

    @Test
    void findById_conIdExistente_retorna200() throws Exception {
        when(service.findById(1L)).thenReturn(buildPackage());

        mockMvc.perform(get("/api/packages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(1500000.0));
    }

    @Test
    void findById_conIdInexistente_retorna404() throws Exception {
        when(service.findById(99L)).thenThrow(new NotFoundException("Paquete de viaje no encontrado con id: 99"));

        mockMvc.perform(get("/api/packages/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchByName_retorna200ConCoincidencias() throws Exception {
        when(service.searchByName("cartagena")).thenReturn(List.of(buildPackage()));

        mockMvc.perform(get("/api/packages/search").param("q", "cartagena"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void create_conDatosValidos_retorna201() throws Exception {
        TravelPackage pkg = buildPackage();
        when(service.create(any(TravelPackage.class))).thenReturn(pkg);

        mockMvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pkg)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_conNombreVacio_retorna400() throws Exception {
        TravelPackage invalido = new TravelPackage(null, "", "desc", 100.0, 3, List.of("X"));

        mockMvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_conPrecioExcesivo_retorna422() throws Exception {
        TravelPackage pkg = buildPackage();
        pkg.setPrice(15_000_000.0);
        when(service.create(any(TravelPackage.class)))
                .thenThrow(new BusinessException("El precio del paquete no puede superar los $10.000.000"));

        mockMvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pkg)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void update_conDatosValidos_retorna200() throws Exception {
        TravelPackage actualizado = buildPackage();
        actualizado.setName("Cartagena Premium");
        when(service.update(any(Long.class), any(TravelPackage.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/packages/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cartagena Premium"));
    }

    @Test
    void delete_conIdExistente_retorna204() throws Exception {
        mockMvc.perform(delete("/api/packages/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_conIdInexistente_retorna404() throws Exception {
        org.mockito.Mockito.doThrow(new NotFoundException("Paquete de viaje no encontrado con id: 99"))
                .when(service).delete(anyLong());

        mockMvc.perform(delete("/api/packages/99"))
                .andExpect(status().isNotFound());
    }
}
