package com.travelgo.destination.service.impl;

import com.travelgo.destination.exception.BusinessException;
import com.travelgo.destination.exception.NotFoundException;
import com.travelgo.destination.model.Destination;
import com.travelgo.destination.repository.DestinationRepository;
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
class DestinationServiceImplTest {

    @Mock
    private DestinationRepository repo;

    @InjectMocks
    private DestinationServiceImpl service;

    private Destination buildDestination() {
        Destination d = new Destination();
        d.setId(1L);
        d.setName("Cartagena");
        d.setCountry("Colombia");
        d.setDescription("Ciudad amurallada en el Caribe");
        return d;
    }

    @Test
    void findAll_retornaTodosLosDestinos() {
        when(repo.findAll()).thenReturn(List.of(buildDestination()));
        List<Destination> result = service.findAll();
        assertThat(result).hasSize(1);
        verify(repo).findAll();
    }

    @Test
    void findById_conIdExistente_retornaElDestino() {
        when(repo.findById(1L)).thenReturn(Optional.of(buildDestination()));
        Destination result = service.findById(1L);
        assertThat(result.getName()).isEqualTo("Cartagena");
    }

    @Test
    void findById_conIdInexistente_lanzaNotFoundException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(99L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_conNombreNuevo_creaElDestino() {
        Destination destination = buildDestination();
        when(repo.existsByNameIgnoreCase("Cartagena")).thenReturn(false);
        when(repo.save(any(Destination.class))).thenReturn(destination);
        Destination created = service.create(destination);
        assertThat(created.getId()).isEqualTo(1L);
        verify(repo).save(destination);
    }

    @Test
    void create_conNombreDuplicado_lanzaBusinessException() {
        Destination destination = buildDestination();
        when(repo.existsByNameIgnoreCase("Cartagena")).thenReturn(true);
        assertThatThrownBy(() -> service.create(destination)).isInstanceOf(BusinessException.class);
        verify(repo, never()).save(any());
    }

    @Test
    void delete_conIdExistente_eliminaElDestino() {
        when(repo.existsById(1L)).thenReturn(true);
        service.delete(1L);
        verify(repo).deleteById(1L);
    }

    @Test
    void delete_conIdInexistente_lanzaNotFoundException() {
        when(repo.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(99L)).isInstanceOf(NotFoundException.class);
        verify(repo, never()).deleteById(anyLong());
    }
}