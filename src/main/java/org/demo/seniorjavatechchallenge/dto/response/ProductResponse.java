package org.demo.seniorjavatechchallenge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ProductResponse", description = "Created product representation")
public record ProductResponse(
		@Schema(description = "Product id", example = "1") Long id,
		@Schema(description = "Product name", example = "Zapatillas deportivas") String name,
		@Schema(description = "Product description", example = "Modelo 2025 ediciÃ³n limitada") String description) {
}



