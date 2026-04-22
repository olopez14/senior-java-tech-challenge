package org.demo.seniorjavatechchallenge.infrastructure.persistence.mapper;

import org.demo.seniorjavatechchallenge.domain.model.Product;
import org.demo.seniorjavatechchallenge.domain.model.ProductDescription;
import org.demo.seniorjavatechchallenge.domain.model.ProductName;
import org.demo.seniorjavatechchallenge.infrastructure.persistence.entity.ProductEntity;

public class PersistenceProductMapper {

    private PersistenceProductMapper() {}

    public static ProductEntity toEntity(Product product) {
        return new ProductEntity(
            product.getId(),
            product.getName() != null ? product.getName().getValue() : null,
            product.getDescription() != null ? product.getDescription().getValue() : null
        );
    }

    public static Product toDomain(ProductEntity entity) {
        return new Product(
            entity.getId(),
            new ProductName(entity.getName()),
            new ProductDescription(entity.getDescription())
        );
    }
}
