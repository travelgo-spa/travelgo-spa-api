package com.travelgo.wishlist.service.impl;

import com.travelgo.wishlist.Client.PackageServiceClient;
import com.travelgo.wishlist.exception.BusinessException;
import com.travelgo.wishlist.exception.NotFoundException;
import com.travelgo.wishlist.model.WishlistItem;
import com.travelgo.wishlist.repository.WishlistItemRepository;
import com.travelgo.wishlist.service.WishlistService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private static final Logger log = LoggerFactory.getLogger(WishlistServiceImpl.class);

    private final WishlistItemRepository repo;
    private final PackageServiceClient packageServiceClient;

    public WishlistServiceImpl(WishlistItemRepository repo, PackageServiceClient packageServiceClient) {
        this.repo = repo;
        this.packageServiceClient = packageServiceClient;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistItem> findByUserId(Long userId) {
        log.debug("Buscando wishlist del usuario id: {}", userId);
        List<WishlistItem> items = repo.findByUserId(userId);
        log.info("Usuario id: {} tiene {} items en su wishlist", userId, items.size());
        return items;
    }

    @Override
    public WishlistItem create(WishlistItem item) {
        log.info("Agregando a wishlist: userId={}, packageId={}", item.getUserId(), item.getPackageId());

        try {
            packageServiceClient.getPackageById(item.getPackageId());
        } catch (FeignException.NotFound ex) {
            log.warn("Wishlist rechazada: el paquete id={} no existe", item.getPackageId());
            throw new NotFoundException(
                    "El paquete de viaje con id " + item.getPackageId() + " no existe.");
        }

        if (repo.existsByUserIdAndPackageId(item.getUserId(), item.getPackageId())) {
            log.warn("Wishlist rechazada: el paquete {} ya esta en la wishlist del usuario {}",
                    item.getPackageId(), item.getUserId());
            throw new BusinessException(
                    "Este paquete ya se encuentra en la lista de deseos del usuario.");
        }

        WishlistItem saved = repo.save(item);
        log.info("Item de wishlist creado con id: {}", saved.getId());
        return saved;
    }

    @Override
    public void delete(Long id) {
        log.info("Eliminando item de wishlist con id: {}", id);
        if (!repo.existsById(id)) {
            throw new NotFoundException("Item de wishlist no encontrado con id: " + id);
        }
        repo.deleteById(id);
        log.info("Item de wishlist id: {} eliminado correctamente", id);
    }
}