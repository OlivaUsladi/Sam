package com.example.backend.dto;

import com.example.backend.entity.GoalEntity;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class GoalDtos {

    private GoalDtos() {}

    public record CreateGoalRequest(
            @NotBlank @Size(max = 100) String name,
            @Size(max = 250) String description,
            @NotNull @DecimalMin("0.01") BigDecimal targetAmount,
            LocalDate targetDate,
            @DecimalMin("0.00") BigDecimal monthlyAmount
    ) {}

    public record UpdateGoalRequest(
            @NotBlank @Size(max = 100) String name,
            @Size(max = 250) String description,
            @NotNull @DecimalMin("0.01") BigDecimal targetAmount,
            @NotNull @DecimalMin("0.00") BigDecimal currentAmount,
            LocalDate targetDate,
            @DecimalMin("0.00") BigDecimal monthlyAmount
    ) {}

    public record GoalResponse(
            Integer id,
            String name,
            String description,
            BigDecimal targetAmount,
            BigDecimal currentAmount,
            LocalDate targetDate,
            BigDecimal monthlyAmount
    ) {
        public static GoalResponse from(GoalEntity e) {
            return new GoalResponse(
                    e.getId(), e.getName(), e.getDescription(),
                    e.getTargetAmount(), e.getCurrentAmount(),
                    e.getTargetDate(), e.getMonthlyAmount());
        }
    }
}
