package com.travelgo.notification.service.impl;

import com.travelgo.notification.exception.BusinessException;
import com.travelgo.notification.exception.NotFoundException;
import com.travelgo.notification.model.Notification;
import com.travelgo.notification.repository.NotificationRepository;
import com.travelgo.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository repo;

    public NotificationServiceImpl(NotificationRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findByUserId(Long userId) {
        log.debug("Buscando notificaciones del usuario id: {}", userId);
        List<Notification> notifications = repo.findByUserId(userId);
        log.info("Usuario id: {} tiene {} notificaciones", userId, notifications.size());
        return notifications;
    }

    @Override
    public Notification create(Notification notification) {
        log.info("Creando notificacion para userId={}: '{}'",
                notification.getUserId(), notification.getMessage());
        notification.setReadStatus(false);
        Notification saved = repo.save(notification);
        log.info("Notificacion creada con id: {}", saved.getId());
        return saved;
    }

    @Override
    public Notification markAsRead(Long id) {
        log.info("Marcando como leida la notificacion id: {}", id);
        Notification notification = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Notificacion no encontrada con id: " + id));

        if (Boolean.TRUE.equals(notification.getReadStatus())) {
            log.warn("Notificacion id={} ya estaba marcada como leida", id);
            throw new BusinessException("Esta notificacion ya habia sido marcada como leida.");
        }

        notification.setReadStatus(true);
        Notification updated = repo.save(notification);
        log.info("Notificacion id={} marcada como leida", id);
        return updated;
    }
}