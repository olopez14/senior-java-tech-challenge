package org.demo.seniorjavatechchallenge.exception;

public class PriceOverlapException extends RuntimeException {

    public PriceOverlapException(Long productId) {
        super("The provided price range overlaps an existing price for product %d".formatted(productId));
    }
}

