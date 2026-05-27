package com.example.backend.controller;

import com.example.backend.dto.GoalDtos.*;
import com.example.backend.security.AuthenticatedUser;
import com.example.backend.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/finance/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService service;

    @GetMapping
    public List<GoalResponse> list(@AuthenticationPrincipal AuthenticatedUser principal) {
        return service.list(principal.id());
    }

    @GetMapping("/{goalId}")
    public GoalResponse get(@AuthenticationPrincipal AuthenticatedUser principal,
                            @PathVariable Integer goalId) {
        return service.get(principal.id(), goalId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalResponse create(@AuthenticationPrincipal AuthenticatedUser principal,
                               @Valid @RequestBody CreateGoalRequest req) {
        return service.create(principal.id(), req);
    }

    @PutMapping("/{goalId}")
    public GoalResponse update(@AuthenticationPrincipal AuthenticatedUser principal,
                               @PathVariable Integer goalId,
                               @Valid @RequestBody UpdateGoalRequest req) {
        return service.update(principal.id(), goalId, req);
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthenticatedUser principal,
                                       @PathVariable Integer goalId) {
        service.delete(principal.id(), goalId);
        return ResponseEntity.noContent().build();
    }
}
