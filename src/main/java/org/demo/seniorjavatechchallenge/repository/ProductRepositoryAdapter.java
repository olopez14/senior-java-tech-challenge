package org.demo.seniorjavatechchallenge.repository;

import java.util.Optional;

import org.demo.seniorjavatechchallenge.adapter.jpa.ProductJpaEntity;
import org.demo.seniorjavatechchallenge.adapter.jpa.ProductJpaRepository;
import org.demo.seniorjavatechchallenge.domain.Product;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;


@Component
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final EntityManager entityManager;

    public ProductRepositoryAdapter(ProductJpaRepository jpaRepository, EntityManager entityManager) {
        this.jpaRepository = jpaRepository;
        this.entityManager = entityManager;
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
        return jpaRepository.findById(id)
                .map(entity -> {
                    
                    entityManager.lock(entity, LockModeType.PESSIMISTIC_WRITE);
                    return entity.toDomain();
                });
    }
}



