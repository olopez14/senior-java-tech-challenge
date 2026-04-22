package org.demo.seniorjavatechchallenge.infrastructure.persistence.mapper;

import org.demo.seniorjavatechchallenge.domain.model.DateRange;
import org.demo.seniorjavatechchallenge.domain.model.Money;
import org.demo.seniorjavatechchallenge.domain.model.Price;
import org.demo.seniorjavatechchallenge.domain.model.Product;
import org.demo.seniorjavatechchallenge.infrastructure.persistence.entity.PriceEntity;

public class PersistencePriceMapper {

    private PersistencePriceMapper() {}

    public static PriceEntity toEntity(Price price) {
        Long productId = price.getProduct().getId();
        return new PriceEntity(
            price.getId(),
            productId,
            price.getValue().getAmount(),
            price.getDateRange().getStart(),
            price.getDateRange().getEnd()
        );
    }

    public static Price toDomain(PriceEntity entity, Product product) {
        return new Price(
            entity.getId(),
            product,
            new Money(entity.getPriceValue()),
            new DateRange(entity.getInitDate(), entity.getEndDate())
        );
    }
}
