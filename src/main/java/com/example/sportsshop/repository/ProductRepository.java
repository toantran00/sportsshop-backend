package com.example.sportsshop.repository;

import com.example.sportsshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Integer> {
    // Tìm kiếm vợt/giày theo tên và phân trang
    Page<Product> findByNameContaining(String name, Pageable pageable);
}
