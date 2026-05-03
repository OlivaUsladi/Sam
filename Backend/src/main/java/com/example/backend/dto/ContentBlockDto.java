package com.example.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContentBlockDto {
    private String type;      // "paragraph" или "image"
    private String text;
    private String style;     // "normal", "bold", "italic", "underlined"
    private Integer size;
    private String area;      // "left", "center", "right"
    private Integer imageId;
    private String url;
    private Integer width;
    private Integer height;
}