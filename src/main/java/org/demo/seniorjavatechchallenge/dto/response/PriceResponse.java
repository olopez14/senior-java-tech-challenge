package org.demo.seniorjavatechchallenge.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PriceResponse(BigDecimal value, LocalDate initDate, LocalDate endDate) {
}

