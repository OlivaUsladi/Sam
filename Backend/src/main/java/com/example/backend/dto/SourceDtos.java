package com.example.backend.dto;

import com.example.backend.entity.SourceEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class SourceDtos {

    private SourceDtos() {}

    public record CreateSourceRequest(
            @NotBlank @Size(max = 50) String name,
            @NotBlank @Pattern(regexp = "bank|cash|card") String type
    ) {}

    public record UpdateSourceRequest(
            @NotBlank @Size(max = 50) String name,
            @NotBlank @Pattern(regexp = "bank|cash|card") String type
    ) {}

    public record SourceResponse(
            Integer id,
            String name,
            String type
    ) {
        public static SourceResponse from(SourceEntity e) {
            return new SourceResponse(e.getId(), e.getName(), e.getType());
        }
    }
}
