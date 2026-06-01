package com.example.backend.dto;

import java.time.LocalDate;

public final class ImportReportDtos {

    private ImportReportDtos() {}


    public record ImportReportResponse(
            String fileName,
            Integer sourceId,
            int imported,
            int skipped,
            int total,
            LocalDate firstDate,
            LocalDate lastDate,
            String suggestedMonth
    ) {}
}
