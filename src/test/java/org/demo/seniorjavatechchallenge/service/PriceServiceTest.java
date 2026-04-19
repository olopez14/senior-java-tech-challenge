package org.demo.seniorjavatechchallenge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.demo.seniorjavatechchallenge.domain.Price;
import org.demo.seniorjavatechchallenge.domain.Product;
import org.demo.seniorjavatechchallenge.dto.request.CreatePriceRequest;
import org.demo.seniorjavatechchallenge.dto.response.CreatedPriceResponse;
import org.demo.seniorjavatechchallenge.exception.InvalidDateRangeException;
import org.demo.seniorjavatechchallenge.exception.PriceNotFoundForDateException;
import org.demo.seniorjavatechchallenge.exception.PriceOverlapException;
import org.demo.seniorjavatechchallenge.exception.ProductNotFoundException;
import org.demo.seniorjavatechchallenge.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("PriceService Unit Tests")
class PriceServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private PriceService priceService;

    private Product mockProduct;
    private Long productId = 1L;

    @BeforeEach
    void setUp() {
        mockProduct = new Product("Test Product", "Test Description");
    }

    @Test
    @DisplayName("Should create price with valid closed date range")
    void createPrice_WithValidClosedDateRange_SucceedsAndSavesPrice() {
        LocalDate initDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 30);
        BigDecimal value = new BigDecimal("99.99");

        CreatePriceRequest request = new CreatePriceRequest(value, initDate, endDate);

        when(productService.findProductForUpdateOrThrow(productId)).thenReturn(mockProduct);
        when(priceRepository.existsOverlappingPrice(productId, initDate, endDate)).thenReturn(false);
        when(priceRepository.save(any(Price.class))).thenAnswer(invocation -> {
            Price price = invocation.getArgument(0);
            return new Price(price.getProduct(), price.getValue(), price.getInitDate(), price.getEndDate());
        });

        CreatedPriceResponse response = priceService.createPrice(productId, request);

        assertEquals(value, response.value());
        assertEquals(initDate, response.initDate());
        assertEquals(endDate, response.endDate());
        verify(priceRepository, times(1)).save(any(Price.class));
    }

    @Test
    @DisplayName("Should create price with null end date (open range)")
    void createPrice_WithNullEndDate_SucceedsAndSavesPrice() {
        LocalDate initDate = LocalDate.of(2024, 1, 1);
        BigDecimal value = new BigDecimal("99.99");

        CreatePriceRequest request = new CreatePriceRequest(value, initDate, null);

        when(productService.findProductForUpdateOrThrow(productId)).thenReturn(mockProduct);
        when(priceRepository.existsOverlappingPrice(productId, initDate, null)).thenReturn(false);
        when(priceRepository.save(any(Price.class))).thenAnswer(invocation -> {
            Price price = invocation.getArgument(0);
            return new Price(price.getProduct(), price.getValue(), price.getInitDate(), price.getEndDate());
        });

        CreatedPriceResponse response = priceService.createPrice(productId, request);

        assertEquals(value, response.value());
        assertEquals(initDate, response.initDate());
        assertEquals(null, response.endDate());
        verify(priceRepository, times(1)).save(any(Price.class));
    }

    @Test
    @DisplayName("Should reject price creation when initDate >= endDate")
    void createPrice_WithInitDateGreaterThanEndDate_ThrowsInvalidDateRangeException() {
        LocalDate initDate = LocalDate.of(2024, 6, 30);
        LocalDate endDate = LocalDate.of(2024, 1, 1);
        BigDecimal value = new BigDecimal("99.99");

        CreatePriceRequest request = new CreatePriceRequest(value, initDate, endDate);

        assertThrows(InvalidDateRangeException.class, () -> priceService.createPrice(productId, request));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Should reject price creation when initDate equals endDate")
    void createPrice_WithInitDateEqualToEndDate_ThrowsInvalidDateRangeException() {
        LocalDate initDate = LocalDate.of(2024, 6, 30);
        LocalDate endDate = LocalDate.of(2024, 6, 30);
        BigDecimal value = new BigDecimal("99.99");

        CreatePriceRequest request = new CreatePriceRequest(value, initDate, endDate);

        assertThrows(InvalidDateRangeException.class, () -> priceService.createPrice(productId, request));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Should reject price creation when overlapping with existing price")
    void createPrice_WithOverlappingDateRange_ThrowsPriceOverlapException() {
        LocalDate initDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 30);
        BigDecimal value = new BigDecimal("99.99");

        CreatePriceRequest request = new CreatePriceRequest(value, initDate, endDate);

        when(productService.findProductForUpdateOrThrow(productId)).thenReturn(mockProduct);
        when(priceRepository.existsOverlappingPrice(productId, initDate, endDate)).thenReturn(true);

        assertThrows(PriceOverlapException.class, () -> priceService.createPrice(productId, request));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Should reject price creation when product does not exist")
    void createPrice_WithNonExistentProduct_ThrowsProductNotFoundException() {
        LocalDate initDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 30);
        BigDecimal value = new BigDecimal("99.99");

        CreatePriceRequest request = new CreatePriceRequest(value, initDate, endDate);

        when(productService.findProductForUpdateOrThrow(productId)).thenThrow(new ProductNotFoundException(productId));

        assertThrows(ProductNotFoundException.class, () -> priceService.createPrice(productId, request));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Should return current price when found for given date")
    void getCurrentPrice_WithValidDate_ReturnsPriceResponse() {
        LocalDate date = LocalDate.of(2024, 4, 15);
        Price mockPrice = new Price(mockProduct, new BigDecimal("99.99"),
                                    LocalDate.of(2024, 1, 1),
                                    LocalDate.of(2024, 6, 30));

        when(productService.findProductOrThrow(productId)).thenReturn(mockProduct);
        when(priceRepository.findCurrentPrice(productId, date)).thenReturn(Optional.of(mockPrice));

        var response = priceService.getCurrentPrice(productId, date);

        assertEquals(new BigDecimal("99.99"), response.value());
        verify(priceRepository, times(1)).findCurrentPrice(productId, date);
    }

    @Test
    @DisplayName("Should throw exception when no price found for given date")
    void getCurrentPrice_WithNoPriceForDate_ThrowsPriceNotFoundForDateException() {
        LocalDate date = LocalDate.of(2024, 4, 15);

        when(productService.findProductOrThrow(productId)).thenReturn(mockProduct);
        when(priceRepository.findCurrentPrice(productId, date)).thenReturn(Optional.empty());

        assertThrows(PriceNotFoundForDateException.class,
                     () -> priceService.getCurrentPrice(productId, date));
    }

    @Test
    @DisplayName("Should throw exception when product does not exist during price query")
    void getCurrentPrice_WithNonExistentProduct_ThrowsProductNotFoundException() {
        LocalDate date = LocalDate.of(2024, 4, 15);

        when(productService.findProductOrThrow(productId)).thenThrow(new ProductNotFoundException(productId));

        assertThrows(ProductNotFoundException.class,
                     () -> priceService.getCurrentPrice(productId, date));
        verify(priceRepository, never()).findCurrentPrice(anyLong(), any(LocalDate.class));
    }

}

