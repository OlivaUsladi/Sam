package com.example.backend.dto;

public record ArticleContentBlockDto(
        String type,
        String text,
        String style,
        Integer size,
        String area,
        Integer imageId,
        String url,
        Integer width,
        Integer height
) {
}