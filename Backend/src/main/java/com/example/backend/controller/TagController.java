package com.example.backend.controller;

import com.example.backend.dto.TagDtos.*;
import com.example.backend.dto.TransactionDtos.*;
import com.example.backend.security.AuthenticatedUser;
import com.example.backend.service.TagService;
import com.example.backend.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/finance/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final TransactionService transactionService;

    @GetMapping
    public List<TagResponse> list(@AuthenticationPrincipal AuthenticatedUser principal) {
        return tagService.list(principal.id());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagResponse create(@AuthenticationPrincipal AuthenticatedUser principal,
                              @Valid @RequestBody CreateTagRequest req) {
        return tagService.create(principal.id(), req);
    }

    @PutMapping("/{tagId}")
    public TagResponse update(@AuthenticationPrincipal AuthenticatedUser principal,
                              @PathVariable Integer tagId,
                              @Valid @RequestBody UpdateTagRequest req) {
        return tagService.update(principal.id(), tagId, req);
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthenticatedUser principal,
                                       @PathVariable Integer tagId) {
        tagService.delete(principal.id(), tagId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{tagId}/transactions")
    public List<TransactionResponse> transactions(@AuthenticationPrincipal AuthenticatedUser principal,
                                                  @PathVariable Integer tagId) {
        return transactionService.listByTag(principal.id(), tagId);
    }

    @PutMapping("/{tagId}/transactions")
    public ResponseEntity<Void> assign(@AuthenticationPrincipal AuthenticatedUser principal,
                                       @PathVariable Integer tagId,
                                       @Valid @RequestBody AssignTagRequest req) {
        transactionService.replaceTagAssignment(principal.id(), tagId, req.transactionIds());
        return ResponseEntity.noContent().build();
    }
}
