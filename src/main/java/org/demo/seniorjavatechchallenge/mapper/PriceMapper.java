package org.demo.seniorjavatechchallenge.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.demo.seniorjavatechchallenge.domain.Price;
import org.demo.seniorjavatechchallenge.domain.Product;
import org.demo.seniorjavatechchallenge.dto.request.CreatePriceRequest;
import org.demo.seniorjavatechchallenge.dto.response.CreatedPriceResponse;
import org.demo.seniorjavatechchallenge.dto.response.CurrentPriceResponse;
import org.demo.seniorjavatechchallenge.dto.response.PriceResponse;

/**
 * Mapper utility para convertir entre DTOs de Price y entidades de dominio.
 * Proporciona métodos builder para garantizar type-safety y validaciones.
 *
 * Uso:
 *   Price price = PriceMapper.toPrice(product, createPriceRequest);
 *   PriceResponse response = PriceMapper.toResponse(price);
 */
public class PriceMapper {

    private PriceMapper() {
        // Utility class
    }

    // ============================================
    // PRICE MAPPERS
    // ============================================

    /**
     * Convierte CreatePriceRequest a entidad Price.
     * Requiere el Product padre para establecer la relación.
     *
     * @param product Entidad Product padre
     * @param request Request del cliente
     * @return Entidad Price lista para persistir
     * @throws IllegalArgumentException si falta información requerida
     */
    public static Price toPrice(Product product, CreatePriceRequest request) {
        if (product == null) {
            throw new IllegalArgumentException("Product no puede ser null");
        }
        if (request == null) {
            throw new IllegalArgumentException("CreatePriceRequest no puede ser null");
        }

        return new Price(
            product,
            request.value(),
            request.initDate(),
            request.endDate()
        );
    }

    /**
     * Convierte entidad Price a PriceResponse.
     * Para respuestas de historial de precios.
     *
     * @param price Entidad desde BD
     * @return DTO de respuesta
     */
    public static PriceResponse toResponse(Price price) {
        if (price == null) {
            throw new IllegalArgumentException("Price no puede ser null");
        }

        return PriceResponseBuilder.create()
                .value(price.getValue())
                .initDate(price.getInitDate())
                .endDate(price.getEndDate())
                .build();
    }

    /**
     * Convierte entidad Price a CurrentPriceResponse.
     * Para respuesta de consulta de precio vigente.
     *
     * @param price Entidad desde BD
     * @return DTO con precio vigente
     */
    public static CurrentPriceResponse toCurrentPriceResponse(Price price) {
        if (price == null) {
            throw new IllegalArgumentException("Price no puede ser null");
        }

        return new CurrentPriceResponse(price.getValue());
    }

    /**
     * Convierte entidad Price a CreatedPriceResponse.
     * Para respuesta de creación de precio (POST).
     *
     * @param price Entidad creada y persistida
     * @return DTO con el precio creado
     */
    public static CreatedPriceResponse toCreatedResponse(Price price) {
        if (price == null) {
            throw new IllegalArgumentException("Price no puede ser null");
        }

        return CreatedPriceResponseBuilder.create()
                .productId(price.getProduct().getId())
                .value(price.getValue())
                .initDate(price.getInitDate())
                .endDate(price.getEndDate())
                .build();
    }

    // ============================================
    // BUILDER INTERNO PARA PriceResponse
    // ============================================
    public static class PriceResponseBuilder {
        private BigDecimal value;
        private LocalDate initDate;
        private LocalDate endDate;

        public static PriceResponseBuilder create() {
            return new PriceResponseBuilder();
        }

        public PriceResponseBuilder value(BigDecimal value) {
            this.value = value;
            return this;
        }

        public PriceResponseBuilder initDate(LocalDate initDate) {
            this.initDate = initDate;
            return this;
        }

        public PriceResponseBuilder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public PriceResponse build() {
            validate();
            return new PriceResponse(value, initDate, endDate);
        }

        private void validate() {
            if (value == null) throw new IllegalArgumentException("value es requerido");
            if (value.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("value debe ser positivo");
            if (initDate == null) throw new IllegalArgumentException("initDate es requerido");
        }
    }

    // ============================================
    // BUILDER INTERNO PARA CreatedPriceResponse
    // ============================================
    public static class CreatedPriceResponseBuilder {
        private Long productId;
        private BigDecimal value;
        private LocalDate initDate;
        private LocalDate endDate;

        public static CreatedPriceResponseBuilder create() {
            return new CreatedPriceResponseBuilder();
        }

        public CreatedPriceResponseBuilder productId(Long productId) {
            this.productId = productId;
            return this;
        }

        public CreatedPriceResponseBuilder value(BigDecimal value) {
            this.value = value;
            return this;
        }

        public CreatedPriceResponseBuilder initDate(LocalDate initDate) {
            this.initDate = initDate;
            return this;
        }

        public CreatedPriceResponseBuilder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public CreatedPriceResponse build() {
            validate();
            return new CreatedPriceResponse(productId, value, initDate, endDate);
        }

        private void validate() {
            if (productId == null) throw new IllegalArgumentException("productId es requerido");
            if (value == null) throw new IllegalArgumentException("value es requerido");
            if (value.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("value debe ser positivo");
            if (initDate == null) throw new IllegalArgumentException("initDate es requerido");
        }
    }
}

