package org.demo.seniorjavatechchallenge.exception;

import java.time.LocalDate;

public class PriceNotFoundForDateException extends RuntimeException {

    public PriceNotFoundForDateException(Long productId, LocalDate date) {
        super("No price found for product %d on date %s".formatted(productId, date));
    }
}

