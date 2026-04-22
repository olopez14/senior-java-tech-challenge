package org.demo.seniorjavatechchallenge.domain.exception;

public class PriceOverlapException extends RuntimeException {

    public PriceOverlapException(Long productId) {
        super("The provided price range overlaps an existing price for product %d".formatted(productId));
    }
}

