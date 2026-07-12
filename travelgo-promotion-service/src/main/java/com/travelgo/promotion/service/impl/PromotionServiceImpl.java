package com.travelgo.promotion.service.impl;

import com.travelgo.promotion.exception.BusinessException;
import com.travelgo.promotion.exception.NotFoundException;
import com.travelgo.promotion.model.Promotion;
import com.travelgo.promotion.repository.PromotionRepository;
import com.travelgo.promotion.service.PromotionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PromotionServiceImpl implements PromotionService {

    private static final Logger log = LoggerFactory.getLogger(PromotionServiceImpl.class);

    private final PromotionRepository repo;

    public PromotionServiceImpl(PromotionRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Promotion> findAll() {
        List<Promotion> promotions = repo.findAll();
        log.info("Se encontraron {} promociones", promotions.size());
        return promotions;
    }

    @Override
    public Promotion create(Promotion promotion) {
        log.info("Creando promocion con codigo: '{}'", promotion.getCode());

        if (repo.existsByCodeIgnoreCase(promotion.getCode())) {
            log.warn("Promocion rechazada: el codigo '{}' ya existe", promotion.getCode());
            throw new BusinessException(
                    "Ya existe una promocion con el codigo: " + promotion.getCode());
        }

        Promotion saved = repo.save(promotion);
        log.info("Promocion creada con id: {}", saved.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Promotion validateCode(String code) {
        log.debug("Validando codigo de promocion: '{}'", code);
        Promotion promotion = repo.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new NotFoundException("Codigo de promocion no encontrado: " + code));

        LocalDate expiration = LocalDate.parse(promotion.getExpirationDate());
        if (expiration.isBefore(LocalDate.now())) {
            log.warn("Promocion rechazada: el codigo '{}' vencio el {}", code, promotion.getExpirationDate());
            throw new BusinessException(
                    "El codigo de promocion '" + code + "' se encuentra vencido.");
        }

        return promotion;
    }

    @Override
    public void delete(Long id) {
        log.info("Eliminando promocion con id: {}", id);
        if (!repo.existsById(id)) {
            throw new NotFoundException("Promocion no encontrada con id: " + id);
        }
        repo.deleteById(id);
        log.info("Promocion id: {} eliminada correctamente", id);
    }
}