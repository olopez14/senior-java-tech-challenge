package org.demo.seniorjavatechchallenge.infrastructure.controller;

import java.time.LocalDate;


import org.demo.seniorjavatechchallenge.application.dto.request.CreatePriceRequest;
import org.demo.seniorjavatechchallenge.application.dto.request.CreateProductRequest;
import org.demo.seniorjavatechchallenge.application.dto.response.CreatedPriceResponse;
import org.demo.seniorjavatechchallenge.application.dto.response.CurrentPriceResponse;
import org.demo.seniorjavatechchallenge.application.dto.response.ProductPriceHistoryResponse;
import org.demo.seniorjavatechchallenge.application.dto.response.ProductResponse;
import org.demo.seniorjavatechchallenge.application.service.CreateProductService;
import org.demo.seniorjavatechchallenge.application.service.CreatePriceService;
import org.demo.seniorjavatechchallenge.application.service.GetCurrentPriceService;
import org.demo.seniorjavatechchallenge.application.service.GetProductPriceHistoryService;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

    private final CreateProductService createProductService;
    private final CreatePriceService createPriceService;
    private final GetCurrentPriceService getCurrentPriceService;
    private final GetProductPriceHistoryService getProductPriceHistoryService;

    public ProductController(
            CreateProductService createProductService,
            CreatePriceService createPriceService,
            GetCurrentPriceService getCurrentPriceService,
            GetProductPriceHistoryService getProductPriceHistoryService
    ) {
        this.createProductService = createProductService;
        this.createPriceService = createPriceService;
        this.getCurrentPriceService = getCurrentPriceService;
        this.getProductPriceHistoryService = getProductPriceHistoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a product", description = "Creates a new product and returns it with generated id")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        return createProductService.execute(request);
    }

    @PostMapping("/{id}/prices")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add price to a product", description = "Adds a price to the product identified by id. Validates date ranges and overlapping.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Price created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreatedPriceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Price overlap conflict")
    })
    public CreatedPriceResponse createPrice(@PathVariable Long id, @Valid @RequestBody CreatePriceRequest request) {
        return createPriceService.execute(id, request);
    }

    @GetMapping(value = "/{id}/prices", params = "date")
    @Operation(summary = "Get current price for a date", description = "Returns the price that is effective for the given date (inclusive)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Price found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrentPriceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product or price not found")
    })
    public CurrentPriceResponse getCurrentPrice(
            @PathVariable Long id,
            @Parameter(description = "Date to query (YYYY-MM-DD)", example = "2024-04-15") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return getCurrentPriceService.execute(id, date);
    }

    @GetMapping("/{id}/prices")
    @Operation(summary = "Get full price history", description = "Returns product info and full ordered list of prices")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "History returned", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductPriceHistoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ProductPriceHistoryResponse getPriceHistory(@PathVariable Long id) {
        return getProductPriceHistoryService.execute(id);
    }
}



