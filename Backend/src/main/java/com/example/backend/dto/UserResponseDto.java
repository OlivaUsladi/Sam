package com.example.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDto {
    private Integer id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}