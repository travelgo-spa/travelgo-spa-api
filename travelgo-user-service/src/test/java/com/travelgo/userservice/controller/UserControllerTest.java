package com.travelgo.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelgo.userservice.exception.BusinessException;
import com.travelgo.userservice.exception.GlobalExceptionHandler;
import com.travelgo.userservice.exception.NotFoundException;
import com.travelgo.userservice.model.User;
import com.travelgo.userservice.service.UserService;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService service;

    @InjectMocks
    private UserController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private User buildUser() {
        return new User(1, "jdoe", "jdoe@travelgo.com", "John Doe");
    }

    @Test
    void getAll_retorna200ConLaLista() throws Exception {
        when(service.findAll()).thenReturn(List.of(buildUser()));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("jdoe"));
    }

    @Test
    void getById_conIdExistente_retorna200() throws Exception {
        when(service.findById(1)).thenReturn(buildUser());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    void getById_conIdInexistente_retorna404() throws Exception {
        when(service.findById(99)).thenThrow(new NotFoundException("Usuario no encontrado con id: 99"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void checkExists_retorna200ConBooleano() throws Exception {
        when(service.existsById(1)).thenReturn(true);

        mockMvc.perform(get("/api/users/check/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void create_conDatosValidos_retorna201() throws Exception {
        User user = buildUser();
        when(service.create(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_conEmailInvalido_retorna400() throws Exception {
        User invalido = new User(null, "jdoe", "no-es-un-email", "John Doe");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_conUsernameDuplicado_retorna422() throws Exception {
        User user = buildUser();
        when(service.create(any(User.class)))
                .thenThrow(new BusinessException("Ya existe un usuario con el username: " + user.getUsername()));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void delete_conIdExistente_retorna204() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_conIdInexistente_retorna404() throws Exception {
        org.mockito.Mockito.doThrow(new NotFoundException("Usuario no encontrado con id: 99"))
                .when(service).delete(anyInt());

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }
}
