package org.demo.seniorjavatechchallenge.application.mapper;

import org.demo.seniorjavatechchallenge.domain.exception.PreconditionViolationException;
import org.demo.seniorjavatechchallenge.domain.model.Product;
import org.demo.seniorjavatechchallenge.application.dto.request.CreateProductRequest;
import org.demo.seniorjavatechchallenge.application.dto.response.PriceResponse;
import org.demo.seniorjavatechchallenge.application.dto.response.ProductPriceHistoryResponse;
import org.demo.seniorjavatechchallenge.application.dto.response.ProductResponse;
import org.demo.seniorjavatechchallenge.domain.model.ProductDescription;
import org.demo.seniorjavatechchallenge.domain.model.ProductName;

import java.util.stream.Collectors;

public class ProductMapper {
    private ProductMapper() {}

    public static Product toProduct(CreateProductRequest request) {
        if (request == null) throw new PreconditionViolationException("CreateProductRequest no puede ser null");
        if (request.name() == null) throw new PreconditionViolationException("name no puede ser null");
        if (request.description() == null) throw new PreconditionViolationException("description no puede ser null");
        return new Product(
            null,
            new ProductName(request.name()),
            new ProductDescription(request.description())
        );
    }

    public static ProductResponse toResponse(Product product) {
        if (product == null) throw new PreconditionViolationException("Product no puede ser null");
        return new ProductResponse(
            product.getId(),
            product.getName() != null ? product.getName().getValue() : null,
            product.getDescription() != null ? product.getDescription().getValue() : null
        );
    }

    public static ProductPriceHistoryResponse toHistoryResponse(Product product) {
        if (product == null) throw new PreconditionViolationException("Product no puede ser null");
        java.util.List<PriceResponse> prices = product.getPrices() == null
                ? java.util.List.of()
                : product.getPrices().stream().map(PriceMapper::toResponse).collect(Collectors.toList());
        return new ProductPriceHistoryResponse(
            product.getId(),
            product.getName() != null ? product.getName().getValue() : null,
            product.getDescription() != null ? product.getDescription().getValue() : null,
            prices
        );
    }
}
