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


public class ProductMapper {

    private ProductMapper() {
        
    }

    
    
    

    
    public static Product toProduct(CreateProductRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CreateProductRequest no puede ser null");
        }
        
        return new Product(
            request.name(),
            request.description()
        );
    }

    
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



