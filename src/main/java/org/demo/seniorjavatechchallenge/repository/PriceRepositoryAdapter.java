package org.demo.seniorjavatechchallenge.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.demo.seniorjavatechchallenge.adapter.jpa.PriceJpaEntity;
import org.demo.seniorjavatechchallenge.adapter.jpa.PriceJpaRepository;
import org.demo.seniorjavatechchallenge.adapter.jpa.ProductJpaEntity;
import org.demo.seniorjavatechchallenge.adapter.jpa.ProductJpaRepository;
import org.demo.seniorjavatechchallenge.domain.Price;
import org.demo.seniorjavatechchallenge.repository.specification.PriceSpecifications;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Component;


@Component
public class PriceRepositoryAdapter implements PriceRepository {

    private final PriceJpaRepository jpaRepository;
    private final ProductJpaRepository productJpaRepository;

    public PriceRepositoryAdapter(PriceJpaRepository jpaRepository, ProductJpaRepository productJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public List<Price> findByProductIdOrderByInitDateAsc(Long productId) {
        return jpaRepository.findByProductIdOrderByInitDateAsc(productId)
                .stream()
                .map(PriceJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Price> findCurrentPrice(Long productId, LocalDate date) {
        return jpaRepository.findOne(PriceSpecifications.currentPriceAt(productId, date))
                .map(PriceJpaEntity::toDomain);
    }

    @Override
    public Optional<BigDecimal> findCurrentPriceValue(Long productId, LocalDate date) {
        return jpaRepository.findCurrentPriceValue(productId, date, Limit.of(1));
    }

    @Override
    public boolean existsOverlappingPrice(Long productId, LocalDate initDate, LocalDate endDate) {
        return jpaRepository.count(PriceSpecifications.overlappingWith(productId, initDate, endDate)) > 0;
    }

    @Override
    public java.util.List<Price> findOverlappingPrices(Long productId, LocalDate initDate, LocalDate endDate) {
        return jpaRepository.findAll(PriceSpecifications.overlappingWith(productId, initDate, endDate))
                .stream()
                .map(PriceJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Price save(Price price) {
        ProductJpaEntity jpaProduct = productJpaRepository
                .findById(price.getProduct().getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        PriceJpaEntity jpaEntity = PriceJpaEntity.fromDomain(price, jpaProduct);
        PriceJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return savedEntity.toDomain();
    }
}



