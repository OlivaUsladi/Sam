package com.example.backend.dto;

import lombok.Builder;
import lombok.Data;

//Builder пример: Author.builder().id("1").name("Maria").surname("Williams").build()
@Data
@Builder
public class CategoryResponseDto {
    private Integer id;
    private String name;
    private String description;
}