package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "article_contents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleContentEntity {

    @Id
    @Column(name = "id")
    private Integer id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "blocks", columnDefinition = "JSONB")
    private String blocks;

    @Column(name = "checklist", columnDefinition = "TEXT")
    private String checklist;
}
