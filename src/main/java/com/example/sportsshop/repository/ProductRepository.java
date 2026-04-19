package com.example.sportsshop.repository;

import com.example.sportsshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    /**
     * Bộ lọc siêu việt: Tìm theo từ khoá + khoảng giá + danh mục.
     * Sorting được xử lý bởi Pageable (truyền Sort từ controller).
     */
    @Query("SELECT p FROM Product p WHERE " +
            "(:keyword IS NULL OR TRIM(:keyword) = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:category IS NULL OR TRIM(:category) = '' OR LOWER(p.category.name) = LOWER(:category))")
    Page<Product> filterProducts(
            @Param("keyword") String keyword,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("category") String category,
            Pageable pageable);
}
