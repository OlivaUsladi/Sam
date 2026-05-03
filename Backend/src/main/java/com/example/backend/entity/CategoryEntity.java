package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


//@Data – это сокращенная аннотация, сочетающая возможности @ToString,
// @EqualsAndHashCode, @Getter @Setter и @RequiredArgsConstructor

@Entity
@Table(name = "recipe_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 250)
    private String description;
}