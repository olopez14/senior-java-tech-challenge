package org.demo.seniorjavatechchallenge.exception;

public class InvalidDateRangeException extends RuntimeException {

    public InvalidDateRangeException() {
        super("initDate must be before endDate when both dates are provided");
    }
}

