package com.travelgo.review.service.impl;

import com.travelgo.review.Client.PackageServiceClient;
import com.travelgo.review.exception.BusinessException;
import com.travelgo.review.exception.NotFoundException;
import com.travelgo.review.model.Review;
import com.travelgo.review.repository.ReviewRepository;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository repo;

    @Mock
    private PackageServiceClient packageServiceClient;

    @InjectMocks
    private ReviewServiceImpl service;

    private Review buildReview() {
        Review r = new Review();
        r.setId(1L);
        r.setPackageId(1L);
        r.setUserId(10L);
        r.setRating(5);
        r.setComment("Excelente viaje");
        r.setCreatedAt("2026-07-11");
        return r;
    }

    private FeignException.NotFound buildFeignNotFound() {
        Request request = Request.create(Request.HttpMethod.GET, "/api/packages/1",
                java.util.Collections.emptyMap(), null, new RequestTemplate());
        return new FeignException.NotFound("not found", request, null, null);
    }

    @Test
    void findAll_retornaTodasLasResenas() {
        when(repo.findAll()).thenReturn(List.of(buildReview()));
        List<Review> result = service.findAll();
        assertThat(result).hasSize(1);
        verify(repo).findAll();
    }

    @Test
    void findByPackageId_retornaResenasDelPaquete() {
        when(repo.findByPackageId(1L)).thenReturn(List.of(buildReview()));
        List<Review> result = service.findByPackageId(1L);
        assertThat(result).hasSize(1);
        verify(repo).findByPackageId(1L);
    }

    @Test
    void create_conPaqueteYSinDuplicado_creaLaResena() {
        Review review = buildReview();
        when(packageServiceClient.getPackageById(1L)).thenReturn("ok");
        when(repo.existsByUserIdAndPackageId(10L, 1L)).thenReturn(false);
        when(repo.save(any(Review.class))).thenReturn(review);
        Review created = service.create(review);
        assertThat(created.getId()).isEqualTo(1L);
        verify(repo).save(review);
    }

    @Test
    void create_conPaqueteInexistente_lanzaNotFoundException() {
        Review review = buildReview();
        when(packageServiceClient.getPackageById(1L)).thenThrow(buildFeignNotFound());
        assertThatThrownBy(() -> service.create(review)).isInstanceOf(NotFoundException.class);
        verify(repo, never()).save(any());
    }

    @Test
    void create_conResenaDuplicada_lanzaBusinessException() {
        Review review = buildReview();
        when(packageServiceClient.getPackageById(1L)).thenReturn("ok");
        when(repo.existsByUserIdAndPackageId(10L, 1L)).thenReturn(true);
        assertThatThrownBy(() -> service.create(review)).isInstanceOf(BusinessException.class);
        verify(repo, never()).save(any());
    }

    @Test
    void delete_conIdExistente_eliminaLaResena() {
        when(repo.existsById(1L)).thenReturn(true);
        service.delete(1L);
        verify(repo).deleteById(1L);
    }

    @Test
    void delete_conIdInexistente_lanzaNotFoundException() {
        when(repo.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(99L)).isInstanceOf(NotFoundException.class);
        verify(repo, never()).deleteById(anyLong());
    }
}