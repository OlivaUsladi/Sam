package com.example.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ArticleDetailResponseDto(
        Integer id,
        String title,
        ArticleCategoryResponseDto category,
        List<String> mainWords,
        String author,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer likesCount,
        Boolean isFavorite,
        Boolean isLiked,
        List<ContentBlockDto> blocks,
        String checklist
) {
}
