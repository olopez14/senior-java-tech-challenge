package org.demo.seniorjavatechchallenge.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.demo.seniorjavatechchallenge.application.service.CreatePriceService;
import org.demo.seniorjavatechchallenge.application.service.GetCurrentPriceService;
import org.demo.seniorjavatechchallenge.application.service.FindProductForUpdateService;
import org.demo.seniorjavatechchallenge.application.service.FindProductService;
import org.demo.seniorjavatechchallenge.domain.model.Price;
import org.demo.seniorjavatechchallenge.domain.model.Product;
import org.demo.seniorjavatechchallenge.application.dto.request.CreatePriceRequest;
import org.demo.seniorjavatechchallenge.application.dto.response.CreatedPriceResponse;

import org.demo.seniorjavatechchallenge.domain.exception.InvalidDateRangeException;
import org.demo.seniorjavatechchallenge.domain.exception.PriceNotFoundForDateException;
import org.demo.seniorjavatechchallenge.domain.exception.PriceOverlapException;
import org.demo.seniorjavatechchallenge.domain.exception.ProductNotFoundException;
import org.demo.seniorjavatechchallenge.domain.exception.PreconditionViolationException;
import org.demo.seniorjavatechchallenge.domain.model.ProductDescription;
import org.demo.seniorjavatechchallenge.domain.model.ProductName;
import org.demo.seniorjavatechchallenge.domain.repository.PriceRepository;
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
    private PriceRepository priceRepository;
    @Mock
    private FindProductForUpdateService findProductForUpdateService;
    @Mock
    private FindProductService findProductService;

    private CreatePriceService createPriceService;
    private GetCurrentPriceService getCurrentPriceService;

    private Product mockProduct;
    private final Long productId = 1L;

    @BeforeEach
    void setUp() {
        mockProduct = new Product(
            productId,
            new ProductName("Test Product"),
            new ProductDescription("Test Description")
        );
        mockProduct = spy(mockProduct);
        createPriceService = new CreatePriceService(priceRepository, findProductForUpdateService, null);
        getCurrentPriceService = new GetCurrentPriceService(priceRepository, findProductService);
    }

    @Test
    @DisplayName("Should create price with valid closed date range")
    void createPrice_WithValidClosedDateRange_SucceedsAndSavesPrice() {
        LocalDate initDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 30);
        BigDecimal value = new BigDecimal("99.99");

        CreatePriceRequest request = new CreatePriceRequest(value, initDate, endDate);

        when(findProductForUpdateService.execute(productId)).thenReturn(mockProduct);
        when(priceRepository.save(any(Price.class))).thenAnswer(invocation -> {
            Price price = invocation.getArgument(0);
            return new Price(
                price.getProduct(),
                price.getValue(),
                price.getDateRange()
            );
        });

        CreatedPriceResponse response = createPriceService.execute(productId, request);

        assertEquals(value, response.value());
        assertEquals(initDate, response.initDate());
        assertEquals(endDate, response.endDate());
        verify(priceRepository, times(1)).save(any(Price.class));
    }

    @Test
    @DisplayName("Should throw PreconditionViolationException when CreatePriceRequest is null")
    void createPrice_WithNullRequest_ThrowsPreconditionViolationException() {
        assertThrows(PreconditionViolationException.class, () -> createPriceService.execute(productId, null));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Should throw PreconditionViolationException when productId is null")
    void createPrice_WithNullProductId_ThrowsPreconditionViolationException() {
        CreatePriceRequest request = new CreatePriceRequest(new BigDecimal("10.0"), LocalDate.now(), null);
        assertThrows(PreconditionViolationException.class, () -> createPriceService.execute(null, request));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Should create price with null end date (open range)")
    void createPrice_WithNullEndDate_SucceedsAndSavesPrice() {
        LocalDate initDate = LocalDate.of(2024, 1, 1);
        BigDecimal value = new BigDecimal("99.99");

        CreatePriceRequest request = new CreatePriceRequest(value, initDate, null);

        when(findProductForUpdateService.execute(productId)).thenReturn(mockProduct);
        when(priceRepository.save(any(Price.class))).thenAnswer(invocation -> {
            Price price = invocation.getArgument(0);
            return new Price(
                price.getProduct(),
                price.getValue(),
                price.getDateRange()
            );
        });

        CreatedPriceResponse response = createPriceService.execute(productId, request);

        assertEquals(value, response.value());
        assertEquals(initDate, response.initDate());
        assertNull(response.endDate());
        verify(priceRepository, times(1)).save(any(Price.class));
    }

    @Test
    @DisplayName("Should throw PreconditionViolationException when CreatePriceRequest.value is null")
    void createPrice_WithNullValue_ThrowsPreconditionViolationException() {
        CreatePriceRequest request = new CreatePriceRequest(null, LocalDate.now(), null);
        assertThrows(PreconditionViolationException.class, () -> createPriceService.execute(productId, request));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Should throw PreconditionViolationException when CreatePriceRequest.initDate is null")
    void createPrice_WithNullInitDate_ThrowsPreconditionViolationException() {
        CreatePriceRequest request = new CreatePriceRequest(new BigDecimal("10.0"), null, null);
        assertThrows(PreconditionViolationException.class, () -> createPriceService.execute(productId, request));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Should reject price creation when initDate >= endDate")
    void createPrice_WithInitDateGreaterThanEndDate_ThrowsInvalidDateRangeException() {
        LocalDate initDate = LocalDate.of(2024, 6, 30);
        LocalDate endDate = LocalDate.of(2024, 1, 1);
        BigDecimal value = new BigDecimal("99.99");

        CreatePriceRequest request = new CreatePriceRequest(value, initDate, endDate);

        assertThrows(InvalidDateRangeException.class, () -> createPriceService.execute(productId, request));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Should reject price creation when initDate equals endDate")
    void createPrice_WithInitDateEqualToEndDate_ThrowsInvalidDateRangeException() {
        LocalDate initDate = LocalDate.of(2024, 6, 30);
        LocalDate endDate = LocalDate.of(2024, 6, 30);
        BigDecimal value = new BigDecimal("99.99");

        CreatePriceRequest request = new CreatePriceRequest(value, initDate, endDate);

        assertThrows(InvalidDateRangeException.class, () -> createPriceService.execute(productId, request));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Should reject price creation when overlapping with existing price")
    void createPrice_WithOverlappingDateRange_ThrowsPriceOverlapException() {
        LocalDate initDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 30);
        BigDecimal value = new BigDecimal("99.99");

        CreatePriceRequest request = new CreatePriceRequest(value, initDate, endDate);

        when(findProductForUpdateService.execute(productId)).thenReturn(mockProduct);
        // Simular solapamiento correctamente:
        Price overlappingPrice = new Price(
            mockProduct,
            new org.demo.seniorjavatechchallenge.domain.model.Money(value),
            new org.demo.seniorjavatechchallenge.domain.model.DateRange(initDate, endDate)
        );
        when(priceRepository.findOverlappingPrices(productId, initDate, endDate)).thenReturn(List.of(overlappingPrice));

        assertThrows(PriceOverlapException.class, () -> createPriceService.execute(productId, request));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Should reject price creation when product does not exist")
    void createPrice_WithNonExistentProduct_ThrowsProductNotFoundException() {
        LocalDate initDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 30);
        BigDecimal value = new BigDecimal("99.99");

        CreatePriceRequest request = new CreatePriceRequest(value, initDate, endDate);

        when(findProductForUpdateService.execute(productId)).thenThrow(new ProductNotFoundException(productId));

        assertThrows(ProductNotFoundException.class, () -> createPriceService.execute(productId, request));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    @DisplayName("Should return current price when found for given date")
    void getCurrentPrice_WithValidDate_ReturnsPriceResponse() {
        LocalDate date = LocalDate.of(2024, 4, 15);
        when(priceRepository.findCurrentPriceValue(productId, date)).thenReturn(Optional.of(new BigDecimal("99.99")));

        var response = getCurrentPriceService.execute(productId, date);

        assertEquals(new BigDecimal("99.99"), response.value());
        verify(priceRepository, times(1)).findCurrentPriceValue(productId, date);
    }

    @Test
    @DisplayName("Should throw exception when no price found for given date")
    void getCurrentPrice_WithNoPriceForDate_ThrowsPriceNotFoundForDateException() {
        LocalDate date = LocalDate.of(2024, 4, 15);

        when(findProductService.execute(productId)).thenReturn(mockProduct);
        when(priceRepository.findCurrentPriceValue(productId, date)).thenReturn(Optional.empty());

        assertThrows(PriceNotFoundForDateException.class,
                     () -> getCurrentPriceService.execute(productId, date));
    }

    @Test
    @DisplayName("Should throw exception when product does not exist during price query")
    void getCurrentPrice_WithNonExistentProduct_ThrowsProductNotFoundException() {
        LocalDate date = LocalDate.of(2024, 4, 15);

        when(priceRepository.findCurrentPriceValue(productId, date)).thenReturn(Optional.empty());
        when(findProductService.execute(productId)).thenThrow(new ProductNotFoundException(productId));

        assertThrows(ProductNotFoundException.class,
                     () -> getCurrentPriceService.execute(productId, date));
        verify(priceRepository, times(1)).findCurrentPriceValue(productId, date);
        verify(findProductService, times(1)).execute(productId);
    }
}

