package com.travelgo.biblioteca.service.impl;

import com.travelgo.biblioteca.exception.BusinessException;
import com.travelgo.biblioteca.exception.NotFoundException;
import com.travelgo.biblioteca.model.User;
import com.travelgo.biblioteca.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserServiceImpl(repository);
    }

    @Test
    void findAllReturnsUsers() {
        when(repository.findAll()).thenReturn(List.of(validUser()));

        List<User> result = service.findAll();

        assertEquals(1, result.size());
        verify(repository).findAll();
    }

    @Test
    void findByIdReturnsUser() {
        User user = validUser();
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        User result = service.findById(1L);

        assertEquals("andres", result.getUsername());
    }

    @Test
    void findByIdThrowsWhenUserDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void createRejectsDuplicatedEmail() {
        User user = validUser();
        when(repository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.create(user));
        verify(repository, never()).save(any());
    }

    @Test
    void createSavesValidUser() {
        User user = validUser();
        when(repository.existsByEmail(user.getEmail())).thenReturn(false);
        when(repository.save(user)).thenReturn(user);

        User saved = service.create(user);

        assertEquals("andres", saved.getUsername());
        verify(repository).save(user);
    }

    @Test
    void deleteRemovesExistingUser() {
        User user = validUser();
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    private User validUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("andres");
        user.setEmail("andres@example.com");
        return user;
    }
}
