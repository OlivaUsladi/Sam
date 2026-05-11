package com.example.backend.dto;

import com.example.backend.entity.UserEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FavouriteResponseDto {
    private Long id;
    private UserResponseDto user;
    private RecipeResponseDto recipe;
}
