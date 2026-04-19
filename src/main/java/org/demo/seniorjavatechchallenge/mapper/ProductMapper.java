package org.demo.seniorjavatechchallenge.mapper;

import java.math.BigDecimal;

import org.demo.seniorjavatechchallenge.domain.Price;
import org.demo.seniorjavatechchallenge.domain.Product;
import org.demo.seniorjavatechchallenge.dto.request.CreatePriceRequest;
import org.demo.seniorjavatechchallenge.dto.request.CreateProductRequest;
import org.demo.seniorjavatechchallenge.dto.response.CreatedPriceResponse;
import org.demo.seniorjavatechchallenge.dto.response.CurrentPriceResponse;
import org.demo.seniorjavatechchallenge.dto.response.PriceResponse;
import org.demo.seniorjavatechchallenge.dto.response.ProductPriceHistoryResponse;
import org.demo.seniorjavatechchallenge.dto.response.ProductResponse;

/**
 * Mapper utility para convertir entre DTOs y entidades de dominio.
 * Proporciona métodos builder para garantizar type-safety.
 *
 * Uso:
 *   Product product = ProductMapper.toProduct(createProductRequest);
 *   ProductResponse response = ProductMapper.toResponse(product);
 */
public class ProductMapper {

    private ProductMapper() {
        // Utility class
    }

    // ============================================
    // PRODUCT MAPPERS
    // ============================================

    /**
     * Convierte CreateProductRequest a entidad Product.
     * Utiliza builder pattern para mayor seguridad.
     *
     * @param request Request del cliente
     * @return Entidad Product lista para persistir
     */
    public static Product toProduct(CreateProductRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CreateProductRequest no puede ser null");
        }

        return new Product(
            request.name(),
            request.description()
        );
    }

    /**
     * Convierte entidad Product a ProductResponse.
     * Para respuestas API sin datos internos.
     *
     * @param product Entidad desde BD
     * @return DTO de respuesta
     */
    public static ProductResponse toResponse(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product no puede ser null");
        }

        return ProductResponseBuilder.create()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .build();
    }

    /**
     * Convierte entidad Product a ProductPriceHistoryResponse.
     * Incluye historial de precios.
     *
     * @param product Entidad con precios asociados
     * @return DTO con historial completo
     */
    public static ProductPriceHistoryResponse toHistoryResponse(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product no puede ser null");
        }

        var prices = product.getPrices()
                .stream()
                .map(PriceMapper::toResponse)
                .toList();

        return new ProductPriceHistoryResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            prices
        );
    }

    // ============================================
    // BUILDER INTERNO PARA ProductResponse
    // ============================================
    public static class ProductResponseBuilder {
        private Long id;
        private String name;
        private String description;

        public static ProductResponseBuilder create() {
            return new ProductResponseBuilder();
        }

        public ProductResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ProductResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProductResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ProductResponse build() {
            validate();
            return new ProductResponse(id, name, description);
        }

        private void validate() {
            if (id == null) throw new IllegalArgumentException("id es requerido");
            if (name == null || name.isBlank()) throw new IllegalArgumentException("name es requerido");
            if (description == null || description.isBlank()) throw new IllegalArgumentException("description es requerido");
        }
    }
}

