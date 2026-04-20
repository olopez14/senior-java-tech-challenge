package org.demo.seniorjavatechchallenge.service;

import java.time.LocalDate;
import java.util.List;

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
import com.github.benmanes.caffeine.cache.Cache;
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

        List<Price> overlapping = priceRepository.findOverlappingPrices(productId, request.initDate(), request.endDate());

        boolean conflict = overlapping.stream().anyMatch(p -> {
            if (!p.getInitDate().isBefore(request.initDate())) return true;
            if (p.getEndDate() != null && !p.getEndDate().isBefore(request.initDate())) return true;
            return false;
        });

        if (conflict) {
            throw new PriceOverlapException(productId);
        }

        for (Price prev : overlapping) {
            if (prev.getInitDate().isBefore(request.initDate()) && prev.getEndDate() == null) {
                prev.setEndDate(request.initDate().minusDays(1));
                priceRepository.save(prev);
            }
        }

        Price priceEntity = PriceMapper.toPrice(product, request);
        Price savedPrice = priceRepository.save(priceEntity);
        
        
        if (cacheManager != null) {
            try {
                var cache = cacheManager.getCache("currentPrice");
                if (cache instanceof CaffeineCache) {
                    Cache<Object, Object> nativeCache = ((CaffeineCache) cache).getNativeCache();

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




