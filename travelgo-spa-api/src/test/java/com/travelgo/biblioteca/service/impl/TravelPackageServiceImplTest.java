package com.travelgo.biblioteca.service.impl;

import com.travelgo.biblioteca.exception.BusinessException;
import com.travelgo.biblioteca.exception.NotFoundException;
import com.travelgo.biblioteca.model.TravelPackage;
import com.travelgo.biblioteca.repository.TravelPackageRepository;
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
class TravelPackageServiceImplTest {

    @Mock
    private TravelPackageRepository repository;

    private TravelPackageServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TravelPackageServiceImpl(repository);
    }

    @Test
    void findAllReturnsPackages() {
        when(repository.findAll()).thenReturn(List.of(validPackage()));

        List<TravelPackage> result = service.findAll();

        assertEquals(1, result.size());
        verify(repository).findAll();
    }

    @Test
    void findByIdThrowsWhenPackageDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void createRejectsPackageWithPriceOverBusinessLimit() {
        TravelPackage travelPackage = validPackage();
        travelPackage.setPrice(10_000_001.0);

        assertThrows(BusinessException.class, () -> service.create(travelPackage));
        verify(repository, never()).save(any());
    }

    @Test
    void createSavesValidPackage() {
        TravelPackage travelPackage = validPackage();
        when(repository.save(travelPackage)).thenReturn(travelPackage);

        TravelPackage saved = service.create(travelPackage);

        assertEquals("San Pedro", saved.getName());
        verify(repository).save(travelPackage);
    }

    @Test
    void updateChangesExistingPackage() {
        TravelPackage current = validPackage();
        current.setId(1L);
        TravelPackage update = validPackage();
        update.setName("Torres del Paine");
        when(repository.findById(1L)).thenReturn(Optional.of(current));
        when(repository.save(current)).thenReturn(current);

        TravelPackage result = service.update(1L, update);

        assertEquals("Torres del Paine", result.getName());
        verify(repository).save(current);
    }

    @Test
    void deleteRemovesExistingPackage() {
        TravelPackage current = validPackage();
        current.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(current));

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    private TravelPackage validPackage() {
        TravelPackage travelPackage = new TravelPackage();
        travelPackage.setName("San Pedro");
        travelPackage.setDescription("Paquete turístico");
        travelPackage.setPrice(250000.0);
        travelPackage.setDurationDays(3);
        travelPackage.setDestinations(List.of("Atacama"));
        return travelPackage;
    }
}
