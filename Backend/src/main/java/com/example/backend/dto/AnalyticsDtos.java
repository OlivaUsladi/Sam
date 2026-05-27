package com.example.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class AnalyticsDtos {

    private AnalyticsDtos() {}

    public record DailyTotalPoint(LocalDate date, BigDecimal amount) {}

    public record SourceBucket(Integer sourceId, String sourceName, BigDecimal amount) {}

    public record TagBucket(Integer tagId, String tagName, BigDecimal amount) {}

    public record AnalyticsResponse(
            LocalDate periodStart,
            LocalDate periodEnd,
            String type,                       // income/expense
            BigDecimal total,
            List<DailyTotalPoint> daily,
            List<SourceBucket> bySource,
            List<TagBucket>    byTag           // null для income
    ) {}
}
