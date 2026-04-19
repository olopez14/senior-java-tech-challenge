package org.demo.seniorjavatechchallenge.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateProductRequest(
        @NotBlank(message = "name is required")
        String name,
        @NotBlank(message = "description is required")
        String description) {
}



