package org.demo.seniorjavatechchallenge.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import org.demo.seniorjavatechchallenge.application.service.CreateProductService;
import org.demo.seniorjavatechchallenge.application.service.FindProductService;
import org.demo.seniorjavatechchallenge.application.service.FindProductForUpdateService;
import org.demo.seniorjavatechchallenge.application.service.GetProductPriceHistoryService;
import org.demo.seniorjavatechchallenge.domain.model.*;

import org.demo.seniorjavatechchallenge.application.dto.request.CreateProductRequest;
import org.demo.seniorjavatechchallenge.application.dto.response.ProductPriceHistoryResponse;
import org.demo.seniorjavatechchallenge.application.dto.response.ProductResponse;
import org.demo.seniorjavatechchallenge.domain.exception.ProductNotFoundException;
import org.demo.seniorjavatechchallenge.domain.exception.PreconditionViolationException;
import org.demo.seniorjavatechchallenge.domain.repository.PriceRepository;
import org.demo.seniorjavatechchallenge.domain.repository.ProductRepository;
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

    private CreateProductService createProductService;
    private FindProductService findProductService;
    private FindProductForUpdateService findProductForUpdateService;
    private GetProductPriceHistoryService getProductPriceHistoryService;

    @BeforeEach
    void setUp() {
        createProductService = new CreateProductService(productRepository);
        findProductService = new FindProductService(productRepository);
        findProductForUpdateService = new FindProductForUpdateService(productRepository);
        getProductPriceHistoryService = new GetProductPriceHistoryService(priceRepository, findProductService);
    }

    @Test
    @DisplayName("Should create product successfully")
    void createProduct_WithValidRequest_ReturnsProductResponse() {
        CreateProductRequest request = new CreateProductRequest("Test Product", "Test Description");
        Product product = new Product(
            1L,
            new ProductName("Test Product"),
            new ProductDescription("Test Description")
        );
        product = spy(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        ProductResponse response = createProductService.execute(request);
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Test Product", response.name());
        assertEquals("Test Description", response.description());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw PreconditionViolationException when CreateProductRequest is null")
    void createProduct_WithNullRequest_ThrowsPreconditionViolationException() {
        assertThrows(PreconditionViolationException.class, () -> createProductService.execute(null));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw PreconditionViolationException when CreateProductRequest.name is null")
    void createProduct_WithNullName_ThrowsPreconditionViolationException() {
        CreateProductRequest request = new CreateProductRequest(null, "desc");
        assertThrows(PreconditionViolationException.class, () -> createProductService.execute(request));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw PreconditionViolationException when CreateProductRequest.description is null")
    void createProduct_WithNullDescription_ThrowsPreconditionViolationException() {
        CreateProductRequest request = new CreateProductRequest("name", null);
        assertThrows(PreconditionViolationException.class, () -> createProductService.execute(request));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should find product successfully")
    void findProductOrThrow_WithExistingProduct_ReturnsProduct() {
        Long productId = 1L;
        Product product = new Product(
            productId,
            new org.demo.seniorjavatechchallenge.domain.model.ProductName("Test Product"),
            new org.demo.seniorjavatechchallenge.domain.model.ProductDescription("Test Description")
        );
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        Product result = findProductService.execute(productId);
        assertNotNull(result);
        assertEquals("Test Product", result.getName().getValue());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product not found")
    void findProductOrThrow_WithNonExistentProduct_ThrowsProductNotFoundException() {
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> findProductService.execute(productId));
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should find product for update successfully")
    void findProductForUpdateOrThrow_WithExistingProduct_ReturnsProduct() {
        Long productId = 1L;
        Product product = new Product(
            productId,
            new org.demo.seniorjavatechchallenge.domain.model.ProductName("Test Product"),
            new org.demo.seniorjavatechchallenge.domain.model.ProductDescription("Test Description")
        );
        when(productRepository.findByIdForUpdate(productId)).thenReturn(Optional.of(product));
        Product result = findProductForUpdateService.execute(productId);
        assertNotNull(result);
        assertEquals("Test Product", result.getName().getValue());
        verify(productRepository, times(1)).findByIdForUpdate(productId);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product not found for update")
    void findProductForUpdateOrThrow_WithNonExistentProduct_ThrowsProductNotFoundException() {
        Long productId = 999L;
        when(productRepository.findByIdForUpdate(productId)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> findProductForUpdateService.execute(productId));
        verify(productRepository, times(1)).findByIdForUpdate(productId);
    }

    @Test
    @DisplayName("Should get product price history successfully")
    void getProductPriceHistory_WithExistingProduct_ReturnsPriceHistory() {
        Long productId = 1L;
        Product product = new Product(
            productId,
            new ProductName("Test Product"),
            new ProductDescription("Test Description")
        );
        Price price = new Price(
            product,
            new Money(new java.math.BigDecimal("99.99")),
            new DateRange(java.time.LocalDate.of(2024, 1, 1), java.time.LocalDate.of(2024, 6, 30))
        );
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(priceRepository.findByProductIdOrderByInitDateAsc(productId))
            .thenReturn(Collections.singletonList(price));
        ProductPriceHistoryResponse response = getProductPriceHistoryService.execute(productId);
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
        Product product = new Product(
            productId,
            new ProductName("Test Product"),
            new ProductDescription("Test Description")
        );
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(priceRepository.findByProductIdOrderByInitDateAsc(productId))
            .thenReturn(Collections.emptyList());
        ProductPriceHistoryResponse response = getProductPriceHistoryService.execute(productId);
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
        assertThrows(ProductNotFoundException.class, () -> getProductPriceHistoryService.execute(productId));
        verify(productRepository, times(1)).findById(productId);
    }
}
