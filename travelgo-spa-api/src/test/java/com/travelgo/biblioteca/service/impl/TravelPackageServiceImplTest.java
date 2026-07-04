package com.travelgo.biblioteca.service.impl;

import com.travelgo.biblioteca.exception.BusinessException;
import com.travelgo.biblioteca.exception.NotFoundException;
import com.travelgo.biblioteca.model.TravelPackage;
import com.travelgo.biblioteca.repository.TravelPackageRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TravelPackageServiceImplTest {

    @Mock
    private TravelPackageRepository repo;

    @InjectMocks
    private TravelPackageServiceImpl service;

    private TravelPackage buildPackage() {
        return new TravelPackage(1L, "Cartagena Full", "Playa y ciudad amurallada",
                1500000.0, 5, List.of("Cartagena"));
    }

    @Test
    void findAll_retornaTodosLosPaquetes() {
        when(repo.findAll()).thenReturn(List.of(buildPackage()));

        assertThat(service.findAll()).hasSize(1);
        verify(repo).findAll();
    }

    @Test
    void findById_conIdExistente_retornaElPaquete() {
        when(repo.findById(1L)).thenReturn(Optional.of(buildPackage()));

        TravelPackage found = service.findById(1L);

        assertThat(found.getName()).isEqualTo("Cartagena Full");
    }

    @Test
    void findById_conIdInexistente_lanzaNotFoundException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void searchByName_retornaCoincidencias() {
        when(repo.findByNameContainingIgnoreCase("cartagena")).thenReturn(List.of(buildPackage()));

        List<TravelPackage> results = service.searchByName("cartagena");

        assertThat(results).hasSize(1);
    }

    @Test
    void create_conPrecioValido_creaElPaquete() {
        TravelPackage pkg = buildPackage();
        when(repo.save(pkg)).thenReturn(pkg);

        TravelPackage created = service.create(pkg);

        assertThat(created.getId()).isEqualTo(1L);
        verify(repo).save(pkg);
    }

    @Test
    void create_conPrecioExcesivo_lanzaBusinessException() {
        TravelPackage pkg = buildPackage();
        pkg.setPrice(15_000_000.0);

        assertThatThrownBy(() -> service.create(pkg))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("$10.000.000");

        verify(repo, never()).save(any());
    }

    @Test
    void update_conDatosValidos_actualizaElPaquete() {
        TravelPackage existing = buildPackage();
        TravelPackage data = new TravelPackage(null, "Cartagena Premium", "Actualizado",
                2000000.0, 7, List.of("Cartagena", "Barú"));
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any(TravelPackage.class))).thenAnswer(inv -> inv.getArgument(0));

        TravelPackage updated = service.update(1L, data);

        assertThat(updated.getName()).isEqualTo("Cartagena Premium");
        assertThat(updated.getDurationDays()).isEqualTo(7);
    }

    @Test
    void update_conPrecioExcesivo_lanzaBusinessException() {
        TravelPackage existing = buildPackage();
        TravelPackage data = new TravelPackage(null, "Cartagena Premium", "Actualizado",
                20_000_000.0, 7, List.of("Cartagena"));
        when(repo.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.update(1L, data))
                .isInstanceOf(BusinessException.class);

        verify(repo, never()).save(any());
    }

    @Test
    void delete_conIdExistente_eliminaElPaquete() {
        TravelPackage pkg = buildPackage();
        when(repo.findById(1L)).thenReturn(Optional.of(pkg));

        service.delete(1L);

        verify(repo).deleteById(1L);
    }

    @Test
    void delete_conIdInexistente_lanzaNotFoundException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(NotFoundException.class);

        verify(repo, never()).deleteById(any());
    }
}
