package org.demo.seniorjavatechchallenge.infrastructure.persistence.mapper;

import org.demo.seniorjavatechchallenge.domain.exception.PreconditionViolationException;
import org.demo.seniorjavatechchallenge.domain.model.Product;
import org.demo.seniorjavatechchallenge.domain.model.ProductDescription;
import org.demo.seniorjavatechchallenge.domain.model.ProductName;
import org.demo.seniorjavatechchallenge.infrastructure.persistence.entity.ProductEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PersistenceProductMapper unit tests")
class PersistenceProductMapperTest {

    @Test
    void toEntity_validInput_mapsCorrectly() {
        Product product = new Product(
            1L,
            new ProductName("P"),
            new ProductDescription("D")
        );
        ProductEntity entity = PersistenceProductMapper.toEntity(product);
        assertEquals(1L, entity.getId());
        assertEquals("P", entity.getName());
        assertEquals("D", entity.getDescription());
    }


    @Test
    void toDomain_validInput_mapsCorrectly() {
        ProductEntity entity = new ProductEntity(1L, "P", "D");
        Product product = PersistenceProductMapper.toDomain(entity);
        assertEquals("P", product.getName().getValue());
        assertEquals("D", product.getDescription().getValue());
        assertEquals(1L, product.getId());
    }
}
