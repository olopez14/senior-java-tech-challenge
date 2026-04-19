package org.demo.seniorjavatechchallenge.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.demo.seniorjavatechchallenge.adapter.jpa.PriceJpaEntity;
import org.demo.seniorjavatechchallenge.adapter.jpa.PriceJpaRepository;
import org.demo.seniorjavatechchallenge.adapter.jpa.ProductJpaEntity;
import org.demo.seniorjavatechchallenge.adapter.jpa.ProductJpaRepository;
import org.demo.seniorjavatechchallenge.domain.Price;
import org.demo.seniorjavatechchallenge.domain.Product;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA que implementa el puerto PriceRepository.
 * Convierte entre entidades de dominio y entidades JPA.
 */
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
        return jpaRepository.findCurrentPrice(productId, date)
                .map(PriceJpaEntity::toDomain);
    }

    @Override
    public boolean existsOverlappingPrice(Long productId, LocalDate initDate, LocalDate endDate) {
        return jpaRepository.existsOverlappingPrice(productId, initDate, endDate);
    }

    @Override
    public Price save(Price price) {
        // Obtener producto JPA
        ProductJpaEntity jpaProduct = productJpaRepository
                .findById(price.getProduct().getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Convertir dominio a JPA
        PriceJpaEntity jpaEntity = PriceJpaEntity.fromDomain(price, jpaProduct);

        // Guardar
        PriceJpaEntity savedEntity = jpaRepository.save(jpaEntity);

        // Convertir JPA a dominio
        return savedEntity.toDomain();
    }
}

