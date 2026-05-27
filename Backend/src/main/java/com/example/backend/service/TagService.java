package com.example.backend.service;

import com.example.backend.dto.TagDtos.*;
import com.example.backend.entity.TagEntity;
import com.example.backend.repository.TagRepository;
import com.example.backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public List<TagResponse> list(Integer userId) {
        return tagRepository.findByUserIdOrderByNameAsc(userId)
                .stream().map(TagResponse::from).toList();
    }

    @Transactional
    public TagResponse create(Integer userId, CreateTagRequest req) {
        if (tagRepository.existsByUserIdAndNameIgnoreCase(userId, req.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Тэг с таким именем уже есть");
        }
        TagEntity e = new TagEntity();
        e.setUserId(userId);
        e.setName(req.name().trim());
        return TagResponse.from(tagRepository.save(e));
    }

    @Transactional
    public TagResponse update(Integer userId, Integer tagId, UpdateTagRequest req) {
        TagEntity e = tagRepository.findByIdAndUserId(tagId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Тэг не найден"));
        e.setName(req.name().trim());
        return TagResponse.from(tagRepository.save(e));
    }

    @Transactional
    public void delete(Integer userId, Integer tagId) {
        TagEntity e = tagRepository.findByIdAndUserId(tagId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Тэг не найден"));
        transactionRepository.clearTagFromUserTransactions(tagId, userId);
        tagRepository.delete(e);
    }
}
