package org.demo.seniorjavatechchallenge.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePriceRequest(
        @NotNull(message = "value is required")
        @Positive(message = "value must be positive")
        BigDecimal value,
        @NotNull(message = "initDate is required")
        LocalDate initDate,
        LocalDate endDate) {
}

