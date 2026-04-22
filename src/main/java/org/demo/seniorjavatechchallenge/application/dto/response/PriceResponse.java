package org.demo.seniorjavatechchallenge.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PriceResponse", description = "Price entry with dates")
public record PriceResponse(
		@Schema(description = "Price value", example = "99.99") BigDecimal value,
		@Schema(description = "Initial date (inclusive)", example = "2024-01-01") LocalDate initDate,
		@Schema(description = "End date (inclusive). Null means open range", example = "2024-06-30") LocalDate endDate) {
}




