package org.demo.seniorjavatechchallenge.dto.response;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ProductPriceHistoryResponse", description = "Product with full ordered price history")
public record ProductPriceHistoryResponse(
		@Schema(description = "Product id", example = "1") Long id,
		@Schema(description = "Product name", example = "Zapatillas deportivas") String name,
		@Schema(description = "Product description", example = "Modelo 2025 ediciÃ³n limitada") String description,
		@Schema(description = "Ordered list of prices (by initDate)") List<PriceResponse> prices) {
}




