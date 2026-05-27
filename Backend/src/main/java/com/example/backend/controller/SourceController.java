package com.example.backend.controller;

import com.example.backend.dto.SourceDtos.*;
import com.example.backend.security.AuthenticatedUser;
import com.example.backend.service.SourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/finance/sources")
@RequiredArgsConstructor
public class SourceController {

    private final SourceService service;

    @GetMapping
    public List<SourceResponse> list(@AuthenticationPrincipal AuthenticatedUser principal) {
        return service.list(principal.id());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SourceResponse create(@AuthenticationPrincipal AuthenticatedUser principal,
                                 @Valid @RequestBody CreateSourceRequest req) {
        return service.create(principal.id(), req);
    }

    @PutMapping("/{sourceId}")
    public SourceResponse update(@AuthenticationPrincipal AuthenticatedUser principal,
                                 @PathVariable Integer sourceId,
                                 @Valid @RequestBody UpdateSourceRequest req) {
        return service.update(principal.id(), sourceId, req);
    }

    @DeleteMapping("/{sourceId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthenticatedUser principal,
                                       @PathVariable Integer sourceId) {
        service.delete(principal.id(), sourceId);
        return ResponseEntity.noContent().build();
    }
}
