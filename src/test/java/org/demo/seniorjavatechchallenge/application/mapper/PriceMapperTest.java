package org.demo.seniorjavatechchallenge.application.mapper;

import org.demo.seniorjavatechchallenge.application.dto.request.CreatePriceRequest;
import org.demo.seniorjavatechchallenge.application.dto.response.CreatedPriceResponse;
import org.demo.seniorjavatechchallenge.application.dto.response.PriceResponse;
import org.demo.seniorjavatechchallenge.domain.exception.PreconditionViolationException;
import org.demo.seniorjavatechchallenge.domain.model.Price;
import org.demo.seniorjavatechchallenge.domain.model.Product;
import org.demo.seniorjavatechchallenge.domain.model.ProductDescription;
import org.demo.seniorjavatechchallenge.domain.model.ProductName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PriceMapper unit tests")
class PriceMapperTest {

    @Test
    void toPrice_validInput_mapsCorrectly() {
        Product product = new Product(
            null,
            new ProductName("P"),
            new ProductDescription("D")
        );
        // Simula el id del producto
        java.lang.reflect.Field f = null;
        try {
            f = Product.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(product, 1L);
        } catch (Exception e) { throw new RuntimeException(e); }
        CreatePriceRequest req = new CreatePriceRequest(new java.math.BigDecimal("10.0"), java.time.LocalDate.now(), null);
        Price price = PriceMapper.toPrice(product, req);
        assertEquals(product, price.getProduct());
        assertEquals(req.value(), price.getValue().getAmount());
        assertEquals(req.initDate(), price.getDateRange().getStart());
        assertNull(price.getDateRange().getEnd());
    }

    // Ya no se testean nulos, la validación está en los servicios

    @Test
    void toCreatedResponse_validInput_mapsCorrectly() {
        Product product = new Product(
            null,
            new ProductName("P"),
            new ProductDescription("D")
        );
        // Simula el id del producto
        java.lang.reflect.Field f = null;
        try {
            f = Product.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(product, 1L);
        } catch (Exception e) { throw new RuntimeException(e); }
        Price price = new Price(2L, product,
            new org.demo.seniorjavatechchallenge.domain.model.Money(new java.math.BigDecimal("10.0")),
            new org.demo.seniorjavatechchallenge.domain.model.DateRange(java.time.LocalDate.now(), null)
        );
        CreatedPriceResponse resp = PriceMapper.toCreatedResponse(price);
        assertEquals(1L, resp.productId());
        assertEquals(price.getValue().getAmount(), resp.value());
        assertEquals(price.getDateRange().getStart(), resp.initDate());
        assertNull(resp.endDate());
    }

    @Test
    void toResponse_validInput_mapsCorrectly() {
        Product product = new Product(
            null,
            new org.demo.seniorjavatechchallenge.domain.model.ProductName("P"),
            new org.demo.seniorjavatechchallenge.domain.model.ProductDescription("D")
        );
        // Simula el id del producto
        java.lang.reflect.Field f = null;
        try {
            f = Product.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(product, 1L);
        } catch (Exception e) { throw new RuntimeException(e); }
        Price price = new Price(product,
            new org.demo.seniorjavatechchallenge.domain.model.Money(new java.math.BigDecimal("10.0")),
            new org.demo.seniorjavatechchallenge.domain.model.DateRange(java.time.LocalDate.now(), null)
        );
        PriceResponse resp = PriceMapper.toResponse(price);
        assertEquals(price.getValue().getAmount(), resp.value());
        assertEquals(price.getDateRange().getStart(), resp.initDate());
        assertNull(resp.endDate());
    }
}
