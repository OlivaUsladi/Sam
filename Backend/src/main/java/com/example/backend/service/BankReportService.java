package com.example.backend.service;

import com.example.backend.dto.ImportReportDtos.ImportReportResponse;
import com.example.backend.entity.TransactionEntity;
import com.example.backend.repository.SourceRepository;
import com.example.backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BankReportService {

    private final TransactionRepository transactionRepository;
    private final SourceRepository sourceRepository;
    private final BankStatementParser parser;

    private static final DateTimeFormatter YM = DateTimeFormatter.ofPattern("yyyy-MM");

    @Transactional
    public ImportReportResponse importReport(
            Integer userId, Integer sourceId, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Файл пустой");
        }
        String contentType = file.getContentType();
        String original = file.getOriginalFilename() == null ? "statement.pdf" : file.getOriginalFilename();
        if (contentType != null
                && !contentType.equals("application/pdf")
                && !original.toLowerCase().endsWith(".pdf")) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Поддерживаются только PDF-выписки");
        }

        sourceRepository.findByIdAndUserId(sourceId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Источник не найден"));

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Не удалось прочитать файл: " + ex.getMessage());
        }

        var parsed = parser.parse(original, bytes);
        int imported = 0, skipped = 0;

        LocalDate firstDate = null;
        LocalDate lastDate  = null;

        Map<String, Integer> perMonth = new HashMap<>();

        for (var p : parsed) {
            boolean dup = transactionRepository.existsDuplicate(
                    userId, sourceId, p.type(), p.date(), p.name(), p.amount());
            if (dup) {
                skipped++;
                continue;
            }
            TransactionEntity e = new TransactionEntity();
            e.setUserId(userId);
            e.setName(p.name());
            e.setAmount(p.amount());
            e.setType(p.type());
            e.setDescription(p.description());
            e.setTransactionDate(p.date());
            e.setSourceId(sourceId);
            e.setTagId(null);
            transactionRepository.save(e);
            imported++;

            if (firstDate == null || p.date().isBefore(firstDate)) firstDate = p.date();
            if (lastDate  == null || p.date().isAfter(lastDate))  lastDate  = p.date();

            String ym = YearMonth.from(p.date()).format(YM);
            perMonth.merge(ym, 1, Integer::sum);
        }

        String suggestedMonth = perMonth.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return new ImportReportResponse(
                original, sourceId, imported, skipped, parsed.size(),
                firstDate, lastDate, suggestedMonth);
    }
}
