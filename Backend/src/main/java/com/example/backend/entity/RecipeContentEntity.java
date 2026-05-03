package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "recipe_contents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeContentEntity {

    @Id
    private Integer id;

    @OneToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    @MapsId
    private RecipeEntity recipe;

    @Column(name = "cooking_steps", columnDefinition = "JSONB")
    private String cookingSteps;

    @Column(length = 500)
    private String tips;
}