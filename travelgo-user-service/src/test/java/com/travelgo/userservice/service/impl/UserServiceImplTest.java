package com.travelgo.userservice.service.impl;

import com.travelgo.userservice.exception.BusinessException;
import com.travelgo.userservice.exception.NotFoundException;
import com.travelgo.userservice.model.User;
import com.travelgo.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repo;

    @InjectMocks
    private UserServiceImpl service;

    private User buildUser() {
        return new User(1, "jdoe", "jdoe@travelgo.com", "John Doe");
    }

    @Test
    void findAll_retornaTodosLosUsuarios() {
        when(repo.findAll()).thenReturn(List.of(buildUser()));

        assertThat(service.findAll()).hasSize(1);
    }

    @Test
    void findById_conIdExistente_retornaElUsuario() {
        when(repo.findById(1)).thenReturn(Optional.of(buildUser()));

        User found = service.findById(1);

        assertThat(found.getUsername()).isEqualTo("jdoe");
    }

    @Test
    void findById_conIdInexistente_lanzaNotFoundException() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void existsById_delegaEnElRepositorio() {
        when(repo.existsById(1)).thenReturn(true);

        assertThat(service.existsById(1)).isTrue();
    }

    @Test
    void create_conDatosUnicos_creaElUsuario() {
        User user = buildUser();
        when(repo.existsByUsername(user.getUsername())).thenReturn(false);
        when(repo.existsByEmail(user.getEmail())).thenReturn(false);
        when(repo.save(user)).thenReturn(user);

        User created = service.create(user);

        assertThat(created.getId()).isEqualTo(1);
        verify(repo).save(user);
    }

    @Test
    void create_conUsernameDuplicado_lanzaBusinessException() {
        User user = buildUser();
        when(repo.existsByUsername(user.getUsername())).thenReturn(true);

        assertThatThrownBy(() -> service.create(user))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(user.getUsername());

        verify(repo, never()).save(any());
    }

    @Test
    void create_conEmailDuplicado_lanzaBusinessException() {
        User user = buildUser();
        when(repo.existsByUsername(user.getUsername())).thenReturn(false);
        when(repo.existsByEmail(user.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> service.create(user))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(user.getEmail());

        verify(repo, never()).save(any());
    }

    @Test
    void delete_conIdExistente_eliminaElUsuario() {
        when(repo.findById(1)).thenReturn(Optional.of(buildUser()));

        service.delete(1);

        verify(repo).deleteById(1);
    }

    @Test
    void delete_conIdInexistente_lanzaNotFoundException() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
                .isInstanceOf(NotFoundException.class);

        verify(repo, never()).deleteById(anyInt());
    }
}
