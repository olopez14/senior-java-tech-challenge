package org.demo.seniorjavatechchallenge.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import org.demo.seniorjavatechchallenge.domain.Price;
import org.demo.seniorjavatechchallenge.domain.Product;
import org.demo.seniorjavatechchallenge.dto.request.CreateProductRequest;
import org.demo.seniorjavatechchallenge.dto.response.ProductPriceHistoryResponse;
import org.demo.seniorjavatechchallenge.dto.response.ProductResponse;
import org.demo.seniorjavatechchallenge.exception.ProductNotFoundException;
import org.demo.seniorjavatechchallenge.repository.PriceRepository;
import org.demo.seniorjavatechchallenge.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PriceRepository priceRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, priceRepository);
    }

    @Test
    @DisplayName("Should create product successfully")
    void createProduct_WithValidRequest_ReturnsProductResponse() {
        
        CreateProductRequest request = new CreateProductRequest("Test Product", "Test Description");
        Product product = new Product("Test Product", "Test Description");
        product = spy(product);
        when(product.getId()).thenReturn(1L);

        when(productRepository.save(any(Product.class))).thenReturn(product);

        
        ProductResponse response = productService.createProduct(request);

        
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Test Product", response.name());
        assertEquals("Test Description", response.description());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should find product successfully")
    void findProductOrThrow_WithExistingProduct_ReturnsProduct() {
        
        Long productId = 1L;
        Product product = new Product("Test Product", "Test Description");
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        
        Product result = productService.findProductOrThrow(productId);

        
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product not found")
    void findProductOrThrow_WithNonExistentProduct_ThrowsProductNotFoundException() {
        
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        
        assertThrows(ProductNotFoundException.class, () -> productService.findProductOrThrow(productId));
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should find product for update successfully")
    void findProductForUpdateOrThrow_WithExistingProduct_ReturnsProduct() {
        
        Long productId = 1L;
        Product product = new Product("Test Product", "Test Description");
        when(productRepository.findByIdForUpdate(productId)).thenReturn(Optional.of(product));

        
        Product result = productService.findProductForUpdateOrThrow(productId);

        
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).findByIdForUpdate(productId);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product not found for update")
    void findProductForUpdateOrThrow_WithNonExistentProduct_ThrowsProductNotFoundException() {
        
        Long productId = 999L;
        when(productRepository.findByIdForUpdate(productId)).thenReturn(Optional.empty());

        
        assertThrows(ProductNotFoundException.class, () -> productService.findProductForUpdateOrThrow(productId));
        verify(productRepository, times(1)).findByIdForUpdate(productId);
    }

    @Test
    @DisplayName("Should get product price history successfully")
    void getProductPriceHistory_WithExistingProduct_ReturnsPriceHistory() {
        
        Long productId = 1L;
        Product product = new Product("Test Product", "Test Description");
        product.setId(productId);

        Price price = new Price(product, new BigDecimal("99.99"), LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 6, 30));

        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        
        when(priceRepository.findByProductIdOrderByInitDateAsc(productId))
                .thenReturn(Collections.singletonList(price));

        
        ProductPriceHistoryResponse response = productService.getProductPriceHistory(productId);

        
        assertNotNull(response);
        assertEquals(productId, response.id());
        assertEquals("Test Product", response.name());
        assertEquals("Test Description", response.description());
        assertEquals(1, response.prices().size());
        verify(productRepository, times(1)).findById(productId);
        verify(priceRepository, times(1)).findByProductIdOrderByInitDateAsc(productId);
    }

    @Test
    @DisplayName("Should get product price history with empty prices")
    void getProductPriceHistory_WithNoPrice_ReturnsEmptyPriceHistory() {
        
        Long productId = 1L;
        Product product = new Product("Test Product", "Test Description");
        product.setId(productId);

        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        
        when(priceRepository.findByProductIdOrderByInitDateAsc(productId))
                .thenReturn(Collections.emptyList());

        
        ProductPriceHistoryResponse response = productService.getProductPriceHistory(productId);

        
        assertNotNull(response);
        assertEquals(productId, response.id());
        assertEquals(0, response.prices().size());
        verify(productRepository, times(1)).findById(productId);
        verify(priceRepository, times(1)).findByProductIdOrderByInitDateAsc(productId);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when getting price history of non-existent product")
    void getProductPriceHistory_WithNonExistentProduct_ThrowsProductNotFoundException() {
        
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        
        assertThrows(ProductNotFoundException.class, () -> productService.getProductPriceHistory(productId));
        verify(productRepository, times(1)).findById(productId);
    }
}





