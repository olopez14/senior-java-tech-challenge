package org.demo.seniorjavatechchallenge.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.demo.seniorjavatechchallenge.domain.Price;


public interface PriceRepository {

    List<Price> findByProductIdOrderByInitDateAsc(Long productId);

    Optional<Price> findCurrentPrice(Long productId, LocalDate date);

    
    Optional<BigDecimal> findCurrentPriceValue(Long productId, LocalDate date);

    boolean existsOverlappingPrice(Long productId, LocalDate initDate, LocalDate endDate);

    /**
     * Returns the list of prices that overlap with the given interval for the product.
     * Used by the service when applying truncation policies.
     */
    java.util.List<org.demo.seniorjavatechchallenge.domain.Price> findOverlappingPrices(Long productId, LocalDate initDate, LocalDate endDate);

    Price save(Price price);
}





