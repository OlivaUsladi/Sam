package com.example.backend.service;

import com.example.backend.dto.TransactionDtos.*;
import com.example.backend.entity.TransactionEntity;
import com.example.backend.repository.SourceRepository;
import com.example.backend.repository.TagRepository;
import com.example.backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final SourceRepository sourceRepository;
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<TransactionResponse> list(Integer userId, String type, LocalDate start, LocalDate end) {
        List<TransactionEntity> result;
        if (type != null && start != null && end != null) {
            result = transactionRepository
                    .findByUserIdAndTypeAndTransactionDateBetweenOrderByTransactionDateDescIdDesc(
                            userId, type, start, end);
        } else if (type != null) {
            result = transactionRepository
                    .findByUserIdAndTypeOrderByTransactionDateDescIdDesc(userId, type);
        } else if (start != null && end != null) {
            result = transactionRepository
                    .findByUserIdAndTransactionDateBetweenOrderByTransactionDateDescIdDesc(
                            userId, start, end);
        } else {
            result = transactionRepository.findByUserIdOrderByTransactionDateDescIdDesc(userId);
        }
        return result.stream().map(TransactionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> listByTag(Integer userId, Integer tagId) {
        return transactionRepository
                .findByUserIdAndTagIdOrderByTransactionDateDescIdDesc(userId, tagId)
                .stream().map(TransactionResponse::from).toList();
    }

    @Transactional
    public TransactionResponse create(Integer userId, CreateTransactionRequest req) {
        ensureSourceOwnedBy(userId, req.sourceId());
        if (req.tagId() != null) {
            if (!"expense".equals(req.type())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Тэг можно ставить только на расход");
            }
            ensureTagOwnedBy(userId, req.tagId());
        }

        TransactionEntity e = new TransactionEntity();
        e.setUserId(userId);
        e.setName(req.name().trim());
        e.setAmount(req.amount());
        e.setType(req.type());
        e.setDescription(req.description());
        e.setTransactionDate(req.transactionDate());
        e.setSourceId(req.sourceId());
        e.setTagId(req.tagId());
        return TransactionResponse.from(transactionRepository.save(e));
    }

    @Transactional
    public TransactionResponse update(Integer userId, Integer transactionId,
                                      UpdateTransactionRequest req) {
        TransactionEntity e = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Транзакция не найдена"));
        ensureSourceOwnedBy(userId, req.sourceId());
        if (req.tagId() != null) {
            if (!"expense".equals(req.type())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Тэг можно ставить только на расход");
            }
            ensureTagOwnedBy(userId, req.tagId());
        }

        e.setName(req.name().trim());
        e.setAmount(req.amount());
        e.setType(req.type());
        e.setDescription(req.description());
        e.setTransactionDate(req.transactionDate());
        e.setSourceId(req.sourceId());
        e.setTagId(req.tagId());
        return TransactionResponse.from(transactionRepository.save(e));
    }

    @Transactional
    public void delete(Integer userId, Integer transactionId) {
        TransactionEntity e = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Транзакция не найдена"));
        transactionRepository.delete(e);
    }

    @Transactional
    public void replaceTagAssignment(Integer userId, Integer tagId, List<Integer> ids) {
        ensureTagOwnedBy(userId, tagId);
        transactionRepository.clearTagFromUserTransactions(tagId, userId);
        if (ids != null && !ids.isEmpty()) {
            transactionRepository.updateTagForUserTransactions(ids, tagId, userId);
        }
    }

    private void ensureSourceOwnedBy(Integer userId, Integer sourceId) {
        if (!sourceRepository.findByIdAndUserId(sourceId, userId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Источник не найден или не принадлежит пользователю");
        }
    }

    private void ensureTagOwnedBy(Integer userId, Integer tagId) {
        if (!tagRepository.findByIdAndUserId(tagId, userId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Тэг не найден или не принадлежит пользователю");
        }
    }
}
