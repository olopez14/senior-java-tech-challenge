package org.demo.seniorjavatechchallenge.domain.exception;

public class PreconditionViolationException extends RuntimeException {
    public PreconditionViolationException(String message) {
        super(message);
    }
}

