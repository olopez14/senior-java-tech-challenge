package org.demo.seniorjavatechchallenge.service;

import java.time.LocalDate;

import org.demo.seniorjavatechchallenge.domain.Price;
import org.demo.seniorjavatechchallenge.domain.Product;
import org.demo.seniorjavatechchallenge.dto.request.CreatePriceRequest;
import org.demo.seniorjavatechchallenge.dto.response.CreatedPriceResponse;
import org.demo.seniorjavatechchallenge.dto.response.CurrentPriceResponse;
import org.demo.seniorjavatechchallenge.exception.InvalidDateRangeException;
import org.demo.seniorjavatechchallenge.exception.PriceNotFoundForDateException;
import org.demo.seniorjavatechchallenge.exception.PriceOverlapException;
import org.demo.seniorjavatechchallenge.mapper.PriceMapper;
import org.demo.seniorjavatechchallenge.repository.PriceRepository;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PriceService {

    private final ProductService productService;
    private final PriceRepository priceRepository;
    private final CacheManager cacheManager;

    public PriceService(ProductService productService, PriceRepository priceRepository, CacheManager cacheManager) {
        this.productService = productService;
        this.priceRepository = priceRepository;
        this.cacheManager = cacheManager;
    }

    
    @Transactional
    @CacheEvict(value = "currentPrice", key = "#productId + '-' + #request.initDate()")
    public CreatedPriceResponse createPrice(Long productId, CreatePriceRequest request) {
        validateDateRange(request);
        Product product = productService.findProductForUpdateOrThrow(productId);

        if (priceRepository.existsOverlappingPrice(productId, request.initDate(), request.endDate())) {
            throw new PriceOverlapException(productId);
        }

        Price priceEntity = PriceMapper.toPrice(product, request);
        Price savedPrice = priceRepository.save(priceEntity);
        
        
        if (cacheManager != null) {
            try {
                var cache = cacheManager.getCache("currentPrice");
                if (cache instanceof CaffeineCache) {
                    com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = ((CaffeineCache) cache).getNativeCache();
                    
                    nativeCache.asMap().keySet().removeIf(k -> k != null && k.toString().startsWith(productId + "-"));
                }
            } catch (Exception ex) {
                
                System.err.println("[CACHE] Eviction failed for product " + productId + ": " + ex.getMessage());
            }
        }

        return PriceMapper.toCreatedResponse(savedPrice);
    }

    
    @Transactional(readOnly = true)
    @Cacheable(value = "currentPrice", key = "#productId + '-' + #date")
    public CurrentPriceResponse getCurrentPrice(Long productId, LocalDate date) {

        return priceRepository.findCurrentPriceValue(productId, date)
                .map(CurrentPriceResponse::new)
                .orElseThrow(() -> {
                    
                    productService.findProductOrThrow(productId);
                    return new PriceNotFoundForDateException(productId, date);
                });
    }

    
    private void validateDateRange(CreatePriceRequest request) {
        if (request.endDate() != null && !request.initDate().isBefore(request.endDate())) {
            throw new InvalidDateRangeException();
        }
    }
}




