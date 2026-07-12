package com.travelgo.payment.service.impl;

import com.travelgo.payment.exception.BusinessException;
import com.travelgo.payment.exception.NotFoundException;
import com.travelgo.payment.model.Payment;
import com.travelgo.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository repo;

    @InjectMocks
    private PaymentServiceImpl service;

    private Payment buildPayment() {
        Payment p = new Payment();
        p.setId(1L);
        p.setReservationId(1L);
        p.setAmount(150000.0);
        p.setPaymentDate("2026-07-11");
        p.setStatus("PENDIENTE");
        return p;
    }

    @Test
    void findAll_retornaTodosLosPagos() {
        when(repo.findAll()).thenReturn(List.of(buildPayment()));
        List<Payment> result = service.findAll();
        assertThat(result).hasSize(1);
        verify(repo).findAll();
    }

    @Test
    void create_conReservaSinPagoPrevio_registraElPagoYLoConfirma() {
        Payment payment = buildPayment();
        when(repo.existsByReservationId(1L)).thenReturn(false);
        when(repo.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        Payment created = service.create(payment);
        assertThat(created.getStatus()).isEqualTo("CONFIRMADO");
        verify(repo).save(any(Payment.class));
    }

    @Test
    void create_conReservaYaPagada_lanzaBusinessException() {
        Payment payment = buildPayment();
        when(repo.existsByReservationId(1L)).thenReturn(true);
        assertThatThrownBy(() -> service.create(payment)).isInstanceOf(BusinessException.class);
        verify(repo, never()).save(any());
    }

    @Test
    void delete_conIdExistente_eliminaElPago() {
        when(repo.existsById(1L)).thenReturn(true);
        service.delete(1L);
        verify(repo).deleteById(1L);
    }

    @Test
    void delete_conIdInexistente_lanzaNotFoundException() {
        when(repo.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(99L)).isInstanceOf(NotFoundException.class);
    }
}