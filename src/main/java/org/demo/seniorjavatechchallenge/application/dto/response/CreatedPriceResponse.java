package org.demo.seniorjavatechchallenge.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CreatedPriceResponse", description = "Response after creating a price")
public record CreatedPriceResponse(
		@Schema(description = "Product id", example = "1") Long productId,
		@Schema(description = "Price value", example = "99.99") BigDecimal value,
		@Schema(description = "Initial date", example = "2024-01-01") LocalDate initDate,
		@Schema(description = "End date", example = "2024-06-30") LocalDate endDate) {
}




