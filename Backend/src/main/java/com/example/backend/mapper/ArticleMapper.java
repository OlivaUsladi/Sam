package com.example.backend.mapper;

import com.example.backend.dto.ArticleCategoryResponseDto;
import com.example.backend.dto.ArticleDetailResponseDto;
import com.example.backend.dto.ArticleResponseDto;
import com.example.backend.dto.ContentBlockDto;
import com.example.backend.entity.ArticleCategoryEntity;
import com.example.backend.entity.ArticleContentEntity;
import com.example.backend.entity.ArticleEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ArticleMapper {

    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {};
    private static final TypeReference<List<ContentBlockDto>> BLOCKS_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public ArticleCategoryResponseDto toDto(ArticleCategoryEntity category) {
        return new ArticleCategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }

    public ArticleResponseDto toDto(ArticleEntity article,
                                    ArticleCategoryEntity category,
                                    boolean isFavorite,
                                    boolean isLiked) {
        return new ArticleResponseDto(
                article.getId(),
                article.getTitle(),
                toDto(category),
                parseStringList(article.getMainWords()),
                article.getAuthor(),
                article.getImageUrl(),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                article.getLikesCount() == null ? 0 : article.getLikesCount(),
                isFavorite,
                isLiked
        );
    }

    public ArticleDetailResponseDto toDetailDto(ArticleEntity article,
                                                ArticleCategoryEntity category,
                                                ArticleContentEntity content,
                                                boolean isFavorite,
                                                boolean isLiked) {
        return new ArticleDetailResponseDto(
                article.getId(),
                article.getTitle(),
                toDto(category),
                parseStringList(article.getMainWords()),
                article.getAuthor(),
                article.getImageUrl(),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                article.getLikesCount() == null ? 0 : article.getLikesCount(),
                isFavorite,
                isLiked,
                content == null ? List.of() : parseBlocks(content.getBlocks()),
                content == null ? null : content.getChecklist()
        );
    }

    private List<String> parseStringList(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, STRING_LIST_TYPE);
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<ContentBlockDto> parseBlocks(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, BLOCKS_TYPE);
        } catch (Exception e) {
            return List.of();
        }
    }
}
