package com.travelgo.notification.service.impl;

import com.travelgo.notification.exception.BusinessException;
import com.travelgo.notification.exception.NotFoundException;
import com.travelgo.notification.model.Notification;
import com.travelgo.notification.repository.NotificationRepository;
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
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository repo;

    @InjectMocks
    private NotificationServiceImpl service;

    private Notification buildNotification() {
        Notification n = new Notification();
        n.setId(1L);
        n.setUserId(10L);
        n.setMessage("Tu reserva fue confirmada");
        n.setSentAt("2026-07-11");
        n.setReadStatus(false);
        return n;
    }

    @Test
    void findByUserId_retornaNotificacionesDelUsuario() {
        when(repo.findByUserId(10L)).thenReturn(List.of(buildNotification()));
        List<Notification> result = service.findByUserId(10L);
        assertThat(result).hasSize(1);
        verify(repo).findByUserId(10L);
    }

    @Test
    void create_creaLaNotificacionSinLeer() {
        Notification notification = buildNotification();
        when(repo.save(any(Notification.class))).thenReturn(notification);
        Notification created = service.create(notification);
        assertThat(created.getReadStatus()).isFalse();
        verify(repo).save(notification);
    }

    @Test
    void markAsRead_conNotificacionNoLeida_laMarcaComoLeida() {
        Notification notification = buildNotification();
        when(repo.findById(1L)).thenReturn(Optional.of(notification));
        when(repo.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));
        Notification updated = service.markAsRead(1L);
        assertThat(updated.getReadStatus()).isTrue();
    }

    @Test
    void markAsRead_conNotificacionYaLeida_lanzaBusinessException() {
        Notification notification = buildNotification();
        notification.setReadStatus(true);
        when(repo.findById(1L)).thenReturn(Optional.of(notification));
        assertThatThrownBy(() -> service.markAsRead(1L)).isInstanceOf(BusinessException.class);
        verify(repo, never()).save(any());
    }

    @Test
    void markAsRead_conIdInexistente_lanzaNotFoundException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.markAsRead(99L)).isInstanceOf(NotFoundException.class);
    }
}