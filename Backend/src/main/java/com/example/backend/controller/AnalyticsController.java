package com.example.backend.controller;

import com.example.backend.dto.AnalyticsDtos.AnalyticsResponse;
import com.example.backend.security.AuthenticatedUser;
import com.example.backend.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/finance/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService service;

    @GetMapping
    public AnalyticsResponse forMonth(@AuthenticationPrincipal AuthenticatedUser principal,
                                      @RequestParam(name = "month", required = false) String month,
                                      @RequestParam(name = "type", defaultValue = "expense") String type) {
        YearMonth ym = (month == null || month.isBlank())
                ? YearMonth.now()
                : YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        return service.forMonth(principal.id(), ym, type);
    }

    @GetMapping("/range")
    public AnalyticsResponse forRange(@AuthenticationPrincipal AuthenticatedUser principal,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                      @RequestParam(name = "type", defaultValue = "expense") String type) {
        return service.forRange(principal.id(), from, to, type);
    }
}
