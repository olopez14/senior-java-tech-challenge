package org.demo.seniorjavatechchallenge.application.service;

import org.demo.seniorjavatechchallenge.application.dto.response.CurrentPriceResponse;
import org.demo.seniorjavatechchallenge.domain.exception.PriceNotFoundForDateException;
import org.demo.seniorjavatechchallenge.domain.repository.PriceRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;

@Service
public class GetCurrentPriceService {
    private static final Logger log = LoggerFactory.getLogger(GetCurrentPriceService.class);
    private final PriceRepository priceRepository;
    private final FindProductService findProductService;

    public GetCurrentPriceService(PriceRepository priceRepository, FindProductService findProductService) {
        this.priceRepository = priceRepository;
        this.findProductService = findProductService;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "currentPrice", key = "#productId + '-' + #date")
    public CurrentPriceResponse execute(Long productId, LocalDate date) {
        log.info("[CACHE] Ejecutando lógica real para productId={}, date={}", productId, date);
        return priceRepository.findCurrentPriceValue(productId, date)
                .map(CurrentPriceResponse::new)
                .orElseThrow(() -> {
                    findProductService.execute(productId);
                    return new PriceNotFoundForDateException(productId, date);
                });
    }
}



