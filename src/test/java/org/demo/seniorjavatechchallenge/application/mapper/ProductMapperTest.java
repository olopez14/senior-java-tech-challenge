package org.demo.seniorjavatechchallenge.application.mapper;

import org.demo.seniorjavatechchallenge.application.dto.request.CreateProductRequest;
import org.demo.seniorjavatechchallenge.application.dto.response.ProductResponse;
import org.demo.seniorjavatechchallenge.domain.exception.PreconditionViolationException;
import org.demo.seniorjavatechchallenge.domain.model.Product;
import org.demo.seniorjavatechchallenge.domain.model.ProductDescription;
import org.demo.seniorjavatechchallenge.domain.model.ProductName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductMapper unit tests")
class ProductMapperTest {

    @Test
    void toProduct_validInput_mapsCorrectly() {
        CreateProductRequest req = new CreateProductRequest("P", "D");
        Product product = ProductMapper.toProduct(req);
        assertEquals("P", product.getName().getValue());
        assertEquals("D", product.getDescription().getValue());
    }

    // Ya no se testean nulos, la validación está en los servicios

    @Test
    void toResponse_validInput_mapsCorrectly() {
        Product product = new Product(
            1L,
            new ProductName("P"),
            new ProductDescription("D")
        );
        ProductResponse resp = ProductMapper.toResponse(product);
        assertEquals(1L, resp.id());
        assertEquals("P", resp.name());
        assertEquals("D", resp.description());
    }
}
