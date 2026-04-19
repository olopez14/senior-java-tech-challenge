package org.demo.seniorjavatechchallenge.repository;

import java.util.Optional;

import org.demo.seniorjavatechchallenge.adapter.jpa.ProductJpaEntity;
import org.demo.seniorjavatechchallenge.adapter.jpa.ProductJpaRepository;
import org.demo.seniorjavatechchallenge.domain.Product;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA que implementa el puerto ProductRepository.
 * Convierte entre entidades de dominio y entidades JPA.
 */
@Component
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository jpaRepository;

    public ProductRepositoryAdapter(ProductJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity jpaEntity = ProductJpaEntity.fromDomain(product);
        ProductJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id).map(ProductJpaEntity::toDomain);
    }

    @Override
    public Optional<Product> findByIdForUpdate(Long id) {
        return jpaRepository.findByIdForUpdate(id).map(ProductJpaEntity::toDomain);
    }
}

