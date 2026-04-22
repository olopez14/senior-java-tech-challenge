package org.demo.seniorjavatechchallenge.infrastructure.persistence.mapper;

import org.demo.seniorjavatechchallenge.domain.exception.PreconditionViolationException;
import org.demo.seniorjavatechchallenge.domain.model.*;
import org.demo.seniorjavatechchallenge.infrastructure.persistence.entity.PriceEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PersistencePriceMapper unit tests")
class PersistencePriceMapperTest {

    @Test
    void toEntity_validInput_mapsCorrectly() {
        Product product = new Product(
            1L,
            new ProductName("P"),
            new ProductDescription("D")
        );
        Price price = new Price(
            2L,
            product,
            new Money(new java.math.BigDecimal("10.0")),
            new DateRange(java.time.LocalDate.now(), null)
        );
        PriceEntity entity = PersistencePriceMapper.toEntity(price);
        assertEquals(2L, entity.getId());
        assertEquals(1L, entity.getProductId());
        assertEquals(price.getValue().getAmount(), entity.getPriceValue());
        assertEquals(price.getDateRange().getStart(), entity.getInitDate());
        assertNull(entity.getEndDate());
    }


    @Test
    void toDomain_validInput_mapsCorrectly() {
        Product product = new Product(
            1L,
            new ProductName("P"),
            new ProductDescription("D")
        );
        PriceEntity entity = new PriceEntity(2L, 1L, new java.math.BigDecimal("10.0"), java.time.LocalDate.now(), null);
        Price price = PersistencePriceMapper.toDomain(entity, product);
        assertEquals(product, price.getProduct());
        assertEquals(entity.getPriceValue(), price.getValue().getAmount());
        assertEquals(entity.getInitDate(), price.getDateRange().getStart());
        assertNull(price.getDateRange().getEnd());
    }
}
