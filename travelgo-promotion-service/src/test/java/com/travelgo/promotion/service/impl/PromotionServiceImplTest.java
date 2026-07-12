package com.travelgo.promotion.service.impl;

import com.travelgo.promotion.exception.BusinessException;
import com.travelgo.promotion.exception.NotFoundException;
import com.travelgo.promotion.model.Promotion;
import com.travelgo.promotion.repository.PromotionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionServiceImplTest {

    @Mock
    private PromotionRepository repo;

    @InjectMocks
    private PromotionServiceImpl service;

    private Promotion buildPromotion(String expirationDate) {
        Promotion p = new Promotion();
        p.setId(1L);
        p.setCode("VERANO2026");
        p.setDiscountPercentage(15.0);
        p.setExpirationDate(expirationDate);
        return p;
    }

    @Test
    void findAll_retornaTodasLasPromociones() {
        when(repo.findAll()).thenReturn(List.of(buildPromotion("2026-12-31")));
        List<Promotion> result = service.findAll();
        assertThat(result).hasSize(1);
        verify(repo).findAll();
    }

    @Test
    void create_conCodigoNuevo_creaLaPromocion() {
        Promotion promotion = buildPromotion("2026-12-31");
        when(repo.existsByCodeIgnoreCase("VERANO2026")).thenReturn(false);
        when(repo.save(any(Promotion.class))).thenReturn(promotion);
        Promotion created = service.create(promotion);
        assertThat(created.getCode()).isEqualTo("VERANO2026");
        verify(repo).save(promotion);
    }

    @Test
    void create_conCodigoDuplicado_lanzaBusinessException() {
        Promotion promotion = buildPromotion("2026-12-31");
        when(repo.existsByCodeIgnoreCase("VERANO2026")).thenReturn(true);
        assertThatThrownBy(() -> service.create(promotion)).isInstanceOf(BusinessException.class);
        verify(repo, never()).save(any());
    }

    @Test
    void validateCode_conCodigoVigente_loRetorna() {
        Promotion promotion = buildPromotion(LocalDate.now().plusDays(10).toString());
        when(repo.findByCodeIgnoreCase("VERANO2026")).thenReturn(Optional.of(promotion));
        Promotion result = service.validateCode("VERANO2026");
        assertThat(result.getCode()).isEqualTo("VERANO2026");
    }

    @Test
    void validateCode_conCodigoVencido_lanzaBusinessException() {
        Promotion promotion = buildPromotion(LocalDate.now().minusDays(1).toString());
        when(repo.findByCodeIgnoreCase("VERANO2026")).thenReturn(Optional.of(promotion));
        assertThatThrownBy(() -> service.validateCode("VERANO2026")).isInstanceOf(BusinessException.class);
    }

    @Test
    void validateCode_conCodigoInexistente_lanzaNotFoundException() {
        when(repo.findByCodeIgnoreCase("NOEXISTE")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.validateCode("NOEXISTE")).isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_conIdExistente_eliminaLaPromocion() {
        when(repo.existsById(1L)).thenReturn(true);
        service.delete(1L);
        verify(repo).deleteById(1L);
    }
}