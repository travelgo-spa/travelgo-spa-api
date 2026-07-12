package com.travelgo.biblioteca.service.impl;

import com.travelgo.biblioteca.exception.BusinessException;
import com.travelgo.biblioteca.exception.NotFoundException;
import com.travelgo.biblioteca.model.TravelPackage;
import com.travelgo.biblioteca.repository.TravelPackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TravelPackageServiceImplTest {

    @Mock
    private TravelPackageRepository repository;

    @InjectMocks
    private TravelPackageServiceImpl travelPackageService;

    private TravelPackage validPackage;

    @BeforeEach
    void setUp() {
        validPackage = new TravelPackage();
        validPackage.setId(1L);
        validPackage.setName("Paquete a Cartagena");
        validPackage.setDescription("5 días, todo incluido");
        validPackage.setPrice(850000.0);
        validPackage.setDurationDays(5);
        validPackage.setDestinations(List.of("Cartagena"));
    }

    // ---------- findAll ----------

    @Test
    void findAll_conPaquetesExistentes_retornaLaLista() {
        // Given
        when(repository.findAll()).thenReturn(List.of(validPackage));

        // When
        List<TravelPackage> result = travelPackageService.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Paquete a Cartagena");
        verify(repository, times(1)).findAll();
    }

    // ---------- findById ----------

    @Test
    void findById_conIdExistente_retornaElPaquete() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(validPackage));

        // When
        TravelPackage result = travelPackageService.findById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_conIdInexistente_lanzaNotFoundException() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> travelPackageService.findById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).save(any());
    }

    // ---------- create: camino feliz ----------

    @Test
    void create_conDatosValidos_guardaElPaqueteCorrectamente() {
        // Given
        when(repository.save(any(TravelPackage.class))).thenReturn(validPackage);

        // When
        TravelPackage result = travelPackageService.create(validPackage);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPrice()).isEqualTo(850000.0);
        verify(repository, times(1)).save(validPackage);
    }

    // ---------- create: reglas de negocio ----------

    @Test
    void create_conPrecioSuperiorAlLimite_lanzaBusinessException() {
        // Given: la regla de negocio prohíbe precios mayores a 10.000.000
        TravelPackage paqueteCaro = new TravelPackage();
        paqueteCaro.setName("Paquete de lujo");
        paqueteCaro.setPrice(15_000_000.0);
        paqueteCaro.setDurationDays(10);

        // When / Then
        assertThatThrownBy(() -> travelPackageService.create(paqueteCaro))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("10.000.000");

        // La regla de negocio debe cortar el flujo antes de tocar el repositorio
        verify(repository, never()).save(any());
    }

    @Test
    void create_conPrecioEnElLimiteExacto_seGuardaCorrectamente() {
        // Given: el límite es estrictamente "mayor a", 10.000.000 exacto debe pasar
        TravelPackage paqueteLimite = new TravelPackage();
        paqueteLimite.setName("Paquete límite");
        paqueteLimite.setPrice(10_000_000.0);
        paqueteLimite.setDurationDays(7);

        when(repository.save(any(TravelPackage.class))).thenReturn(paqueteLimite);

        // When
        TravelPackage result = travelPackageService.create(paqueteLimite);

        // Then
        assertThat(result).isNotNull();
        verify(repository, times(1)).save(paqueteLimite);
    }

    // ---------- update ----------

    @Test
    void update_conDatosValidos_actualizaCorrectamente() {
        // Given
        TravelPackage cambios = new TravelPackage();
        cambios.setName("Paquete actualizado");
        cambios.setDescription("Nueva descripción");
        cambios.setPrice(900000.0);
        cambios.setDurationDays(6);
        cambios.setDestinations(List.of("Cartagena", "Santa Marta"));

        when(repository.findById(1L)).thenReturn(Optional.of(validPackage));
        when(repository.save(any(TravelPackage.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        TravelPackage result = travelPackageService.update(1L, cambios);

        // Then
        assertThat(result.getName()).isEqualTo("Paquete actualizado");
        assertThat(result.getPrice()).isEqualTo(900000.0);
        verify(repository, times(1)).save(any(TravelPackage.class));
    }

    @Test
    void update_conPrecioInvalido_lanzaBusinessExceptionYNoGuarda() {
        // Given
        TravelPackage cambios = new TravelPackage();
        cambios.setName("Paquete actualizado");
        cambios.setPrice(20_000_000.0);
        cambios.setDurationDays(6);

        when(repository.findById(1L)).thenReturn(Optional.of(validPackage));

        // When / Then
        assertThatThrownBy(() -> travelPackageService.update(1L, cambios))
                .isInstanceOf(BusinessException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void update_conIdInexistente_lanzaNotFoundException() {
        // Given
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> travelPackageService.update(500L, validPackage))
                .isInstanceOf(NotFoundException.class);
    }

    // ---------- delete ----------

    @Test
    void delete_conIdExistente_eliminaCorrectamente() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(validPackage));
        doNothing().when(repository).deleteById(1L);

        // When
        travelPackageService.delete(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void delete_conIdInexistente_lanzaNotFoundExceptionYNoEliminaNada() {
        // Given
        when(repository.findById(404L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> travelPackageService.delete(404L))
                .isInstanceOf(NotFoundException.class);

        verify(repository, never()).deleteById(any());
    }
}