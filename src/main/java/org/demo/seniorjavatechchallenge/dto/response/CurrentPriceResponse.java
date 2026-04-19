package org.demo.seniorjavatechchallenge.dto.response;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CurrentPriceResponse", description = "Response with the current price value")
public record CurrentPriceResponse(@Schema(description = "Price value", example = "99.99") BigDecimal value) {
}




