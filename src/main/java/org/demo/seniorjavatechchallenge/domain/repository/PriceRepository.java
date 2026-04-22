package org.demo.seniorjavatechchallenge.domain.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.demo.seniorjavatechchallenge.domain.model.Price;


public interface PriceRepository {

    List<Price> findByProductIdOrderByInitDateAsc(Long productId);

    Optional<BigDecimal> findCurrentPriceValue(Long productId, LocalDate date);

    List<Price> findOverlappingPrices(Long productId, LocalDate initDate, LocalDate endDate);

    Price save(Price price);
}






