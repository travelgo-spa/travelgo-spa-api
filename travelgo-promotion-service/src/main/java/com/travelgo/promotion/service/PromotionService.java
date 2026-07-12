package com.travelgo.promotion.service;

import com.travelgo.promotion.model.Promotion;

import java.util.List;

public interface PromotionService {
    List<Promotion> findAll();
    Promotion create(Promotion promotion);
    Promotion validateCode(String code);
    void delete(Long id);
}