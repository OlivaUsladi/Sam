package com.example.backend.service;

import com.example.backend.dto.AnalyticsDtos.*;
import com.example.backend.entity.SourceEntity;
import com.example.backend.entity.TagEntity;
import com.example.backend.entity.TransactionEntity;
import com.example.backend.repository.SourceRepository;
import com.example.backend.repository.TagRepository;
import com.example.backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;


@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final SourceRepository sourceRepository;
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public AnalyticsResponse forMonth(Integer userId, YearMonth month, String type) {
        LocalDate from = month.atDay(1);
        LocalDate to   = month.atEndOfMonth();
        return analyse(userId, from, to, type);
    }

    @Transactional(readOnly = true)
    public AnalyticsResponse forRange(Integer userId, LocalDate from, LocalDate to, String type) {
        return analyse(userId, from, to, type);
    }

    private AnalyticsResponse analyse(Integer userId, LocalDate from, LocalDate to, String type) {
        List<TransactionEntity> txs = transactionRepository
                .findByUserIdAndTypeAndTransactionDateBetweenOrderByTransactionDateDescIdDesc(
                        userId, type, from, to);

        // По дням
        Map<LocalDate, BigDecimal> dailyMap = new TreeMap<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            dailyMap.put(d, BigDecimal.ZERO);
        }
        BigDecimal total = BigDecimal.ZERO;
        for (TransactionEntity t : txs) {
            dailyMap.merge(t.getTransactionDate(), t.getAmount(), BigDecimal::add);
            total = total.add(t.getAmount());
        }
        List<DailyTotalPoint> daily = dailyMap.entrySet().stream()
                .map(en -> new DailyTotalPoint(en.getKey(), en.getValue()))
                .toList();

        // По источникам
        Map<Integer, BigDecimal> bySourceMap = new HashMap<>();
        for (TransactionEntity t : txs) {
            bySourceMap.merge(t.getSourceId(), t.getAmount(), BigDecimal::add);
        }
        Map<Integer, String> sourceNames = new HashMap<>();
        for (SourceEntity s : sourceRepository.findByUserIdOrderByNameAsc(userId)) {
            sourceNames.put(s.getId(), s.getName());
        }
        List<SourceBucket> bySource = bySourceMap.entrySet().stream()
                .map(en -> new SourceBucket(en.getKey(),
                        sourceNames.getOrDefault(en.getKey(), "—"), en.getValue()))
                .sorted(Comparator.comparing(SourceBucket::amount).reversed())
                .toList();

        // По тэгам
        List<TagBucket> byTag = null;
        if ("expense".equals(type)) {
            Map<Integer, BigDecimal> byTagMap = new HashMap<>();
            for (TransactionEntity t : txs) {
                if (t.getTagId() == null) continue;
                byTagMap.merge(t.getTagId(), t.getAmount(), BigDecimal::add);
            }
            Map<Integer, String> tagNames = new HashMap<>();
            for (TagEntity tag : tagRepository.findByUserIdOrderByNameAsc(userId)) {
                tagNames.put(tag.getId(), tag.getName());
            }
            byTag = byTagMap.entrySet().stream()
                    .map(en -> new TagBucket(en.getKey(),
                            tagNames.getOrDefault(en.getKey(), "—"), en.getValue()))
                    .sorted(Comparator.comparing(TagBucket::amount).reversed())
                    .toList();
        }

        return new AnalyticsResponse(from, to, type, total, daily, bySource, byTag);
    }
}
