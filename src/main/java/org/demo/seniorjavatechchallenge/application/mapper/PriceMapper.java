package org.demo.seniorjavatechchallenge.application.mapper;

import org.demo.seniorjavatechchallenge.domain.exception.PreconditionViolationException;
import org.demo.seniorjavatechchallenge.domain.model.DateRange;
import org.demo.seniorjavatechchallenge.domain.model.Money;
import org.demo.seniorjavatechchallenge.domain.model.Price;
import org.demo.seniorjavatechchallenge.domain.model.Product;
import org.demo.seniorjavatechchallenge.application.dto.request.CreatePriceRequest;
import org.demo.seniorjavatechchallenge.application.dto.response.CreatedPriceResponse;
import org.demo.seniorjavatechchallenge.application.dto.response.PriceResponse;


public class PriceMapper {
    private PriceMapper() {}

    public static Price toPrice(Product product, CreatePriceRequest request) {
        if (product == null) throw new PreconditionViolationException("Product no puede ser null");
        if (request == null) throw new PreconditionViolationException("CreatePriceRequest no puede ser null");
        if (request.value() == null) throw new PreconditionViolationException("value no puede ser null");
        if (request.initDate() == null) throw new PreconditionViolationException("initDate no puede ser null");
        return new Price(
            product,
            new Money(request.value()),
            new DateRange(request.initDate(), request.endDate())
        );
    }

    public static CreatedPriceResponse toCreatedResponse(Price saved) {
        if (saved == null) throw new PreconditionViolationException("Price no puede ser null");
        return new CreatedPriceResponse(
            saved.getProduct().getId(),
            saved.getValue().getAmount(),
            saved.getDateRange().getStart(),
            saved.getDateRange().getEnd()
        );
    }

    public static PriceResponse toResponse(Price p) {
        if (p == null) throw new PreconditionViolationException("Price no puede ser null");
        return new PriceResponse(
            p.getValue().getAmount(),
            p.getDateRange().getStart(),
            p.getDateRange().getEnd()
        );
    }
}
