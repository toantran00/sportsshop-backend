package com.example.sportsshop.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private int productId;
    private int quantity;
}
