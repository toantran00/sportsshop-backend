package com.example.sportsshop.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @Min(value = 0, message = "Giá sản phẩm không hợp lệ")
    private double price;

    @Min(value = 0, message = "Số lượng không hợp lệ")
    private int quantity;

    @JsonAlias({"category", "categoryName"})
    @NotBlank(message = "Danh mục không được để trống")
    private String categoryName;

    private String imageUrl;
}