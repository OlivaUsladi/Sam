package com.example.backend.service;

import com.example.backend.dto.GoalDtos.*;
import com.example.backend.entity.GoalEntity;
import com.example.backend.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;

    @Transactional(readOnly = true)
    public List<GoalResponse> list(Integer userId) {
        return goalRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(GoalResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public GoalResponse get(Integer userId, Integer goalId) {
        return goalRepository.findByIdAndUserId(goalId, userId)
                .map(GoalResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Цель не найдена"));
    }

    @Transactional
    public GoalResponse create(Integer userId, CreateGoalRequest req) {
        GoalEntity e = new GoalEntity();
        e.setUserId(userId);
        e.setName(req.name().trim());
        e.setDescription(req.description());
        e.setTargetAmount(req.targetAmount());
        e.setCurrentAmount(BigDecimal.ZERO);
        e.setTargetDate(req.targetDate());
        e.setMonthlyAmount(req.monthlyAmount() != null
                ? req.monthlyAmount()
                : recommendedMonthlyAmount(req.targetAmount(), BigDecimal.ZERO, req.targetDate()));
        return GoalResponse.from(goalRepository.save(e));
    }

    @Transactional
    public GoalResponse update(Integer userId, Integer goalId, UpdateGoalRequest req) {
        GoalEntity e = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Цель не найдена"));
        e.setName(req.name().trim());
        e.setDescription(req.description());
        e.setTargetAmount(req.targetAmount());
        e.setCurrentAmount(req.currentAmount());
        e.setTargetDate(req.targetDate());
        e.setMonthlyAmount(req.monthlyAmount() != null
                ? req.monthlyAmount()
                : recommendedMonthlyAmount(req.targetAmount(), req.currentAmount(), req.targetDate()));
        return GoalResponse.from(goalRepository.save(e));
    }

    @Transactional
    public void delete(Integer userId, Integer goalId) {
        GoalEntity e = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Цель не найдена"));
        goalRepository.delete(e);
    }

    private static BigDecimal recommendedMonthlyAmount(BigDecimal target, BigDecimal current, LocalDate targetDate) {
        if (targetDate == null) return null;
        long months = ChronoUnit.MONTHS.between(LocalDate.now().withDayOfMonth(1),
                                                targetDate.withDayOfMonth(1));
        if (months <= 0) return null;
        BigDecimal remaining = target.subtract(current);
        if (remaining.signum() <= 0) return BigDecimal.ZERO;
        return remaining.divide(BigDecimal.valueOf(months), 0, RoundingMode.CEILING);
    }
}
