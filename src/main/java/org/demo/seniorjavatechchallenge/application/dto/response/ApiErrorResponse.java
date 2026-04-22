package org.demo.seniorjavatechchallenge.application.dto.response;

import java.time.OffsetDateTime;

public record ApiErrorResponse(
        int status,
        String error,
        String message,
        String path,
        OffsetDateTime timestamp) {
}



