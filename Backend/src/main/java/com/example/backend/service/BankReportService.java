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

@Service
@RequiredArgsConstructor
public class BankReportService {

    private final TransactionRepository transactionRepository;
    private final SourceRepository sourceRepository;
    private final BankStatementParser parser;

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
        }

        return new ImportReportResponse(original, sourceId, imported, skipped, parsed.size());
    }
}
