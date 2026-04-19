package org.demo.seniorjavatechchallenge.service;

import java.time.LocalDate;

import org.demo.seniorjavatechchallenge.domain.Price;
import org.demo.seniorjavatechchallenge.domain.Product;
import org.demo.seniorjavatechchallenge.dto.request.CreatePriceRequest;
import org.demo.seniorjavatechchallenge.dto.response.CreatedPriceResponse;
import org.demo.seniorjavatechchallenge.dto.response.CurrentPriceResponse;
import org.demo.seniorjavatechchallenge.exception.InvalidDateRangeException;
import org.demo.seniorjavatechchallenge.exception.PriceNotFoundForDateException;
import org.demo.seniorjavatechchallenge.exception.PriceOverlapException;
import org.demo.seniorjavatechchallenge.repository.PriceRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PriceService {

    private final ProductService productService;
    private final PriceRepository priceRepository;

    public PriceService(ProductService productService, PriceRepository priceRepository) {
        this.productService = productService;
        this.priceRepository = priceRepository;
    }

    @Transactional
    public CreatedPriceResponse createPrice(Long productId, CreatePriceRequest request) {
        validateDateRange(request);
        Product product = productService.findProductOrThrow(productId);
        if (priceRepository.existsOverlappingPrice(productId, request.initDate(), request.endDate())) {
            throw new PriceOverlapException(productId);
        }

        Price savedPrice = priceRepository.save(new Price(product, request.value(), request.initDate(), request.endDate()));
        return new CreatedPriceResponse(savedPrice.getProduct().getId(), savedPrice.getValue(), savedPrice.getInitDate(),
                savedPrice.getEndDate());
    }

    @Transactional(readOnly = true)
    public CurrentPriceResponse getCurrentPrice(Long productId, LocalDate date) {
        productService.findProductOrThrow(productId);
        return priceRepository.findCurrentPrices(productId, date, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(price -> new CurrentPriceResponse(price.getValue()))
                .orElseThrow(() -> new PriceNotFoundForDateException(productId, date));
    }

    private void validateDateRange(CreatePriceRequest request) {
        if (request.endDate() != null && !request.initDate().isBefore(request.endDate())) {
            throw new InvalidDateRangeException();
        }
    }
}

