package com.travelgo.payment.service;

import com.travelgo.payment.model.Payment;

import java.util.List;

public interface PaymentService {
    List<Payment> findAll();
    Payment create(Payment payment);
    void delete(Long id);
}