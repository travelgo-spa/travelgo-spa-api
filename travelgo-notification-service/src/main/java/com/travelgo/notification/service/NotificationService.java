package com.travelgo.notification.service;

import com.travelgo.notification.model.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> findByUserId(Long userId);
    Notification create(Notification notification);
    Notification markAsRead(Long id);
}