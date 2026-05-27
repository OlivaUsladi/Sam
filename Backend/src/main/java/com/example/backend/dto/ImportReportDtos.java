package com.example.backend.dto;

public final class ImportReportDtos {

    private ImportReportDtos() {}

    public record ImportReportResponse(
            String fileName,
            Integer sourceId,
            int imported,
            int skipped,
            int total
    ) {}
}
