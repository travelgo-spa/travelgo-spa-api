package com.travelgo.userservice.service.impl;

import com.travelgo.userservice.exception.BusinessException;
import com.travelgo.userservice.exception.NotFoundException;
import com.travelgo.userservice.model.User;
import com.travelgo.userservice.repository.UserRepository;
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
        when(repository.findById(1)).thenReturn(Optional.of(user));

        User result = service.findById(1);

        assertEquals("andres", result.getUsername());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById(99));
    }

    @Test
    void existsByIdDelegatesToRepository() {
        when(repository.existsById(1)).thenReturn(true);

        assertTrue(service.existsById(1));
        verify(repository).existsById(1);
    }

    @Test
    void createRejectsDuplicatedUsername() {
        User user = validUser();
        when(repository.existsByUsername(user.getUsername())).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.create(user));
        verify(repository, never()).save(any());
    }

    @Test
    void createRejectsDuplicatedEmail() {
        User user = validUser();
        when(repository.existsByUsername(user.getUsername())).thenReturn(false);
        when(repository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.create(user));
        verify(repository, never()).save(any());
    }

    @Test
    void createSavesValidUser() {
        User user = validUser();
        when(repository.existsByUsername(user.getUsername())).thenReturn(false);
        when(repository.existsByEmail(user.getEmail())).thenReturn(false);
        when(repository.save(user)).thenReturn(user);

        User saved = service.create(user);

        assertEquals("andres", saved.getUsername());
        verify(repository).save(user);
    }

    @Test
    void deleteRemovesExistingUser() {
        User user = validUser();
        when(repository.findById(1)).thenReturn(Optional.of(user));

        service.delete(1);

        verify(repository).deleteById(1);
    }

    private User validUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("andres");
        user.setEmail("andres@example.com");
        user.setFullName("Andres Test");
        return user;
    }
}
