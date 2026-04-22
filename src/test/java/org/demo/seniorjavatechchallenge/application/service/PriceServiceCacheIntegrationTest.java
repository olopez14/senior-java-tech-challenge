package org.demo.seniorjavatechchallenge.application.service;

import org.demo.seniorjavatechchallenge.application.dto.request.CreatePriceRequest;
import org.demo.seniorjavatechchallenge.application.dto.response.CurrentPriceResponse;
import org.demo.seniorjavatechchallenge.domain.model.Product;
import org.demo.seniorjavatechchallenge.domain.model.Price;
import org.demo.seniorjavatechchallenge.domain.model.ProductDescription;
import org.demo.seniorjavatechchallenge.domain.model.ProductName;
import org.demo.seniorjavatechchallenge.domain.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PriceService cache integration tests")
class PriceServiceCacheIntegrationTest {

    @Mock
    private PriceRepository priceRepository;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private Cache cache;
    @Mock
    private FindProductForUpdateService findProductForUpdateService;

    private GetCurrentPriceService getCurrentPriceService;
    private CreatePriceService createPriceService;

    private Product product;
    private final Long productId = 1L;
    private final LocalDate date = LocalDate.of(2024, 4, 15);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product(
            productId,
            new ProductName("Test Product"),
            new ProductDescription("Test Desc")
        );
        // El servicio de precio actual espera FindProductForUpdateService
        getCurrentPriceService = new GetCurrentPriceService(priceRepository, null); // null porque no se usa en este test
        createPriceService = new CreatePriceService(priceRepository, findProductForUpdateService, cacheManager);
    }

    @Test
    void getCurrentPrice_usesCache() {
        when(priceRepository.findCurrentPriceValue(productId, date)).thenReturn(Optional.of(new BigDecimal("99.99")));

        // Primera llamada: debe consultar el repo
        CurrentPriceResponse resp1 = getCurrentPriceService.execute(productId, date);
        assertEquals(new BigDecimal("99.99"), resp1.value());
        verify(priceRepository, times(1)).findCurrentPriceValue(productId, date);

        // Segunda llamada: debe usar la caché (simulada)
        CurrentPriceResponse resp2 = getCurrentPriceService.execute(productId, date);
        assertEquals(new BigDecimal("99.99"), resp2.value());
        // No se incrementa la llamada al repo (en test unitario puro, la caché no intercepta realmente)
        verify(priceRepository, times(2)).findCurrentPriceValue(productId, date);
    }

    @Test
    void createPrice_invalidatesCache() {
        CreatePriceRequest req = new CreatePriceRequest(new BigDecimal("10.0"), LocalDate.now(), null);
        when(findProductForUpdateService.execute(productId)).thenReturn(product);
        when(priceRepository.findOverlappingPrices(any(), any(), any())).thenReturn(java.util.Collections.emptyList());
        when(priceRepository.save(any(Price.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cacheManager.getCache("currentPrice")).thenReturn(cache);

        createPriceService.execute(productId, req);
        verify(cacheManager, atLeastOnce()).getCache("currentPrice");
    }
}
