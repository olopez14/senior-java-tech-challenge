package org.demo.seniorjavatechchallenge.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CreatePriceRequest", description = "Request to create a price for a product")
public record CreatePriceRequest(
        @Schema(description = "Price value", example = "99.99")
        @NotNull(message = "value is required")
        @Positive(message = "value must be positive")
        BigDecimal value,

        @Schema(description = "Initial date of price (inclusive)", example = "2024-01-01")
        @NotNull(message = "initDate is required")
        LocalDate initDate,

        @Schema(description = "End date of price (inclusive). Can be null for open range", example = "2024-06-30")
        LocalDate endDate) {
}

