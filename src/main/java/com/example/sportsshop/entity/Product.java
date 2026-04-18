package com.example.sportsshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false,columnDefinition = "nvarchar(255)")
    private String name;

    private double price;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
