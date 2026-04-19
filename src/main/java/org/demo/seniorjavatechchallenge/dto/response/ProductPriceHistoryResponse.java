package org.demo.seniorjavatechchallenge.dto.response;

import java.util.List;

public record ProductPriceHistoryResponse(Long id, String name, String description, List<PriceResponse> prices) {
}

