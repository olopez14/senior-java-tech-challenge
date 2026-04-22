package org.demo.seniorjavatechchallenge.application.service;

import org.demo.seniorjavatechchallenge.application.dto.request.CreatePriceRequest;
import org.demo.seniorjavatechchallenge.application.dto.response.CreatedPriceResponse;
import org.demo.seniorjavatechchallenge.application.mapper.PriceMapper;
import org.demo.seniorjavatechchallenge.domain.exception.InvalidDateRangeException;
import org.demo.seniorjavatechchallenge.domain.exception.PreconditionViolationException;
import org.demo.seniorjavatechchallenge.domain.exception.PriceOverlapException;
import org.demo.seniorjavatechchallenge.domain.model.Price;
import org.demo.seniorjavatechchallenge.domain.model.Product;
import org.demo.seniorjavatechchallenge.domain.repository.PriceRepository;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.benmanes.caffeine.cache.Cache;
import java.util.List;

@Service
public class CreatePriceService {
    private final PriceRepository priceRepository;
    private final FindProductForUpdateService findProductForUpdateService;
    private final CacheManager cacheManager;

    public CreatePriceService(PriceRepository priceRepository, FindProductForUpdateService findProductForUpdateService, CacheManager cacheManager) {
        this.priceRepository = priceRepository;
        this.findProductForUpdateService = findProductForUpdateService;
        this.cacheManager = cacheManager;
    }

    @Transactional
    public CreatedPriceResponse execute(Long productId, CreatePriceRequest request) {
        if (productId == null) throw new PreconditionViolationException("productId no puede ser null");
        if (request == null) throw new PreconditionViolationException("CreatePriceRequest no puede ser null");
        if (request.value() == null) throw new PreconditionViolationException("CreatePriceRequest no puede ser null");
        if (request.initDate() == null) throw new PreconditionViolationException("initDate no puede ser null");
        validateDateRange(request);
        Product product = findProductForUpdateService.execute(productId);

        List<Price> overlapping = priceRepository.findOverlappingPrices(productId, request.initDate(), request.endDate());
        boolean conflict = overlapping.stream().anyMatch(p -> {
            if (!p.getDateRange().getStart().isBefore(request.initDate())) return true;
            if (p.getDateRange().getEnd() != null && !p.getDateRange().getEnd().isBefore(request.initDate())) return true;
            return false;
        });
        if (conflict) {
            throw new PriceOverlapException(productId);
        }
        for (Price prev : overlapping) {
            if (prev.getDateRange().getStart().isBefore(request.initDate()) && prev.getDateRange().getEnd() == null) {
                Price actualizado = new Price(
                    prev.getId(),
                    prev.getProduct(),
                    prev.getValue(),
                    new org.demo.seniorjavatechchallenge.domain.model.DateRange(
                        prev.getDateRange().getStart(),
                        request.initDate().minusDays(1)
                    )
                );
                priceRepository.save(actualizado);
            }
        }
        Price priceEntity = PriceMapper.toPrice(product, request);
        Price savedPrice = priceRepository.save(priceEntity);
        evictCache(productId);
        return PriceMapper.toCreatedResponse(savedPrice);
    }

    private void validateDateRange(CreatePriceRequest request) {
        if (request.endDate() != null && !request.initDate().isBefore(request.endDate())) {
            throw new InvalidDateRangeException();
        }
    }

    private void evictCache(Long productId) {
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
    }
}
