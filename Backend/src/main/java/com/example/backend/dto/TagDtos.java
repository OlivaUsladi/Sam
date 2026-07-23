package com.example.backend.dto;

import com.example.backend.entity.TagEntity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public final class TagDtos {

    private TagDtos() {}

    public record CreateTagRequest(
            @NotBlank @Size(max = 50) String name,
            @DecimalMin(value = "0.0", inclusive = false) BigDecimal monthlyLimit
    ) {}

    public record UpdateTagRequest(
            @NotBlank @Size(max = 50) String name,
            @DecimalMin(value = "0.0", inclusive = false) BigDecimal monthlyLimit
    ) {}

    public record TagResponse(
            Integer id,
            String name,
            BigDecimal totalAmountSpent,
            BigDecimal monthlyLimit
    ) {
        public static TagResponse from(TagEntity e) {
            return new TagResponse(e.getId(), e.getName(), e.getTotalAmountSpent(), e.getMonthlyLimit());
        }
    }
}
