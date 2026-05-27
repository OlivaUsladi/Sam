package com.example.backend.controller;

import com.example.backend.dto.TransactionDtos.*;
import com.example.backend.security.AuthenticatedUser;
import com.example.backend.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/finance/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    @GetMapping
    public List<TransactionResponse> list(@AuthenticationPrincipal AuthenticatedUser principal,
                                          @RequestParam(required = false) String type,
                                          @RequestParam(name = "from", required = false)
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                          @RequestParam(name = "to", required = false)
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.list(principal.id(), type, from, to);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@AuthenticationPrincipal AuthenticatedUser principal,
                                      @Valid @RequestBody CreateTransactionRequest req) {
        return service.create(principal.id(), req);
    }

    @PutMapping("/{transactionId}")
    public TransactionResponse update(@AuthenticationPrincipal AuthenticatedUser principal,
                                      @PathVariable Integer transactionId,
                                      @Valid @RequestBody UpdateTransactionRequest req) {
        return service.update(principal.id(), transactionId, req);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthenticatedUser principal,
                                       @PathVariable Integer transactionId) {
        service.delete(principal.id(), transactionId);
        return ResponseEntity.noContent().build();
    }
}
