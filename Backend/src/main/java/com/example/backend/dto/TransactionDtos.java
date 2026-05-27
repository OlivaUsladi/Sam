package com.example.backend.dto;

import com.example.backend.entity.TransactionEntity;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class TransactionDtos {

    private TransactionDtos() {}

    public record CreateTransactionRequest(
            @NotBlank @Size(max = 100) String name,
            @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
            @NotBlank @Pattern(regexp = "income|expense") String type,
            @Size(max = 200) String description,
            @NotNull LocalDate transactionDate,
            @NotNull Integer sourceId,
            Integer tagId
    ) {}

    public record UpdateTransactionRequest(
            @NotBlank @Size(max = 100) String name,
            @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
            @NotBlank @Pattern(regexp = "income|expense") String type,
            @Size(max = 200) String description,
            @NotNull LocalDate transactionDate,
            @NotNull Integer sourceId,
            Integer tagId
    ) {}

    public record TransactionResponse(
            Integer id,
            String name,
            BigDecimal amount,
            String type,
            String description,
            LocalDate transactionDate,
            Integer sourceId,
            Integer tagId
    ) {
        public static TransactionResponse from(TransactionEntity e) {
            return new TransactionResponse(
                    e.getId(), e.getName(), e.getAmount(), e.getType(),
                    e.getDescription(), e.getTransactionDate(),
                    e.getSourceId(), e.getTagId());
        }
    }

    public record AssignTagRequest(
            @NotNull List<@NotNull Integer> transactionIds
    ) {}
}
