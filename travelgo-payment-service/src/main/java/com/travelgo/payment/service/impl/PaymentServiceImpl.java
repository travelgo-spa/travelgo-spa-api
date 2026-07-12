package com.travelgo.payment.service.impl;

import com.travelgo.payment.exception.BusinessException;
import com.travelgo.payment.exception.NotFoundException;
import com.travelgo.payment.model.Payment;
import com.travelgo.payment.repository.PaymentRepository;
import com.travelgo.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository repo;

    public PaymentServiceImpl(PaymentRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> findAll() {
        List<Payment> payments = repo.findAll();
        log.info("Se encontraron {} pagos", payments.size());
        return payments;
    }

    @Override
    public Payment create(Payment payment) {
        log.info("Registrando pago para reservationId={}, monto={}",
                payment.getReservationId(), payment.getAmount());

        if (repo.existsByReservationId(payment.getReservationId())) {
            log.warn("Pago rechazado: la reserva {} ya tiene un pago registrado",
                    payment.getReservationId());
            throw new BusinessException(
                    "Ya existe un pago registrado para la reserva con id: " + payment.getReservationId());
        }

        payment.setStatus("CONFIRMADO");
        Payment saved = repo.save(payment);
        log.info("Pago creado con id: {}", saved.getId());
        return saved;
    }

    @Override
    public void delete(Long id) {
        log.info("Eliminando pago con id: {}", id);
        if (!repo.existsById(id)) {
            throw new NotFoundException("Pago no encontrado con id: " + id);
        }
        repo.deleteById(id);
        log.info("Pago id: {} eliminado correctamente", id);
    }
}