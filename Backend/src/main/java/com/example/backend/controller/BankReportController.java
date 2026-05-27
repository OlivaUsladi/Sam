package com.example.backend.controller;

import com.example.backend.dto.ImportReportDtos.ImportReportResponse;
import com.example.backend.security.AuthenticatedUser;
import com.example.backend.service.BankReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/finance/reports")
@RequiredArgsConstructor
public class BankReportController {

    private final BankReportService service;

    @PostMapping(path = "/import", consumes = "multipart/form-data")
    public ImportReportResponse importReport(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam("sourceId") Integer sourceId,
            @RequestPart("file") MultipartFile file) {
        return service.importReport(principal.id(), sourceId, file);
    }
}
