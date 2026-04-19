package com.example.sportsshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String customerName;
    private String customerEmail;
    private String shippingAddress;
    private String productName;
    private int quantityBought;
    private double totalPrice;
    private LocalDateTime orderDate;
    private String status;

}
