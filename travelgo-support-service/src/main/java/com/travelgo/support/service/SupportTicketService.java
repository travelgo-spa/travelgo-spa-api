package com.travelgo.support.service;

import com.travelgo.support.model.SupportTicket;

import java.util.List;

public interface SupportTicketService {
    List<SupportTicket> findByUserId(Long userId);
    SupportTicket create(SupportTicket ticket);
    SupportTicket updateStatus(Long id, String newStatus);
}