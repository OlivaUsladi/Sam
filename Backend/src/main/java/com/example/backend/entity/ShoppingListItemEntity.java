package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "shopping_list_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "shopping_list_id", nullable = false)
    private Integer shoppingListId;

    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @Column(name = "is_checked")
    private Boolean isChecked = false;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "unit", length = 20)
    private String unit;

}