package com.travelgo.review.service.impl;

import com.travelgo.review.Client.PackageServiceClient;
import com.travelgo.review.exception.BusinessException;
import com.travelgo.review.exception.NotFoundException;
import com.travelgo.review.model.Review;
import com.travelgo.review.repository.ReviewRepository;
import com.travelgo.review.service.ReviewService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewRepository repo;
    private final PackageServiceClient packageServiceClient;

    public ReviewServiceImpl(ReviewRepository repo, PackageServiceClient packageServiceClient) {
        this.repo = repo;
        this.packageServiceClient = packageServiceClient;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findAll() {
        List<Review> reviews = repo.findAll();
        log.info("Se encontraron {} resenas", reviews.size());
        return reviews;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findByPackageId(Long packageId) {
        log.debug("Buscando resenas del paquete id: {}", packageId);
        return repo.findByPackageId(packageId);
    }

    @Override
    public Review create(Review review) {
        log.info("Creando resena: userId={}, packageId={}, rating={}",
                review.getUserId(), review.getPackageId(), review.getRating());

        try {
            packageServiceClient.getPackageById(review.getPackageId());
        } catch (FeignException.NotFound ex) {
            log.warn("Resena rechazada: el paquete id={} no existe", review.getPackageId());
            throw new NotFoundException(
                    "El paquete de viaje con id " + review.getPackageId() + " no existe.");
        }

        if (repo.existsByUserIdAndPackageId(review.getUserId(), review.getPackageId())) {
            log.warn("Resena rechazada: el usuario {} ya reseno el paquete {}",
                    review.getUserId(), review.getPackageId());
            throw new BusinessException(
                    "El usuario ya ha resenado este paquete de viaje anteriormente.");
        }

        Review saved = repo.save(review);
        log.info("Resena creada con id: {}", saved.getId());
        return saved;
    }

    @Override
    public void delete(Long id) {
        log.info("Eliminando resena con id: {}", id);
        if (!repo.existsById(id)) {
            throw new NotFoundException("Resena no encontrada con id: " + id);
        }
        repo.deleteById(id);
        log.info("Resena id: {} eliminada correctamente", id);
    }
}
