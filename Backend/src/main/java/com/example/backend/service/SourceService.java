package com.example.backend.service;

import com.example.backend.dto.SourceDtos.*;
import com.example.backend.entity.SourceEntity;
import com.example.backend.repository.SourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SourceService {

    private final SourceRepository sourceRepository;

    @Transactional(readOnly = true)
    public List<SourceResponse> list(Integer userId) {
        return sourceRepository.findByUserIdOrderByNameAsc(userId)
                .stream().map(SourceResponse::from).toList();
    }

    @Transactional
    public SourceResponse create(Integer userId, CreateSourceRequest req) {
        if (sourceRepository.existsByUserIdAndNameIgnoreCase(userId, req.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Источник с таким именем уже есть");
        }
        SourceEntity e = new SourceEntity();
        e.setUserId(userId);
        e.setName(req.name().trim());
        e.setType(req.type());
        return SourceResponse.from(sourceRepository.save(e));
    }

    @Transactional
    public SourceResponse update(Integer userId, Integer sourceId, UpdateSourceRequest req) {
        SourceEntity e = sourceRepository.findByIdAndUserId(sourceId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Источник не найден"));
        e.setName(req.name().trim());
        e.setType(req.type());
        return SourceResponse.from(sourceRepository.save(e));
    }

    @Transactional
    public void delete(Integer userId, Integer sourceId) {
        SourceEntity e = sourceRepository.findByIdAndUserId(sourceId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Источник не найден"));
        sourceRepository.delete(e);
    }
}
