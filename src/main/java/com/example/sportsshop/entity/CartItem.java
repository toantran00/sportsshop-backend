package com.example.sportsshop.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

    private int quantity;
}
