package org.demo.seniorjavatechchallenge.infrastructure.persistence.adapters;

import java.util.Optional;

import org.demo.seniorjavatechchallenge.domain.model.Product;
import org.demo.seniorjavatechchallenge.domain.repository.ProductRepository;
import org.demo.seniorjavatechchallenge.infrastructure.persistence.repository.ProductJdbcRepository;
import org.demo.seniorjavatechchallenge.infrastructure.persistence.mapper.PersistenceProductMapper;
import org.demo.seniorjavatechchallenge.infrastructure.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;


@Component
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJdbcRepository productJdbcRepository;

    public ProductRepositoryAdapter(ProductJdbcRepository productJdbcRepository) {
        this.productJdbcRepository = productJdbcRepository;
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = PersistenceProductMapper.toEntity(product);
        ProductEntity saved = productJdbcRepository.save(entity);
        return PersistenceProductMapper.toDomain(saved);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJdbcRepository.findById(id).map(PersistenceProductMapper::toDomain);
    }

    @Override
    public Optional<Product> findByIdForUpdate(Long id) {
        return productJdbcRepository.findByIdForUpdate(id).map(PersistenceProductMapper::toDomain);
    }
}
