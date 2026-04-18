package com.example.sportsshop.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class OrderRequest {
    private int productId;
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private int quantity;
}
