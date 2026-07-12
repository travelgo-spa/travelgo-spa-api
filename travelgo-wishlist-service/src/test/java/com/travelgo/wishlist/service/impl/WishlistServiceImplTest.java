package com.travelgo.wishlist.service.impl;

import com.travelgo.wishlist.Client.PackageServiceClient;
import com.travelgo.wishlist.exception.BusinessException;
import com.travelgo.wishlist.exception.NotFoundException;
import com.travelgo.wishlist.model.WishlistItem;
import com.travelgo.wishlist.repository.WishlistItemRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceImplTest {

    @Mock
    private WishlistItemRepository repo;

    @Mock
    private PackageServiceClient packageServiceClient;

    @InjectMocks
    private WishlistServiceImpl service;

    private WishlistItem buildItem() {
        WishlistItem item = new WishlistItem();
        item.setId(1L);
        item.setUserId(10L);
        item.setPackageId(1L);
        item.setAddedAt("2026-07-11");
        return item;
    }

    private FeignException.NotFound buildFeignNotFound() {
        Request request = Request.create(Request.HttpMethod.GET, "/api/packages/1",
                java.util.Collections.emptyMap(), null, new RequestTemplate());
        return new FeignException.NotFound("not found", request, null, null);
    }

    @Test
    void findByUserId_retornaItemsDelUsuario() {
        when(repo.findByUserId(10L)).thenReturn(List.of(buildItem()));
        List<WishlistItem> result = service.findByUserId(10L);
        assertThat(result).hasSize(1);
        verify(repo).findByUserId(10L);
    }

    @Test
    void create_conPaqueteYSinDuplicado_agregaAWishlist() {
        WishlistItem item = buildItem();
        when(packageServiceClient.getPackageById(1L)).thenReturn("ok");
        when(repo.existsByUserIdAndPackageId(10L, 1L)).thenReturn(false);
        when(repo.save(any(WishlistItem.class))).thenReturn(item);
        WishlistItem created = service.create(item);
        assertThat(created.getId()).isEqualTo(1L);
        verify(repo).save(item);
    }

    @Test
    void create_conPaqueteInexistente_lanzaNotFoundException() {
        WishlistItem item = buildItem();
        when(packageServiceClient.getPackageById(1L)).thenThrow(buildFeignNotFound());
        assertThatThrownBy(() -> service.create(item)).isInstanceOf(NotFoundException.class);
        verify(repo, never()).save(any());
    }

    @Test
    void create_conItemDuplicado_lanzaBusinessException() {
        WishlistItem item = buildItem();
        when(packageServiceClient.getPackageById(1L)).thenReturn("ok");
        when(repo.existsByUserIdAndPackageId(10L, 1L)).thenReturn(true);
        assertThatThrownBy(() -> service.create(item)).isInstanceOf(BusinessException.class);
        verify(repo, never()).save(any());
    }

    @Test
    void delete_conIdExistente_eliminaElItem() {
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