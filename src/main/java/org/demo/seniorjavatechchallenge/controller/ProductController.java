package org.demo.seniorjavatechchallenge.controller;

import java.time.LocalDate;

import org.demo.seniorjavatechchallenge.dto.request.CreatePriceRequest;
import org.demo.seniorjavatechchallenge.dto.request.CreateProductRequest;
import org.demo.seniorjavatechchallenge.dto.response.CreatedPriceResponse;
import org.demo.seniorjavatechchallenge.dto.response.CurrentPriceResponse;
import org.demo.seniorjavatechchallenge.dto.response.ProductPriceHistoryResponse;
import org.demo.seniorjavatechchallenge.dto.response.ProductResponse;
import org.demo.seniorjavatechchallenge.service.PriceService;
import org.demo.seniorjavatechchallenge.service.ProductService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final PriceService priceService;

    public ProductController(ProductService productService, PriceService priceService) {
        this.productService = productService;
        this.priceService = priceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        return productService.createProduct(request);
    }

    @PostMapping("/{id}/prices")
    @ResponseStatus(HttpStatus.CREATED)
    public CreatedPriceResponse createPrice(@PathVariable Long id, @Valid @RequestBody CreatePriceRequest request) {
        return priceService.createPrice(id, request);
    }

    @GetMapping(value = "/{id}/prices", params = "date")
    public CurrentPriceResponse getCurrentPrice(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return priceService.getCurrentPrice(id, date);
    }

    @GetMapping("/{id}/prices")
    public ProductPriceHistoryResponse getPriceHistory(@PathVariable Long id) {
        return productService.getProductPriceHistory(id);
    }
}

